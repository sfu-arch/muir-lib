//package dnn
//
//import FPU.{FPUALU, FType}
//import chisel3._
//import chisel3.experimental.FixedPoint
//import chisel3.iotesters.{ChiselFlatSpec, Driver, OrderedDecoupledHWIOTester, PeekPokeTester}
//import chisel3.Module
//import chisel3.core.FixedPoint
//import chisel3.testers._
//import chisel3.util._
//import org.scalatest.{FlatSpec, Matchers}
//import config._
//import interfaces._
//import muxes._
//import util._
//import node._
//
//class scalar()(implicit p: Parameters) extends Shapes {
//  val data = UInt(xlen.W)
//
//  override def cloneType = new scalar( ).asInstanceOf[this.type]
//}
//
//
//object MatOrVec_Scalar_OpCode {
//  val Add   = 1
//  val Sub   = 2
//  val Mul   = 3
//  val Magic = 4
//
//  def foo(a: UInt, b: UInt): UInt = {
//    a + b
//  }
//
//
//  val opMap  = Map(
//    "Add" -> Add,
//    "add" -> Add,
//    "Sub" -> Sub,
//    "sub" -> Sub,
//    "Mul" -> Mul,
//    "mul" -> Mul,
//    "magic" -> Magic,
//    "Magic" -> Magic
//  )
//  val length = 4
//}
//
//import node.AluGenerator
//
//object operation_matNxN {
//
//  trait OperatorMatOrVec_Scalar[T] {
//    def addition(l: T, r: scalar)(implicit p: Parameters): T
//
//    def subtraction(l: T, r: scalar)(implicit p: Parameters): T
//
//    def multiplication(l: T, r: scalar)(implicit p: Parameters): T
//
//    def magic(l: T, r: scalar, f_op: (UInt, UInt) => UInt)(implicit p: Parameters): T
//  }
//
//  object OperatorMatOrVec_Scalar {
//
//    implicit object matNxN_scalar extends OperatorMatOrVec_Scalar[matNxN] {
//      def addition(l: matNxN, r: scalar)(implicit p: Parameters): matNxN = {
//        val x = Wire(new matNxN(l.N))
//        for (i <- 0 until l.N) {
//          for (j <- 0 until l.N) {
//            x.data(i)(j) := l.data(i)(j) + r.data
//          }
//        }
//        x
//      }
//
//      def subtraction(l: matNxN, r: scalar)(implicit p: Parameters): matNxN = {
//        val x = Wire(new matNxN(l.N))
//        for (i <- 0 until l.N) {
//          for (j <- 0 until l.N) {
//            x.data(i)(j) := l.data(i)(j) - r.data
//          }
//        }
//        x
//      }
//
//      def multiplication(l: matNxN, r: scalar)(implicit p: Parameters): matNxN = {
//        val x = Wire(new matNxN(l.N))
//        for (i <- 0 until l.N) {
//          for (j <- 0 until l.N) {
//            x.data(i)(j) := l.data(i)(j) * r.data
//          }
//        }
//        x
//      }
//
//      def magic(l: matNxN, r: scalar, f_op: (UInt, UInt) => UInt)(implicit p: Parameters): matNxN = {
//        val x = Wire(new matNxN(l.N))
//        for (i <- 0 until l.N) {
//          for (j <- 0 until l.N) {
//            x.data(i)(j) := f_op(l.data(i)(j), r.data)
//          }
//        }
//        x
//      }
//    }
//
//  }
//
//  def addition[T](l: T, r: scalar)(implicit op: OperatorMatOrVec_Scalar[T], p: Parameters): T = op.addition(l, r)
//
//  def subtraction[T](l: T, r: scalar)(implicit op: OperatorMatOrVec_Scalar[T], p: Parameters): T = op.subtraction(l, r)
//
//  def multiplication[T](l: T, r: scalar)(implicit op: OperatorMatOrVec_Scalar[T], p: Parameters): T = op.multiplication(l, r)
//
//  def magic[T](l: T, r: scalar, f_op: (UInt, UInt) => UInt)(implicit op: OperatorMatOrVec_Scalar[T], p: Parameters): T = op.magic(l, r, f_op)
//
//}
//
//
//import operation_matNxN._
//
//class OperatorMatOrVec_ScalarModule[T <: Shapes : OperatorMatOrVec_Scalar](left: => T, right: => scalar, val opCode: String, f_op: (UInt, UInt) => UInt = MatOrVec_Scalar_OpCode.foo)(implicit val p: Parameters) extends Module {
//  val io          = IO(new Bundle {
//    val a = Flipped(Valid(left))
//    val b = Flipped(Valid(right))
//    val o = Output(Valid(left))
//  })
//  val ScalarOrVec = right.getClass.getName
//  if (!(ScalarOrVec.contains("scalar"))) {
//    assert(false, "Right operand. Only scalar!")
//  }
//  require(right.getWidth == scala.math.sqrt(left.getWidth))
//
//  io.o.valid := io.a.valid && io.b.valid
//
//  val aluOp = Array(
//    MatOrVec_Scalar_OpCode.Add -> (addition(io.a.bits, io.b.bits)),
//    MatOrVec_Scalar_OpCode.Sub -> (subtraction(io.a.bits, io.b.bits)),
//    MatOrVec_Scalar_OpCode.Mul -> (multiplication(io.a.bits, io.b.bits)),
//    MatOrVec_Scalar_OpCode.Magic -> (magic(io.a.bits, io.b.bits, f_op)
//      )
//  )
//
//  assert(!AluOpCode.opMap.get(opCode).isEmpty, "Wrong matrix OP. Check operator!")
//
//  io.o.bits := AluGenerator(AluOpCode.opMap(opCode), aluOp)
//
//}
//
//
//class MatOrVec_Scalar_ComputeIO[T <: Shapes : OperatorMatOrVec_Scalar](NumOuts: Int)(left: => T, right: => scalar)(output: => T)(implicit p: Parameters)
//  extends HandShakingIONPS(NumOuts)(new CustomDataBundle(UInt(output.getWidth))) {
//  // LeftIO: Left input data for computation
//  val LeftIO = Flipped(Decoupled(new CustomDataBundle(UInt((left.getWidth).W))))
//
//  // RightIO: Right input data for computation
//  val RightIO = Flipped(Decoupled(new CustomDataBundle(UInt((right.getWidth).W))))
//
//  override def cloneType = new MatOrVec_Scalar_ComputeIO(NumOuts)(left, right)(output).asInstanceOf[this.type]
//}
//
//class MatOrVec_Scalar_Compute[T <: Shapes : OperatorMatOrVec_Scalar, T2 <: Shapes](NumOuts: Int, ID: Int, opCode: String)(sign: Boolean)(left: => T, right: => scalar)(output: => T)(implicit p: Parameters)
//  extends HandShakingNPS(NumOuts, ID)(new CustomDataBundle(UInt(output.getWidth.W)))(p) {
//  override lazy val io = IO(new MatOrVec_Scalar_ComputeIO(NumOuts)(left, right)(output))
//
//  /*===========================================*
// *            Registers                      *
// *===========================================*/
//  // OP Inputs
//  val left_R = RegInit(CustomDataBundle.default(0.U((left.getWidth).W)))
//
//  // Memory Response
//  val right_R = RegInit(CustomDataBundle.default(0.U((right.getWidth).W)))
//
//  // Output register
//  val data_R = RegInit(CustomDataBundle.default(0.U((output.getWidth).W)))
//
//  val s_idle :: s_LATCH :: s_COMPUTE :: Nil = Enum(3)
//  val state                                 = RegInit(s_idle)
//
//  /*==========================================*
//   *           Predicate Evaluation           *
//   *==========================================*/
//
//  val predicate = left_R.predicate & right_R.predicate & IsEnable( )
//  val start     = left_R.valid & right_R.valid & IsEnableValid( )
//
//  /*===============================================*
//   *            Latch inputs. Wire up output       *
//   *===============================================*/
//
//  // Predicate register
//  val pred_R = RegInit(init = false.B)
//
//  //printfInfo("start: %x\n", start)
//
//  io.LeftIO.ready := ~left_R.valid
//  when(io.LeftIO.fire( )) {
//    //printfInfo("Latch left data\n")
//    state := s_LATCH
//    left_R.data := io.LeftIO.bits.data
//    left_R.valid := true.B
//    left_R.predicate := io.LeftIO.bits.predicate
//  }
//
//  io.RightIO.ready := ~right_R.valid
//  when(io.RightIO.fire( )) {
//    //printfInfo("Latch right data\n")
//    state := s_LATCH
//    right_R.data := io.RightIO.bits.data
//    right_R.valid := true.B
//    right_R.predicate := io.RightIO.bits.predicate
//  }
//
//  // Wire up Outputs
//  for (i <- 0 until NumOuts) {
//    io.Out(i).bits.data := data_R.data
//    io.Out(i).bits.valid := true.B
//    io.Out(i).bits.predicate := predicate
//    io.Out(i).bits.taskID := left_R.taskID | right_R.taskID | enable_R.taskID
//  }
//
//  /*============================================*
//   *            ACTIONS (possibly dangerous)    *
//   *============================================*/
//
//  val FU = Module(new OperatorMatOrVec_ScalarModule(left, right, opCode))
//  FU.io.a.bits := (left_R.data).asTypeOf(left)
//  FU.io.b.bits := (right_R.data).asTypeOf(right)
//  data_R.data := (FU.io.o.bits).asTypeOf(UInt(output.getWidth.W))
//  data_R.predicate := predicate
//  pred_R := predicate
//  FU.io.a.valid := left_R.valid
//  FU.io.b.valid := right_R.valid
//  data_R.valid := FU.io.o.valid
//  //  This is written like this to enable FUs that are dangerous in the future.
//  // If you don't start up then no value passed into function
//  when(start & predicate & state =/= s_COMPUTE) {
//    state := s_COMPUTE
//    // Next cycle it will become valid.
//    ValidOut( )
//  }.elsewhen(start && !predicate && state =/= s_COMPUTE) {
//    state := s_COMPUTE
//    ValidOut( )
//  }
//
//  when(IsOutReady( ) && state === s_COMPUTE) {
//    left_R := CustomDataBundle.default(0.U((left.getWidth).W))
//    right_R := CustomDataBundle.default(0.U((right.getWidth).W))
//    data_R := CustomDataBundle.default(0.U((output.getWidth).W))
//    Reset( )
//    state := s_idle
//  }
//
//  var classname: String = (left.getClass).toString
//  var signed            = if (sign == true) "S" else "U"
//  override val printfSigil =
//    opCode + "[" + classname.replaceAll("class node.", "") + "]_" + ID + ":"
//
//  if (log == true && (comp contains "TYPOP")) {
//    val x = RegInit(0.U(xlen.W))
//    x := x + 1.U
//
//    verb match {
//      case "high" => {
//      }
//      case "med" => {
//      }
//      case "low" => {
//        printfInfo("Cycle %d : { \"Inputs\": {\"Left\": %x, \"Right\": %x},", x, (left_R.valid), (right_R.valid))
//        printf("\"State\": {\"State\": \"%x\", \"(L,R)\": \"%x,%x\",  \"O(V,D,P)\": \"%x,%x,%x\" },", state, left_R.data, right_R.data, io.Out(0).valid, data_R.data, io.Out(0).bits.predicate)
//        printf("\"Outputs\": {\"Out\": %x}", io.Out(0).fire( ))
//        printf("}")
//      }
//      case everythingElse => {
//      }
//    }
//  }
//}