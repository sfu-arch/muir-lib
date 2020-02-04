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


abstract class Debug03IO(implicit val p: Parameters) extends Module with HasAccelParams {
  val io = IO(new Bundle {
    val Enable = Input(Bool())
    val MemResp = Flipped(Valid(new MemResp))
    val MemReq = Decoupled(new MemReq)
//    val out = Decoupled(new Call(List()))
  })
}

class Debug03DF(implicit p: Parameters) extends Debug03IO()(p) {


  /* ================================================================== *
   *                   PRINTING MEMORY MODULES                          *
   * ================================================================== */



  val MemCtrl = Module(new UnifiedController(ID = 0, Size = 32, NReads = 0, NWrites = 4)
  //Num of Writes and NumOps of Write should be the same as number of buffer nodes
  (WControl = new WriteMemoryController(NumOps = 4, BaseSize = 2, NumEntries = 2))
  (RControl = new ReadMemoryController(NumOps = 0, BaseSize = 2, NumEntries = 2))
  (RWArbiter = new ReadWriteArbiter()))
  io.MemReq <> MemCtrl.io.MemReq
  MemCtrl.io.MemResp <> io.MemResp

  //ID from 1, RoutID from 0, Bore_ID same as the ID of the node_to_be_logged and node_cnt for memory spaces from 0
  val buf_0 = Module(new DebugBufferNode(ID = 1, RouteID = 0, Bore_ID = 2, node_cnt = 0.U))
  buf_0.io.Enable := io.Enable
  val buf_1 = Module(new DebugBufferNode(ID = 2, RouteID = 1, Bore_ID = 4, node_cnt = 1.U))
  buf_1.io.Enable := io.Enable
  val buf_2 = Module(new DebugBufferNode(ID = 3, RouteID = 2, Bore_ID = 5, node_cnt = 2.U))
  buf_2.io.Enable := io.Enable
  val buf_3 = Module(new DebugBufferNode(ID = 4, RouteID = 3, Bore_ID = 0, node_cnt = 3.U))
  buf_3.io.Enable := io.Enable


  // Memory writes to connect
  //-----------------------------------p
  MemCtrl.io.WriteIn(0) <> buf_0.io.memReq
  buf_0.io.memResp <> MemCtrl.io.WriteOut(0)

  MemCtrl.io.WriteIn(1) <> buf_1.io.memReq
  buf_1.io.memResp <> MemCtrl.io.WriteOut(1)

  MemCtrl.io.WriteIn(2) <> buf_2.io.memReq
  buf_2.io.memResp <> MemCtrl.io.WriteOut(2)

  MemCtrl.io.WriteIn(3) <> buf_3.io.memReq
  buf_3.io.memResp <> MemCtrl.io.WriteOut(3)
  //----------------------------------------------v


}

import java.io.{File, FileWriter}

object Debug03Top extends App {
  val dir = new File("RTL/Debug03Top");
  dir.mkdirs
  //implicit val p = Parameters.root((new MiniConfig).toInstance)
  implicit val p = new WithAccelConfig
  val chirrtl = firrtl.Parser.parse(chisel3.Driver.emit(() => new Debug03DF()))

  val verilogFile = new File(dir, s"${chirrtl.main}.v")
  val verilogWriter = new FileWriter(verilogFile)
  val compileResult = (new firrtl.VerilogCompiler).compileAndEmit(firrtl.CircuitState(chirrtl, firrtl.ChirrtlForm))
  val compiledStuff = compileResult.getEmittedCircuit
  verilogWriter.write(compiledStuff.value)
  verilogWriter.close()
}



