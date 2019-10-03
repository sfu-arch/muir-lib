package dandelion.node

import chisel3._
import chisel3.iotesters.{ChiselFlatSpec, Driver, OrderedDecoupledHWIOTester, PeekPokeTester}
import chisel3.Module
import chisel3.testers._
import chisel3.util._
import chisel3.util.experimental.BoringUtils
import org.scalatest.{FlatSpec, Matchers}
import dandelion.config._
import dandelion.interfaces._
import util._
import utility.UniformPrintfs


class ComputeNodeIO(NumOuts: Int, Debug: Boolean)
                   (implicit p: Parameters)
  extends HandShakingIONPS(NumOuts, Debug)(new DataBundle) {
  // LeftIO: Left input data for computation
  val LeftIO = Flipped(Decoupled(new DataBundle()))

  // RightIO: Right input data for computation
  val RightIO = Flipped(Decoupled(new DataBundle()))

  //p
  //val LogIO = Decoupled(new DataBundle())

  val DebugEnable = if (Debug) Some(Flipped(new Bool)) else None

  //v
  override def cloneType = new ComputeNodeIO(NumOuts, Debug).asInstanceOf[this.type]

}

class ComputeNode(NumOuts: Int, ID: Int, opCode: String)
                 (sign: Boolean, Debug: Boolean = false)
                 (implicit p: Parameters,
                  name: sourcecode.Name,
                  file: sourcecode.File)
  extends HandShakingNPS(NumOuts, ID, Debug)(new DataBundle())(p) {
  override lazy val io = IO(new ComputeNodeIO(NumOuts, Debug))

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

  /**
    * val debug = RegInit(0.U)
    * debug := io.DebugIO.get
    *
    * val debug = RegNext(io.DebugIO.get, init = 0.U)
    */

  //Output register
  val out_data_R = RegNext(Mux(enable_R.control, FU.io.out, 0.U), init = 0.U)
  val predicate = Mux(enable_valid_R, enable_R.control ,io.enable.bits.control)
  val taskID = Mux(enable_valid_R, enable_R.taskID ,io.enable.bits.taskID)



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

  //-------------------p
  val address = 512.U
  if (Debug){
   when(io.DebugEnable.get){
     dbg_counter.inc()
     CaptureLog(state, address)
//     CaptureLog( state , (dbg_counter.value << 2.U).asUInt())


   }
  }

  if(ID == 4 && Debug){
    val test_value = Wire(UInt(4.W))
    val test_value_valid = Wire(Bool())
    val test_value_ready = Wire(Bool())
    test_value := cycleCount
    test_value_valid := false.B
    test_value_ready := false.B
    BoringUtils.addSource(test_value, "Test_data")
    BoringUtils.addSource(test_value_valid, "Test_valid")
    BoringUtils.addSink(test_value_ready, "Test_ready")

    when(io.DebugEnable.get){
       test_value_valid := true.B
    }.otherwise{
      test_value_valid := false.B
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
        io.Out.foreach(_.bits := DataBundle(FU.io.out, taskID, predicate))
        io.Out.foreach(_.valid := true.B)
        ValidOut()
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
        left_valid_R := false.B
        right_valid_R := false.B

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

