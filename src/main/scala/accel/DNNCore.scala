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
import interfaces.{ControlBundle, CustomDataBundle, DataBundle}
import junctions.SplitCallNew
import node.{FXmatNxN, UnTypStore, matNxN, vecN}
import shell._
import dnn.memory.ISA._
import dnn_layers.{DW_Block, DW_PW_Block, PDP_Block, PW_Block}
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

  val NumChannel = 3
  val MACperCH = 4
  val Fx = 2

  val NumPWFilter = 2

  val memShape = new vecN(16, 0, false)

  val macDWShape = new matNxN(3, false)
  val macPW2Shape = new vecN(Fx, 0, false)

  val wgtDWShape = new vecN(9, 0, false)
  val wgtPW2Shape = new vecN(Fx, 0, false)

  val CxShape = new vecN(5, 0, false)

//  val DW_B1 = Module(new DW_Block(3, "wgt", "inp")(memShape)(wgtDWShape)(macDWShape))

//  val conv = Module(new DW_PW_Block(NumChannel, MACperCH, NumPWFilter, "wgt", "wgtPW", "inp")
//                   (memShape)(wgtDWShape)(wgtPWShape)(macDWShape)(macPWShape))

//  val conv = Module(new PW_Block(MACperCH, Fx, 5, "wgtPW", "inp")(memShape)(CxShape))
  val conv = Module(new PDP_Block(MACperCH, Fx, 5, NumPWFilter,
                    "wgtPW1", "wgt", "wgtPW2", "inp")
                    (memShape)(CxShape)
                    (wgtDWShape)(macDWShape)
                    (wgtPW2Shape)(macPW2Shape))

  /* ================================================================== *
     *                      Basic Block signals                         *
     * ================================================================== */
  conv.io.wgtPW1index := 0.U
  conv.io.wgtDWindex := 0.U
  conv.io.wgtPW2index := 0.U

  conv.io.rowWidth := 3.U

  /* ================================================================== *
     *                           Connections                            *
     * ================================================================== */

  io.vcr.ecnt(0).bits := cycle_count.value

  io.vcr.ecnt(1).bits := 1.U //conv.io.inDMA_act_time
  io.vcr.ecnt(2).bits := 2.U//conv.io.inDMA_wgt_time
  io.vcr.ecnt(3).bits := 3.U//conv.io.mac_time

  for (i <- 0 until MACperCH) {
    io.vme.rd(i) <> conv.io.vme_rd(i)
  }

  for (i <- 0 until NumPWFilter * MACperCH) {
    io.vme.wr(i) <> conv.io.vme_wr(i)
  }

  io.vme.rd(MACperCH) <> conv.io.vme_wgtPW1_rd
  io.vme.rd(MACperCH + 1) <> conv.io.vme_wgtDW_rd
  io.vme.rd(MACperCH + 2) <> conv.io.vme_wgtPW2_rd
//  io.vme.rd(13) <> conv.io.vme_wgtPW_rd

  conv.io.start := false.B

  conv.io.inBaseAddr := io.vcr.ptrs(0)

  conv.io.wgtPW1_baddr := io.vcr.ptrs(1)
  conv.io.wgtDW_baddr := io.vcr.ptrs(1)
  conv.io.wgtPW2_baddr := io.vcr.ptrs(1)

  conv.io.outBaseAddr := io.vcr.ptrs(2)

  val sIdle :: sExec :: sFinish :: Nil = Enum(3)

  val state = RegInit(sIdle)
  switch(state) {
    is(sIdle) {
      when(io.vcr.launch) {
        conv.io.start := true.B
        state := sExec
      }
    }
    is(sExec) {
      when(conv.io.done) {
        state := sIdle
      }
    }
  }

  val last = state === sExec && conv.io.done
  io.vcr.finish := last
  io.vcr.ecnt(0).valid := last
  io.vcr.ecnt(1).valid := last
  io.vcr.ecnt(2).valid := last
  io.vcr.ecnt(3).valid := last

  when(state =/= sIdle) {
    cycle_count.inc()
  }
}
