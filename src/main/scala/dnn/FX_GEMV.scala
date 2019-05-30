package dnn

import FPU.{FPUALU, FType}
import chisel3._
import chisel3.iotesters.{ChiselFlatSpec, Driver, OrderedDecoupledHWIOTester, PeekPokeTester}
import chisel3.Module
import chisel3.testers._
import chisel3.util._
import org.scalatest.{FlatSpec, Matchers}
import config._
import dnn.operation_mixed_FXmatNxN.OperatorFXmat_X.FXmatNxN_FXvecN
import interfaces._
import muxes._
import util._
import node._
import node.AluGenerator
import org.antlr.v4.runtime.atn.SemanticContext.Operator

object operation_mixed_FXmatNxN {

  trait OperatorFXmat_X[T, T2] {
    def addition(l: T, r: T2)(implicit p: Parameters): T

    def subtraction(l: T, r: T2)(implicit p: Parameters): T

    def multiplication(l: T, r: T2)(implicit p: Parameters): T2

  }

  object OperatorFXmat_X {

    implicit object FXmatNxN_FXvecN extends OperatorFXmat_X[FXmatNxN, FXvecN] {
      def addition(l: FXmatNxN, r: FXvecN)(implicit p: Parameters): FXmatNxN = {
        val x = Wire(new FXmatNxN(l.N, l.fraction))
        for (i <- 0 until l.N) {
          for (j <- 0 until l.N) {
            x.data(i)(j) := l.data(i)(j) + r.data(j)
          }
        }
        x
      }

      def subtraction(l: FXmatNxN, r: FXvecN)(implicit p: Parameters): FXmatNxN = {
        val x = Wire(new FXmatNxN(l.N, l.fraction))
        for (i <- 0 until l.N) {
          for (j <- 0 until l.N) {
            x.data(i)(j) := l.data(i)(j) - r.data(j)
          }
        }
        x
      }

      def multiplication(l: FXmatNxN, r: FXvecN)(implicit p: Parameters): FXvecN = {
        val x = Wire(new FXvecN(r.N, r.fraction))
        val products = for (i <- 0 until l.N) yield {
          for (j <- 0 until l.N) yield {
            l.data(i)(j) * r.data(j)
          }
        }
        for (i <- 0 until l.N) {
          x.data(i) := products(i).reduceLeft(_ + _)
        }
        x
      }

    }


  }

  def addFX[T, U](l: T, r: U)(implicit op: OperatorFXmat_X[T, U], p: Parameters): T = op.addition(l, r)

  def getfns(l: => Numbers, R: => Numbers)(implicit p: Parameters): Array[(Int, Numbers)] = {
    val aluOp = Array(
      Mat_X_OpCode.Add -> (implicitly[OperatorFXmat_X[FXmatNxN, FXvecN]].
        addition(l.asInstanceOf[FXmatNxN], R.asInstanceOf[FXvecN])),
      Mat_X_OpCode.Sub -> (implicitly[OperatorFXmat_X[FXmatNxN, FXvecN]].
        subtraction(l.asInstanceOf[FXmatNxN], R.asInstanceOf[FXvecN])),
      Mat_X_OpCode.Sub -> (implicitly[OperatorFXmat_X[FXmatNxN, FXvecN]].
        multiplication(l.asInstanceOf[FXmatNxN], R.asInstanceOf[FXvecN])))
    aluOp
  }
}

import operation_mixed_FXmatNxN._


class OperatorFXmat_XModule[L <: Numbers, R <: Numbers, O <: Numbers](left: => L, right: => R, output: => O, val opCode: String)(implicit val p: Parameters) extends Module {
  val io     = IO(new Bundle {
    val a      = Flipped(Valid(left))
    val b      = Flipped(Valid(right))
    val o      = Output(Valid(output))
    val active = Input(Bool( ))
  })
  // Check if right is vec
  val is_vec = right.getClass.getName
  if (!(is_vec.contains("vec"))) {
    assert(false, "Right operand. Only vector!")
  }


  val aluOp = operation_mixed_FXmatNxN.getfns(io.a.bits, io.b.bits)

  assert(!Mat_X_OpCode.opMap.get(opCode).isEmpty, "Wrong matrix OP. Check operator!")

  // Replace with counter.
  val (latCnt, latDone) = Counter(io.active, 2)
  io.o.valid := latDone

  printf(p"\n Count: ${latCnt} ${io.a.valid} ${io.b.valid}")
  val start = io.a.valid && io.b.valid
  io.o.bits := AluGenerator(Mat_X_OpCode.opMap(opCode), aluOp)

}

class FXMat_X_ComputeIO[L <: Numbers, R <: Numbers, O <: Numbers](NumOuts: Int)(left: => L, right: => R)(output: => O)(implicit p: Parameters)
  extends HandShakingIONPS(NumOuts)(new CustomDataBundle(UInt(output.getWidth))) {
  // LeftIO: Left input data for computation
  val LeftIO = Flipped(Decoupled(new CustomDataBundle(UInt((left.getWidth).W))))

  // RightIO: Right input data for computation
  val RightIO = Flipped(Decoupled(new CustomDataBundle(UInt((right.getWidth).W))))

  override def cloneType = new FXMat_X_ComputeIO(NumOuts)(left, right)(output).asInstanceOf[this.type]
}

class FXMat_X_Compute[L <: Numbers, R <: Numbers, O <: Numbers](NumOuts: Int, ID: Int, opCode: String)(sign: Boolean)(left: => L, right: R)(output: => O)(implicit p: Parameters)
  extends HandShakingNPS(NumOuts, ID)(new CustomDataBundle(UInt(output.getWidth.W)))(p) {
  override lazy val io = IO(new FXMat_X_ComputeIO(NumOuts)(left, right)(output))

  /*===========================================*
 *            Registers                      *
 *===========================================*/
  // OP Inputs
  val left_R = RegInit(CustomDataBundle.default(0.U((left.getWidth).W)))

  // Memory Response
  val right_R = RegInit(CustomDataBundle.default(0.U((right.getWidth).W)))

  // Output register
  val data_R = RegInit(CustomDataBundle.default(0.U((output.getWidth).W)))

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

  val FU = Module(new OperatorFXmat_XModule(left, right, output, opCode))
  FU.io.active := false.B
  FU.io.a.bits := (left_R.data).asTypeOf(left)
  FU.io.b.bits := (right_R.data).asTypeOf(right)
  data_R.data := (FU.io.o.bits).asTypeOf(UInt(output.getWidth.W))
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
    FU.io.active := true.B
    when(FU.io.o.valid) {
      ValidOut( )
      state := s_COMPUTE
    }
  }
  when(IsOutReady( ) && state === s_COMPUTE) {
    left_R := CustomDataBundle.default(0.U((left.getWidth).W))
    right_R := CustomDataBundle.default(0.U((right.getWidth).W))
    data_R := CustomDataBundle.default(0.U((output.getWidth).W))
    Reset( )
    state := s_idle
  }

  printf(p"\n State : ${state} Predicate ${predicate} Left ${left_R} Right ${right_R} Output: ${data_R}")

  var classname: String = (left.getClass).toString
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


//implicit object FXFXmatNxN_scalar extends OperatorMatOrVec_Scalar[FXFXmatNxN] {
//  def addition(l: FXFXmatNxN, r: scalar)(implicit p: Parameters): FXFXmatNxN = {
//    val x = Wire(new FXFXmatNxN(l.N, l.fraction))
//    for (i <- 0 until l.N) {
//      for (j <- 0 until l.N) {
//        x.data(i)(j) := l.data(i)(j) + r.data.asFixedPoint(l.fraction.BP)
//      }
//    }
//    x
//  }

//  def subtraction(l: FXFXmatNxN, r: scalar)(implicit p: Parameters): FXFXmatNxN = {
//    val x = Wire(new FXFXmatNxN(l.N, l.fraction))
//    for (i <- 0 until l.N) {
//      for (j <- 0 until l.N) {
//        val xlen = p(XLEN)
//        x.data(i)(j) := l.data(i)(j) - r.data.asFixedPoint(l.fraction.BP)
//      }
//    }
//    x
//  }
//
//  def multiplication(l: FXFXmatNxN, r: scalar)(implicit p: Parameters): FXFXmatNxN = {
//    val x = Wire(new FXFXmatNxN(l.N, l.fraction))
//    for (i <- 0 until l.N) {
//      for (j <- 0 until l.N) {
//        x.data(i)(j) := l.data(i)(j) * r.data
//      }
//    }
//    x
//  }
//
//  def magic(l: FXFXmatNxN, r: scalar, f_op: (UInt, UInt) => UInt)(implicit p: Parameters): FXFXmatNxN = {
//    val x = Wire(new FXFXmatNxN(l.N, l.fraction))
//    for (i <- 0 until l.N) {
//      for (j <- 0 until l.N) {
//        x.data(i)(j) := f_op(l.data(i)(j).asUInt( ), r.data)
//      }
//    }
//    x
//  }
//}
