package dandelion.generator.cilk

import chisel3._
import chipsalliance.rocketchip.config._
import dandelion.config._
import dandelion.concurrent._
import dandelion.control._
import dandelion.interfaces._
import dandelion.junctions._
import dandelion.loop._
import dandelion.node._
import util._



  /* ================================================================== *
   *                   PRINTING PORTS DEFINITION                        *
   * ================================================================== */

abstract class bgemm_detach2DFIO(implicit val p: Parameters) extends Module with HasAccelParams {
  val io = IO(new Bundle {
    val in = Flipped(Decoupled(new Call(List(32, 32, 32, 32, 32))))
    val call_8_out = Decoupled(new Call(List(32, 32, 32, 32, 32, 32)))
    val call_8_in = Flipped(Decoupled(new Call(List())))
    val MemResp = Flipped(Valid(new MemResp))
    val MemReq = Decoupled(new MemReq)
    val out = Decoupled(new Call(List()))
  })
}

class bgemm_detach2DF(implicit p: Parameters) extends bgemm_detach2DFIO()(p) {


  /* ================================================================== *
   *                   PRINTING MEMORY MODULES                          *
   * ================================================================== */

  //Remember if there is no mem operation io memreq/memresp should be grounded
  io.MemReq <> DontCare
  io.MemResp <> DontCare

  val InputSplitter = Module(new SplitCallNew(List(1, 1, 1, 1, 1)))
  InputSplitter.io.In <> io.in



  /* ================================================================== *
   *                   PRINTING LOOP HEADERS                            *
   * ================================================================== */

  val Loop_0 = Module(new LoopBlockNode(NumIns = List(1, 1, 1, 1, 1), NumOuts = List(), NumCarry = List(1), NumExits = 1, ID = 0))



  /* ================================================================== *
   *                   PRINTING BASICBLOCK NODES                        *
   * ================================================================== */

  val bb_my_loopi0 = Module(new BasicBlockNoMaskFastNode(NumInputs = 1, NumOuts = 1, BID = 0))

  val bb_my_pfor_detach231 = Module(new BasicBlockNode(NumInputs = 2, NumOuts = 3, NumPhi = 1, BID = 1))

  val bb_my_pfor_inc2 = Module(new BasicBlockNoMaskFastNode(NumInputs = 1, NumOuts = 5, BID = 2))

  val bb_my_pfor_cond_cleanup213 = Module(new BasicBlockNoMaskFastNode(NumInputs = 1, NumOuts = 1, BID = 3))

  val bb_my_sync_continue4 = Module(new BasicBlockNoMaskFastNode(NumInputs = 1, NumOuts = 1, BID = 4))

  val bb_my_offload_loopk5 = Module(new BasicBlockNoMaskFastNode(NumInputs = 1, NumOuts = 2, BID = 5))



  /* ================================================================== *
   *                   PRINTING INSTRUCTION NODES                       *
   * ================================================================== */

  //  br label %my_pfor.detach23, !UID !10, !BB_UID !11
  val br_0 = Module(new UBranchNode(ID = 0))

  //  %1 = phi i32 [ 0, %my_loopi ], [ %2, %my_pfor.inc ], !UID !12
  val phi1 = Module(new PhiFastNode(NumInputs = 2, NumOutputs = 2, ID = 1, Res = true))

  //  detach within %0, label %my_offload.loopk, label %my_pfor.inc, !UID !13, !BB_UID !14
  val detach_2 = Module(new Detach(ID = 2))

  //  %2 = add nuw nsw i32 %1, 1, !UID !15
  val binaryOp_3 = Module(new ComputeNode(NumOuts = 2, ID = 3, opCode = "add")(sign = false))

  //  %3 = icmp eq i32 %2, 4, !UID !16
  val icmp_4 = Module(new ComputeNode(NumOuts = 1, ID = 4, opCode = "eq")(sign = false))

  //  br i1 %3, label %my_pfor.cond.cleanup21, label %my_pfor.detach23, !llvm.loop !17, !UID !19, !BB_UID !20
  val br_5 = Module(new CBranchNodeVariable(NumTrue = 1, NumFalse = 1, NumPredecessor = 0, ID = 5))

  //  sync within %0, label %my_sync.continue, !UID !21, !BB_UID !22
  val sync_6 = Module(new SyncTC(ID = 6, NumInc=1, NumDec=1, NumOuts=1))

  //  ret void, !UID !23, !BB_UID !24
  val ret_7 = Module(new RetNode2(retTypes = List(), ID = 7))

  //  call void @bgemm_detach3(i32 %1, i32 %mul11.in, i32* %m1.in, i32 %mul1.in, i32* %m2.in, i32* %prod.in), !UID !25
  val call_8_out = Module(new CallOutNode(ID = 8, NumSuccOps = 0, argTypes = List(32,32,32,32,32,32)))

  val call_8_in = Module(new CallInNode(ID = 8, argTypes = List()))

  //  reattach within %0, label %my_pfor.inc, !UID !26, !BB_UID !27
  val reattach_9 = Module(new Reattach(NumPredOps= 1, ID = 9))



  /* ================================================================== *
   *                   PRINTING CONSTANTS NODES                         *
   * ================================================================== */

  //i32 0
  val const0 = Module(new ConstFastNode(value = 0, ID = 0))

  //i32 1
  val const1 = Module(new ConstFastNode(value = 1, ID = 1))

  //i32 4
  val const2 = Module(new ConstFastNode(value = 4, ID = 2))



  /* ================================================================== *
   *                   BASICBLOCK -> PREDICATE INSTRUCTION              *
   * ================================================================== */

  bb_my_loopi0.io.predicateIn(0) <> InputSplitter.io.Out.enable

  bb_my_pfor_inc2.io.predicateIn(0) <> detach_2.io.Out(0)

  bb_my_sync_continue4.io.predicateIn(0) <> sync_6.io.Out(0)

  bb_my_offload_loopk5.io.predicateIn(0) <> detach_2.io.Out(1)



  /* ================================================================== *
   *                   BASICBLOCK -> PREDICATE LOOP                     *
   * ================================================================== */

  bb_my_pfor_detach231.io.predicateIn(1) <> Loop_0.io.activate_loop_start

  bb_my_pfor_detach231.io.predicateIn(0) <> Loop_0.io.activate_loop_back

  bb_my_pfor_cond_cleanup213.io.predicateIn(0) <> Loop_0.io.loopExit(0)



  /* ================================================================== *
   *                   PRINTING PARALLEL CONNECTIONS                    *
   * ================================================================== */

  sync_6.io.incIn(0) <> detach_2.io.Out(2)

  sync_6.io.decIn(0) <> reattach_9.io.Out(0)



  /* ================================================================== *
   *                   LOOP -> PREDICATE INSTRUCTION                    *
   * ================================================================== */

  Loop_0.io.enable <> br_0.io.Out(0)

  Loop_0.io.loopBack(0) <> br_5.io.FalseOutput(0)

  Loop_0.io.loopFinish(0) <> br_5.io.TrueOutput(0)



  /* ================================================================== *
   *                   ENDING INSTRUCTIONS                              *
   * ================================================================== */



  /* ================================================================== *
   *                   LOOP INPUT DATA DEPENDENCIES                     *
   * ================================================================== */

  Loop_0.io.InLiveIn(0) <> InputSplitter.io.Out.data.elements("field0")(0)

  Loop_0.io.InLiveIn(1) <> InputSplitter.io.Out.data.elements("field1")(0)

  Loop_0.io.InLiveIn(2) <> InputSplitter.io.Out.data.elements("field2")(0)

  Loop_0.io.InLiveIn(3) <> InputSplitter.io.Out.data.elements("field3")(0)

  Loop_0.io.InLiveIn(4) <> InputSplitter.io.Out.data.elements("field4")(0)



  /* ================================================================== *
   *                   LOOP DATA LIVE-IN DEPENDENCIES                   *
   * ================================================================== */

  call_8_out.io.In.elements("field1") <> Loop_0.io.OutLiveIn.elements("field0")(0)

  call_8_out.io.In.elements("field2") <> Loop_0.io.OutLiveIn.elements("field1")(0)

  call_8_out.io.In.elements("field3") <> Loop_0.io.OutLiveIn.elements("field2")(0)

  call_8_out.io.In.elements("field4") <> Loop_0.io.OutLiveIn.elements("field3")(0)

  call_8_out.io.In.elements("field5") <> Loop_0.io.OutLiveIn.elements("field4")(0)



  /* ================================================================== *
   *                   LOOP DATA LIVE-OUT DEPENDENCIES                  *
   * ================================================================== */



  /* ================================================================== *
   *                   LOOP LIVE OUT DEPENDENCIES                       *
   * ================================================================== */



  /* ================================================================== *
   *                   LOOP CARRY DEPENDENCIES                          *
   * ================================================================== */

  Loop_0.io.CarryDepenIn(0) <> binaryOp_3.io.Out(0)



  /* ================================================================== *
   *                   LOOP DATA CARRY DEPENDENCIES                     *
   * ================================================================== */

  phi1.io.InData(1) <> Loop_0.io.CarryDepenOut.elements("field0")(0)



  /* ================================================================== *
   *                   BASICBLOCK -> ENABLE INSTRUCTION                 *
   * ================================================================== */

  br_0.io.enable <> bb_my_loopi0.io.Out(0)


  const0.io.enable <> bb_my_pfor_detach231.io.Out(0)

  phi1.io.enable <> bb_my_pfor_detach231.io.Out(1)


  detach_2.io.enable <> bb_my_pfor_detach231.io.Out(2)


  const1.io.enable <> bb_my_pfor_inc2.io.Out(0)

  const2.io.enable <> bb_my_pfor_inc2.io.Out(1)

  binaryOp_3.io.enable <> bb_my_pfor_inc2.io.Out(2)


  icmp_4.io.enable <> bb_my_pfor_inc2.io.Out(3)


  br_5.io.enable <> bb_my_pfor_inc2.io.Out(4)


  sync_6.io.enable <> bb_my_pfor_cond_cleanup213.io.Out(0)


  ret_7.io.In.enable <> bb_my_sync_continue4.io.Out(0)


  call_8_in.io.enable <> bb_my_offload_loopk5.io.Out(1)

  call_8_out.io.enable <> bb_my_offload_loopk5.io.Out(0)




  /* ================================================================== *
   *                   CONNECTING PHI NODES                             *
   * ================================================================== */

  phi1.io.Mask <> bb_my_pfor_detach231.io.MaskBB(0)



  /* ================================================================== *
   *                   PRINT ALLOCA OFFSET                              *
   * ================================================================== */



  /* ================================================================== *
   *                   CONNECTING MEMORY CONNECTIONS                    *
   * ================================================================== */



  /* ================================================================== *
   *                   PRINT SHARED CONNECTIONS                         *
   * ================================================================== */



  /* ================================================================== *
   *                   CONNECTING DATA DEPENDENCIES                     *
   * ================================================================== */

  phi1.io.InData(0) <> const0.io.Out

  binaryOp_3.io.RightIO <> const1.io.Out

  icmp_4.io.RightIO <> const2.io.Out

  binaryOp_3.io.LeftIO <> phi1.io.Out(0)

  call_8_out.io.In.elements("field0") <> phi1.io.Out(1)

  icmp_4.io.LeftIO <> binaryOp_3.io.Out(1)

  br_5.io.CmpIO <> icmp_4.io.Out(0)

  reattach_9.io.predicateIn(0).enq(DataBundle.active(1.U))



  /* ================================================================== *
   *                   PRINTING CALLIN AND CALLOUT INTERFACE            *
   * ================================================================== */

  call_8_in.io.In <> io.call_8_in

  io.call_8_out <> call_8_out.io.Out(0)

  reattach_9.io.enable <> call_8_in.io.Out.enable



  /* ================================================================== *
   *                   PRINTING OUTPUT INTERFACE                        *
   * ================================================================== */

  io.out <> ret_7.io.Out

}

