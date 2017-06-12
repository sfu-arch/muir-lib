package node

/**
 * Created by nvedula on 15/5/17.
 */

import chisel3._
import chisel3.util._
import org.scalacheck.Prop.False

import config._
import interfaces._
import utility.Constants._
import utility.UniformPrintfs

// Design Doc
//////////
/// DRIVER ///
/// 1. Memory response only available atleast 1 cycle after request
//  2. Need registers for pipeline handshaking e.g., _valid,
// _ready need to latch ready and valid signals.
//////////

class StoreSimpleIO(NumPredOps: Int,
  NumSuccOps: Int,
  NumOuts: Int)(implicit p: Parameters)
  extends HandShakingIOPS(NumPredOps, NumSuccOps, NumOuts) {
  // Node specific IO
  // GepAddr: The calculated address comming from GEP node
  val GepAddr = Flipped(Decoupled(new DataBundle))
  // Store data.
  val inData = Flipped(Decoupled(new DataBundle))
  // Memory request
  val memReq = Decoupled(new WriteReq())
  // Memory response.
  val memResp = Input(Flipped(new WriteResp()))
}

/**
 * @brief Store Node. Implements store operations
 * @details [long description]
 *
 * @param NumPredOps [Number of predicate memory operations]
 */
class StoreSimpleNode(NumPredOps: Int,
  NumSuccOps: Int,
  NumOuts: Int,
  Typ: UInt = MT_W, ID: Int)(implicit p: Parameters)
  extends HandShaking(NumPredOps, NumSuccOps, NumOuts, ID)(p) {

  // Set up StoreSimpleIO
  override lazy val io = IO(new StoreSimpleIO(NumPredOps, NumSuccOps, NumOuts))

  // Printf debugging
  override val printfSigil = "Store ID: " + ID + " "

/*=============================================
=            Register declarations            =
=============================================*/

  // OP Inputs
  val addr_R = RegInit(DataBundle.default)
  val data_R = RegInit(DataBundle.default)

  // State machine
  val s_idle :: s_RECEIVING :: s_Done :: Nil = Enum(3)
  val state = RegInit(s_idle)

  val ReqValid = RegInit(false.B)

/*============================================
=            Predicate Evaluation            =
============================================*/

  val predicate = addr_R.predicate & data_R.predicate & IsEnable()
  val start  = addr_R.valid & data_R.valid & IsPredValid() & IsEnableValid()

/*================================================
=            Latch inputs. Set output            =
================================================*/

  //Initialization READY-VALIDs for GepAddr and Predecessor memory ops
  io.GepAddr.ready := ~addr_R.valid
  io.inData.ready := ~data_R.valid

  // ACTION: GepAddr
  io.GepAddr.ready := ~addr_R.valid
  when(io.GepAddr.fire()) {
    addr_R := io.GepAddr.bits
    addr_R.valid := true.B
  }

  // ACTION: inData
  when(io.inData.fire()) {
    // Latch the data
    data_R := io.inData.bits
    data_R.valid := true.B
  }

  // Wire up Outputs
  for (i <- 0 until NumOuts) {
    io.Out(i).bits := data_R
    io.Out(i).bits.predicate := predicate
  }

when (start & predicate) {
/*=============================================
=            ACTIONS (possibly dangerous)     =
=============================================*/

  // ACTION:  Memory request
  //  Check if address is valid and data has arrive and predecessors have completed.
  val mem_req_fire = addr_R.valid & IsPredValid() & data_R.valid

  // Outgoing Address Req ->
  io.memReq.bits.address := addr_R.data
  io.memReq.bits.node := nodeID_R
  io.memReq.bits.data := data_R.data
  io.memReq.bits.Typ := Typ
  io.memReq.valid    := false.B

  // ACTION: Memory Request
  // -> Send memory request
  when((state === s_idle) && (mem_req_fire)) {
    io.memReq.valid := true.B
  }

  //  ACTION: Arbitration ready
  when((state === s_idle) && (io.memReq.ready === true.B) && (io.memReq.valid === true.B)) {
    // ReqValid := false.B
    state := s_RECEIVING
  }

  //  ACTION:  <- Incoming Data
  when(state === s_RECEIVING && io.memResp.valid) {
    // Set output to valid
    ValidSucc()
    ValidOut()
    state := s_Done
  }
}.elsewhen(start & ~predicate){
  ValidSucc()
  ValidOut()
  state := s_Done
}
/*===========================================
=            Output Handshaking and Reset   =
===========================================*/

  //  ACTION: <- Check Out READY and Successors READY
  when(state === s_Done) {
    // When successors are complete and outputs are ready you can reset.
    // data already valid and would be latched in this cycle.
    val complete = IsSuccReady() & IsOutReady()

    when(complete) {
      // Clear all the valid states.
      // Reset address
      addr_R := DataBundle.default
      // Reset data.
      data_R := DataBundle.default
      // Clear all other state
      Reset()
      // Reset state.
      state := s_idle

    }
  }
  // Trace detail.
  printfInfo("State : %x, Out: %x Succ Mem Op [", state, io.Out(0).bits.data)
  for (i <- 0 until NumSuccOps) {
  printf(p"${io.SuccOp(i).valid},")}
  printf("]\n")
}