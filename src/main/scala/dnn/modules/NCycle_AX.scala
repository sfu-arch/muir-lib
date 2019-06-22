package dnn

import FPU._
import chisel3._
import chisel3.iotesters.{ChiselFlatSpec, Driver, OrderedDecoupledHWIOTester, PeekPokeTester}
import chisel3.Module
import chisel3.experimental.FixedPoint
import chisel3.internal.firrtl.BinaryPoint
import chisel3.testers._
import chisel3.util._
import org.scalatest.{FlatSpec, Matchers}
import config._
import interfaces._
import muxes._
import util._
import node._
import utility.UniformPrintfs


object SCAL {

  trait OperatorSCAL[T] {
    def scal(l: T, r: T, opcode: String)(implicit p: Parameters): T
  }

  object OperatorSCAL {

    implicit object UIntSCAL extends OperatorSCAL[UInt] {
      def scal(l: UInt, r: UInt, opcode: String)(implicit p: Parameters): UInt = {
        val x = Wire(l.cloneType)
        val FXALU = Module(new UALU(p(XLEN), opcode))
        FXALU.io.in1 := l
        FXALU.io.in2 := r
        x := FXALU.io.out.asTypeOf(l)
        x
      }
    }

    implicit object SIntSCAL extends OperatorSCAL[SInt] {
      def scal(l: SInt, r: SInt, opcode: String)(implicit p: Parameters): SInt = {
        val x = Wire(l.cloneType)
        val FXALU = Module(new UALU(p(XLEN), opcode, true))
        FXALU.io.in1 := l.asUInt
        FXALU.io.in2 := r.asUInt
        x := FXALU.io.out.asTypeOf(l)
        x
      }
    }


    implicit object FixedPointSCAL extends OperatorSCAL[FixedPoint] {
      def scal(l: FixedPoint, r: FixedPoint, opcode: String)(implicit p: Parameters): FixedPoint = {
        val x = Wire(l.cloneType)
        val FXALU = Module(new DSPALU(FixedPoint(l.getWidth.W, l.binaryPoint), opcode))
        FXALU.io.in1 := l
        FXALU.io.in2 := r
        x := FXALU.io.out.asTypeOf(l)
        // Uncomment if you do not have access to DSP tools and need to use chisel3.experimental FixedPoint. DSP tools provides implicit support for truncation.
        //  val mul = ((l.data * r.data) >> l.fraction.U).asFixedPoint(l.fraction.BP)
        // x.data := mul + c.data
        x
      }
    }


    implicit object FP_SCAL extends OperatorSCAL[FloatingPoint] {
      def scal(l: FloatingPoint, r: FloatingPoint, opcode: String)(implicit p: Parameters): FloatingPoint = {
        val x = Wire(new FloatingPoint(l.t))
        val mac = Module(new FPMAC(p(XLEN), opcode, t = l.t))
        mac.io.in1 := l.value
        mac.io.in2 := r.value
        x.value := mac.io.out
        x
      }
    }

  }

  def scal[T](l: T, r: T, opcode: String)(implicit op: OperatorSCAL[T], p: Parameters): T = op.scal(l, r, opcode)
}

////
class SCAL_PE_IO(implicit p: Parameters) extends CoreBundle( )(p) {
  // LeftIO: Left input data for computation
  val left = Input(Valid(UInt(xlen.W)))

  // RightIO: Right input data for computation
  val right = Input(Valid(UInt(xlen.W)))

  val out = Output(Valid(UInt(xlen.W)))

}


class SCAL_PE[T <: Data : SCAL.OperatorSCAL](gen: T, opcode: String)(implicit val p: Parameters)
  extends Module with CoreParams with UniformPrintfs {
  val io = IO(new SCAL_PE_IO( ))

  io.out.valid := false.B
  io.out.bits := SCAL.scal(io.left.bits.asTypeOf(gen), io.right.bits.asTypeOf(gen), opcode).asUInt

  when(io.left.valid & io.right.valid) {
    io.out.valid := true.B
  }

}

//
class NCycle_SCAL[T <: Data : SCAL.OperatorSCAL](val gen: T, val N: Int, val lanes: Int, val opcode: String)(implicit val p: Parameters)
  extends Module with CoreParams with UniformPrintfs {
  val io = IO(new Bundle {
    val input_vec = Input(Vec(N, UInt(xlen.W)))
    val scalar    = Input(UInt(xlen.W))
    val activate  = Input(Bool( ))
    val stat      = Output(UInt(xlen.W))
    val output    = Output(Vec(lanes, UInt(xlen.W)))
  })

  require(gen.getWidth == xlen, "Size of element does not match xlen OR Size of vector does not match shape")
  require(N % lanes == 0, "Size of vector should be multiple of lanes")

  def latency(): Int = {
    N / lanes
  }

  val PEs =
    for (i <- 0 until lanes) yield {
      Module(new SCAL_PE(gen, opcode))
    }

  /* PE Control */
  val s_idle :: s_ACTIVE :: s_COMPUTE :: Nil = Enum(3)
  val state                                  = RegInit(s_idle)
  val input_steps                            = new Counter(latency)
  io.stat := input_steps.value
  when(state === s_idle) {
    when(io.activate) {
      state := s_ACTIVE
    }
  }.elsewhen(state === s_ACTIVE) {
    input_steps.inc( )
    when(input_steps.value === (latency - 1).U) {
      state := s_idle
    }
  }

  val io_inputs = for (i <- 0 until lanes) yield {
    for (j <- 0 until N / lanes) yield {
      j.U -> io.input_vec(i + j * lanes)
    }
  }

  val input_muxes = for (i <- 0 until lanes) yield {
    val mx = MuxLookup(input_steps.value, 0.U, io_inputs(i))
    mx
  }

  for (i <- 0 until lanes) {
    PEs(i).io.left.bits := input_muxes(i)
    PEs(i).io.right.bits := io.scalar
    PEs(i).io.left.valid := false.B
    PEs(i).io.right.valid := false.B
  }

  when(state === s_ACTIVE) {
    for (i <- 0 until lanes) {
      PEs(i).io.left.valid := true.B
      PEs(i).io.right.valid := true.B
    }
  }

  //printf(p"\n ${input_steps.value}")
  //  printf(p"1.U, 1.U ${
  //    Hexadecimal(PEs(0).io.out.bits)
  //  }")
  for (i <- 0 until lanes) {
    io.output(i) <> PEs(i).io.out.bits
    when(state === s_idle) {
      PEs(i).reset := true.B
    }
  }
}