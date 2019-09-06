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

package dnn.memory

import chisel3._
import chisel3.util._
import config._
import dnn._
import interfaces.{ControlBundle, DataBundle, TypBundle, WriteReq, WriteResp}
import node.TypStore
import shell._

/** Load.
  *
  * Load inputs and weights from memory (DRAM) into scratchpads (SRAMs).
  * This module instantiate the TensorLoad unit which is in charge of
  * loading 1D and 2D tensors to scratchpads, so it can be used by
  * other modules such as Compute.
  */
class VME_Load(debug: Boolean = false)(implicit p: Parameters) extends Module {
  val mp = p(ShellKey).memParams
  val io = IO(new Bundle {
    val nRd = p(ShellKey).vmeParams.nReadClients
    val nWr = p(ShellKey).vmeParams.nWriteClients
    val start = Input(Bool())
    val done = Output(Bool())
    val vme_cmd = Flipped(Decoupled(new VMECmd()))
    val vme_read = new VMEReadMaster()
    val base_addr = Input(new DataBundle())
    val memReq = Decoupled(new WriteReq())
    val memResp = Input(Flipped(new WriteResp()))

  })

  val inDataCounter = Counter(math.pow(2, io.vme_cmd.bits.lenBits).toInt)

  val StoreType = Module(new TypStore(NumPredOps = 0, NumSuccOps = 0, NumOuts = 1, ID = 0, RouteID = 0))

  StoreType.io.enable.bits := ControlBundle.active()
  StoreType.io.enable.valid := true.B
  StoreType.io.Out(0).ready := true.B

  io.memReq <> StoreType.io.memReq
  StoreType.io.memResp <> io.memResp

  val vme_data_queue = Queue(io.vme_read.data, 50)

  io.vme_read.cmd <> io.vme_cmd

  val sIdle :: sReadData :: sGepAddr :: Nil = Enum(3)
  val state = RegInit(sIdle)

  io.done := false.B
  switch(state) {
    is(sIdle) {
      when(io.start && io.vme_cmd.fire()) {
        state := sGepAddr
      }
    }
    is(sGepAddr) {
      when(StoreType.io.GepAddr.ready && StoreType.io.inData.ready && vme_data_queue.valid) {
        StoreType.io.inData.enq(vme_data_queue.deq())
        StoreType.io.GepAddr.enq(io.base_addr + inDataCounter.value)
        state := sReadData
      }
    }
    is(sReadData) {
      when(StoreType.io.Out(0).fire) {
        inDataCounter.inc()
        state := sGepAddr
      }.elsewhen(inDataCounter.value === io.vme_cmd.bits.len) {
        state := sIdle
        io.done := true.B
      }
    }
  }

  // debug
  if (debug) {
    // start
    when(state === sIdle && io.start) {
      printf("[VME_Load] start\n")
    }
    // done
    when(state === sReadData) {
      when(io.done) {
        printf("[Load] Reading data\n")
      }.otherwise {
        printf("[VME_Load] Read is done\n")
      }
    }
  }
}
