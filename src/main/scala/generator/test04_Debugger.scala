package dandelion.generator

import chisel3._
import dandelion.config._
import chipsalliance.rocketchip.config._
import dandelion.control._
import dandelion.interfaces._
import dandelion.junctions._
import dandelion.memory._
import dandelion.node._
import dandelion.shell.VMEWriteMaster
import util._


/* ================================================================== *
 *                   PRINTING PORTS DEFINITION                        *
 * ================================================================== */


abstract class DebugVME04IO(implicit val p: Parameters) extends Module with HasAccelParams with HasAccelShellParams {
  val io = IO(new Bundle {
    val addrDebug = Input(UInt(memParams.addrBits.W))
    /**
     * Mem Interface to talk with VME
     */
    val vmeOut = new VMEWriteMaster

    val Enable = Input(Bool())
  })
}

class DebugVME04DF(implicit p: Parameters) extends DebugVME04IO()(p)  {


  /* ================================================================== *
   *                   PRINTING MEMORY MODULES                          *
   * ================================================================== */




  //ID from 1, RoutID from 0, Bore_ID same as the ID of the node_to_be_logged and node_cnt for memory spaces from 0
  val buf_0 = Module(new DebugVMEBufferNode(ID = 1, Bore_ID = 4))
  buf_0.io.Enable := io.Enable
  buf_0.io.addrDebug := io.addrDebug
  io.vmeOut.data.bits := buf_0.io.vmeOut.data.bits
  io.vmeOut.data.valid := buf_0.io.vmeOut.data.valid
  buf_0.io.vmeOut.data.ready := io.vmeOut.data.ready


  io.vmeOut.cmd.bits := buf_0.io.vmeOut.cmd.bits
  io.vmeOut.cmd.valid:= buf_0.io.vmeOut.cmd.valid
  buf_0.io.vmeOut.cmd.ready := io.vmeOut.cmd.ready

  buf_0.io.vmeOut.ack := io.vmeOut.ack



}
