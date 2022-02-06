package dandelion.node

import chisel3._
import chisel3.util._
import chipsalliance.rocketchip.config._
import chisel3.util.experimental.BoringUtils
import dandelion.interfaces._
import utility.Constants._

/**
 * Design Doc
 * 1. Memory response only available atleast 1 cycle after request
 * 2. Need registers for pipeline handshaking e.g., _valid,
 * @param NumPredOps Number of parents
 * @param NumSuccOps Number of successors
 * @param NumOuts    Number of outputs
 *
 *
 * @param Debug
 * @param p
 */
class StoreIO(NumPredOps: Int,
              NumSuccOps: Int,
              NumOuts: Int, Debug: Boolean = false)(implicit p: Parameters)
  extends HandShakingIOPS(NumPredOps, NumSuccOps, NumOuts, Debug)(new DataBundle) {
  // Node specific IO
  // GepAddr: The calculated address comming from GEP node
  val GepAddr = Flipped(Decoupled(new DataBundle))
  // Store data.
  val inData = Flipped(Decoupled(new DataBundle))
  // Memory request
  val memReq = Decoupled(new WriteReq())
  // Memory response.
  val memResp = Flipped(Valid(new WriteResp()))

}

/**
  * @brief Store Node. Implements store operations
  * @details [long description]
  * @param NumPredOps [Number of predicate memory operations]
  */
class UnTypStore(NumPredOps: Int,
                 NumSuccOps: Int,
                 NumOuts: Int = 1,
                 Typ: UInt = MT_W, ID: Int, RouteID: Int, Debug: Boolean = false, GuardValData : Int = 0 , GuardValAddr : Int = 0)
                (implicit p: Parameters,
                 name: sourcecode.Name,
                 file: sourcecode.File)
  extends HandShaking(NumPredOps, NumSuccOps, NumOuts, ID, Debug)(new DataBundle)(p) {

  // Set up StoreIO
  override lazy val io = IO(new StoreIO(NumPredOps, NumSuccOps, NumOuts, Debug))
  // Printf debugging
  val node_name = name.value
  val module_name = file.value.split("/").tail.last.split("\\.").head.capitalize
  override val printfSigil = "[" + module_name + "] " + node_name + ": " + ID + " "
  val (cycleCount, _) = Counter(true.B, 32 * 1024)

  /*=============================================
  =            Register declarations            =
  =============================================*/

  // OP Inputs
  val addr_R = RegInit(DataBundle.default)
  val data_R = RegInit(DataBundle.default)
  val addr_valid_R = RegInit(false.B)
  val data_valid_R = RegInit(false.B)

  // State machine
  val s_idle :: s_RECEIVING :: s_Done :: Nil = Enum(3)
  val state = RegInit(s_idle)

  val ReqValid = RegInit(false.B)

  //------------------------------
  var log_id = WireInit(ID.U((6).W))
  var GuardFlag = WireInit(0.U(1.W))

  var log_data_reg = RegInit(0.U((xlen-26).W))
  var log_addr_reg = RegInit(0.U(15.W))
  val writeFinish = RegInit(false.B)
  //log_id := ID.U
  //test_value := Cat(GuardFlag,log_id, log_out)
  val log_value = WireInit(0.U(xlen.W))
  log_value := Cat(GuardFlag, log_id, log_data_reg, log_addr_reg)



  if (Debug) {
    val test_value_valid = Wire(Bool())
    val test_value_ready = Wire(Bool())
    val test_value_valid_r = RegInit(false.B)
    test_value_valid := test_value_valid_r
    test_value_ready := false.B
    BoringUtils.addSource(log_value, "data" + ID)
    BoringUtils.addSource(test_value_valid, "valid" + ID)
    BoringUtils.addSink(test_value_ready, "ready" + ID)



    when(enable_valid_R ) {
      test_value_valid_r := true.B
    }
    when(state === s_Done){
      test_value_valid_r := false.B
    }

  }
  //----------------------------------



  /*============================================
  =            Predicate Evaluation            =
  ============================================*/

  //  val predicate = IsEnable()
  //  val start = addr_valid_R & data_valid_R & IsPredValid() & IsEnableValid()

  /*================================================
  =            Latch inputs. Set output            =
  ================================================*/

  //Initialization READY-VALIDs for GepAddr and Predecessor memory ops
  io.GepAddr.ready := ~addr_valid_R
  io.inData.ready := ~data_valid_R

  // ACTION: GepAddr
  io.GepAddr.ready := ~addr_valid_R
  when(io.GepAddr.fire()) {
    addr_R := io.GepAddr.bits
    addr_valid_R := true.B
  }
  when(io.enable.fire()) {
    succ_bundle_R.foreach(_ := io.enable.bits)
  }
  // ACTION: inData
  when(io.inData.fire()) {
    // Latch the data
    data_R := io.inData.bits
    data_valid_R := true.B
  }

  // Wire up Outputs
  for (i <- 0 until NumOuts) {
    io.Out(i).bits := data_R
    io.Out(i).bits.taskID := data_R.taskID | addr_R.taskID | enable_R.taskID
  }
  // Outgoing Address Req ->
  //here
  io.memReq.bits.address := addr_R.data
  io.memReq.bits.data := data_R.data
  io.memReq.bits.Typ := Typ
  io.memReq.bits.RouteID := RouteID.U
  io.memReq.bits.taskID := data_R.taskID | addr_R.taskID | enable_R.taskID
  io.memReq.bits.mask := 15.U
  io.memReq.valid := false.B

  dontTouch(io.memResp)

  /*=============================================
  =            ACTIONS (possibly dangerous)     =
  =============================================*/
  val mem_req_fire = addr_valid_R & IsPredValid() & data_valid_R
  val complete = IsSuccReady() & IsOutReady()

  switch(state) {
    is(s_idle) {
      when(enable_valid_R) {
        when(data_valid_R && addr_valid_R) {
          when(enable_R.control && mem_req_fire) {
            io.memReq.valid := true.B

            if (Debug) {
              when(data_R.data =/= GuardValData.U || addr_R.data =/= GuardValAddr.U ) {
                GuardFlag := 1.U
                log_data_reg :=  data_R.data
                log_addr_reg := addr_R.data
                data_R.data := GuardValData.U
                addr_R.data := GuardValAddr.U

              }.otherwise {
                GuardFlag := 0.U
                log_data_reg :=  data_R.data
                log_addr_reg := addr_R.data
              }
            }
            when(io.memReq.ready) {
              state := s_RECEIVING
            }
          }.otherwise {
            ValidSucc()
            ValidOut()
            data_R.predicate := false.B
            state := s_Done
          }
        }
      }
    }
    is(s_RECEIVING) {
      when(io.memResp.valid) {
        ValidSucc()
        ValidOut()
        state := s_Done
      }
    }
    is(s_Done) {
      when(complete) {
        // Clear all the valid states.
        // Reset address
        addr_R := DataBundle.default
        addr_valid_R := false.B
        // Reset data.
        data_R := DataBundle.default
        data_valid_R := false.B
        // Clear all other state
        Reset()
        // Reset state.
        state := s_idle
        if (log) {
          printf("[LOG] " + "[" + module_name + "] [TID->%d] [STORE]" + node_name + ": Fired @ %d Mem[%d] = %d\n",
            enable_R.taskID, cycleCount, addr_R.data, data_R.data)
          //printf("DEBUG " + node_name + ": $%d = %d\n", addr_R.data, data_R.data)
        }
      }
    }
  }
  // Trace detail.
  if (log == true && (comp contains "STORE")) {
    val x = RegInit(0.U(xlen.W))
    x := x + 1.U
    verb match {
      case "high" => {}
      case "med" => {}
      case "low" => {
        printfInfo("Cycle %d : { \"Inputs\": {\"GepAddr\": %x},", x, (addr_valid_R))
        printf("\"State\": {\"State\": \"%x\", \"data_R(Valid,Data,Pred)\": \"%x,%x,%x\" },", state, data_valid_R, data_R.data, io.Out(0).bits.predicate)
        printf("\"Outputs\": {\"Out\": %x}", io.Out(0).fire())
        printf("}")
      }
      case everythingElse => {}
    }
  }

  def isDebug(): Boolean = {
    Debug
  }

}

