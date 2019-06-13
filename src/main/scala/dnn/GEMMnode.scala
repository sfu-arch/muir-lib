package dnn

import FPU.{FPUALU, FType}
import chisel3._
import chisel3.iotesters.{ChiselFlatSpec, Driver, OrderedDecoupledHWIOTester, PeekPokeTester}
import chisel3.Module
import chisel3.testers._
import chisel3.util._
import org.scalatest.{FlatSpec, Matchers}
import config._
import dnn.GEMM.OperatorGEMM
import interfaces._
import muxes._
import util._
import node._

object GEMM {
  // Declare trait to encapsulate implicit functions
  trait OperatorGEMM[T] {
    def multiplication(l: T, r: T, start: Bool)(implicit p: Parameters): T
  }
  // Implementation of actual functions
  object OperatorGEMM {
    implicit object FXmatNxN extends OperatorGEMM[FXmatNxN] {
      def multiplication(l: FXmatNxN, r: FXmatNxN, start: Bool)(implicit p: Parameters): FXmatNxN = {
        val x = Wire(new FXmatNxN(r.N, r.fraction))
        x
      }
    }

  }

  // Implicit functions to invoke.
  def GEMM[T](l: T, r: T, start : Bool)(implicit op: OperatorGEMM[T], p: Parameters): T = op.multiplication(l, r, start)
}


class OperatorGEMMModule[T <: Numbers : OperatorGEMM](operand: => T, val opCode: String)(implicit val p: Parameters) extends Module {
  val io     = IO(new Bundle {
    val a      = Flipped(Valid(operand))
    val b      = Flipped(Valid(operand))
    val o      = Output(Valid(operand))
    val active = Input(Bool( ))
  })


  // Replace with counter.
  val (latCnt, latDone) = Counter(io.active, 2)
  io.o.valid := latDone

  printf(p"\n Count: ${latCnt} ${io.a.valid} ${io.b.valid}")
  val start = io.a.valid && io.b.valid
  io.o.bits := GEMM.GEMM(io.a.bits,io.b.bits,start)

}

class GEMM_ComputeIO[T <: Numbers](NumOuts: Int)(operand: => T)(implicit p: Parameters)
  extends HandShakingIONPS(NumOuts)(new CustomDataBundle(UInt(operand.getWidth))) {
  // LeftIO: Left input data for computation
  val LeftIO = Flipped(Decoupled(new CustomDataBundle(UInt((operand.getWidth).W))))

  // RightIO: Right input data for computation
  val RightIO = Flipped(Decoupled(new CustomDataBundle(UInt((operand.getWidth).W))))

  override def cloneType = new GEMM_ComputeIO(NumOuts)(operand).asInstanceOf[this.type]
}

class GEMM_Compute[T <: Numbers : OperatorGEMM](NumOuts: Int, ID: Int, opCode: String)(sign: Boolean)(operand: => T)(implicit p: Parameters)
  extends HandShakingNPS(NumOuts, ID)(new CustomDataBundle(UInt(operand.getWidth.W)))(p) {
  override lazy val io = IO(new GEMM_ComputeIO(NumOuts)(operand))

  /*===========================================*
 *            Registers                      *
 *===========================================*/
  // OP Inputs
  val left_R = RegInit(CustomDataBundle.default(0.U((operand.getWidth).W)))

  // Memory Response
  val right_R = RegInit(CustomDataBundle.default(0.U((operand.getWidth).W)))

  // Output register
  val data_R = RegInit(CustomDataBundle.default(0.U((operand.getWidth).W)))

  val s_idle :: s_LATCH :: s_ACTIVE :: s_COMPUTE :: Nil = Enum(4)
  val state                                             = RegInit(s_idle)

  /*==========================================*
   *           Predicate Evaluation           *
   *==========================================*/

  val predicate = left_R.predicate & right_R.predicate & IsEnable( )
  val start     = left_R.valid & right_R.valid & IsEnableValid( )

  /*===============================================*
   *            Latch inputs. Wire up output       *
   *===============================================*/

  // Predicate register
  val pred_R = RegInit(init = false.B)

  //printfInfo("start: %x\n", start)

  io.LeftIO.ready := ~left_R.valid
  when(io.LeftIO.fire( )) {
    //printfInfo("Latch left data\n")
    state := s_LATCH
    left_R.data := io.LeftIO.bits.data
    left_R.valid := true.B
    left_R.predicate := io.LeftIO.bits.predicate
  }

  io.RightIO.ready := ~right_R.valid
  when(io.RightIO.fire( )) {
    //printfInfo("Latch right data\n")
    state := s_LATCH
    right_R.data := io.RightIO.bits.data
    right_R.valid := true.B
    right_R.predicate := io.RightIO.bits.predicate
  }

  // Wire up Outputs
  for (i <- 0 until NumOuts) {
    io.Out(i).bits.data := data_R.data
    io.Out(i).bits.valid := true.B
    io.Out(i).bits.predicate := predicate
    io.Out(i).bits.taskID := left_R.taskID | right_R.taskID | enable_R.taskID
  }

  /*============================================*
   *            ACTIONS (possibly dangerous)    *
   *============================================*/

  val FU = Module(new OperatorGEMMModule(operand, opCode))
  FU.io.active := false.B
  FU.io.a.bits := (left_R.data).asTypeOf(operand)
  FU.io.b.bits := (right_R.data).asTypeOf(operand)
  data_R.data := (FU.io.o.bits).asTypeOf(UInt(operand.getWidth.W))
  data_R.predicate := predicate
  pred_R := predicate
  FU.io.a.valid := false.B
  FU.io.b.valid := false.B
  data_R.valid := FU.io.o.valid
  //  This is written like this to enable FUs that are dangerous in the future.
  // If you don't start up then no value passed into function
  when(start & predicate && ((state === s_idle) || (state === s_LATCH))) {

    FU.io.a.valid := true.B
    FU.io.b.valid := true.B
    state := s_ACTIVE
    FU.io.active := true.B
    // Next cycle it will become valid.
  }.elsewhen(start && !predicate && ((state === s_idle) || (state === s_LATCH))) {
    state := s_COMPUTE
    ValidOut( )
  }
  when(state === s_ACTIVE) {
     when(FU.io.o.valid) {
      ValidOut( )
      state := s_COMPUTE
    }.otherwise {
      FU.io.active := true.B
    }
  }
  when(IsOutReady( ) && state === s_COMPUTE) {
    left_R := CustomDataBundle.default(0.U((operand.getWidth).W))
    right_R := CustomDataBundle.default(0.U((operand.getWidth).W))
    data_R := CustomDataBundle.default(0.U((operand.getWidth).W))
    Reset( )
    state := s_idle
  }

  printf(p"\n State : ${state} Predicate ${predicate} Left ${left_R} Right ${right_R} Output: ${data_R}")

  var classname: String = (operand.getClass).toString
  var signed            = if (sign == true) "S" else "U"
  override val printfSigil =
    opCode + "[" + classname.replaceAll("class node.", "") + "]_" + ID + ":"

  if (log == true && (comp contains "TYPOP")) {
    val x = RegInit(0.U(xlen.W))
    x := x + 1.U

    verb match {
      case "high" => {
      }
      case "med" => {
      }
      case "low" => {
        printfInfo("Cycle %d : { \"Inputs\": {\"Left\": %x, \"Right\": %x},", x, (left_R.valid), (right_R.valid))
        printf("\"State\": {\"State\": \"%x\", \"(L,R)\": \"%x,%x\",  \"O(V,D,P)\": \"%x,%x,%x\" },", state, left_R.data, right_R.data, io.Out(0).valid, data_R.data, io.Out(0).bits.predicate)
        printf("\"Outputs\": {\"Out\": %x}", io.Out(0).fire( ))
        printf("}")
      }
      case everythingElse => {
      }
    }
  }
}

