package dnn

import FPU.{FPMAC, FType, FloatingPoint}
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

object MAC {

  trait OperatorMAC[T] {
    def mac(l: T, r: T, c: T)(implicit p: Parameters): T
  }

  object OperatorMAC {

    implicit object UIntMAC extends OperatorMAC[UInt] {
      def mac(l: UInt, r: UInt, c: UInt)(implicit p: Parameters): UInt = {
        val x = Wire(l.cloneType)
        val FXALU = Module(new UALU(p(XLEN), "Mac"))
        FXALU.io.in1 := l
        FXALU.io.in2 := r
        FXALU.io.in3.get := c
        x := FXALU.io.out.asTypeOf(l)
        x
      }
    }


    implicit object FixedPointMAC extends OperatorMAC[FixedPoint] {
      def mac(l: FixedPoint, r: FixedPoint, c: FixedPoint)(implicit p: Parameters): FixedPoint = {
        val x = Wire(l.cloneType)
        val FXALU = Module(new DSPALU(FixedPoint(l.getWidth.W, l.binaryPoint), "Mac"))
        FXALU.io.in1 := l
        FXALU.io.in2 := r
        FXALU.io.in3.get := c
        x := FXALU.io.out.asTypeOf(l)
        // Uncomment if you do not have access to DSP tools and need to use chisel3.experimental FixedPoint. DSP tools provides implicit support for truncation.
        //  val mul = ((l.data * r.data) >> l.fraction.U).asFixedPoint(l.fraction.BP)
        // x.data := mul + c.data
        x
      }
    }


    implicit object FP_MAC extends OperatorMAC[FloatingPoint] {
      def mac(l: FloatingPoint, r: FloatingPoint, c: FloatingPoint)(implicit p: Parameters): FloatingPoint = {
        val x = Wire(new FloatingPoint(l.t))
        val mac = Module(new FPMAC(p(XLEN), opCode = "Mac", t = l.t))
        mac.io.in1 := l.value
        mac.io.in2 := r.value
        mac.io.in3.get := c.value
        x.value := mac.io.out
        x
      }
    }

  }

  def mac[T](l: T, r: T, c: T)(implicit op: OperatorMAC[T], p: Parameters): T = op.mac(l, r, c)
}

class PEIO(implicit p: Parameters) extends CoreBundle( )(p) {
  // LeftIO: Left input data for computation
  val Left = Input(Valid(UInt(xlen.W)))

  // RightIO: Right input data for computation
  val Top = Input(Valid(UInt(xlen.W)))

  val Right = Output(Valid(UInt(xlen.W)))

  val Bottom = Output(Valid(UInt(xlen.W)))

  val Out = Output(Valid(UInt(xlen.W)))

}


class PE[T <: Data : MAC.OperatorMAC](gen: T, left_delay: Int, top_delay: Int, val row: Int, val col: Int)(implicit val p: Parameters)
  extends Module with CoreParams with UniformPrintfs {
  val io = IO(new PEIO)

  val top_reg  = Pipe(io.Top.valid, io.Top.bits, latency = top_delay)
  val left_reg = Pipe(io.Left.valid, io.Left.bits, latency = left_delay)

  val accumalator       = RegInit(init = 0.U(xlen.W))
  val accumalator_valid = RegInit(init = false.B)
  when(top_reg.valid & left_reg.valid) {
    accumalator := MAC.mac(left_reg.bits.asTypeOf(gen), top_reg.bits.asTypeOf(gen), accumalator.asTypeOf(gen)).asUInt
    accumalator_valid := top_reg.valid & left_reg.valid
  }

  io.Right := left_reg

  io.Bottom := top_reg

  io.Out.bits := accumalator

  io.Out.valid := accumalator_valid

}


class SystolicSquare[T <: Data : MAC.OperatorMAC](gen: T, val N: Int)(implicit val p: Parameters)
  extends Module with CoreParams with UniformPrintfs {
  val io = IO(new Bundle {
    val left        = Input(Vec(N * N, UInt(xlen.W)))
    val right       = Input(Vec(N * N, UInt(xlen.W)))
    val activate    = Input(Bool( ))
    val async_reset = Input(Bool( ))
    val output      = Output(Vec(N * N, UInt(xlen.W)))
  })

  val PEs =
    for (i <- 0 until N) yield
      for (j <- 0 until N) yield {
        if (i == 0 & j == 0)
          Module(new PE(gen, left_delay = 0, top_delay = 0, row = 0, col = 0))
        else if (j == 0)
          Module(new PE(gen, left_delay = i, top_delay = 1, row = i, col = j))
        else if (i == 0)
          Module(new PE(gen, left_delay = 1, top_delay = j, row = i, col = j))
        else
          Module(new PE(gen, left_delay = 1, top_delay = 1, row = i, col = j))
      }

  /* PE Control */
  val s_idle :: s_ACTIVE :: s_COMPUTE :: Nil = Enum(3)
  val state                                  = RegInit(s_idle)

  val input_steps = new Counter(3 * N - 1)
  when(state === s_idle) {
    when(io.activate) {
      state := s_ACTIVE
    }
  }.elsewhen(state === s_ACTIVE) {
    input_steps.inc( )
    when(input_steps.value === (N - 1).U) {
      state := s_COMPUTE
    }
  }.elsewhen(state === s_COMPUTE) {
    input_steps.inc( )
    when(input_steps.value === ((3 * N) - 2).U) {
      state := s_idle
    }
  }

  val io_lefts = for (i <- 0 until N) yield
    for (j <- 0 until N) yield {
      j.U -> io.left(i * N + j)
    }

  val io_rights = for (i <- 0 until N) yield
    for (j <- 0 until N) yield {
      j.U -> io.right(i + j * N)
    }


  val left_muxes = for (i <- 0 until N) yield {
    val mx = MuxLookup(input_steps.value, 0.U, io_lefts(i))
    mx
  }

  val top_muxes = for (i <- 0 until N) yield {
    val mx = MuxLookup(input_steps.value, 0.U, io_rights(i))
    mx
  }


  for (i <- 0 until N) {
    for (j <- 0 until N) {
      if (j != N - 1) {
        PEs(i)(j + 1).io.Left <> PEs(i)(j).io.Right
      }
      if (i != N - 1) {
        PEs(i + 1)(j).io.Top <> PEs(i)(j).io.Bottom
      }
      if (i == 0) {
        PEs(0)(j).io.Top.bits := top_muxes(j)
      }
      if (j == 0) {
        PEs(i)(0).io.Left.bits := left_muxes(i)
      }
    }
  }

  for (i <- 0 until N) {
    PEs(0)(i).io.Top.valid := false.B
    PEs(i)(0).io.Left.valid := false.B
  }

  when(state === s_ACTIVE) {
    for (i <- 0 until N) {
      PEs(0)(i).io.Top.valid := true.B
      PEs(i)(0).io.Left.valid := true.B
    }
  }

  printf("\nGrid  %d \n ", input_steps.value)
  printf(p"1.U, 1.U ${Hexadecimal(PEs(0)(0).io.Out.bits)}")
  for (i <- 0 until N) {
    for (j <- 0 until N) {
      io.output(i * (N) + j) <> PEs(i)(j).io.Out.bits
      when(state === s_idle) {
        PEs(i)(j).reset := true.B
        //        done_input := false.B
      }
    }
    //    printf("\n")
  }
}
