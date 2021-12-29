package dandelion.node

import chisel3._
import chisel3.iotesters.{ChiselFlatSpec, Driver, OrderedDecoupledHWIOTester, PeekPokeTester}
import chisel3.Module
import chisel3.testers._
import chisel3.util._
import org.scalatest.{FlatSpec, Matchers}
import chipsalliance.rocketchip.config._
import dandelion.config._
import dandelion.interfaces._
import muxes._
import util._
import utility.UniformPrintfs

class AllocaNodeIO(NumOuts: Int, Debug: Boolean)(implicit p: Parameters)
  extends HandShakingIONPS(NumOuts, Debug)(new DataBundle) {
  /**
   * @note requested size for address
   */
  val allocaInputIO = Flipped(Decoupled(new AllocaIO()))

  /**
   * @note Alloca interface to talk with stack
   */
  val allocaReqIO = Decoupled(new AllocaReq)
  val allocaRespIO = Flipped(Valid(new AllocaResp))

  override def cloneType = new AllocaNodeIO(NumOuts, Debug).asInstanceOf[this.type]

}

class AllocaNode(NumOuts: Int, ID: Int, RouteID: Int, FrameSize: Int = 16, Debug: Boolean = false)
                (implicit p: Parameters,
                 name: sourcecode.Name,
                 file: sourcecode.File)
  extends HandShakingNPS(NumOuts, ID, Debug)(new DataBundle)(p) {
  override lazy val io = IO(new AllocaNodeIO(NumOuts, Debug))
  // Printf debugging
  val node_name = name.value
  val module_name = file.value.split("/").tail.last.split("\\.").head.capitalize
  override val printfSigil = "[" + module_name + "] " + node_name + ": " + ID + " "
  val (cycleCount, _) = Counter(true.B, 32 * 1024)

  val FrameBits = log2Ceil(FrameSize)


  /*===========================================*
   *            Registers                      *
   *===========================================*/
  // OP Inputs
  val alloca_R = RegInit(AllocaIO.default)
  val alloca_valid_R = RegInit(false.B)

  // Output register
  val data_R = RegInit(0.U(xlen.W))
  val pred_R = RegInit(false.B)

  // Alloca req
  val alloca_req_R = RegInit(AllocaReq.default)

  // Alloca resp
  val alloca_resp_R = RegInit(AllocaResp.default)

  val taskID_R = RegInit(0.U(tlen.W))

  /*==========================================*
   *           Predicate Evaluation           *
   *==========================================*/

  val predicate = alloca_R.predicate & IsEnable()
  val start = alloca_valid_R & IsEnableValid()


  /*===============================================*
   *            Latch inputs. Wire up output       *
   *===============================================*/

  when(io.enable.fire()) {
    taskID_R := io.enable.bits.taskID
  }

  // Predicate register
  io.allocaInputIO.ready := ~alloca_valid_R

  // Input Register
  when(io.allocaInputIO.fire()) {
    alloca_R.size := io.allocaInputIO.bits.size
    alloca_R.numByte := io.allocaInputIO.bits.numByte
    alloca_valid_R := true.B
    alloca_R.predicate := io.allocaInputIO.bits.predicate
  }

  /**
   * Defaults assignments
   */

  io.allocaReqIO.bits.size := alloca_R.size
  io.allocaReqIO.bits.numByte := alloca_R.numByte
  //  io.allocaReqIO.bits.taskID  := taskID_R
  io.allocaReqIO.bits.RouteID := RouteID.U
  io.allocaReqIO.valid := false.B

  /**
   * State Machine
   */
  val s_idle :: s_req :: s_done :: Nil = Enum(3)
  val state = RegInit(s_idle)
  val req_valid = RegInit(false.B)
  io.allocaReqIO.valid := req_valid

  switch(state) {
    is(s_idle) {
      when(start & predicate) {
        req_valid := true.B

        state := s_req

      }
    }
    is(s_req) {
      when(io.allocaRespIO.valid) {
        req_valid := false.B
        //        data_R := Cat(Fill(xlen-(tlen+FrameBits)-1,0.U),taskID_R,io.allocaRespIO.ptr(FrameBits-1,0))
        data_R := Cat(taskID_R, io.allocaRespIO.bits.ptr(FrameBits - 1, 0))
        ValidOut()
        // Completion state.
        state := s_done
      }
    }
    is(s_done) {
      when(IsOutReady()) {
        alloca_R := AllocaIO.default
        data_R := 0.U
        pred_R := false.B
        pred_R := false.B
        state := s_idle
        Reset()
        when(predicate) {
          printf("[LOG] " + "[" + module_name + "] [TID ->%d] [ALLOCA] " +
            node_name + ": Output fired @ %d\n", enable_R.taskID, cycleCount)
        }
      }
    }
  }

  // Wire up Outputs.
  for (i <- 0 until NumOuts) {
    io.Out(i).bits.data := data_R
    io.Out(i).bits.predicate := predicate
    io.Out(i).bits.taskID := 0.U
  }
}



class AllocaConstNode(value: BigInt = 0, NumOuts: Int = 1, ID: Int)
               (implicit p: Parameters,
                name: sourcecode.Name,
                file: sourcecode.File)
  extends HandShakingNPS(NumOuts, ID )(new DataBundle())(p) {

  override lazy val io = IO(new HandShakingIONPS(NumOuts)(new DataBundle()))
  val node_name = name.value
  val module_name = file.value.split("/").tail.last.split("\\.").head.capitalize

  override val printfSigil = "[" + module_name + "] " + node_name + ": " + ID + " "
  val (cycleCount, _) = Counter(true.B, 32 * 1024)

  /*===========================================*
   *            Registers                      *
   *===========================================*/
  //val task_ID_R = RegNext(next = enable_R.taskID)
  val task_ID_W = io.enable.bits.taskID

  //Output register
  val out_data_R = RegInit(DataBundle.default)

  val s_IDLE :: s_COMPUTE :: Nil = Enum(2)
  val state = RegInit(s_IDLE)


  //val predicate = left_R.predicate & right_R.predicate// & IsEnable()

  /*===============================================*
   *            Latch inputs. Wire up output       *
   *===============================================*/

  // Wire up Outputs
  for (i <- 0 until NumOuts) {
    io.Out(i).bits := out_data_R
  }

  /*============================================*
   *            State Machine                   *
   *============================================*/
  switch(state) {
    is(s_IDLE) {
      when(io.enable.fire()) {
        ValidOut()
        io.Out.foreach(_.valid := true.B)
        io.Out.foreach(_.bits := DataBundle(value.asSInt(xlen.W).asUInt()))
        when(io.enable.bits.control) {
          out_data_R := DataBundle(value.asSInt(xlen.W).asUInt())
        }

        state := s_COMPUTE
      }
    }
    is(s_COMPUTE) {
      when(IsOutReady()) {
        //Reset state

        state := s_IDLE
        out_data_R.predicate := false.B
        Reset()
        if (log) {
          printf("[LOG] " + "[" + module_name + "] " + "[TID->%d] " +
            node_name + ": Output fired @ %d, Value: %d\n",
            task_ID_W, cycleCount, value.asSInt(xlen.W))
        }
      }
    }
  }
}


