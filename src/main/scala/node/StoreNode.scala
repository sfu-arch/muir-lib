
package dandelion.node

import chisel3._
import chisel3.util._
import org.scalacheck.Prop.False

import chipsalliance.rocketchip.config._
import dandelion.config._
import dandelion.interfaces._
import utility.Constants._
import utility.UniformPrintfs

/**
 *
 * 1. Memory response only available atleast 1 cycle after request
 * 2. Need registers for pipeline handshaking e.g., _valid,
 * @param p
 */
class StoreNodeIO()(implicit p: Parameters) extends AccelBundle()(p) {
  // Node specific IO
  // GepAddr: The calculated address comming from GEP node
  val GepAddr = Flipped(Decoupled(new DataBundle))
  // Store data.
  val inData = Flipped(Decoupled(new DataBundle))
  // Memory request
  val memReq = Decoupled(new WriteReq())
  // Memory response.
  val memResp = Flipped(Valid(new WriteResp()))


  // Predicate enable
  val enable = Flipped(Decoupled(new ControlBundle()))

  val Finish = Output(Bool())

}

/**
  * @brief Store Node. Implements store operations
  * @note [long description]
  */
class StoreNode(Typ: UInt = MT_W, ID: Int, RouteID: Int)(implicit val p: Parameters)
  extends Module with HasAccelParams with UniformPrintfs {

  // Set up StoreIO
  val io = IO(new StoreNodeIO())

  override val printfSigil = "Node (STORE) ID: " + ID + " "


  /*=============================================
  =            Register declarations            =
  =============================================*/

  // OP Inputs
  val addr_R = RegInit(DataBundle.default)
  val addr_valid_R = RegInit(false.B)

  val data_R = RegInit(DataBundle.default)
  val data_valid_R = RegInit(false.B)

  // Predicate register
  val enable_R = RegInit(ControlBundle.default)
  val enable_valid_R = RegInit(false.B)

  val nodeID_R = RegInit(ID.U)

  // State machine
  val s_idle :: s_WAITING :: s_RECEIVING :: s_Done :: Nil = Enum(4)
  val state = RegInit(s_idle)

  val ReqValid = RegInit(false.B)


  /*============================================
  =            Predicate Evaluation            =
  ============================================*/

  val predicate = enable_R.control

  val start = addr_valid_R & data_valid_R & enable_valid_R

  /*================================================
  =            Latch inputs. Set output            =
  ================================================*/

  // ACTION: GepAddr
  io.GepAddr.ready := ~addr_valid_R
  when(io.GepAddr.fire) {
    addr_R <> io.GepAddr.bits
    addr_valid_R := true.B
  }

  // ACTION: inData
  io.inData.ready := ~data_valid_R
  when(io.inData.fire) {
    // Latch the data
    data_R <> io.inData.bits
    data_valid_R := true.B
  }

  io.enable.ready := ~enable_valid_R
  when(io.enable.fire) {
    enable_R <> io.enable.bits
    enable_valid_R := true.B
  }

  // Outgoing Address Req ->
  io.memReq.bits.address := addr_R.data
  io.memReq.bits.data := data_R.data
  io.memReq.bits.taskID := addr_R.taskID | enable_R.taskID
  io.memReq.bits.Typ := Typ
  io.memReq.bits.RouteID := RouteID.U
  io.memReq.bits.mask := 0.U
  //  io.memReq.valid := false.B

  /*=============================================
  =            ACTIONS (possibly dangerous)     =
  =============================================*/

  val mem_req_fire = addr_valid_R & data_valid_R

  switch(state) {
    is(s_idle) {

      io.memReq.valid := false.B

      when(start && predicate) {
        when(mem_req_fire) {
          state := s_WAITING
        }
      }.elsewhen(start && !predicate) {
        state := s_Done
      }

    }

    is(s_WAITING) {
      io.memReq.valid := true.B

      when(io.memReq.ready) {
        state := s_RECEIVING
      }
    }

    is(s_RECEIVING) {
      io.memReq.valid := false.B

      when(io.memResp.valid) {
        state := s_Done
      }
    }

    is(s_Done) {
      io.memReq.valid := false.B

      addr_R := DataBundle.default
      addr_valid_R := false.B
      // Reset data.
      data_R := DataBundle.default
      data_valid_R := false.B

      //Reset enable.
      enable_R := ControlBundle.default
      enable_valid_R := false.B

      // Clear all other state
      // Reset:

      // Reset state.
      state := s_idle
      printfInfo("Output fired")

    }

  }

}
