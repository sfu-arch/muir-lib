package dandelion.node

import chisel3._
import chisel3.util._
import chipsalliance.rocketchip.config._
import chisel3.util.experimental.BoringUtils
import dandelion.config.{HasAccelShellParams, HasDebugCodes}
import dandelion.interfaces._

class LoadCacheIO(NumPredOps: Int,
                  NumSuccOps: Int,
                  NumOuts: Int,
                  Debug: Boolean = false)(implicit p: Parameters)
  extends HandShakingIOPS(NumPredOps, NumSuccOps, NumOuts, Debug)(new DataBundle) {

  val GepAddr = Flipped(Decoupled(new DataBundle))
  val MemReq = Decoupled(new MemReq)
  val MemResp = Flipped(Valid(new MemResp))

  override def cloneType = new LoadCacheIO(NumPredOps, NumSuccOps, NumOuts, Debug).asInstanceOf[this.type]
}

/**
 * @brief Load Node. Implements load operations
 * @details [load operations can either reference values in a scratchpad or cache]
 * @param NumPredOps [Number of predicate memory operations]
 */
class UnTypLoadCache(NumPredOps: Int,
                     NumSuccOps: Int,
                     NumOuts: Int,
                     ID: Int,
                     RouteID: Int,
                     Debug: Boolean = false,
                     GuardAddress: Seq[Int] = List(),
                     GuardData: Seq[Int] = List())
                    (implicit p: Parameters,
                     name: sourcecode.Name,
                     file: sourcecode.File)
  extends HandShaking(NumPredOps, NumSuccOps, NumOuts, ID, Debug)(new DataBundle)(p)
    with HasAccelShellParams
    with HasDebugCodes {

  override lazy val io = IO(new LoadCacheIO(NumPredOps, NumSuccOps, NumOuts, Debug))

  // Printf debugging
  val node_name = name.value
  val module_name = file.value.split("/").tail.last.split("\\.").head.capitalize
  val (cycleCount, _) = Counter(true.B, 32 * 1024)
  override val printfSigil = "[" + module_name + "] " + node_name + ": " + ID + " "


  /**
   * Registers
   */
  // OP Inputs
  val addr_R = RegInit(DataBundle.default)
  val addr_valid_R = RegInit(false.B)

  // Memory Response
  val data_R = RegInit(DataBundle.default)
  val data_valid_R = RegInit(false.B)

  // State machine
  val s_idle :: s_RECEIVING :: s_Done :: Nil = Enum(3)
  val state = RegInit(s_idle)


  /*================================================
  =            Latch inputs. Wire up output            =
  ================================================*/

  //Initialization READY-VALIDs for GepAddr and Predecessor memory ops
  io.GepAddr.ready := ~addr_valid_R
  when(io.GepAddr.fire()) {
    addr_R := io.GepAddr.bits
    addr_valid_R := true.B
  }

  /**
   * Debug signals
   */
  val address_value_valid = WireInit(false.B)
  val address_value_ready = WireInit(true.B)

  val data_value_valid = WireInit(false.B)
  val data_value_ready = WireInit(true.B)

  val arb = Module(new Arbiter(UInt(dbgParams.packetLen.W), 2))
  val data_queue = Module(new Queue(UInt(dbgParams.packetLen.W), entries = 20))


  def isAddrFire(): Bool = {
    enable_valid_R && addr_valid_R && enable_R.control && state === s_idle && io.MemReq.ready && address_value_ready
  }

  def complete(): Bool = {
    IsSuccReady() && IsOutReady()
  }

  def isRespValid(): Bool = {
    state === s_Done && complete() && data_value_ready
  }


  val predicate = enable_R.control
  val mem_req_fire = addr_valid_R && IsPredValid()


  val (guard_address_index, _) = Counter(isAddrFire(), GuardAddress.length)
  val (guard_data_index, _) = Counter(isRespValid(), GuardData.length)

  val is_data_buggy = RegInit(false.B)

  val guard_address_values = if (Debug) Some(VecInit(GuardAddress.map(_.U(xlen.W)))) else None
  val guard_data_values = if (Debug) Some(VecInit(GuardData.map(_.U(xlen.W)))) else None

  val log_address_data = WireInit(0.U((dbgParams.packetLen).W))
  val log_data_data = WireInit(0.U((dbgParams.packetLen).W))

  val log_address_packet = DebugPacket(gflag = 0.U, id = ID.U, code = DbgLoadAddress, iteration = guard_address_index, data = addr_R.data)(dbgParams)
  val log_data_packet = DebugPacket(gflag = is_data_buggy, id = ID.U, code = DbgLoadData, iteration = guard_data_index, data = data_R.data)(dbgParams)


  log_address_data := log_address_packet.packet()
  log_data_data := log_data_packet.packet()


  arb.io.in(0).bits := log_address_data
  arb.io.in(0).valid := address_value_valid
  address_value_ready := arb.io.in(0).ready

  arb.io.in(1).bits := log_data_data
  arb.io.in(1).valid := data_value_valid
  data_value_ready := arb.io.in(1).ready

  val bore_queue_ready = WireInit(false.B)

  data_queue.io.enq <> arb.io.out
  data_queue.io.deq.ready := bore_queue_ready

  if (Debug) {
    BoringUtils.addSource(data_queue.io.deq.bits, s"data${ID}")
    BoringUtils.addSource(data_queue.io.deq.valid, s"valid${ID}")
    BoringUtils.addSink(bore_queue_ready, s"Buffer_ready${ID}")

    address_value_valid := isAddrFire()
    data_value_valid := isRespValid()
  }

  val correct_address_val = RegNext(if (Debug) DataBundle(guard_address_values.get(guard_address_index)) else DataBundle.default)
  val correct_data_val = RegNext(if (Debug) DataBundle(guard_data_values.get(guard_data_index)) else DataBundle.default)

  // Wire up Outputs
  for (i <- 0 until NumOuts) {
    io.Out(i).bits := Mux(is_data_buggy, correct_data_val, data_R)
  }

  // Initilizing the MemRequest bus
  io.MemReq.valid := false.B
  io.MemReq.bits.data := 0.U
  io.MemReq.bits.addr := addr_R.data
  io.MemReq.bits.tag := RouteID.U
  io.MemReq.bits.taskID := addr_R.taskID
  io.MemReq.bits.mask := 0.U
  io.MemReq.bits.iswrite := false.B

  // Connect successors outputs to the enable status
  when(io.enable.fire()) {
    succ_bundle_R.foreach(_ := io.enable.bits)
  }


  /*=============================================
  =            ACTIONS (possibly dangerous)     =
  =============================================*/


  switch(state) {
    is(s_idle) {
      when(enable_valid_R && mem_req_fire && address_value_ready) {
        when(enable_R.control && predicate) {
          io.MemReq.valid := true.B
          when(io.MemReq.ready) {
            state := s_RECEIVING
            /**
             * This is where we fire memory request
             */
            if (Debug) {
              when(addr_R.data =/= guard_address_values.get(guard_address_index)) {
                log_address_packet.gFlag := 1.U
                //addr_R.data := guard_address_values.get(guard_address_index)
                //io.MemReq.bits.addr := guard_address_values.get(guard_address_index)

                if (log) {
                  printf("[DEBUG] [" + module_name + "] [TID->%d] [LOAD] " + node_name +
                    " Sent address value: %d, correct value: %d\n",
                    addr_R.taskID, addr_R.data, guard_address_values.get(guard_address_index))
                }
              }
            }
          }
        }.otherwise {
          data_R.predicate := false.B
          ValidSucc()
          ValidOut()
          state := s_Done
        }
      }
    }
    is(s_RECEIVING) {
      when(io.MemResp.valid) {
        data_R.data := io.MemResp.bits.data
        data_R.predicate := true.B

        ValidSucc()
        ValidOut()
        // Completion state.
        state := s_Done

        if (Debug) {
          when(io.MemResp.bits.data =/= guard_data_values.get(guard_data_index)) {
            log_data_packet.gFlag := 1.U
            data_R.data := guard_address_values.get(guard_address_index)

            if (log) {
              printf("[DEBUG] [" + module_name + "] [TID->%d] [PHI] " + node_name +
                " Received data value: %d, correct value: %d\n",
                data_R.taskID, data_R.data, guard_data_values.get(guard_data_index))
            }
          }
        }


      }
    }
    is(s_Done) {
      when(complete && data_value_ready) {
        // Clear all the valid states.
        addr_R := DataBundle.default
        addr_valid_R := false.B
        // Reset data
        data_R := DataBundle.default
        data_valid_R := false.B
        // Reset state.
        Reset()
        // Reset state.
        state := s_idle
        if (log) {
          printf(p"[LOG] [${module_name}] [TID: ${enable_R.taskID}] [LOAD] " +
            p"[${node_name}] [Pred: ${enable_R.control}] " +
            p"[Addr: 0x${Hexadecimal(addr_R.data)}] " +
            p"[Data: 0x${Hexadecimal(data_R.data)}] " +
            p"[Cycle: ${cycleCount}]\n")
        }
      }
    }
  }
}
