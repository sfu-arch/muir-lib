package dandelion.node

import chisel3._
import chisel3.util._
import chisel3.util.experimental.BoringUtils
import dandelion.config._
import dandelion.interfaces._
import utility.Constants._
import utility.UniformPrintfs

// Design Doc
//////////
/// DRIVER ///
/// 1. Memory response only available atleast 1 cycle after request
//  2. Need registers for pipeline handshaking e.g., _valid,
// _ready need to latch ready and valid signals.
//////////

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
  val memResp = Input(Flipped(new WriteResp()))

  override def cloneType = new StoreIO(NumPredOps, NumSuccOps, NumOuts, Debug).asInstanceOf[this.type]
}

/**
  * @brief Store Node. Implements store operations
  * @details [long description]
  * @param NumPredOps [Number of predicate memory operations]
  */
class UnTypStore(NumPredOps: Int,
                 NumSuccOps: Int,
                 NumOuts: Int = 1,
                 Typ: UInt = MT_W, ID: Int, RouteID: Int, Debug: Boolean = false)
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

  //  if (ID == 7) {
  //    val SinkVal = Wire (UInt(6.W))
  //    SinkVal:= 0.U
  //    val Uniq_name = "me"
  //    BoringUtils.addSink(SinkVal, Uniq_name)
  //    printf("[***************************sinksource*******************" + SinkVal)
  //  }
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


//class DebugBufferIO(NumPredOps: Int = 0,
//                    NumSuccOps: Int = 0,
//                    NumOuts: Int = 1)(implicit p: Parameters)
//  extends HandShakingIOPS(NumPredOps, NumSuccOps, NumOuts)(new DataBundle) {
//
//  // Memory request
//  val memReq = Decoupled(new WriteReq())
//  // Memory response.
//  val memResp = Input(Flipped(new WriteResp()))
//
//  override def cloneType = new DebugBufferIO(NumPredOps, NumSuccOps, NumOuts).asInstanceOf[this.type]
//}

/**
  * @brief Store Node. Implements store operations
  * @details [long description]
  * @param NumPredOps [Number of predicate memory operations]
  */

class DebugBufferNode(
                       NumOuts: Int = 1,
                       Typ: UInt = MT_W, ID: Int, RouteID: Int, Bore_ID: Int, node_cnt : UInt)
                     (implicit val p: Parameters,
                      name: sourcecode.Name,
                      file: sourcecode.File)
  extends Module with CoreParams with UniformPrintfs {


  val io = IO(new Bundle {
    //  // Memory request
    val memReq = Decoupled(new WriteReq())
    //  // Memory response.
    val memResp = Input(Flipped(new WriteResp()))

    val Enable = Input(Bool())
  })
  val node_name = name.value
  val module_name = file.value.split("/").tail.last.split("\\.").head.capitalize
  override val printfSigil = "[" + module_name + "] " + node_name + ": " + ID + " "
  val (cycleCount, _) = Counter(true.B, 32 * 1024)
  val inData = new DataBundle
  val GepAddr = new DataBundle
  //------------------------------
  val dbg_counter = Counter(1024)
  val Uniq_name_Data = "meData"
  //val Uniq_name_Adr = "meAdr"
  //---------------------------
  // -------

  val LogData = Module(new Queue(UInt((xlen*4).W), 20))



  val st_node = Module(new UnTypDebugStore(ID = 0, RouteID = RouteID))

  LogData.io.enq.bits := 0.U
  LogData.io.enq.valid := false.B
  LogData.io.deq.ready := false.B

  val queue_data = WireInit(0.U((xlen*4).W))
  val queue_valid = WireInit(false.B)
  val queue_ready = WireInit(false.B)

  BoringUtils.addSink(queue_data, "data" + Bore_ID)
  BoringUtils.addSink(queue_valid, "valid" +  Bore_ID)
  BoringUtils.addSource(queue_ready, "ready" + Bore_ID)

  LogData.io.enq.bits := queue_data
  LogData.io.enq.valid := queue_valid && io.Enable
  queue_ready := LogData.io.enq.ready


  st_node.io.InData.bits := DataBundle(LogData.io.deq.bits)
  st_node.io.InData.valid := LogData.io.deq.valid
  LogData.io.deq.ready := st_node.io.InData.ready

  io.memReq <> st_node.io.memReq
  st_node.io.memResp <> io.memResp


  var (addr_cnt, wrap) = Counter(st_node.io.InData.fire, 4096)

  //  addr_cnt = node_cnt * 20.U

  val addr_offset = node_cnt * 20.U
  val addr = addr_offset + (addr_cnt << 2.U).asUInt()



  st_node.io.GepAddr.bits := DataBundle(addr)
  st_node.io.GepAddr.valid := true.B


}


/**
  * This is a test for debug store node
  *
  * @brief Store Node. Implements store operations
  * @details [long description]
  */
class UnTypDebugStore(
                       NumOuts: Int = 1,
                       Typ: UInt = MT_W, ID: Int, RouteID: Int)
                     (implicit val p: Parameters,
                      name: sourcecode.Name,
                      file: sourcecode.File)
  extends Module with CoreParams with UniformPrintfs {

  // Set up StoreIO
  val io = IO(new Bundle {

    //Input address
    val GepAddr = Flipped(Decoupled(new DataBundle()))
    //Input data
    val InData = Flipped(Decoupled(new DataBundle()))

    // Memory request
    val memReq = Decoupled(new WriteReq())
    // Memory response.
    val memResp = Input(Flipped(new WriteResp()))
  })
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
  val addr_valid_R = RegInit(false.B)

  val data_R = RegInit(DataBundle.default)
  val data_valid_R = RegInit(false.B)

  // State machine
  val s_idle :: s_RECEIVING :: s_Done :: Nil = Enum(3)
  val state = RegInit(s_idle)


  // GepAddr
  io.GepAddr.ready := ~addr_valid_R
  when(io.GepAddr.fire()) {
    addr_R := io.GepAddr.bits
    addr_valid_R := true.B
  }

  // InData
  io.InData.ready := ~data_valid_R
  when(io.InData.fire()) {
    data_R := io.InData.bits
    data_valid_R := true.B
  }

  // Default values for memory ports
  io.memReq.bits.address := addr_R.data
  io.memReq.bits.data := data_R.data
  io.memReq.bits.Typ := Typ
  io.memReq.bits.RouteID := RouteID.U
  io.memReq.bits.taskID := data_R.taskID | addr_R.taskID
  io.memReq.bits.mask := 15.U
  io.memReq.valid := false.B

  /*=============================================
  =            ACTIONS (possibly dangerous)     =
  =============================================*/
  val latch_data = addr_valid_R && data_valid_R

  switch(state) {
    is(s_idle) {
      when(latch_data) {
        io.memReq.valid := true.B
        when(io.memReq.fire) {
          state := s_RECEIVING
        }
      }
    }
    is(s_RECEIVING) {
      when(io.memResp.valid) {
        state := s_Done
      }
    }
    is(s_Done) {
      // Clear all the valid states.
      addr_R := DataBundle.default
      addr_valid_R := false.B
      // Reset data.
      data_R := DataBundle.default
      data_valid_R := false.B

      state := s_idle
      if (log) {
        printf("[LOG] " + "[" + module_name + "] [TID->%d] [STORE]" + node_name + ": Fired @ %d Mem[%d] = %d\n",
          data_R.taskID, cycleCount, addr_R.data, data_R.data)
      }
    }
  }
}

