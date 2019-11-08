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
import dnn.memory.{ReadTensorController, TensorLoad, TensorMaster, TensorStore, WriteTensorController, inDMA_act, inDMA_wgt, outDMA_act}
import dnn.{DotNode, MacNode, ReduceNode}
import interfaces.{ControlBundle, CustomDataBundle, DataBundle}
import junctions.SplitCallNew
import node.{FXmatNxN, UnTypStore, matNxN, vecN}
import shell._
import dnn.memory.ISA._
import dnnnode.{Mac2dTensor, ShapeTransformer, StoreQueue, TLoad, TStore, WeightShapeTransformer}
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
  val macShape = new matNxN(3, false)
  val wgtShape = new vecN(9, 0, false)

  val inDMA_act = Module(new inDMA_act(4, 1, "inp")(memShape))

  val inDMA_wgt = Module(new inDMA_wgt(20, 100, wgtTensorType = "wgt", memTensorType = "inp")(wgtShape))
  val readTensorController2 = Module(new ReadTensorController(1, "wgt")(wgtShape))

  val outDMA_act = Module(new outDMA_act(2, 20, "inp"))

  val mac2dTensor = Module(new Mac2dTensor(2, "wgt", "inp")(memShape)(wgtShape)(macShape))

  /* ================================================================== *
     *                      Basic Block signals                         *
     * ================================================================== */
  mac2dTensor.io.enable.bits <> ControlBundle.active()
  mac2dTensor.io.enable.valid := true.B

  mac2dTensor.io.wgtIndex := 0.U
  mac2dTensor.io.rowWidth := 18.U

  inDMA_act.io.rowWidth := 20.U
  inDMA_wgt.io.numWeight := 7.U
  outDMA_act.io.rowWidth := 18.U
  /* ================================================================== *
     *                           Connections                            *
     * ================================================================== */
  outDMA_act.io.in(0) <> mac2dTensor.io.Out(0)
  outDMA_act.io.in(1) <> mac2dTensor.io.Out(1)

  outDMA_act.io.last.foreach(a => a := mac2dTensor.io.last)

  inDMA_act.io.ReadIn(0)(0) <> mac2dTensor.io.tensorReq(0)
  inDMA_act.io.ReadIn(1)(0) <> mac2dTensor.io.tensorReq(1)
  inDMA_act.io.ReadIn(2)(0) <> mac2dTensor.io.tensorReq(2)
  inDMA_act.io.ReadIn(3)(0) <> mac2dTensor.io.tensorReq(3)

  mac2dTensor.io.tensorResp(0) <> inDMA_act.io.ReadOut(0)(0)
  mac2dTensor.io.tensorResp(1) <> inDMA_act.io.ReadOut(1)(0)
  mac2dTensor.io.tensorResp(2) <> inDMA_act.io.ReadOut(2)(0)
  mac2dTensor.io.tensorResp(3) <> inDMA_act.io.ReadOut(3)(0)

  readTensorController2.io.ReadIn(0) <> mac2dTensor.io.wgtTensorReq
  mac2dTensor.io.wgtTensorResp <> readTensorController2.io.ReadOut(0)
  inDMA_wgt.io.tensor <> readTensorController2.io.tensor

  /* ================================================================== *
    *                      VME and VCR Connections                      *
    * ================================================================== */
  io.vcr.ecnt(0).bits := cycle_count.value

  io.vme.rd(0) <> inDMA_act.io.vme_rd(0)
  io.vme.rd(1) <> inDMA_act.io.vme_rd(1)
  io.vme.rd(2) <> inDMA_act.io.vme_rd(2)
  io.vme.rd(3) <> inDMA_act.io.vme_rd(3)

  io.vme.rd(4) <> inDMA_wgt.io.vme_rd

  io.vme.wr(0) <> outDMA_act.io.vme_wr(0)
  io.vme.wr(1) <> outDMA_act.io.vme_wr(1)

  mac2dTensor.io.start := false.B

  inDMA_act.io.start := false.B
  inDMA_act.io.baddr := io.vcr.ptrs(0)

  inDMA_wgt.io.start := false.B
  inDMA_wgt.io.baddr := io.vcr.ptrs(1)

  outDMA_act.io.start := false.B
  outDMA_act.io.baddr := io.vcr.ptrs(2)

  val sIdle :: sReadTensor1 :: sReadTensor2 :: sMacStart :: sMacWaiting :: sWriteTensor :: sFinish :: Nil = Enum(7)

  val state = RegInit(sIdle)
  switch(state) {
    is(sIdle) {
      when(io.vcr.launch) {
        inDMA_act.io.start := true.B
        state := sReadTensor1
      }
    }
    is(sReadTensor1) {
      when(inDMA_act.io.done) {
        inDMA_wgt.io.start := true.B
        state := sReadTensor2
      }
    }
    is(sReadTensor2) {
      when(inDMA_wgt.io.done) {
        state := sMacStart
      }
    }
    is(sMacStart) {
      mac2dTensor.io.start := true.B
      state := sMacWaiting
    }
    is(sMacWaiting) { //4
      when(mac2dTensor.io.done) {
        state := sWriteTensor
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

  val last = state === sFinish && outDMA_act.io.done
  io.vcr.finish := last
  io.vcr.ecnt(0).valid := last

  when(state =/= sIdle) {
    cycle_count.inc()
  }
}
