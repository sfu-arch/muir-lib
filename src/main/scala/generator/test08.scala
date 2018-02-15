package dataflow

import chisel3._
import chisel3.util._
import chisel3.Module
import chisel3.testers._
import chisel3.iotesters._
import org.scalatest.{FlatSpec, Matchers}
import muxes._
import config._
import control._
import util._
import interfaces._
import regfile._
import memory._
import stack._
import arbiters._
import loop._
import accel._
import node._
import junctions._


/**
  * This Object should be initialized at the first step
  * It contains all the transformation from indices to their module's name
  */

object Data_test08_FlowParam{

  val bb_entry_pred = Map(
    "active" -> 0
  )


  val bb_for_cond_pred = Map(
    "br0" -> 0,
    "br8" -> 1
  )


  val bb_for_inc_pred = Map(
    "br6" -> 0
  )


  val bb_for_body_pred = Map(
    "br4" -> 0
  )


  val bb_for_end_pred = Map(
    "br4" -> 0
  )


  val br0_brn_bb = Map(
    "bb_for_cond" -> 0
  )


  val br4_brn_bb = Map(
    "bb_for_body" -> 0,
    "bb_for_end" -> 1
  )


  val br6_brn_bb = Map(
    "bb_for_inc" -> 0
  )


  val br8_brn_bb = Map(
    "bb_for_cond" -> 0
  )


  val bb_entry_activate = Map(
    "br0" -> 0
  )


  val bb_for_cond_activate = Map(
    "phi1" -> 0,
    "phi2" -> 1,
    "icmp3" -> 2,
    "br4" -> 3
  )


  val bb_for_body_activate = Map(
    "add5" -> 0,
    "br6" -> 1
  )


  val bb_for_inc_activate = Map(
    "add7" -> 0,
    "br8" -> 1
  )


  val bb_for_end_activate = Map(
    "ret9" -> 0
  )


  val phi1_phi_in = Map(
    "field0" -> 0,
    "add5" -> 1
  )


  val phi2_phi_in = Map(
    "const_0" -> 0,
    "add7" -> 1
  )


  //  %foo.0 = phi i32 [ %j, %entry ], [ %inc, %for.inc ], !UID !10, !ScalaLabel !11
  val phi1_in = Map(
    "field0" -> 0,
    "add5" -> 0
  )


  //  %i.0 = phi i32 [ 0, %entry ], [ %inc1, %for.inc ], !UID !12, !ScalaLabel !13
  val phi2_in = Map(
    "add7" -> 0
  )


  //  %cmp = icmp ult i32 %i.0, 5, !UID !14, !ScalaLabel !15
  val icmp3_in = Map(
    "phi2" -> 0
  )


  //  br i1 %cmp, label %for.body, label %for.end, !UID !16, !BB_UID !17, !ScalaLabel !18
  val br4_in = Map(
    "icmp3" -> 0
  )


  //  %inc = add i32 %foo.0, 1, !UID !19, !ScalaLabel !20
  val add5_in = Map(
    "phi1" -> 0
  )


  //  %inc1 = add i32 %i.0, 1, !UID !24, !ScalaLabel !25
  val add7_in = Map(
    "phi2" -> 1
  )


  //  ret i32 %foo.0, !UID !37, !BB_UID !38, !ScalaLabel !39
  val ret9_in = Map(
    "phi1" -> 1
  )


}




  /* ================================================================== *
   *                   PRINTING PORTS DEFINITION                        *
   * ================================================================== */


abstract class test08DFIO(implicit val p: Parameters) extends Module with CoreParams {
  val io = IO(new Bundle {
    val in = Flipped(Decoupled(new Call(List(32))))
    val CacheResp = Flipped(Valid(new CacheRespT))
    val CacheReq = Decoupled(new CacheReq)
    val out = Decoupled(new Call(List(32)))
  })
}




  /* ================================================================== *
   *                   PRINTING MODULE DEFINITION                       *
   * ================================================================== */


class test08DF(implicit p: Parameters) extends test08DFIO()(p) {



  /* ================================================================== *
   *                   PRINTING MEMORY SYSTEM                           *
   * ================================================================== */


	val StackPointer = Module(new Stack(NumOps = 1))

	val RegisterFile = Module(new TypeStackFile(ID=0,Size=32,NReads=2,NWrites=2)
		            (WControl=new WriteMemoryController(NumOps=2,BaseSize=2,NumEntries=2))
		            (RControl=new ReadMemoryController(NumOps=2,BaseSize=2,NumEntries=2)))

	val CacheMem = Module(new UnifiedController(ID=0,Size=32,NReads=2,NWrites=2)
		            (WControl=new WriteMemoryController(NumOps=2,BaseSize=2,NumEntries=2))
		            (RControl=new ReadMemoryController(NumOps=2,BaseSize=2,NumEntries=2))
		            (RWArbiter=new ReadWriteArbiter()))

  io.CacheReq <> CacheMem.io.CacheReq
  CacheMem.io.CacheResp <> io.CacheResp

  val InputSplitter = Module(new SplitCall(List(32)))
  InputSplitter.io.In <> io.in



  /* ================================================================== *
   *                   PRINTING LOOP HEADERS                            *
   * ================================================================== */


  val loop_L_5_liveIN_0 = Module(new LiveInNode(NumOuts = 1, ID = 0))


  /* ================================================================== *
   *                   PRINTING BASICBLOCK NODES                        *
   * ================================================================== */


  //Initializing BasicBlocks: 

  val bb_entry = Module(new BasicBlockNoMaskNode(NumInputs = 1, NumOuts = 1, BID = 0)(p))

  val bb_for_cond = Module(new BasicBlockNode(NumInputs = 2, NumOuts = 6, NumPhi = 2, BID = 1)(p))

  val bb_for_body = Module(new BasicBlockNoMaskNode(NumInputs = 1, NumOuts = 2, BID = 2)(p))

  val bb_for_inc = Module(new BasicBlockNoMaskNode(NumInputs = 1, NumOuts = 2, BID = 3)(p))

  val bb_for_end = Module(new BasicBlockNoMaskNode(NumInputs = 1, NumOuts = 1, BID = 4)(p))






  /* ================================================================== *
   *                   PRINTING INSTRUCTION NODES                       *
   * ================================================================== */


  //Initializing Instructions: 

  // [BasicBlock]  entry:

  //  br label %for.cond, !UID !7, !BB_UID !8, !ScalaLabel !9
  val br0 = Module (new UBranchNode(ID = 0)(p))



  // [BasicBlock]  for.cond:

  //  %foo.0 = phi i32 [ %j, %entry ], [ %inc, %for.inc ], !UID !10, !ScalaLabel !11
  val phi1 = Module (new PhiNode(NumInputs = 2, NumOuts = 2, ID = 1)(p))


  //  %i.0 = phi i32 [ 0, %entry ], [ %inc1, %for.inc ], !UID !12, !ScalaLabel !13
  val phi2 = Module (new PhiNode(NumInputs = 2, NumOuts = 2, ID = 2)(p))


  //  %cmp = icmp ult i32 %i.0, 5, !UID !14, !ScalaLabel !15
  val icmp3 = Module (new IcmpNode(NumOuts = 1, ID = 3, opCode = "ULT")(sign=false)(p))


  //  br i1 %cmp, label %for.body, label %for.end, !UID !16, !BB_UID !17, !ScalaLabel !18
  val br4 = Module (new CBranchNode(ID = 4)(p))

  val bb_for_cond_expand = Module(new ExpandNode(NumOuts=2, ID=0))



  // [BasicBlock]  for.body:

  //  %inc = add i32 %foo.0, 1, !UID !19, !ScalaLabel !20
  val add5 = Module (new ComputeNode(NumOuts = 1, ID = 5, opCode = "add")(sign=false)(p))


  //  br label %for.inc, !UID !21, !BB_UID !22, !ScalaLabel !23
  val br6 = Module (new UBranchNode(ID = 6)(p))



  // [BasicBlock]  for.inc:

  //  %inc1 = add i32 %i.0, 1, !UID !24, !ScalaLabel !25
  val add7 = Module (new ComputeNode(NumOuts = 1, ID = 7, opCode = "add")(sign=false)(p))


  //  br label %for.cond, !llvm.loop !26, !UID !34, !BB_UID !35, !ScalaLabel !36
  val br8 = Module (new UBranchNode(ID = 8)(p))



  // [BasicBlock]  for.end:

  //  ret i32 %foo.0, !UID !37, !BB_UID !38, !ScalaLabel !39
  val ret9 = Module(new RetNode(NumPredIn=1, retTypes=List(32), ID=9))







  /* ================================================================== *
   *                   INITIALIZING PARAM                               *
   * ================================================================== */


  /**
    * Instantiating parameters
    */
  val param = Data_test08_FlowParam



  /* ================================================================== *
   *                   CONNECTING BASIC BLOCKS TO PREDICATE INSTRUCTIONS*
   * ================================================================== */


  /**
     * Connecting basic blocks to predicate instructions
     */


  bb_entry.io.predicateIn(0) <> InputSplitter.io.Out.enable

  /**
    * Connecting basic blocks to predicate instructions
    */

  //Connecting br0 to bb_for_cond
  bb_for_cond.io.predicateIn(param.bb_for_cond_pred("br0")) <> br0.io.Out(param.br0_brn_bb("bb_for_cond"))


  //Connecting br4 to bb_for_body
  bb_for_body.io.predicateIn(param.bb_for_body_pred("br4")) <> br4.io.Out(param.br4_brn_bb("bb_for_body"))


  //Connecting br4 to bb_for_end
  bb_for_cond_expand.io.InData <> br4.io.Out(param.br4_brn_bb("bb_for_end"))
  bb_for_end.io.predicateIn(param.bb_for_end_pred("br4")) <> bb_for_cond_expand.io.Out(0)


  //Connecting br6 to bb_for_inc
  bb_for_inc.io.predicateIn(param.bb_for_inc_pred("br6")) <> br6.io.Out(param.br6_brn_bb("bb_for_inc"))


  //Connecting br8 to bb_for_cond
  bb_for_cond.io.predicateIn(param.bb_for_cond_pred("br8")) <> br8.io.Out(param.br8_brn_bb("bb_for_cond"))



  // There is no detach instruction




  /* ================================================================== *
   *                   CONNECTING BASIC BLOCKS TO INSTRUCTIONS          *
   * ================================================================== */


  /**
    * Wiring enable signals to the instructions
    */

  br0.io.enable <> bb_entry.io.Out(param.bb_entry_activate("br0"))



  phi1.io.enable <> bb_for_cond.io.Out(param.bb_for_cond_activate("phi1"))

  phi2.io.enable <> bb_for_cond.io.Out(param.bb_for_cond_activate("phi2"))

  icmp3.io.enable <> bb_for_cond.io.Out(param.bb_for_cond_activate("icmp3"))

  br4.io.enable <> bb_for_cond.io.Out(param.bb_for_cond_activate("br4"))

  bb_for_cond_expand.io.enable <> bb_for_cond.io.Out(5)

  loop_L_5_liveIN_0.io.enable <> bb_for_cond.io.Out(4)

  loop_L_5_liveIN_0.io.Finish <> bb_for_cond_expand.io.Out(1)



  add5.io.enable <> bb_for_body.io.Out(param.bb_for_body_activate("add5"))

  br6.io.enable <> bb_for_body.io.Out(param.bb_for_body_activate("br6"))



  add7.io.enable <> bb_for_inc.io.Out(param.bb_for_inc_activate("add7"))

  br8.io.enable <> bb_for_inc.io.Out(param.bb_for_inc_activate("br8"))



  ret9.io.enable <> bb_for_end.io.Out(param.bb_for_end_activate("ret9"))





  /* ================================================================== *
   *                   DUMPING PHI NODES                                *
   * ================================================================== */


  /**
    * Connecting PHI Masks
    */
  //Connect PHI node

  // Wiring Live in to PHI node

  phi1.io.InData(param.phi1_phi_in("field0")) <> loop_L_5_liveIN_0.io.Out(param.phi1_in("field0"))

  phi1.io.InData(param.phi1_phi_in("add5")) <> add5.io.Out(param.phi1_in("add5"))

  phi2.io.InData(param.phi2_phi_in("const_0")).bits.data := 0.U
  phi2.io.InData(param.phi2_phi_in("const_0")).bits.predicate := true.B
  phi2.io.InData(param.phi2_phi_in("const_0")).valid := true.B

  phi2.io.InData(param.phi2_phi_in("add7")) <> add7.io.Out(param.phi2_in("add7"))

  /**
    * Connecting PHI Masks
    */
  //Connect PHI node

  phi1.io.Mask <> bb_for_cond.io.MaskBB(0)

  phi2.io.Mask <> bb_for_cond.io.MaskBB(1)



  /* ================================================================== *
   *                   CONNECTING LOOPHEADERS                           *
   * ================================================================== */


  // Connecting function argument to the loop header
  //i32 %j
  loop_L_5_liveIN_0.io.InData <> InputSplitter.io.Out.data("field0")



  /* ================================================================== *
   *                   DUMPING DATAFLOW                                 *
   * ================================================================== */


  /**
    * Connecting Dataflow signals
    */

  // Wiring instructions
  icmp3.io.LeftIO <> phi2.io.Out(param.icmp3_in("phi2"))

  // Wiring constant
  icmp3.io.RightIO.bits.data := 5.U
  icmp3.io.RightIO.bits.predicate := true.B
  icmp3.io.RightIO.valid := true.B

  // Wiring Branch instruction
  br4.io.CmpIO <> icmp3.io.Out(param.br4_in("icmp3"))

  // Wiring instructions
  add5.io.LeftIO <> phi1.io.Out(param.add5_in("phi1"))

  // Wiring constant
  add5.io.RightIO.bits.data := 1.U
  add5.io.RightIO.bits.predicate := true.B
  add5.io.RightIO.valid := true.B

  // Wiring instructions
  add7.io.LeftIO <> phi2.io.Out(param.add7_in("phi2"))

  // Wiring constant
  add7.io.RightIO.bits.data := 1.U
  add7.io.RightIO.bits.predicate := true.B
  add7.io.RightIO.valid := true.B

  // Wiring return instruction
  ret9.io.predicateIn(0).bits.control := true.B
  ret9.io.predicateIn(0).bits.taskID := 0.U
  ret9.io.predicateIn(0).valid := true.B
  ret9.io.In.data("field0") <> phi1.io.Out(param.ret9_in("phi1"))
  io.out <> ret9.io.Out


}

import java.io.{File, FileWriter}
object test08Main extends App {
  val dir = new File("RTL/test08") ; dir.mkdirs
  implicit val p = config.Parameters.root((new MiniConfig).toInstance)
  val chirrtl = firrtl.Parser.parse(chisel3.Driver.emit(() => new test08DF()))

  val verilogFile = new File(dir, s"${chirrtl.main}.v")
  val verilogWriter = new FileWriter(verilogFile)
  val compileResult = (new firrtl.VerilogCompiler).compileAndEmit(firrtl.CircuitState(chirrtl, firrtl.ChirrtlForm))
  val compiledStuff = compileResult.getEmittedCircuit
  verilogWriter.write(compiledStuff.value)
  verilogWriter.close()
}

