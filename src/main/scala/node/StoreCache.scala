package dandelion.node

import chisel3._
import chisel3.util._
import chipsalliance.rocketchip.config._
import chisel3.util.experimental.BoringUtils
import dandelion.config._
import dandelion.interfaces._
import utility.Constants._
import utility.UniformPrintfs

class StoreCacheIO(NumPredOps: Int,
                   NumSuccOps: Int,
                   NumOuts: Int, Debug: Boolean = false)(implicit p: Parameters)
  extends HandShakingIOPS(NumPredOps, NumSuccOps, NumOuts, Debug)(new DataBundle) {
  val GepAddr = Flipped(Decoupled(new DataBundle))
  val inData = Flipped(Decoupled(new DataBundle))
  val MemReq = Decoupled(new MemReq)
  val MemResp = Flipped(Valid(new MemResp))

  override def cloneType = new StoreCacheIO(NumPredOps, NumSuccOps, NumOuts, Debug).asInstanceOf[this.type]
}

/**
 * @brief Store Node. Implements store operations
 * @details [long description]
 * @param NumPredOps [Number of predicate memory operations]
 */
class UnTypStoreCache(NumPredOps: Int,
                      NumSuccOps: Int,
                      NumOuts: Int = 1,
                      Typ: UInt = MT_W,
                      ID: Int,
                      RouteID: Int,
                      Debug: Boolean = false,
                      GuardAddress: Seq[Int] = List())
                     (implicit p: Parameters,
                      name: sourcecode.Name,
                      file: sourcecode.File)
  extends HandShaking(NumPredOps, NumSuccOps, NumOuts, ID, Debug)(new DataBundle)(p)
    with HasAccelShellParams
    with HasDebugCodes {

  // Set up StoreIO
  override lazy val io = IO(new StoreCacheIO(NumPredOps, NumSuccOps, NumOuts, Debug))
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

  when(io.inData.fire()) {
    data_R := io.inData.bits
    data_valid_R := true.B
  }

  when(io.enable.fire()) {
    succ_bundle_R.foreach(_ := io.enable.bits)
  }


  // Wire up Outputs
  for (i <- 0 until NumOuts) {
    io.Out(i).bits := data_R
    io.Out(i).bits.taskID := data_R.taskID
  }


  val mem_req_fire = addr_valid_R & IsPredValid() & data_valid_R

  /**
   * Debug signals
   */
  val address_value_valid = WireInit(false.B)
  val address_value_ready = WireInit(true.B)

  def isAddrFire(): Bool = {
    enable_valid_R && addr_valid_R && enable_R.control && state === s_idle && io.MemReq.ready && address_value_ready && mem_req_fire
  }

  def complete(): Bool = {
    IsSuccReady() && IsOutReady()
  }


  val (guard_address_index, _) = Counter(isAddrFire(), GuardAddress.length)
  val is_address_buggy = WireInit(false.B)
  val guard_address_values = if (Debug) Some(VecInit(GuardAddress.map(_.U(xlen.W)))) else None
  val log_address_data = WireInit(0.U((dbgParams.packetLen).W))
  val log_address_packet = DebugPacket(gflag = 0.U, id = ID.U, code = DbgStoreAddress, iteration = guard_address_index, data = addr_R.data)(dbgParams)

  log_address_data := log_address_packet.packet

  if (Debug) {
    BoringUtils.addSource(log_address_data, s"data${ID}")
    BoringUtils.addSource(address_value_valid, s"valid${ID}")
    BoringUtils.addSink(address_value_ready, s"Buffer_ready${ID}")

    address_value_valid := isAddrFire()
  }

  val correctVal = RegNext(if (Debug) guard_address_values.get(guard_address_index) else 0.U)

  // Outgoing Address Req ->
  //here
  io.MemReq.bits.addr := Mux(is_address_buggy, correctVal, addr_R.data)
  io.MemReq.bits.data := data_R.data
  io.MemReq.bits.tag := RouteID.U
  io.MemReq.bits.taskID := data_R.taskID | addr_R.taskID | enable_R.taskID
  io.MemReq.bits.mask := "hFF".U
  io.MemReq.bits.iswrite := true.B
  io.MemReq.valid := false.B

  /*=============================================
  =            ACTIONS (possibly dangerous)     =
  =============================================*/

  switch(state) {
    is(s_idle) {
      when(enable_valid_R && address_value_ready) {
        when(data_valid_R && addr_valid_R) {
          when(enable_R.control && mem_req_fire) {
            io.MemReq.valid := true.B
            when(io.MemReq.ready) {
              state := s_RECEIVING

              /**
               * This is where we fire memory request
               */
              if (Debug) {
                when(addr_R.data =/= guard_address_values.get(guard_address_index)) {
                  log_address_packet.gFlag := 1.U
                  is_address_buggy := true.B

                  if (log) {
                    printf("[DEBUG] [" + module_name + "] [TID->%d] [STORE] " + node_name +
                      " Sent address value: %d, correct value: %d\n",
                      addr_R.taskID, addr_R.data, guard_address_values.get(guard_address_index))
                  }
                }
              }


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
      when(io.MemResp.valid) {
        ValidSucc()
        ValidOut()
        state := s_Done
      }
    }
    is(s_Done) {
      when(complete) {

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
          printf(p"[LOG] [${module_name}] [TID: ${enable_R.taskID}] [STORE] " +
            p"[Name: ${node_name}] [ID: ${ID}] [Pred: ${enable_R.control}] " +
            p"[Addr: 0x${Hexadecimal(addr_R.data)}] " +
            p"[Data: 0x${Hexadecimal(data_R.data)}] " +
            p"[Cycle: ${cycleCount}]\n")
        }
      }
    }
  }

}

