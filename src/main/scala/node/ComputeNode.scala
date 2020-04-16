package dandelion.node

import chisel3._
import chisel3.util._
import chisel3.util.experimental.BoringUtils
import org.scalatest.{FlatSpec, Matchers}
import dandelion.config._
import chisel3.Module
import dandelion.interfaces._
import util._
import chipsalliance.rocketchip.config._
import dandelion.config._


class ComputeNodeIO(NumOuts: Int, Debug: Boolean, GuardVals: Seq[Int] = List())
                   (implicit p: Parameters)
  extends HandShakingIONPS(NumOuts, Debug)(new DataBundle) {
  val LeftIO = Flipped(Decoupled(new DataBundle()))
  val RightIO = Flipped(Decoupled(new DataBundle()))

  override def cloneType = new ComputeNodeIO(NumOuts, Debug).asInstanceOf[this.type]

}

class ComputeNode(NumOuts: Int, ID: Int, opCode: String)
                 (sign: Boolean, Debug: Boolean = false, GuardVals: Seq[Int] = List())
                 (implicit p: Parameters,
                  name: sourcecode.Name,
                  file: sourcecode.File)
  extends HandShakingNPS(NumOuts, ID, Debug)(new DataBundle())(p)
    with HasAccelShellParams
    with HasDebugCodes {
  override lazy val io = IO(new ComputeNodeIO(NumOuts, Debug, GuardVals))

  val dparam = dbgParams

  // Printf debugging
  val node_name = name.value
  val module_name = file.value.split("/").tail.last.split("\\.").head.capitalize

  override val printfSigil = "[" + module_name + "] " + node_name + ": " + ID + " "
  val (cycleCount, _) = Counter(true.B, 32 * 1024)


  val dbg_counter = Counter(1024)

  //val a = dbg_counter.value << 2.U

  /*===========================================*
   *            Registers                      *
   *===========================================*/
  // Left Input
  val left_R = RegInit(DataBundle.default)
  val left_valid_R = RegInit(false.B)

  // Right Input
  val right_R = RegInit(DataBundle.default)
  val right_valid_R = RegInit(false.B)

  //Instantiate ALU with selected code
  val FU = Module(new UALU(xlen, opCode, issign = sign))

  val s_IDLE :: s_COMPUTE :: Nil = Enum(2)
  val state = RegInit(s_IDLE)

  /**
   * Debug variables
   */

  def IsInputValid(): Bool = {
    right_valid_R && left_valid_R
  }

  def isInFire(): Bool = {
    enable_valid_R && IsInputValid() && enable_R.control && state === s_IDLE
  }

  val guard_values = if (Debug) Some(VecInit(GuardVals.map(_.U(xlen.W)))) else None

  val log_flag = WireInit(0.U(dbgParams.gLen.W))
  val log_id = WireInit(ID.U(dbgParams.idLen.W))
  val log_code = WireInit(DbgComputeData)
  val log_iteration = WireInit(0.U(dbgParams.iterLen.W)) // 10
  val log_data = WireInit(0.U((dbgParams.dataLen).W)) // 64 - 17
  val isBuggy = RegInit(false.B)


  //Output register
  val out_data_R = RegNext(Mux(enable_R.control, FU.io.out, 0.U), init = 0.U)
  val predicate = Mux(enable_valid_R, enable_R.control, io.enable.bits.control)
  val taskID = Mux(enable_valid_R, enable_R.taskID, io.enable.bits.taskID)

  //val DebugEnable = enable_R.control && enable_R.debug && enable_valid_R
  val DebugEnable = WireInit(true.B)


  /*===============================================*
   *            Latch inputs. Wire up output       *
   *===============================================*/

  FU.io.in1 := left_R.data
  FU.io.in2 := right_R.data

  io.LeftIO.ready := ~left_valid_R
  when(io.LeftIO.fire()) {
    left_R <> io.LeftIO.bits
    left_valid_R := true.B
  }

  io.RightIO.ready := ~right_valid_R
  when(io.RightIO.fire()) {
    right_R <> io.RightIO.bits
    right_valid_R := true.B
  }


  val log_value = WireInit(0.U(xlen.W))
  log_value := Cat(log_flag, log_id, log_iteration, log_code, log_data)

  val test_value_valid = WireInit(false.B)
  val test_value_ready = WireInit(true.B)

  if (Debug) {
    BoringUtils.addSource(log_value, s"data${ID}")
    BoringUtils.addSource(test_value_valid, s"valid${ID}")
    BoringUtils.addSink(test_value_ready, s"Buffer_ready${ID}")

    test_value_valid := enable_valid_R && !(state === s_COMPUTE)

  }

  val (guard_index, _) = Counter(isInFire(), GuardVals.length)
  log_data := out_data_R
  log_iteration := guard_index


  io.Out.foreach(_.bits := DataBundle(out_data_R, taskID, predicate))

  /*============================================*
   *            State Machine                   *
   *============================================*/
  switch(state) {
    is(s_IDLE) {
      when(enable_valid_R && left_valid_R && right_valid_R && test_value_ready) {
        /**
         * Debug logic: The output of FU is compared against Guard value
         * and if the value is not equal to expected value the correct value
         * will become available
         */
        if (Debug) {
          when(FU.io.out =/= guard_values.get(guard_index)) {
            isBuggy := true.B
            log_flag := 1.U
            io.Out.foreach(_.bits := DataBundle(guard_values.get(guard_index), taskID, predicate))

            if (log) {
              printf(p"[LOG] [DEBUG] [${module_name}] [TID: taskID] [COMPUTE] " +
                p"[${node_name}]  [Out:${FU.io.out} ] [Correct:${guard_values.get(guard_index)} ] [Cycle: ${cycleCount}]\n")
            }

          }.otherwise {
            io.Out.foreach(_.bits := DataBundle(FU.io.out, taskID, predicate))
          }
        }
        else {
          io.Out.foreach(_.bits := DataBundle(FU.io.out, taskID, predicate))
        }
        io.Out.foreach(_.valid := true.B)
        ValidOut()
        left_valid_R := false.B
        right_valid_R := false.B
        state := s_COMPUTE
        if (log) {
          printf(p"[LOG] [${module_name}] [TID: ${taskID}] [COMPUTE] [${node_name}] " +
            p"[Pred: ${enable_R.control}] " +
            p"[In(0): 0x${Hexadecimal(left_R.data)}] " +
            p"[In(1) 0x${Hexadecimal(right_R.data)}] " +
            p"[Out: 0x${Hexadecimal(FU.io.out)}] " +
            p"[OpCode: ${opCode}] " +
            p"[Cycle: ${cycleCount}]\n")
        }
      }
    }
    is(s_COMPUTE) {
      when(IsOutReady()) {
        // Reset data
        isBuggy := false.B
        out_data_R := 0.U

        //Reset state
        state := s_IDLE
        Reset()
      }
    }
  }

}

