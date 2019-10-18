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


abstract class Debug03IO(implicit val p: Parameters) extends Module with CoreParams {
  val io = IO(new Bundle {
    val in = Flipped(Decoupled(new Call(List())))
    val finish = Flipped(Decoupled(new Call(List())))
    val MemResp = Flipped(Valid(new MemResp))
    val MemReq = Decoupled(new MemReq)
//    val out = Decoupled(new Call(List()))
  })
}

class Debug03DF(implicit p: Parameters) extends Debug03IO()(p) {


  /* ================================================================== *
   *                   PRINTING MEMORY MODULES                          *
   * ================================================================== */



  val MemCtrl = Module(new UnifiedController(ID = 0, Size = 32, NReads = 0, NWrites = 1)
  //NumOps = 1 to NumOps = 2
  (WControl = new WriteMemoryController(NumOps = 1, BaseSize = 2, NumEntries = 2))
  (RControl = new ReadMemoryController(NumOps = 0, BaseSize = 2, NumEntries = 2))
  (RWArbiter = new ReadWriteArbiter()))
  io.MemReq <> MemCtrl.io.MemReq
  MemCtrl.io.MemResp <> io.MemResp


  //new
  val buf_0 = Module(new DebugBufferNode(ID = 8, RouteID = 1, Bore_ID = 2))


  /**
    * Debuging states for store node
    */

  /**
    * Debuging states for store node
    */

  val sIdle :: sActive :: Nil = Enum(2)
  val state = RegInit(sIdle)

  io.in.ready := (state === sIdle)
  io.finish.ready := (state === sActive)

  switch(state) {
    is(sIdle) {
      when(io.in.fire) {
        state := sActive
      }
    }
    is(sActive) {
      when(io.finish.fire) {
        state := sIdle
      }
    }

  }

  buf_0.io.Enable := state === sActive




  //-----------------------------------p
  MemCtrl.io.WriteIn(0) <> buf_0.io.memReq
  buf_0.io.memResp <> MemCtrl.io.WriteOut(0)
  //----------------------------------------------v


}

import java.io.{File, FileWriter}

object Debug03Top extends App {
  val dir = new File("RTL/Debug03Top");
  dir.mkdirs
  implicit val p = Parameters.root((new MiniConfig).toInstance)
  val chirrtl = firrtl.Parser.parse(chisel3.Driver.emit(() => new Debug03DF()))

  val verilogFile = new File(dir, s"${chirrtl.main}.v")
  val verilogWriter = new FileWriter(verilogFile)
  val compileResult = (new firrtl.VerilogCompiler).compileAndEmit(firrtl.CircuitState(chirrtl, firrtl.ChirrtlForm))
  val compiledStuff = compileResult.getEmittedCircuit
  verilogWriter.write(compiledStuff.value)
  verilogWriter.close()
}



