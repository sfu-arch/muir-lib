package verilogmain

//            liveIn_R(i).predicate := io.latchEnable.bits.control
//liveIn_R(i).predicate := io.latchEnable.bits.control
import java.io.{File, FileWriter}

import dandelion.node._
import dandelion.config._
import dandelion.interfaces._
import dandelion.arbiters._
import memory._
import dandelion.dataflow._
import dandelion.config._
import util._
import dandelion.interfaces._


object Main extends App {
  val dir = new File(args(0)) ; dir.mkdirs
  implicit val p = Parameters.root((new MiniConfig).toInstance)
  val chirrtl = firrtl.Parser.parse(chisel3.Driver.emit(() => new TypeMemDataFlow()))

  val verilog = new FileWriter(new File(dir, s"${chirrtl.main}.v"))
  val compileResult = (new firrtl.VerilogCompiler).compileAndEmit(firrtl.CircuitState(chirrtl, firrtl.ChirrtlForm))
  val compiledStuff = compileResult.getEmittedCircuit
  verilog.write(compiledStuff.value)
  verilog.close
}
