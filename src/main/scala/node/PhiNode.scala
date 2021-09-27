package dandelion.node

import chisel3._
import chisel3.Module
import chipsalliance.rocketchip.config._
import dandelion.interfaces._
import util._
import utility.UniformPrintfs
import dandelion.config._
import chisel3.util.experimental.BoringUtils


class PhiNodeIO(NumInputs: Int, NumOuts: Int, Debug: Boolean = false)
               (implicit p: Parameters)
  extends HandShakingIONPS(NumOuts, Debug)(new DataBundle) {

  // Vector input
  val InData = Vec(NumInputs, Flipped(Decoupled(new DataBundle)))

  // Predicate mask comming from the basic block
  val Mask = Flipped(Decoupled(UInt(NumInputs.W)))

  override def cloneType = new PhiNodeIO(NumInputs, NumOuts, Debug).asInstanceOf[this.type]
}

abstract class PhiFastNodeIO(val NumInputs: Int = 2, val NumOutputs: Int = 1, val ID: Int, Debug: Boolean = false, GuardVal: Int = 0)
                            (implicit val p: Parameters)
  extends MultiIOModule with HasAccelParams with UniformPrintfs {

  val io = IO(new Bundle {
    //Control signal
    val enable = Flipped(Decoupled(new ControlBundle))

    // Vector input
    val InData = Vec(NumInputs, Flipped(Decoupled(new DataBundle)))

    // Predicate mask comming from the basic block
    val Mask = Flipped(Decoupled(UInt(NumInputs.W)))

    //Output
    val Out = Vec(NumOutputs, Decoupled(new DataBundle))
  })
}


@deprecated("Use PhiFastNode instead", "1.0")
class PhiNode(NumInputs: Int,
              NumOuts: Int,
              ID: Int, Debug: Boolean = false)
             (implicit p: Parameters,
              name: sourcecode.Name,
              file: sourcecode.File)
  extends HandShakingNPS(NumOuts, ID, Debug)(new DataBundle)(p) {
  override lazy val io = IO(new PhiNodeIO(NumInputs, NumOuts, Debug))
  // Printf debugging
  val node_name = name.value
  val module_name = file.value.split("/").tail.last.split("\\.").head.capitalize

  override val printfSigil = "[" + module_name + "] " + node_name + ": " + ID + " "
  val (cycleCount, _) = Counter(true.B, 32 * 1024)

  /*===========================================*
   *            Registers                      *
   *===========================================*/
  // Data Inputs
  val in_data_R = RegInit(VecInit(Seq.fill(NumInputs)(DataBundle.default)))
  val in_data_valid_R = RegInit(VecInit(Seq.fill(NumInputs)(false.B)))

  val out_data_R = RegInit(DataBundle.default)

  // Mask Input
  val mask_R = RegInit(0.U(NumInputs.W))
  val mask_valid_R = RegInit(false.B)


  // Output register
  //val data_R = RegInit(0.U(xlen.W))

  val s_IDLE :: s_COMPUTE :: Nil = Enum(2)
  val state = RegInit(s_IDLE)

  /*==========================================*
   *           Predicate Evaluation           *
   *==========================================*/


  /*===============================================*
   *            Latch inputs. Wire up output       *
   *===============================================*/

  //Instantiating a MUX
  val sel = OHToUInt(mask_R)

  //wire up mask
  io.Mask.ready := ~mask_valid_R
  when(io.Mask.fire()) {
    mask_R := io.Mask.bits
    mask_valid_R := true.B
  }

  //Wire up inputs
  for (i <- 0 until NumInputs) {
    io.InData(i).ready := ~in_data_valid_R(i)
    when(io.InData(i).fire()) {
      in_data_R(i) <> io.InData(i).bits
      in_data_valid_R(i) := true.B
    }
  }

  // Wire up Outputs
  for (i <- 0 until NumOuts) {
    io.Out(i).bits <> out_data_R
  }

  /*============================================*
   *            STATE MACHINE                   *
   *============================================*/
  switch(state) {
    is(s_IDLE) {
      when((enable_valid_R) && mask_valid_R && in_data_valid_R(sel)) {
        state := s_COMPUTE
        when(enable_R.control) {
          out_data_R := in_data_R(sel)
        }
        ValidOut()
      }
    }
    is(s_COMPUTE) {
      when(IsOutReady()) {
        mask_R := 0.U
        mask_valid_R := false.B

        in_data_valid_R := VecInit(Seq.fill(NumInputs)(false.B))
        out_data_R.predicate := false.B

        //Reset state
        //Reset output
        Reset()

        //Print output
        if (log) {
          printf("[LOG] " + "[" + module_name + "] [TID->%d] "
            + node_name + ": Output fired @ %d, Value: %d\n", out_data_R.taskID, cycleCount, out_data_R.data)
        }


      }
    }
  }

  def isDebug(): Boolean = {
    Debug
  }
}


/**
 * A fast version of phi node.
 * The ouput is fired as soon as all the inputs
 * are available.
 *
 * @note: These are design assumptions:
 *        1) At each instance, there is only one input signal which is predicated
 *        there is only one exception.
 *        2) The only exception is the case which one of the input is constant
 *        and because of our design constant is always fired as a first node
 *        and it has only one output. Therefore, whenever we want to restart
 *        the states, we reste all the registers, and by this way we make sure
 *        that nothing is latched.
 *        3) If Phi node itself is not predicated, we restart all the registers and
 *        fire the output with zero predication.
 *        4) There is a bug in LLVM code generation and we need sometime support
 *        revers masking for phi ndoes the issue will be solved in the next version
 *        but for now using Res argument we control direction of mask bits
 * @param NumInputs
 * @param NumOutputs
 * @param ID
 * @param p
 * @param name
 * @param file
 */
class PhiFastNode(NumInputs: Int = 2, NumOutputs: Int = 1, ID: Int, Res: Boolean = false, Induction: Boolean = false,
                  Debug: Boolean = false, val GuardVals: Seq[Int] = List())
                 (implicit p: Parameters,
                  name: sourcecode.Name,
                  file: sourcecode.File)
  extends PhiFastNodeIO(NumInputs, NumOutputs, ID)(p)
    with HasAccelShellParams
    with HasDebugCodes {

  // Printf debugging
  override val printfSigil = "Node (PHIFast) ID: " + ID + " "
  val (cycleCount, _) = Counter(true.B, 32 * 1024)


  val dparam = dbgParams

  // Printf debugging
  val node_name = name.value
  val module_name = file.value.split("/").tail.last.split("\\.").head.capitalize

  // Data Inputs
  val in_data_R = RegInit(VecInit(Seq.fill(NumInputs)(DataBundle.default)))
  val in_data_valid_R = RegInit(VecInit(Seq.fill(NumInputs)(false.B)))

  // Enable Inputs
  val enable_R = RegInit(ControlBundle.default)
  val enable_valid_R = RegInit(false.B)

  // Mask Input
  val mask_R = RegInit(0.U(NumInputs.W))
  val mask_valid_R = RegInit(false.B)

  //Output register
  val s_idle :: s_fire :: s_not_predicated :: Nil = Enum(3)
  val state = RegInit(s_idle)


  //Debug buffer
  val s_init :: s_filling :: s_full :: Nil = Enum(3)
  val buffer_state = RegInit(s_init)

  // Latching output data
  val out_valid_R = Seq.fill(NumOutputs)(RegInit(false.B))

  val fire_R = Seq.fill(NumOutputs)(RegInit(false.B))

  /**
   * Debug variables
   */
  val guard_values = if (Debug) Some(VecInit(GuardVals.map(_.U(xlen.W)))) else None

  val log_flag = WireInit(0.U(dbgParams.gLen.W))
  val log_id = WireInit(ID.U(dbgParams.idLen.W))
  val log_code = WireInit(DbgPhiData)
  val log_mask = WireInit(0.U(NumInputs.W))
  val log_iteration = WireInit(0.U(dbgParams.iterLen.W)) // 10
  val log_data = WireInit(0.U((dbgParams.dataLen - NumInputs).W)) // 64 - 17
  val isBuggy = RegInit(false.B)


  // Latching Mask value
  io.Mask.ready := ~mask_valid_R
  when(io.Mask.fire()) {
    mask_R := io.Mask.bits
    mask_valid_R := true.B
  }

  // Latching enable value
  io.enable.ready := ~enable_valid_R
  when(io.enable.fire()) {
    enable_R <> io.enable.bits
    enable_valid_R := true.B
  }


  for (i <- 0 until NumInputs) {
    io.InData(i).ready := ~in_data_valid_R(i)
    when(io.InData(i).fire) {
      in_data_R(i) <> io.InData(i).bits
      in_data_valid_R(i) := true.B
    }
  }

  val sel =
    if (Res == false) {
      OHToUInt(mask_R)
    }
    else {
      OHToUInt(Reverse(mask_R))
    }

  val select_input = in_data_R(sel).data
  val select_predicate = in_data_R(sel).predicate

  val enable_input = enable_R.control

  val task_input = (io.enable.bits.taskID | enable_R.taskID)

  for (i <- 0 until NumOutputs) {
    when(io.Out(i).fire) {
      fire_R(i) := true.B
      out_valid_R(i) := false.B
    }
  }

  //Getting mask for fired nodes
  val fire_mask = (fire_R zip io.Out.map(_.fire)).map { case (a, b) => a | b }

  def IsInputValid(): Bool = {
    in_data_valid_R.reduce(_ & _)
  }

  def isInFire(): Bool = {
    enable_valid_R && IsInputValid() && enable_R.control && state === s_idle
  }

  //***************************BORE Connection*************************************

  val log_value = WireInit(0.U(xlen.W))
  log_value := Cat(log_flag, log_id, log_iteration, log_code, log_mask, log_data)


  val test_value_valid = WireInit(false.B)
  val test_value_ready = WireInit(true.B)

  //Input log data
  val in_log_value = WireInit(0.U(xlen.W))
  val in_log_value_valid = WireInit(true.B)
  val in_log_value_ready = WireInit(false.B)
//  val in_log_value_start = WireInit(false.B)


  if (Debug) {
    //Input log data
    BoringUtils.addSink(in_log_value, s"in_log_data${ID}")
    BoringUtils.addSink(in_log_value_valid, s"in_log_Buffer_valid${ID}")

    BoringUtils.addSource(in_log_value_ready, s"in_log_Buffer_ready${ID}")
//    BoringUtils.addSource(in_log_value_start, s"in_log_Buffer_start${ID}")

    BoringUtils.addSource(log_value, s"data${ID}")
    BoringUtils.addSource(test_value_valid, s"valid${ID}")
    BoringUtils.addSink(test_value_ready, s"Buffer_ready${ID}")

    test_value_valid := enable_valid_R && !(state === s_fire)
  }


  val (guard_index, _) = Counter(isInFire(), GuardVals.length)
  log_data := in_data_R(sel).data
  log_iteration := guard_index
  log_mask := sel

  //*******************************************************************


  for (i <- 0 until NumOutputs) {
    //TODO: enable for comapring
    //io.Out(i).bits := Mux(isBuggy, in_log_value, in_data_R(sel))
    io.Out(i).bits := in_data_R(sel)
    io.Out(i).valid := out_valid_R(i)
  }


  switch(state) {
    is(s_idle) {
      when(enable_valid_R && IsInputValid()) {
        //Make outputs valid
        out_valid_R.foreach(_ := true.B)
        in_log_value_ready := true.B
        when(enable_R.control && in_log_value_valid) {
          //*********************************
          state := s_fire
          //********************************
          //Print output
          if (Debug) {
            //TODO: Make the check conditional
            when(in_data_R(sel).data =/= in_log_value) {
              isBuggy := true.B
              log_flag := 1.U

              if (log) {
                printf("[DEBUG] [" + module_name + "] [TID->%d] [PHI] " + node_name +
                  " Produced value: %d, correct value: %d\n",
                  in_data_R(sel).taskID, in_data_R(sel).data, in_log_value)
              }
            }
          }

          //*****************************************************************
          if (log) {
            printf(p"[LOG] [${module_name}] [TID: ${io.InData(sel).bits.taskID}] [PHI] " +
              p"[${node_name}] [Pred: ${enable_R.control}] [Out: ${in_data_R(sel).data}] [Cycle: ${cycleCount}]\n")
          }
        }.otherwise {
          state := s_not_predicated
          //Print output
          if (log) {
            printf(p"[LOG] [${module_name}] [TID: ${io.InData(sel).bits.taskID}] [PHI] " +
              p"[${node_name}] [Pred: ${enable_R.control}] [Out: ${in_data_R(sel).data}] [Cycle: ${cycleCount}]\n")
          }
        }
      }
    }
    is(s_fire) {
      when(fire_mask.reduce(_ & _)) {

        /**
         * @note: In this case whenever all the GEP is fired we
         *        restart all the latched values. But it may be cases
         *        that because of pipelining we have latched an interation ahead
         *        and if we may reset the latches values we lost the value.
         *        I'm not sure when this case can happen!
         */
        in_data_R.foreach(_ := DataBundle.default)
        in_data_valid_R.foreach(_ := false.B)

        mask_R := 0.U
        mask_valid_R := false.B

        enable_R := ControlBundle.default
        enable_valid_R := false.B

        fire_R.foreach(_ := false.B)

        isBuggy := false.B

        state := s_idle

      }

    }
    is(s_not_predicated) {
      io.Out.map(_.bits) foreach (_.data := 0.U)
      io.Out.map(_.bits) foreach (_.predicate := false.B)
      io.Out.map(_.bits) foreach (_.taskID := task_input)

      when(fire_mask.reduce(_ & _)) {
        in_data_R.foreach(_ := DataBundle.default)
        in_data_valid_R.foreach(_ := false.B)

        mask_R := 0.U
        mask_valid_R := false.B

        enable_R := ControlBundle.default
        enable_valid_R := false.B

        fire_R.foreach(_ := false.B)

        state := s_idle

      }
    }
  }

}

