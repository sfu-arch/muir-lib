package accel

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import arbiters.TypeStackFile
import chisel3.{when, _}
import chisel3.util._
import config._
import control.BasicBlockNoMaskNode
import dnn.memory.{ReadTensorController, TensorLoad, TensorMaster, TensorStore, inDMA_wgt, WriteTensorController, inDMA_act, outDMA_act}
import dnn.{DotNode, MacNode, ReduceNode}
import interfaces.{ControlBundle, CustomDataBundle, DataBundle}
import junctions.SplitCallNew
import node.{FXmatNxN, UnTypStore, matNxN, vecN}
import shell._
import dnn.memory.ISA._
import dnnnode.{ShapeTransformer, StoreQueue, TLoad, TStore, WeightShapeTransformer}
import firrtl.transforms.DontTouchAnnotation

/** Core.
  *
  * The DNNcore defines the current DNN accelerator by connecting memory and
  * compute modules together such as load/store and compute. Most of the
  * connections in the core are bulk (<>), and we should try to keep it this
  * way, because it is easier to understand what is going on.
  *
  * Also, the DNNcore must be instantiated by a shell using the
  * VTA Control Register (VCR) and the VTA Memory Engine (VME) interfaces.
  * More info about these interfaces and modules can be found in the shell
  * directory.
  */
class DNNCore(implicit val p: Parameters) extends Module {
  val io = IO(new Bundle {
    val vcr = new VCRClient
    val vme = new VMEMaster
  })

  val cycle_count = new Counter(2000)

  val memShape = new vecN(16, 0, false)
  val shapeOut = new matNxN(3, false)
  val wgtShape = new vecN(9, 0, false)


  val inDMA_act = Module(new inDMA_act(3, 1, "inp")(memShape))

  val inDMA_wgt = Module(new inDMA_wgt(20, 100, wgtTensorType = "wgt", memTensorType = "inp")(wgtShape))
  val readTensorController2 = Module(new ReadTensorController(1, "wgt")(wgtShape))

  val outDMA_act = Module(new outDMA_act(1, 20, "inp"))


  val indexCnt = Counter(100)

  val Load1 = Module(new TLoad(NumPredOps = 0, NumSuccOps = 0, NumOuts = 1, ID = 0, RouteID = 0)(memShape))
  val Load2 = Module(new TLoad(NumPredOps = 0, NumSuccOps = 0, NumOuts = 1, ID = 0, RouteID = 0)(memShape))
  val Load3 = Module(new TLoad(NumPredOps = 0, NumSuccOps = 0, NumOuts = 1, ID = 0, RouteID = 0)(memShape))

  val LoadB = Module(new TLoad(NumPredOps = 0, NumSuccOps = 0, NumOuts = 1, ID = 0, RouteID = 0)(wgtShape))
  val macNode = Module(new MacNode(NumOuts = 1, ID = 0, lanes = 3)(shapeOut))

  val shapeTransformer = Module(new ShapeTransformer(NumIns = 3, NumOuts = 1, ID = 0)(memShape)(shapeOut))
  /* ================================================================== *
     *                      Basic Block signals                         *
     * ================================================================== */

  Load1.io.enable.bits <> ControlBundle.active()
  Load1.io.enable.valid := true.B

  Load2.io.enable.bits <> ControlBundle.active()
  Load2.io.enable.valid := true.B

  Load3.io.enable.bits <> ControlBundle.active()
  Load3.io.enable.valid := true.B


  LoadB.io.enable.bits <> ControlBundle.active()
  LoadB.io.enable.valid := true.B

  macNode.io.enable.bits <> ControlBundle.active()
  macNode.io.enable.valid := true.B

  shapeTransformer.io.enable.bits := ControlBundle.active()
  shapeTransformer.io.enable.valid := true.B

  /* ================================================================== *
     *                    Dot and Reduce signals                        *
     * ================================================================== */
  shapeTransformer.io.in(0) <> Load1.io.Out(0)
  shapeTransformer.io.in(1) <> Load2.io.Out(0)
  shapeTransformer.io.in(2) <> Load3.io.Out(0)

  dontTouch(shapeTransformer.io.Out)

  macNode.io.LeftIO <> shapeTransformer.io.Out(0)
  macNode.io.RightIO <> LoadB.io.Out(0)

  // Wire up ReduceNode Outputs
  for (i <- 0 until macNode.NumOuts) {
    outDMA_act.io.in(0) <> macNode.io.Out(i)

  }

  /* ================================================================== *
     *         read/write Tensor Controllers signals                    *
     * ================================================================== */

  inDMA_act.io.ReadIn(0)(0) <> Load1.io.tensorReq
  inDMA_act.io.ReadIn(1)(0) <> Load2.io.tensorReq
  inDMA_act.io.ReadIn(2)(0) <> Load3.io.tensorReq

  Load1.io.tensorResp <> inDMA_act.io.ReadOut(0)(0)
  Load2.io.tensorResp <> inDMA_act.io.ReadOut(1)(0)
  Load3.io.tensorResp <> inDMA_act.io.ReadOut(2)(0)


  readTensorController2.io.ReadIn(0) <> LoadB.io.tensorReq
  LoadB.io.tensorResp <> readTensorController2.io.ReadOut(0)
  inDMA_wgt.io.tensor <> readTensorController2.io.tensor

  /* ================================================================== *
    *                       Load Store signals                          *
    * ================================================================== */
  Load1.io.GepAddr.valid := false.B
  Load1.io.GepAddr.bits.taskID := 0.U
  Load1.io.GepAddr.bits.predicate := true.B
  Load1.io.GepAddr.bits.data := indexCnt.value

  Load2.io.GepAddr.valid := false.B
  Load2.io.GepAddr.bits.taskID := 0.U
  Load2.io.GepAddr.bits.predicate := true.B
  Load2.io.GepAddr.bits.data := indexCnt.value

  Load3.io.GepAddr.valid := false.B
  Load3.io.GepAddr.bits.taskID := 0.U
  Load3.io.GepAddr.bits.predicate := true.B
  Load3.io.GepAddr.bits.data := indexCnt.value

  LoadB.io.GepAddr.valid := false.B
  LoadB.io.GepAddr.bits.taskID := 0.U
  LoadB.io.GepAddr.bits.predicate := true.B
  LoadB.io.GepAddr.bits.data := indexCnt.value


  io.vcr.ecnt(0).bits := cycle_count.value

  io.vme.rd(0) <> inDMA_act.io.vme_rd(0)
  io.vme.rd(1) <> inDMA_act.io.vme_rd(1)
  io.vme.rd(2) <> inDMA_act.io.vme_rd(2)

  io.vme.rd(3) <> inDMA_wgt.io.vme_rd

  io.vme.wr(0) <> outDMA_act.io.vme_wr(0)

  inDMA_act.io.start := false.B
  inDMA_act.io.baddr := io.vcr.ptrs(0)
  inDMA_act.io.rowWidth := 20.U

  inDMA_wgt.io.start := false.B
  inDMA_wgt.io.baddr := io.vcr.ptrs(1)
  inDMA_wgt.io.numWeight := 7.U

  outDMA_act.io.start := false.B
  outDMA_act.io.baddr := io.vcr.ptrs(2)
  outDMA_act.io.rowWidth := 18.U
  outDMA_act.io.last.foreach(a => a := false.B)


  val sIdle :: sReadTensor1 :: sReadTensor2 :: sMacStart :: sMacWaiting :: sNextOp :: sWriteTensor :: sFinish :: Nil = Enum(8)

  val state = RegInit(sIdle)
  switch(state) {
    is(sIdle) { //state: 0
      when(io.vcr.launch) {
        inDMA_act.io.start := true.B
        indexCnt.value := 0.U
        state := sReadTensor1
      }
    }
    is(sReadTensor1) {  //1
      when(inDMA_act.io.done) {
        inDMA_wgt.io.start := true.B
        state := sReadTensor2
      }
    }
    is(sReadTensor2) { //2
      when(inDMA_wgt.io.done) {
        state := sMacStart
      }
    }
    is(sMacStart) { //3
      Load1.io.GepAddr.valid := true.B
      Load2.io.GepAddr.valid := true.B
      Load3.io.GepAddr.valid := true.B
      LoadB.io.GepAddr.valid := true.B
      state := sMacWaiting
    }
    is(sMacWaiting) { //4
      when(macNode.io.Out(0).fire()) {
        state := sNextOp
      }
    }
    is(sNextOp) { //5
      when(indexCnt.value === 17.U) {
        indexCnt.value := 0.U
        state := sWriteTensor
        outDMA_act.io.last.foreach(a => a := true.B)
      }.otherwise {
        state := sMacStart
        indexCnt.inc()
      }
    }

    is(sWriteTensor) {
      outDMA_act.io.start := true.B
      state := sFinish
    }
    is(sFinish) { //7
      when(outDMA_act.io.done) {
        state := sIdle
      }
    }
  }

  val last = state === sFinish && outDMA_act.io.done //tensorStore.io.vme_wr.ack
  io.vcr.finish := last
  io.vcr.ecnt(0).valid := last

  when(state =/= sIdle) {
    cycle_count.inc()
  }
}
