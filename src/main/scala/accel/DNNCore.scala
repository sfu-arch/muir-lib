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
import dnn.memory.{ReadTensorController, TensorLoad, TensorMaster, TensorStore, WriteTensorController}
import dnn.{DotNode, MacNode, ReduceNode}
import interfaces.{ControlBundle, DataBundle}
import junctions.SplitCallNew
import node.{FXmatNxN, UnTypStore, matNxN, vecN}
import shell._
import dnn.memory.ISA._
import dnnnode.{ShapeTransformer, TLoad, TStore}

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

  val shapeIn = new vecN(24, 0, false)
  val shapeOut = new matNxN(3, false)

  val tensorLoad1 = Module(new TensorLoad(tensorType = "inp"))
  val readTensorController1 = Module(new ReadTensorController(1, "inp")(shapeIn))

  val tensorLoad2 = Module(new TensorLoad(tensorType = "inp"))
  val readTensorController2 = Module(new ReadTensorController(1, "inp")(shapeIn))

  val tensorStore = Module(new TensorStore(tensorType = "inp"))
  val writeTensorController = Module(new WriteTensorController(1, "inp")(shapeIn))

  val tl_Inst = Wire(new MemDecode)
  val ts_Inst = Wire(new MemDecode)
  val indexCnt = Counter(100)
  val MacLatency = Counter(200)
  val storeIndex = RegNext(next = indexCnt.value, init = 0.U)


  val conv_bb = Module(new BasicBlockNoMaskNode(NumInputs = 1, NumOuts = 2  , BID = 0))

  val LoadA = Module(new TLoad(NumPredOps = 0, NumSuccOps = 0, NumOuts = 3, ID = 0, RouteID = 0)(shapeIn))
  val LoadB = Module(new TLoad(NumPredOps = 0, NumSuccOps = 0, NumOuts = 1, ID = 0, RouteID = 0)(shapeIn))
  val Store = Module(new TStore(NumPredOps = 0, NumSuccOps = 0, NumOuts = 1, ID = 0, RouteID = 0)(shapeIn))
  val macNode = Module(new MacNode(NumOuts = 1, ID = 0, lanes = 3)(shapeOut))

  val shapeTransformer = Module(new ShapeTransformer(NumIns = 3, NumOuts = 1, ID = 0)(shapeIn)(shapeOut))
  /* ================================================================== *
     *                      Basic Block signals                         *
     * ================================================================== */

  conv_bb.io.predicateIn.bits := ControlBundle(io.vcr.launch)
  conv_bb.io.predicateIn.valid := io.vcr.launch

  LoadA.io.enable <> conv_bb.io.Out(0)
  LoadB.io.enable <> conv_bb.io.Out(1)

  Store.io.enable.bits <> ControlBundle.active()
  Store.io.enable.valid := true.B

  macNode.io.enable.bits <> ControlBundle.active()
  macNode.io.enable.valid := true.B

  shapeTransformer.io.enable.bits := ControlBundle.active()
  shapeTransformer.io.enable.valid := true.B

  /* ================================================================== *
     *                    Dot and Reduce signals                        *
     * ================================================================== */
  shapeTransformer.io.in(0) <> LoadA.io.Out(0)
  shapeTransformer.io.in(1) <> LoadA.io.Out(1)
  shapeTransformer.io.in(2) <> LoadA.io.Out(2)

  macNode.io.LeftIO <> shapeTransformer.io.Out(0)
  macNode.io.RightIO <> LoadB.io.Out(0)

  // Wire up ReduceNode Outputs
  for (i <- 0 until macNode.NumOuts) {
    Store.io.inData <> macNode.io.Out(i)
  }

  /* ================================================================== *
     *         read/write Tensor Controllers signals                    *
     * ================================================================== */
  readTensorController1.io.ReadIn(0) <> LoadA.io.tensorReq
  LoadA.io.tensorResp <> readTensorController1.io.ReadOut(0)
  tensorLoad1.io.tensor <> readTensorController1.io.tensor

  readTensorController2.io.ReadIn(0) <> LoadB.io.tensorReq
  LoadB.io.tensorResp <> readTensorController2.io.ReadOut(0)
  tensorLoad2.io.tensor <> readTensorController2.io.tensor


  writeTensorController.io.WriteIn(0) <> Store.io.tensorReq
  Store.io.tensorResp <> writeTensorController.io.WriteOut(0)
  tensorStore.io.tensor <> writeTensorController.io.tensor

  /* ================================================================== *
    *                       Load Store signals                          *
    * ================================================================== */
  LoadA.io.GepAddr.valid := false.B
  LoadA.io.GepAddr.bits.taskID := 0.U
  LoadA.io.GepAddr.bits.predicate := true.B
  LoadA.io.GepAddr.bits.data := indexCnt.value

  LoadB.io.GepAddr.valid := false.B
  LoadB.io.GepAddr.bits.taskID := 0.U
  LoadB.io.GepAddr.bits.predicate := true.B
  LoadB.io.GepAddr.bits.data := indexCnt.value

  Store.io.GepAddr.valid := macNode.io.Out(0).valid
  Store.io.GepAddr.bits.taskID := 0.U
  Store.io.GepAddr.bits.data := storeIndex
  Store.io.GepAddr.bits.predicate := true.B

  Store.io.Out(0).ready := true.B


  io.vcr.ecnt(0).bits := cycle_count.value

  io.vme.rd(0) <> tensorLoad1.io.vme_rd
  io.vme.rd(1) <> tensorLoad2.io.vme_rd
  io.vme.wr(0) <> tensorStore.io.vme_wr

  tensorLoad1.io.start := false.B
  tensorLoad1.io.baddr := io.vcr.ptrs(0)
  tensorLoad1.io.inst := tl_Inst.asTypeOf(UInt(INST_BITS.W))
  tensorLoad2.io.start := false.B
  tensorLoad2.io.baddr := io.vcr.ptrs(1)
  tensorLoad2.io.inst := tl_Inst.asTypeOf(UInt(INST_BITS.W))


  tensorStore.io.start := false.B
  tensorStore.io.baddr := io.vcr.ptrs(2)
  tensorStore.io.inst := ts_Inst.asTypeOf(UInt(INST_BITS.W))


  tl_Inst.xpad_0 := 0.U
  tl_Inst.xpad_1 := 0.U
  tl_Inst.ypad_0 := 0.U
  tl_Inst.ypad_1 := 0.U
  tl_Inst.xstride := 7.U
  tl_Inst.xsize := 7.U
  tl_Inst.ysize := 1.U
  tl_Inst.empty_0 := 0.U
  tl_Inst.dram_offset := 0.U
  tl_Inst.sram_offset := 0.U
  tl_Inst.id := 3.U
  tl_Inst.push_next := 0.U
  tl_Inst.push_prev := 0.U
  tl_Inst.pop_next := 0.U
  tl_Inst.pop_prev := 0.U
  tl_Inst.op := 0.U

  ts_Inst.xpad_0 := 0.U
  ts_Inst.xpad_1 := 0.U
  ts_Inst.ypad_0 := 0.U
  ts_Inst.ypad_1 := 0.U
  ts_Inst.xstride := 7.U
  ts_Inst.xsize := 7.U
  ts_Inst.ysize := 1.U
  ts_Inst.empty_0 := 0.U
  ts_Inst.dram_offset := 0.U
  ts_Inst.sram_offset := 0.U
  ts_Inst.id := 4.U
  ts_Inst.push_next := 0.U
  ts_Inst.push_prev := 0.U
  ts_Inst.pop_next := 0.U
  ts_Inst.pop_prev := 0.U
  ts_Inst.op := 0.U

  val sIdle :: sReadTensor1 :: sReadTensor2 :: sMacStart :: sMacWaiting :: sNextOp :: sWriteTensor :: sFinish :: Nil = Enum(8)

  val state = RegInit(sIdle)
  switch(state) {
    is(sIdle) { //0
      when(io.vcr.launch) {
        tensorLoad1.io.start := true.B
        indexCnt.value := 0.U
        MacLatency.value := 0.U
        state := sReadTensor1
      }
    }
    is(sReadTensor1) {  //1
      when(tensorLoad1.io.done) {
        tensorLoad2.io.start := true.B
        state := sReadTensor2
      }
    }
    is(sReadTensor2) { //2
      when(tensorLoad2.io.done) {
        state := sMacStart
      }
    }
    is(sMacStart) { //3
      LoadA.io.GepAddr.valid := true.B
      LoadB.io.GepAddr.valid := true.B
      state := sMacWaiting
    }
    is(sMacWaiting) { //4
      when(macNode.io.Out(0).fire()) {
        state := sNextOp
      }
    }
    is(sNextOp) { //5
      when(indexCnt.value === ts_Inst.xsize) {
        indexCnt.value := 0.U
        state := sWriteTensor
      }.otherwise {
        state := sMacStart
        indexCnt.inc()
      }
    }

    is(sWriteTensor) {
      tensorStore.io.start := true.B
      state := sFinish
    }
    is(sFinish) {
      when(tensorStore.io.done) {
        state := sIdle
      }
    }
  }

  when(tensorStore.io.vme_wr.ack && state === sWriteTensor) {
    state := sIdle
  }


  val last = state === sFinish && tensorStore.io.vme_wr.ack
  io.vcr.finish := last
  io.vcr.ecnt(0).valid := last

  when(state =/= sIdle) {
    cycle_count.inc()
  }
}
