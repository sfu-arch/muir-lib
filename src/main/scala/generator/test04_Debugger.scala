package dandelion.generator

import chisel3._
import dandelion.config._
import dandelion.control._
import dandelion.interfaces._
import dandelion.junctions._
import dandelion.memory._
import dandelion.node._
import util._


/* ================================================================== *
 *                   PRINTING PORTS DEFINITION                        *
 * ================================================================== */


abstract class Debug04IO(implicit val p: Parameters) extends Module with HasAccelParams {
  val io = IO(new Bundle {
    val Enable = Input(Bool())
    val MemResp = Flipped(Valid(new MemResp))
    val MemReq = Decoupled(new MemReq)
//    val out = Decoupled(new Call(List()))
  })
}

class Debug04DF(implicit p: Parameters) extends Debug04IO()(p) {


  /* ================================================================== *
   *                   PRINTING MEMORY MODULES                          *
   * ================================================================== */



  val MemCtrl = Module(new UnifiedController(ID = 0, Size = 32, NReads = 0, NWrites = 1)
  //Num of Writes and NumOps of Write should be the same as number of buffer nodes
  (WControl = new WriteMemoryController(NumOps = 1, BaseSize = 2, NumEntries = 2))
  (RControl = new ReadMemoryController(NumOps = 0, BaseSize = 2, NumEntries = 2))
  (RWArbiter = new ReadWriteArbiter()))
  io.MemReq <> MemCtrl.io.MemReq
  MemCtrl.io.MemResp <> io.MemResp

  //ID from 1, RoutID from 0, Bore_ID same as the ID of the node_to_be_logged and node_cnt for memory spaces from 0
  val buf_0 = Module(new DebugBufferNode(ID = 1, RouteID = 0, Bore_ID = 0, node_cnt = 0.U))
  buf_0.io.Enable := io.Enable



  // Memory writes to connect
  //-----------------------------------p
  MemCtrl.io.WriteIn(0) <> buf_0.io.memReq
  buf_0.io.memResp <> MemCtrl.io.WriteOut(0)

  //----------------------------------------------v


}

import java.io.{File, FileWriter}

object Debug04Top extends App {
  val dir = new File("RTL/Debug03Top");
  dir.mkdirs
  implicit val p = new WithAccelConfig
  val chirrtl = firrtl.Parser.parse(chisel3.Driver.emit(() => new Debug03DF()))

  val verilogFile = new File(dir, s"${chirrtl.main}.v")
  val verilogWriter = new FileWriter(verilogFile)
  val compileResult = (new firrtl.VerilogCompiler).compileAndEmit(firrtl.CircuitState(chirrtl, firrtl.ChirrtlForm))
  val compiledStuff = compileResult.getEmittedCircuit
  verilogWriter.write(compiledStuff.value)
  verilogWriter.close()
}



