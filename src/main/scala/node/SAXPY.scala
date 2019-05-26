package node

import FPU.{FPUALU, FType}
import chisel3._
import chisel3.iotesters.{ChiselFlatSpec, Driver, OrderedDecoupledHWIOTester, PeekPokeTester}
import chisel3.Module
import chisel3.testers._
import chisel3.util._
import org.scalatest.{FlatSpec, Matchers}
import config._
import interfaces._
import muxes._
import util._


object Mat_X_OpCode {
  val Add = 1
  val Sub = 2
  val Mul = 3

  val opMap  = Map(
    "Add" -> Add,
    "add" -> Add,
    "Sub" -> Sub,
    "sub" -> Sub,
    "Mul" -> Mul,
    "mul" -> Mul
  )
  val length = 3
}

import node.AluGenerator

object operation_mixed_matNxN {

  trait OperatorMat_X[T] {
    def addition(l: matNxN, r: T)(implicit p: Parameters): matNxN

    def subtraction(l: matNxN, r: T)(implicit p: Parameters): matNxN

    def multiplication(l: matNxN, r: T)(implicit p: Parameters): T
  }

  object OperatorMat_X {

    implicit object matNxN_vecN extends OperatorMat_X[vecN] {
      def addition(l: matNxN, r: vecN)(implicit p: Parameters): matNxN = {
        val x = Wire(new matNxN(l.N))
        for (i <- 0 until l.N) {
          for (j <- 0 until l.N) {
            x.data(i)(j) := l.data(i)(j) + r.data(j)
          }
        }
        x
      }

      def subtraction(l: matNxN, r: vecN)(implicit p: Parameters): matNxN = {
        val x = Wire(new matNxN(l.N))
        for (i <- 0 until l.N) {
          for (j <- 0 until l.N) {
            x.data(i)(j) := l.data(i)(j) - r.data(j)
          }
        }
        x
      }

      def multiplication(l: matNxN, r: vecN)(implicit p: Parameters): vecN = {
        val x = Wire(new vecN(l.N))
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

  def addition[T](l: matNxN, r: T)(implicit op: OperatorMat_X[T], p: Parameters): matNxN = op.addition(l, r)

  def subtraction[T](l: matNxN, r: T)(implicit op: OperatorMat_X[T], p: Parameters): matNxN = op.subtraction(l, r)

  def multiplication[T](l: matNxN, r: T)(implicit op: OperatorMat_X[T], p: Parameters): T = op.multiplication(l, r)

  def getWidth[T <: Numbers](opCode: String, l: T, r: T)(implicit p: Parameters): T = {
    require(!Mat_X_OpCode.opMap.get(opCode).isEmpty)
    Mat_X_OpCode.opMap.getOrElse(opCode, 0) match {
      case Mat_X_OpCode.Add => l
      case Mat_X_OpCode.Sub => l
      case Mat_X_OpCode.Mul => r
      case 0 => l
    }
  }
}

import operation_mixed_matNxN._

class OperatorMat_XModule[T <: Numbers : OperatorMat_X](left: => matNxN, right: => T, val opCode: String)(implicit val p: Parameters) extends Module {
  val io          = IO(new Bundle {
    val a = Flipped(Valid(left))
    val b = Flipped(Valid(right))
    val o = Some(Output(Valid(operation_mixed_matNxN.getWidth(opCode, left, right))))
  })
  val ScalarOrVec = right.getClass.getName
  if (!(ScalarOrVec.contains("vec") || ScalarOrVec.contains("UInt"))) {
    assert(false, "Right operand. Only scalar or vector!")
  }

  io.o.get.valid := io.a.valid && io.b.valid


  val aluOp = Array(
    AluOpCode.Add -> (addition(io.a.bits, io.b.bits)),
    AluOpCode.Sub -> (subtraction(io.a.bits, io.b.bits)),
    AluOpCode.Mul -> (multiplication(io.a.bits, io.b.bits))
  )

  assert(!AluOpCode.opMap.get(opCode).isEmpty, "Wrong matrix OP. Check operator!")

  io.o.get.bits := AluGenerator(AluOpCode.opMap(opCode), aluOp)

}

class Mat_X_ComputeIO[T <: Numbers : OperatorMat_X, T2 <: Numbers](NumOuts: Int)(left: => matNxN, right: => T)(output: => T2)(implicit p: Parameters)
  extends HandShakingIONPS(NumOuts)(new CustomDataBundle(UInt(output.getWidth))) {
  // LeftIO: Left input data for computation
  val LeftIO = Flipped(Decoupled(new CustomDataBundle(UInt((left.getWidth).W))))

  // RightIO: Right input data for computation
  val RightIO = Flipped(Decoupled(new CustomDataBundle(UInt((right.getWidth).W))))

  override def cloneType = new Mat_X_ComputeIO(NumOuts)(left, right)(output).asInstanceOf[this.type]
}

class Mat_X_Compute[T <: Numbers : OperatorMat_X, T2 <: Numbers](NumOuts: Int, ID: Int, opCode: String)(sign: Boolean)(left: => matNxN, right: => T)(output: => T2)(implicit p: Parameters)
  extends HandShakingNPS(NumOuts, ID)(new CustomDataBundle(UInt(output.getWidth)))(p) {
  override lazy val io = IO(new Mat_X_ComputeIO(NumOuts)(left, right)(output))

  /*===========================================*
 *            Registers                      *
 *===========================================*/
  // OP Inputs
  val left_R = RegInit(CustomDataBundle.default(0.U((left.getWidth).W)))

  // Memory Response
  val right_R = RegInit(CustomDataBundle.default(0.U((right.getWidth).W)))

  // Output register
  val data_R = RegInit(CustomDataBundle.default(0.U((output.getWidth).W)))

  val s_idle :: s_LATCH :: s_COMPUTE :: Nil = Enum(3)
  val state                                 = RegInit(s_idle)

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

  val FU = Module(new OperatorMat_XModule(left, right, opCode))

  FU.io.a.bits := (left_R.data).asTypeOf(left)
  FU.io.b.bits := (right_R.data).asTypeOf(right)
  data_R.data := (FU.io.o.get.bits).asTypeOf(UInt(output.getWidth.W))
  pred_R := predicate
  FU.io.a.valid := left_R.valid
  FU.io.b.valid := right_R.valid
  data_R.valid := FU.io.o.get.valid
  //  This is written like this to enable FUs that are dangerous in the future.
  // If you don't start up then no value passed into function
  when(start & predicate & state =/= s_COMPUTE) {
    state := s_COMPUTE
    // Next cycle it will become valid.
    ValidOut( )
  }.elsewhen(start && !predicate && state =/= s_COMPUTE) {
    state := s_COMPUTE
    ValidOut( )
  }

  when(IsOutReady( ) && state === s_COMPUTE) {
    left_R := CustomDataBundle.default(0.U((left.getWidth).W))
    right_R := CustomDataBundle.default(0.U((right.getWidth).W))
    data_R := CustomDataBundle.default(0.U((output.getWidth).W))
    Reset( )
    state := s_idle
  }


  printf(p"${
    left_R
  }")
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