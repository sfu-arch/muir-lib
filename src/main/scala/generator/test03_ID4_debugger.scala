//package dandelion.generator
//
//import dandelion.fpu._
//import dandelion.accel._
//import dandelion.arbiters._
//import chisel3._
//import chisel3.util._
//import chisel3.Module._
//import chisel3.testers._
//import chisel3.iotesters._
//import chisel3.util.experimental.BoringUtils
//import dandelion.config._
//import dandelion.control._
//import dandelion.interfaces._
//import dandelion.junctions._
//import dandelion.loop._
//import dandelion.memory._
//import muxes._
//import dandelion.node._==================== */
//
//abstract class Debugger03DFIO(implicit val p: Parameters) extends Module with CoreParams {
//  val io = IO(new Bundle {
//    val in = Flipped(Decoupled(new Call(List(32))))
//    val MemResp = Flipped(Valid(new MemResp))
//    val MemReq = Decoupled(new MemReq)
//    val out = Decoupled(new Call(List(32)))
//  })
//}
//
//class Debugger03DF(implicit p: Parameters) extends test03DFIO()(p) {
//
//
//  /* ================================================================== *
//   *                   PRINTING MEMORY MODULES                          *
//   * ================================================================== */
//
//
//
//  //-----------------------------------------------------------------------p
//  val MemCtrl = Module(new UnifiedController(ID = 0, Size = 32, NReads = 0, NWrites = 1)
//  //NumOps = 1 to NumOps = 2
//  (WControl = new WriteMemoryController(NumOps = 1, BaseSize = 2, NumEntries = 2))
//  (RControl = new ReadMemoryController(NumOps = 0, BaseSize = 2, NumEntries = 2))
//  (RWArbiter = new ReadWriteArbiter()))
//  io.MemReq <> MemCtrl.io.MemReq
//  MemCtrl.io.MemResp <> io.MemResp
//  //---------------------------------------------------------------------------v
//
//
//  val InputSplitter = Module(new SplitCallNew(List(1)))
//  InputSplitter.io.In <> io.in
//
//  /* ================================================================== *
//   *                   PRINTING LOOP HEADERS                            *
//   * ================================================================== */
//
//
//  /* ================================================================== *
//   *                   PRINTING BASICBLOCK NODES                        *
//   * ================================================================== */
//  val bb_0 = Module(new BasicBlockNoMaskFastNode(NumInputs = 1, NumOuts = 1, BID = 0))
//
//  /* ================================================================== *
//   *                   PRINTING INSTRUCTION NODES                       *
//   * ================================================================== */
//
//
//  //new
//  val buf_0 = Module(new DebugBufferNode(NumPredOps = 0, NumSuccOps = 0, ID = 8, RouteID = 0))
//
//  //new
//  //------------------------------------------------------------------------------------v
//
//  /* ================================================================== *
//   *                   PRINTING CONSTANTS NODES                         *
//   * ================================================================== */
//
//
//
//  /* ================================================================== *
//   *                   BASICBLOCK -> PREDICATE INSTRUCTION              *
//   * ================================================================== */
//
//  bb_0.io.predicateIn(0) <> InputSplitter.io.Out.enable
//
//
//
//  /* ================================================================== *
//   *                   BASICBLOCK -> ENABLE INSTRUCTION                 *
//   * ================================================================== */
//
//
//  //new
//  buf_0.io.enable.bits := ControlBundle.active()
//  buf_0.io.enable.valid := true.B
//  buf_0.io.Out(0).ready := true.B
//
//
//
//
//  /* ================================================================== *
//   *                   CONNECTING MEMORY CONNECTIONS                    *
//   * ================================================================== */
//
//  MemCtrl.io.WriteIn(1) <> buf_0.io.memReq
//  buf_0.io.memResp <> MemCtrl.io.WriteOut(1)
//  //
//
//
//  /* ================================================================== *
//   *                   PRINTING OUTPUT INTERFACE                        *
//   * ================================================================== */
//
//  io.out <> DontCare
//
//}
//
//import java.io.{File, FileWriter}
//
//object test03Top extends App {
//  val dir = new File("RTL/test03Top");
//  dir.mkdirs
//  implicit val p = Parameters.root((new MiniConfig).toInstance)
//  val chirrtl = firrtl.Parser.parse(chisel3.Driver.emit(() => new test03DF()))
//
//  val verilogFile = new File(dir, s"${chirrtl.main}.v")
//  val verilogWriter = new FileWriter(verilogFile)
//  val compileResult = (new firrtl.VerilogCompiler).compileAndEmit(firrtl.CircuitState(chirrtl, firrtl.ChirrtlForm))
//  val compiledStuff = compileResult.getEmittedCircuit
//  verilogWriter.write(compiledStuff.value)
//  verilogWriter.close()
//}