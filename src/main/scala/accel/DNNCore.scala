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
import dnn.memory.VME_Load
import dnn.{DotNode, ReduceNode}
import interfaces.{ControlBundle, DataBundle}
import junctions.SplitCallNew
import memory.{ReadTypMemoryController, WriteTypMemoryController}
import node.{FXmatNxN, TypLoad, TypStore}
import shell._

///** Core parameters */
//case class DNNCoreParams(
//                          batch: Int = 1,
//                          blockOut: Int = 16,
//                          blockIn: Int = 16,
//                          inpBits: Int = 8,
//                          wgtBits: Int = 8,
//                          uopBits: Int = 32,
//                          accBits: Int = 32,
//                          outBits: Int = 8,
//                          uopMemDepth: Int = 512,
//                          inpMemDepth: Int = 512,
//                          wgtMemDepth: Int = 512,
//                          accMemDepth: Int = 512,
//                          outMemDepth: Int = 512,
//                          instQueueEntries: Int = 32
//                        ) {
//  require(uopBits % 8 == 0, s"\n\n[VTA] [CoreParams] uopBits must be byte aligned\n\n")
//}
//
//case object CoreKey extends Field[DNNCoreParams]
//
//
//class CoreConfig extends Config((site, here, up) => {
//  case CoreKey => DNNCoreParams(
//    batch = 1,
//    blockOut = 16,
//    blockIn = 16,
//    inpBits = 8,
//    wgtBits = 8,
//    uopBits = 32,
//    accBits = 32,
//    outBits = 8,
//    uopMemDepth = 2048,
//    inpMemDepth = 2048,
//    wgtMemDepth = 1024,
//    accMemDepth = 2048,
//    outMemDepth = 2048,
//    instQueueEntries = 512)
//})

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
class DNNCore(implicit p: Parameters) extends Module {
  val io = IO(new Bundle {
    val vcr = new VCRClient
    val vme = new VMEMaster
  })

  val buffer = Module(new Queue(io.vme.rd(0).data.bits.cloneType,40))

  val sIdle :: sReq :: sBusy :: Nil = Enum(3)
  val Rstate = RegInit(sIdle)
  val Wstate = RegInit(sIdle)

  val cycle_count = new Counter(200)

  when (Rstate =/= sIdle) {
    cycle_count.inc( )
  }


  io.vcr.ecnt(0.U).bits := cycle_count.value

  // Read state machine
  switch (Rstate) {
    is (sIdle) {
      when (io.vcr.launch) {
        cycle_count.value := 0.U
        Rstate := sReq
      }
    }
    is (sReq) {
      when (io.vme.rd(0).cmd.fire()) {
        Rstate := sBusy
      }
    }
  }
  // Write state machine
  switch (Wstate) {
    is (sIdle) {
      when (io.vcr.launch) {
        Wstate := sReq
      }
    }
    is (sReq) {
      when (io.vme.wr(0).cmd.fire()) {
        Wstate := sBusy
      }
    }
  }

  io.vme.rd(0).cmd.bits.addr := io.vcr.ptrs(0)
  io.vme.rd(0).cmd.bits.len := io.vcr.vals(1)
  io.vme.rd(0).cmd.valid := false.B

  io.vme.wr(0).cmd.bits.addr := io.vcr.ptrs(2)
  io.vme.wr(0).cmd.bits.len := io.vcr.vals(1)
  io.vme.wr(0).cmd.valid := false.B

  when(Rstate === sReq) {
    io.vme.rd(0).cmd.valid := true.B
  }

  when(Wstate === sReq) {
    io.vme.wr(0).cmd.valid := true.B
  }

  // Final
  val last = Wstate === sBusy && io.vme.wr(0).ack
  io.vcr.finish := last
  io.vcr.ecnt(0).valid := last

  when(io.vme.wr(0).ack) {
    Rstate := sIdle
    Wstate := sIdle
  }


  buffer.io.enq <> io.vme.rd(0).data
  buffer.io.enq.bits := io.vme.rd(0).data.bits + io.vcr.vals(0)
  io.vme.wr(0).data <> buffer.io.deq

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

  val VME_LoadA = Module(new VME_Load(debug = false))
  val VME_LoadB = Module(new VME_Load(debug = false))

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
