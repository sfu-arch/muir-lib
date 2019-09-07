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
import chisel3.util._
import chisel3.{Module, RegInit, when, _}
import config._
import control.BasicBlockNoMaskNode
import dnn.memory.VME_Load
import dnn.wrappers.SystolicSquareWrapper
import dnn.{DotNode, ReduceNode}
import interfaces.{ControlBundle, DataBundle}
import junctions.SplitCallNew
import memory.{ReadTypMemoryController, WriteTypMemoryController}
import node.{FXmatNxN, TypLoad, TypStore}
import shell._


/** Shell parameters. */
//case class ShellParams(
//                        hostParams: AXIParams,
//                        memParams: AXIParams,
//                        vcrParams: VCRParams,
//                        vmeParams: VMEParams
//
//                      )
//
//
//case object ShellKey extends Field[ShellParams]

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
class DNNCoreTest(implicit p: Parameters) extends Module {
  val io = IO(new Bundle {
    val host = new AXILiteClient(p(ShellKey).hostParams)
    val mem = new AXIMaster(p(ShellKey).memParams)
  })

  val vcr = Module(new VCR)
  val vmem = Module(new VME)
  val buffer = Module(new Queue(vmem.io.vme.rd(0).data.bits.cloneType,40))

  val sIdle :: sReq :: sBusy :: Nil = Enum(3)
  val Rstate = RegInit(sIdle)
  val Wstate = RegInit(sIdle)

  val cycle_count = new Counter(200)

  when (Rstate =/= sIdle) {
    cycle_count.inc( )
  }


  vcr.io.vcr.ecnt(0.U).bits := cycle_count.value

  // Read state machine

  switch (Rstate) {
    is (sIdle) {
      when (vcr.io.vcr.launch) {
        cycle_count.value := 0.U
        Rstate := sReq
      }
    }
    is (sReq) {
      when (vmem.io.vme.rd(0).cmd.fire()) {
        Rstate := sBusy
      }
    }
  }
//   Write state machine
  switch (Wstate) {
    is (sIdle) {
      when (vcr.io.vcr.launch) {
        Wstate := sReq
      }
    }
    is (sReq) {
      when (vmem.io.vme.wr(0).cmd.fire()) {
        Wstate := sBusy
      }
    }
  }

  vmem.io.vme.rd(0).cmd.bits.addr := vcr.io.vcr.ptrs(0)
  vmem.io.vme.rd(0).cmd.bits.len := vcr.io.vcr.vals(1)
  vmem.io.vme.rd(0).cmd.valid := false.B

  vmem.io.vme.wr(0).cmd.bits.addr := vcr.io.vcr.ptrs(2)
  vmem.io.vme.wr(0).cmd.bits.len := vcr.io.vcr.vals(1)
  vmem.io.vme.wr(0).cmd.valid := false.B

  when(Rstate === sReq) {
    vmem.io.vme.rd(0).cmd.valid := true.B
  }

  when(Wstate === sReq) {
    vmem.io.vme.wr(0).cmd.valid := true.B
  }

  // Final
  val last = Wstate === sBusy && vmem.io.vme.wr(0).ack
  vcr.io.vcr.finish := last
  vcr.io.vcr.ecnt(0).valid := last

  when(vmem.io.vme.wr(0).ack) {
    Rstate := sIdle
    Wstate := sIdle
  }


  buffer.io.enq <> vmem.io.vme.rd(0).data
  buffer.io.enq.bits := vmem.io.vme.rd(0).data.bits + vcr.io.vcr.vals(0)
  vmem.io.vme.wr(0).data <> buffer.io.deq

  io.mem <> vmem.io.mem
  vcr.io.host <> io.host



  /* ================================================================== *
   *                          Enable signals                            *
   * ================================================================== */

  //  val fetch = Module(new Fetch)
  //  val load = Module(new Load)
  //  val compute = Module(new Compute)
  //  val store = Module(new Store)
  //  val ecounters = Module(new EventCounters)
  //
  //  // Read(rd) and write(wr) from/to memory (i.e. DRAM)
  //  io.vme.rd(0) <> fetch.io.vme_rd
  //  io.vme.rd(1) <> compute.io.vme_rd(0)
  //  io.vme.rd(2) <> load.io.vme_rd(0)
  //  io.vme.rd(3) <> load.io.vme_rd(1)
  //  io.vme.rd(4) <> compute.io.vme_rd(1)
  //  io.vme.wr(0) <> store.io.vme_wr
  //
  //  // Fetch instructions (tasks) from memory (DRAM) into queues (SRAMs)
  //  fetch.io.launch := io.vcr.launch
  //  fetch.io.ins_baddr := io.vcr.ptrs(0)
  //  fetch.io.ins_count := io.vcr.vals(0)
  //
  //  // Load inputs and weights from memory (DRAM) into scratchpads (SRAMs)
  //  load.io.i_post := compute.io.o_post(0)
  //  load.io.inst <> fetch.io.inst.ld
  //  load.io.inp_baddr := io.vcr.ptrs(2)
  //  load.io.wgt_baddr := io.vcr.ptrs(3)
  //
  //  // The compute module performs the following:
  //  // - Load micro-ops (uops) and accumulations (acc)
  //  // - Compute dense and ALU instructions (tasks)
  //  compute.io.i_post(0) := load.io.o_post
  //  compute.io.i_post(1) := store.io.o_post
  //  compute.io.inst <> fetch.io.inst.co
  //  compute.io.uop_baddr := io.vcr.ptrs(1)
  //  compute.io.acc_baddr := io.vcr.ptrs(4)
  //  compute.io.inp <> load.io.inp
  //  compute.io.wgt <> load.io.wgt
  //
  //  // The store module performs the following:
  //  // - Writes results from compute into scratchpads (SRAMs)
  //  // - Store results from scratchpads (SRAMs) to memory (DRAM)
  //  store.io.i_post := compute.io.o_post(1)
  //  store.io.inst <> fetch.io.inst.st
  //  store.io.out_baddr := io.vcr.ptrs(5)
  //  store.io.out <> compute.io.out
  //
  //  // Event counterShellKeys
  //  ecounters.io.launch := io.vcr.launch
  //  ecounters.io.finish := compute.io.finish
  //  io.vcr.ecnt <> ecounters.io.ecnt
  //
  //  // Finish instruction is executed and asserts the VCR finish flag
  //  val finish = RegNext(compute.io.finish)
  //  io.vcr.finish := finish
}

//import java.io.{File, FileWriter}
//
//object DNNCoreTestMain extends App {
//  val dir = new File("RTL/DNNCoreTest");
//  dir.mkdirs
//  implicit val p = config.Parameters.root((new MiniConfig).toInstance)
//  val chirrtl = firrtl.Parser.parse(chisel3.Driver.emit(() => new DNNCoreTest()))
//
//  //  () => new SystolicSquareWrapper(UInt(p(XLEN).W), 3)
//
//  val verilogFile   = new File(dir, s"${chirrtl.main}.v")
//  val verilogWriter = new FileWriter(verilogFile)
//  val compileResult = (new firrtl.VerilogCompiler).compileAndEmit(firrtl.CircuitState(chirrtl, firrtl.ChirrtlForm))
//  val compiledStuff = compileResult.getEmittedCircuit
//  verilogWriter.write(compiledStuff.value)
//  verilogWriter.close( )
//}

