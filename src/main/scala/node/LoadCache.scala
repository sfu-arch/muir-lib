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
                     GuardVals: Seq[Int] = List())
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

  /*============================================
  =            Predicate Evaluation            =
  ============================================*/

  def isAddrFire(): Bool = {
    enable_valid_R && addr_valid_R && enable_R.control && state === s_idle && io.MemReq.ready
  }

  def complete(): Bool = {
    IsSuccReady() && IsOutReady()
  }

  val predicate = addr_R.predicate && enable_R.control
  val mem_req_fire = addr_valid_R && IsPredValid()


  //**********************************************************************

  val (guard_index, _) = Counter(isAddrFire(), GuardVals.length)

  /**
   * Debug variables
   */
  val guard_values = if (Debug) Some(VecInit(GuardVals.map(_.U(xlen.W)))) else None
  val log_data = WireInit(0.U((dbgParams.packetLen).W))

  val log_address_packet = DebugPacket(gflag = 0.U, id = ID.U, code = DbgLoadAddress, iteration = guard_index, data = addr_R.data)(dbgParams)
  //  val log_data_packet = DebugPacket(gflag = 0.U, id = ID.U, code = DbgLoadData, iteration = 0.U, data = data_R.data)(dbgParams)

  val isBuggy = RegInit(false.B)

  log_data := log_address_packet.packet()


  if (Debug) {
    val test_value_valid = Wire(Bool())
    val test_value_ready = Wire(Bool())
    val test_value_valid_w = WireInit(false.B)
    test_value_valid := test_value_valid_w
    test_value_ready := false.B
    BoringUtils.addSource(log_data, "data" + ID)
    BoringUtils.addSource(test_value_valid, "valid" + ID)
    BoringUtils.addSink(test_value_ready, "ready" + ID)


    when(enable_valid_R && enable_R.control && mem_req_fire && state === s_idle) {
      test_value_valid_w := true.B
    }.otherwise {
      test_value_valid_w := false.B
    }

  }

  val correct_address_val = RegNext(if (Debug) DataBundle(guard_values.get(guard_index)) else DataBundle.default)

  // Wire up Outputs
  for (i <- 0 until NumOuts) {
    io.Out(i).bits := Mux(isBuggy, correct_address_val, data_R)
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
      when(enable_valid_R && mem_req_fire) {
        when(enable_R.control && predicate) {
          io.MemReq.valid := true.B
          when(io.MemReq.ready) {
            state := s_RECEIVING

            /**
             * This is where we fire memory request
             */
            if (Debug) {
              when(addr_R.data =/= guard_values.get(guard_index)) {
                log_address_packet.gFlag := 1.U
                data_R.data := guard_values.get(guard_index)

                if (log) {
                  printf("[DEBUG] [" + module_name + "] [TID->%d] [PHI] " + node_name +
                    " Produced value: %d, correct value: %d\n",
                    addr_R.taskID, addr_R.data, guard_values.get(guard_index))
                }
              }
            }


          }
        }.otherwise {
          data_R.predicate := false.B
          ValidSucc()
          ValidOut()
          // Completion state.
        }
      }
    }
    is(s_RECEIVING) {
      when(io.MemResp.valid) {

        // Set data output registers
        data_R.data := io.MemResp.bits.data

        //        if (Debug) {
        //          when(data_R.data =/= GuardVal.U) {
        //            GuardFlag := 1.U
        //            log_out_reg := data_R.data
        //            data_R.data := GuardVal.U
        //
        //          }.otherwise {
        //            GuardFlag := 0.U
        //            log_out_reg := data_R.data
        //          }
        //        }
        //

        data_R.predicate := true.B

        ValidSucc()
        ValidOut()
        // Completion state.
        state := s_Done

      }
    }
    is(s_Done) {
      when(complete) {
        // Clear all the valid states.
        // Reset address
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
          printf("[LOG] " + "[" + module_name + "] [TID->%d] [LOAD] " + node_name + ": Output fired @ %d, Address:%d, Value: %d\n",
            enable_R.taskID, cycleCount, addr_R.data, data_R.data)
        }
      }
    }
  }
}
