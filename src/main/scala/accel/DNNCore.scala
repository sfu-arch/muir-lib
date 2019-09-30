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
import chisel3._
import chisel3.util._
import config._
import control.BasicBlockNoMaskNode
import dnn.memory.{TensorLoad, TensorMaster, TensorStore, VMELoad, VMEStore}
import dnn.{DotNode, ReduceNode}
import interfaces.{ControlBundle, DataBundle}
import junctions.SplitCallNew
import memory.{ReadTypMemoryController, WriteTypMemoryController}
import node.{FXmatNxN, TypLoad, TypStore}
import shell._
import dnn.memory.ISA._

/** Core.
  *
  * The core defines the current VTA architecture by connecting memory and
  * compute modules together such as load/store and compute. Most of the
  * connections in the core are bulk (<>), and we should try to keep it this
  * way, because it is easier to understand what is going on.
  *
  * Also, the core must be instantiated by a shell using the
  * VTA Control Register (VCR) and the VTA Memory Engine (VME) interfaces.
  * More info about these interfaces and modules can be found in the shell
  * directory.
  */
class DNNCore(implicit val p: Parameters) extends Module with CoreParams {
//  val mp = p(ShellKey).memParams
  val io = IO(new Bundle {
    val vcr = new VCRClient
    val vme = new VMEMaster
  })


  val tensorLoad = Module(new TensorLoad(tensorType = "inp"))
  val tensorStore = Module(new TensorStore(tensorType = "inp"))
  val tl_Inst = Reg(UInt(INST_BITS.W).asTypeOf(new MemDecode))
  val ts_Inst = Reg(UInt(INST_BITS.W).asTypeOf(new MemDecode))

//  val tensorMaster = new TensorMaster(tensorType = "inp")

  io.vme.rd(0) <> tensorLoad.io.vme_rd
  io.vme.wr(0) <> tensorStore.io.vme_wr

  tensorLoad.io.start := io.vcr.launch
  tensorLoad.io.baddr := io.vcr.ptrs(0)
  tensorLoad.io.inst  := tl_Inst
  io.vcr.finish := tensorLoad.io.done & tensorStore.io.done

  tensorStore.io.start  := io.vcr.launch
  tensorStore.io.baddr := io.vcr.ptrs(1)
  tensorStore.io.inst := ts_Inst


  tensorStore.io.tensor.wr.bits.data := tensorLoad.io.tensor.rd.data
  tensorStore.io.tensor.wr.bits.idx := 0.U

  tl_Inst.xpad_0  :=  0.U
  tl_Inst.xpad_1  :=  0.U
  tl_Inst.ypad_0  :=  0.U
  tl_Inst.ypad_1  :=  0.U
  tl_Inst.xstride  :=  48.U
  tl_Inst.xsize  :=  48.U
  tl_Inst.ysize  :=  1.U
  tl_Inst.empty_0  :=  0.U
  tl_Inst.dram_offset  :=  64.U
  tl_Inst.sram_offset  :=  0.U
  tl_Inst.id  :=  3.U
  tl_Inst.push_next  :=  0.U
  tl_Inst.push_prev  :=  0.U
  tl_Inst.pop_next  :=  0.U
  tl_Inst.pop_prev  :=  0.U
  tl_Inst.op  :=  0.U

  ts_Inst.xpad_0  :=  0.U
  ts_Inst.xpad_1  :=  0.U
  ts_Inst.ypad_0  :=  0.U
  ts_Inst.ypad_1  :=  0.U
  ts_Inst.xstride  :=  48.U
  ts_Inst.xsize  :=  48.U
  ts_Inst.ysize  :=  1.U
  ts_Inst.empty_0  :=  0.U
  ts_Inst.dram_offset  :=  64.U
  ts_Inst.sram_offset  :=  0.U
  ts_Inst.id  :=  4.U
  ts_Inst.push_next  :=  0.U
  ts_Inst.push_prev  :=  0.U
  ts_Inst.pop_next  :=  0.U
  ts_Inst.pop_prev  :=  0.U
  ts_Inst.op  :=  0.U


  //  val VMELoad = Module(new VMELoad(false))

//  VMELoad.io.memReq <> DontCare
//  VMELoad.io.memResp <> DontCare

//  VMELoad.io.base_addr <> DontCare
//  VMELoad.io <> DontCare

//  io.vcr <> DontCare
//  io.vme <> DontCare
 /* val VMEStore = Module(new VMEStore(false))
  VMEStore.io.memResp <> DontCare
  VMEStore.io.memReq <> DontCare
  VMEStore.io.base_addr <> DontCare
//
//  io.vme.rd(0) <> VMELoad.io.vme_read
  io.vme.rd(0) <> DontCare
  io.vme.wr(0) <> VMEStore.io.vme_write
  io.vcr.ecnt(0.U).bits := 342.U
//
//  // launch and finish <---> start and done
//  io.vcr.finish := VMELoad.io.done && VMEStore.io.done
  io.vcr.finish := VMEStore.io.done
  io.vcr.ecnt(0.U).valid := VMEStore.io.done

//  VMELoad.io.start := io.vcr.launch
  VMEStore.io.start := io.vcr.launch

//  VMELoad.io.vme_cmd.bits.addr := io.vcr.ptrs(0)
//  VMELoad.io.vme_cmd.bits.len := io.vcr.vals(0)

  VMEStore.io.vme_cmd.bits.addr := io.vcr.ptrs(0)
  VMEStore.io.vme_cmd.bits.len := io.vcr.vals(0)
  VMEStore.io.vme_cmd.valid := io.vcr.launch*/

  /*val StackFile = Module(new TypeStackFile(ID = 0, Size = 32, NReads = 1, NWrites = 1)
  (WControl = new WriteTypMemoryController(NumOps = 1, BaseSize = 2, NumEntries = 2))
  (RControl = new ReadTypMemoryController(NumOps = 1, BaseSize = 2, NumEntries = 2)))


  StackFile.io.WriteIn(0) <> VMELoad.io.memReq
  VMELoad.io.memResp <> StackFile.io.WriteOut(0)

  StackFile.io.ReadIn(0) <> VMEStore.io.memReq
  VMEStore.io.memResp <> StackFile.io.ReadOut(0)*/


  /*val shape = new FXmatNxN(2, 4)

  val StackFile = Module(new TypeStackFile(ID = 0, Size = 32, NReads = 4, NWrites = 4)
  (WControl = new WriteTypMemoryController(NumOps = 4, BaseSize = 2, NumEntries = 2))
  (RControl = new ReadTypMemoryController(NumOps = 4, BaseSize = 2, NumEntries = 2)))

    val InputSplitter = Module(new SplitCallNew(List(1, 1, 1)))
    InputSplitter.io.In <> io.vme

  val vcr_launch_r = RegInit(Bool())
  val vcr_load_a_ptr = RegInit(UInt(io.vcr.mp.addrBits.W))
  val vcr_load_b_ptr = RegInit(UInt(io.vcr.mp.addrBits.W))
  val vcr_len = RegInit(UInt(io.vcr.mp.dataBits.W))



  val conv_bb = Module(new BasicBlockNoMaskNode(NumInputs = 1, NumOuts = 5, BID = 0))

  val VME_LoadA = Module(new VMELoad(debug = false))
  val VME_LoadB = Module(new VMELoad(debug = false))

  io.vme.rd(0) <> VME_LoadA.io.vme_read
  io.vme.rd(1) <> VME_LoadB.io.vme_read

  //VME_LoadA.io.base_addr := 0.U
  //VME_LoadB.io.base_addr := vcr_len

  VME_LoadA.io.vme_cmd.bits.addr <> io.vcr.ptrs(0)
  VME_LoadA.io.vme_cmd.bits.len <> io.vcr.vals(0)

  VME_LoadB.io.vme_cmd.bits.addr <> io.vcr.ptrs(1)
  VME_LoadB.io.vme_cmd.bits.len <> io.vcr.vals(0)



  val LoadA = Module(new TypLoad(NumPredOps = 0, NumSuccOps = 1, NumOuts = 1, ID = 0, RouteID = 0))
  val LoadB = Module(new TypLoad(NumPredOps = 0, NumSuccOps = 1, NumOuts = 1, ID = 0, RouteID = 1))
  val StoreType = Module(new TypStore(NumPredOps = 2, NumSuccOps = 0, NumOuts = 1, ID = 0, RouteID = 0))

  val dotNode = Module(new DotNode(NumOuts = 1, ID = 0, 4, "Mul")(shape))
  val reduceNode = Module(new ReduceNode(NumOuts = 1, ID = 1, false, "Add")(shape))

//  StackFile.io.WriteIn(0) <> VME_LoadA.io.memReq
//  VME_LoadA.io.memResp <> StackFile.io.WriteOut(0)
//
//  StackFile.io.WriteIn(1) <> VME_LoadB.io.memReq
//  VME_LoadB.io.memResp <> StackFile.io.WriteOut(1)
//
//  StackFile.io.WriteIn(2) <> StoreType.io.memReq
//  StoreType.io.memResp <> StackFile.io.WriteOut(2)
//
//  StackFile.io.ReadIn(0) <> LoadA.io.memReq
//  LoadA.io.memResp <> StackFile.io.ReadOut(0)
//
//  StackFile.io.ReadIn(1) <> LoadB.io.memReq
//  LoadB.io.memResp <> StackFile.io.ReadOut(1)

  //  conv_bb.io.predicateIn <> InputSplitter.io.Out.enable

  conv_bb.io.predicateIn.noenq()
  LoadA.io.GepAddr.noenq()
  LoadB.io.GepAddr.noenq()


  when(io.vcr.launch) {
    vcr_load_a_ptr := io.vcr.ptrs(0)
    vcr_load_b_ptr := io.vcr.ptrs(1)
    vcr_len := io.vcr.vals(0)
  }


  LoadA.io.enable <> conv_bb.io.Out(0)
  LoadB.io.enable <> conv_bb.io.Out(1)
  StoreType.io.enable <> conv_bb.io.Out(2)
  dotNode.io.enable <> conv_bb.io.Out(3)
  reduceNode.io.enable <> conv_bb.io.Out(4)

  //  LoadA.io.GepAddr <> InputSplitter.io.Out.data.elements("field0")(0)
  //  LoadB.io.GepAddr <> InputSplitter.io.Out.data.elements("field1")(0)

  dotNode.io.LeftIO <> LoadA.io.Out(0)
  dotNode.io.RightIO <> LoadB.io.Out(0)

  reduceNode.io.LeftIO <> dotNode.io.Out(0)*/
}
//
//  /* ================================================================== *
//   *                          Enable signals                            *
//   * ================================================================== */
//
//  //  val fetch = Module(new Fetch)
//  //  val load = Module(new Load)
//  //  val compute = Module(new Compute)
//  //  val store = Module(new Store)
//  //  val ecounters = Module(new EventCounters)
//  //
//  //  // Read(rd) and write(wr) from/to memory (i.e. DRAM)
//  //  io.vme.rd(0) <> fetch.io.vme_rd
//  //  io.vme.rd(1) <> compute.io.vme_rd(0)
//  //  io.vme.rd(2) <> load.io.vme_rd(0)
//  //  io.vme.rd(3) <> load.io.vme_rd(1)
//  //  io.vme.rd(4) <> compute.io.vme_rd(1)
//  //  io.vme.wr(0) <> store.io.vme_wr
//  //
//  //  // Fetch instructions (tasks) from memory (DRAM) into queues (SRAMsConfig)
//  //  fetch.io.launch := io.vcr.launch
//  //  fetch.io.ins_baddr := io.vcr.ptrs(0)
//  //  fetch.io.ins_count := io.vcr.vals(0)
//  //
//  //  // Load inputs and weights from memory (DRAM) into scratchpads (SRAMs)
//  //  load.io.i_post := compute.io.o_post(0)
//  //  load.io.inst <> fetch.io.inst.ld
//  //  load.io.inp_baddr := io.vcr.ptrs(2)
//  //  load.io.wgt_baddr := io.vcr.ptrs(3)
//  //
//  //  // The compute module performs the following:
//  //  // - Load micro-ops (uops) and accumulations (acc)
//  //  // - Compute dense and ALU instructions (tasks)
//  //  compute.io.i_post(0) := load.io.o_post
//  //  compute.io.i_post(1) := store.io.o_post
//  //  compute.io.inst <> fetch.io.inst.co
//  //  compute.io.uop_baddr := io.vcr.ptrs(1)
//  //  compute.io.acc_baddr := io.vcr.ptrs(4)
//  //  compute.io.inp <> load.io.inp
//  //  compute.io.wgt <> load.io.wgt
//  //
//  //  // The store module performs the following:
//  //  // - Writes results from compute into scratchpads (SRAMs)
//  //  // - Store results from scratchpads (SRAMs) to memory (DRAM)
//  //  store.io.i_post := compute.io.o_post(1)
//  //  store.io.inst <> fetch.io.inst.st
//  //  store.io.out_baddr := io.vcr.ptrs(5)
//  //  store.io.out <> compute.io.out
//  //
//  //  // Event counters
//  //  ecounters.io.launch := io.vcr.launch
//  //  ecounters.io.finish := compute.io.finish
//  //  io.vcr.ecnt <> ecounters.io.ecn[error] 	at memory.WriteTypMemoryController.$anonfun$new$13(WriteTypMemoryController.scala:213)
//  //
//  //  // Finish instruction is executed and asserts the VCR finish flag
//  //  val finish = RegNext(compute.io.finish)
//  //  io.vcr.finish := finish
//}
//
//
///** VTA.
//  *
//  * This file contains all the configurations supported by VTA.
//  * These configurations are built in a mix/match form based on core
//  * and shell configurations.
//  */
//
//class DefaultPynqConfig extends Config(new CoreConfig ++ new De10Config)
//
//object DefaultPynqConfig extends App {
//  implicit val p: Parameters = new DefaultPynqConfig
//  chisel3.Driver.execute(args, () => new XilinxShell)
//}
