package dandelion.generator
 import chisel3._
  import dandelion.config._
 import dandelion.interfaces._ 
 import dandelion.memory._
 import dandelion.node._
 import util._

abstract class Debug03IO(implicit val p: Parameters) extends Module with CoreParams {
	val io = IO(new Bundle {
		val Enable = Input(Bool())
		val MemResp = Flipped(Valid(new MemResp))
		val MemReq = Decoupled(new MemReq)
	})
}

class Debug03DF(implicit p: Parameters) extends Debug03IO()(p) {
	val MemCtrl = Module(new UnifiedController(ID = 0, Size = 32, NReads = 0, NWrites =2)
	(WControl = new WriteMemoryController(NumOps =2, BaseSize = 2, NumEntries = 2))
	(RControl = new ReadMemoryController(NumOps = 0, BaseSize = 2, NumEntries = 2))
	(RWArbiter = new ReadWriteArbiter()))
	io.MemReq <> MemCtrl.io.MemReq
	MemCtrl.io.MemResp <> io.MemResp


    val buf_0 = Module(new DebugBufferNode(ID = 1, RouteID = 0, Bore_ID = 2, node_cnt = 0.U))
    buf_0.io.Enable := io.Enable
    MemCtrl.io.WriteIn(0) <> buf_0.io.memReq
    buf_0.io.memResp <> MemCtrl.io.WriteOut(0)

    val buf_1 = Module(new DebugBufferNode(ID = 2, RouteID = 1, Bore_ID = 4, node_cnt = 1.U))
    buf_1.io.Enable := io.Enable
    MemCtrl.io.WriteIn(1) <> buf_1.io.memReq
    buf_1.io.memResp <> MemCtrl.io.WriteOut(1)

}

import java.io.{File, FileWriter}

object Debug03Top extends App {
	val dir = new File("RTL/Debug03Top");
	dir.mkdirs
	implicit val p = Parameters.root((new MiniConfig).toInstance)
	val chirrtl = firrtl.Parser.parse(chisel3.Driver.emit(() => new Debug03DF()))
	val verilogFile = new File(dir, s"$\{chirrtl.main\}.v")
	val verilogWriter = new FileWriter(verilogFile)
	val compileResult = (new firrtl.VerilogCompiler).compileAndEmit(firrtl.CircuitState(chirrtl, firrtl.ChirrtlForm))
	val compiledStuff = compileResult.getEmittedCircuit
	verilogWriter.write(compiledStuff.value)
	verilogWriter.close()
}