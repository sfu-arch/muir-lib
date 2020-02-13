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


abstract class DebugVME03IO(implicit val p: Parameters) extends Module with HasAccelParams with HasAccelShellParams {
  val io = IO(new Bundle {
    val addrDebug = Input(UInt(memParams.addrBits.W))
    /**
      * Mem Interface to talk with VME
      */
    val vmeOut = Output(new VMEWriteMaster)

    val Enable = Input(Bool())
  })
}

class DebugVME03DF(implicit p: Parameters) extends DebugVME03IO()(p)  {


  /* ================================================================== *
   *                   PRINTING MEMORY MODULES                          *
   * ================================================================== */


//
//  val MemCtrl = Module(new UnifiedController(ID = 0, Size = 32, NReads = 0, NWrites = 4)
//  (WControl = new WriteMemoryController(NumOps = 4, BaseSize = 2, NumEntries = 2))
//  (RControl = new ReadMemoryController(NumOps = 0, BaseSize = 2, NumEntries = 2))
//  (RWArbiter = new ReadWriteArbiter()))
//  io.MemReq <> MemCtrl.io.MemReq
//  MemCtrl.io.MemResp <> io.MemResp

  //ID from 1, RoutID from 0, Bore_ID same as the ID of the node_to_be_logged and node_cnt for memory spaces from 0
  val buf_0 = Module(new DebugVMEBufferNode(ID = 1, Bore_ID = 2))
  buf_0.io.Enable := io.Enable
  buf_0.io.addrDebug := io.addrDebug
  io.vmeOut := buf_0.io.vmeOut
  
//  val buf_1 = Module(new DebugVMEBufferNode(ID = 2, Bore_ID = 4))
//  buf_1.io.Enable := io.Enable
//  val buf_2 = Module(new DebugVMEBufferNode(ID = 3, Bore_ID = 5))
//  buf_2.io.Enable := io.Enable
//  val buf_3 = Module(new DebugVMEBufferNode(ID = 4, Bore_ID = 0))
//  buf_3.io.Enable := io.Enable


  // Memory writes to connect
//  //-----------------------------------p
//  MemCtrl.io.WriteIn(0) <> buf_0.io.memReq
//  buf_0.io.memResp <> MemCtrl.io.WriteOut(0)
//
//  MemCtrl.io.WriteIn(1) <> buf_1.io.memReq
//  buf_1.io.memResp <> MemCtrl.io.WriteOut(1)
//
//  MemCtrl.io.WriteIn(2) <> buf_2.io.memReq
//  buf_2.io.memResp <> MemCtrl.io.WriteOut(2)
//
//  MemCtrl.io.WriteIn(3) <> buf_3.io.memReq
//  buf_3.io.memResp <> MemCtrl.io.WriteOut(3)
  //----------------------------------------------v


}
