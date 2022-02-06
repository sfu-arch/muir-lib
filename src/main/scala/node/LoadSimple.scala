package dandelion.node

import chisel3._
import chisel3.util._
import chipsalliance.rocketchip.config._
import chisel3.util.experimental.BoringUtils
import dandelion.interfaces._
import utility.Constants._

class LoadIO(NumPredOps: Int,
             NumSuccOps: Int,
             NumOuts: Int,
             Debug : Boolean =false)(implicit p: Parameters)
  extends HandShakingIOPS(NumPredOps, NumSuccOps, NumOuts, Debug)(new DataBundle) {
  // GepAddr: The calculated address comming from GEP node
  val GepAddr = Flipped(Decoupled(new DataBundle))
  // Memory request
  val memReq = Decoupled(new ReadReq())
  // Memory response.
  val memResp = Flipped(Valid(new ReadResp()))
}

/**
  * @brief Load Node. Implements load operations
  * @details [load operations can either reference values in a scratchpad or cache]
  * @param NumPredOps [Number of predicate memory operations]
  */
class UnTypLoad(NumPredOps: Int,
                NumSuccOps: Int,
                NumOuts: Int,
                Typ: UInt = MT_D,
                ID: Int,
                RouteID: Int
               , Debug : Boolean =false
               , GuardVal : Int = 0)
               (implicit p: Parameters,
                name: sourcecode.Name,
                file: sourcecode.File)
  extends HandShaking(NumPredOps, NumSuccOps, NumOuts, ID, Debug)(new DataBundle)(p) {

  override lazy val io = IO(new LoadIO(NumPredOps, NumSuccOps, NumOuts, Debug))
  // Printf debugging
  val node_name = name.value
  val module_name = file.value.split("/").tail.last.split("\\.").head.capitalize
  val (cycleCount, _) = Counter(true.B, 32 * 1024)
  override val printfSigil = "[" + module_name + "] " + node_name + ": " + ID + " "


  /*=============================================
  =            Registers                        =
  =============================================*/
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
  when(io.GepAddr.fire) {
    addr_R := io.GepAddr.bits
    addr_valid_R := true.B
  }

  //**********************************************************************
  var log_id = WireInit(ID.U((6).W))
  var GuardFlag = WireInit(0.U(1.W))

  var log_out_reg = RegInit(0.U((xlen-7).W))
  val writeFinish = RegInit(false.B)
  //log_id := ID.U
  //test_value := Cat(GuardFlag,log_id, log_out)
  val log_value = WireInit(0.U(xlen.W))
  log_value := Cat(GuardFlag, log_id, log_out_reg)


  //test_value := log_out
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




  /*============================================
  =            Predicate Evaluation            =
  ============================================*/

  val complete = IsSuccReady() && IsOutReady()
  val predicate = addr_R.predicate && enable_R.control
  val mem_req_fire = addr_valid_R && IsPredValid()


  // Wire up Outputs
  for (i <- 0 until NumOuts) {
    io.Out(i).bits := data_R
    io.Out(i).bits.predicate := predicate
    io.Out(i).bits.taskID := addr_R.taskID | enable_R.taskID
  }

  io.memReq.valid := false.B
  io.memReq.bits.address := addr_R.data
  io.memReq.bits.Typ := Typ
  io.memReq.bits.RouteID := RouteID.U
  io.memReq.bits.taskID := addr_R.taskID

  // Connect successors outputs to the enable status
  when(io.enable.fire) {
    succ_bundle_R.foreach(_ := io.enable.bits)
  }




  /*=============================================
  =            ACTIONS (possibly dangerous)     =
  =============================================*/


  switch(state) {
    is(s_idle) {
      when(enable_valid_R && mem_req_fire) {
        when(enable_R.control && predicate) {
          io.memReq.valid := true.B
          when(io.memReq.ready) {
            state := s_RECEIVING
          }
        }.otherwise {
          data_R.predicate := false.B
          ValidSucc()
          ValidOut()
        }
      }
    }
    is(s_RECEIVING) {
      when(io.memResp.valid) {

        // Set data output registers
        data_R.data := io.memResp.bits.data

        if (Debug) {
          when(data_R.data =/= GuardVal.U) {
            GuardFlag := 1.U
            log_out_reg :=  data_R.data
            data_R.data := GuardVal.U

          }.otherwise {
            GuardFlag := 0.U
            log_out_reg :=  data_R.data
          }
        }


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
        // addr_R := DataBundle.default
        addr_valid_R := false.B
        // Reset data
        // data_R := DataBundle.default
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
