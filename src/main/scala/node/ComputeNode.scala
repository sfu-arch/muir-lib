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


class ComputeNodeIO(NumOuts: Int, Debug: Boolean, GuardVal: Int = 0)
                   (implicit p: Parameters)
  extends HandShakingIONPS(NumOuts, Debug)(new DataBundle) {
  // LeftIO: Left input data for computation
  val LeftIO = Flipped(Decoupled(new DataBundle()))

  // RightIO: Right input data for computation
  val RightIO = Flipped(Decoupled(new DataBundle()))

  override def cloneType = new ComputeNodeIO(NumOuts, Debug).asInstanceOf[this.type]

}

class ComputeNode(NumOuts: Int, ID: Int, opCode: String)
                 (sign: Boolean, Debug: Boolean = false, GuardVal: Int = 0)
                 (implicit p: Parameters,
                  name: sourcecode.Name,
                  file: sourcecode.File)
  extends HandShakingNPS(NumOuts, ID, Debug)(new DataBundle())(p) {
  override lazy val io = IO(new ComputeNodeIO(NumOuts, Debug, GuardVal))

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
  val FU = Module(new UALU(xlen, opCode))

  val s_IDLE :: s_COMPUTE :: Nil = Enum(2)
  val state = RegInit(s_IDLE)

  val GuardVal_reg = RegInit(GuardVal.U)
  /**
   * val debug = RegInit(0.U)
   * debug := io.DebugIO.get
   *
   * val debug = RegNext(io.DebugIO.get, init = 0.U)
   */

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

  /*hs
  val address = 512.U
  if (Debug){
   when(io.DebugEnable.get){
     dbg_counter.inc()
     //CaptureLog(state, address)
    CaptureLog( state , (dbg_counter.value << 2.U).asUInt())


   }
  }

  hs*/
  //var test_value = Wire(UInt((xlen).W))
  var log_id = WireInit(ID.U((4).W))
  //var log_out = WireInit(0.U((xlen - 5).W))
  var GuardFlag = WireInit(0.U(1.W))
  var log_out_reg = RegInit(0.U((xlen - 5).W))
  val writeFinish = RegInit(false.B)
  //log_id := ID.U
  //test_value := Cat(GuardFlag,log_id, log_out)
  val test_value = WireInit(0.U(xlen.W))
  test_value := Cat(GuardFlag, log_id, log_out_reg)

  //  val test_value = Cat("b1111".U, Cat("b0011".U, log_out))
  //test_value := log_out
  if (Debug) {
    val test_value_valid = Wire(Bool())
    val test_value_ready = Wire(Bool())
    val test_value_valid_r = RegInit(false.B)
    test_value_valid := test_value_valid_r
    test_value_ready := false.B
    BoringUtils.addSource(test_value, "data" + ID)
    BoringUtils.addSource(test_value_valid, "valid" + ID)
    BoringUtils.addSink(test_value_ready, "ready" + ID)


    val writefinishready = Wire(Bool())
    writefinishready := false.B
    BoringUtils.addSource(writeFinish, "writefinish" + ID)



    when(enable_valid_R && left_valid_R && right_valid_R) {
      test_value_valid_r := true.B
    }
    when(state === s_COMPUTE){
      test_value_valid_r := false.B
    }

  }



  //------------------v
  // Wire up Outputs
  // The taskID's should be identical except in the case
  // when one input is tied to a constant.  In that case
  // the taskID will be zero.  Logical OR'ing the IDs
  // Should produce a valid ID in either case regardless of
  // which input is constant.
  io.Out.foreach(_.bits := DataBundle(out_data_R, taskID, predicate))

  /*============================================*
   *            State Machine                   *
   *============================================*/
  switch(state) {
    is(s_IDLE) {
      when(enable_valid_R && left_valid_R && right_valid_R) {
        //********************************************************************************
        if (Debug) {
          when(FU.io.out =/= GuardVal.U) {
            GuardFlag := 1.U
            io.Out.foreach(_.bits := DataBundle(GuardVal.U, taskID, predicate))
            //io.Out.foreach(_.bits := DataBundle(FU.io.out, taskID, predicate))
            log_out_reg := FU.io.out.asUInt()

          }.otherwise {
            GuardFlag := 0.U
            io.Out.foreach(_.bits := DataBundle(FU.io.out, taskID, predicate))
            log_out_reg := FU.io.out.asUInt()
          }
        }
        else {
          io.Out.foreach(_.bits := DataBundle(FU.io.out, taskID, predicate))
          log_out_reg := FU.io.out.asUInt()
        }
        io.Out.foreach(_.valid := true.B)
        ValidOut()
        left_valid_R := false.B
        right_valid_R := false.B
        state := s_COMPUTE
        if (log) {
          printf("[LOG] " + "[" + module_name + "] " + "[TID->%d] [COMPUTE] " +
            node_name + ": Output fired @ %d, Value: %d (%d + %d)\n", taskID, cycleCount, FU.io.out, left_R.data, right_R.data)
        }
      }
    }
    is(s_COMPUTE) {
      when(IsOutReady()) {
        // Reset data
        writeFinish := true.B
        out_data_R := 0.U

        //Reset state
        state := s_IDLE
        Reset()


      }
    }
  }

  def isDebug(): Boolean = {
    Debug
  }

}

