package dnn

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
import node._
import utility.UniformPrintfs

class PEIO()(implicit p: Parameters) extends CoreBundle( )(p) {
  // LeftIO: Left input data for computation
  val Left = Input(UInt(xlen.W))

  // RightIO: Right input data for computation
  val Top = Input(UInt(xlen.W))

  val Right = Output(UInt(xlen.W))

  val Bottom = Output(UInt(xlen.W))

  val Out = Output(UInt(xlen.W))

}


class PE(t: FType, left_delay: Int, top_delay: Int)(implicit val p: Parameters)
  extends Module with CoreParams with UniformPrintfs {
  val io = IO(new PEIO)

  val top_reg  = Pipe(true.B, io.Top, latency = top_delay)
  val left_reg = Pipe(true.B, io.Left, latency = left_delay)

  val FU          = Module(new FPUALU(xlen, "Mac", t))
  val accumalator = RegNext(next = FU.io.out, init = 0.U)

  FU.io.in1 := top_reg
  FU.io.in2 := left_reg
  FU.io.in3.get := accumalator

  io.Right := left_reg
  io.Bottom := top_reg
}

class systolic(val N: Int, t: FType)()(implicit val p: Parameters)
  extends Module with CoreParams with UniformPrintfs {

  val io = IO(new Bundle {})

  val grid = for (i <- 0 until N - 1) yield {
    for (j <- 0 until N - 1) yield {
      val FPadd =
        if (i == 0 && j == 0) {
          Module(new PE(t, top_delay = 0, left_delay = 0))
        } else if (i == 0) {
          Module(new PE(t, top_delay = i, left_delay = 1))
        } else if (j == 0) {
          Module(new PE(t, top_delay = 1, left_delay = i))
        }
        else {
          Module(new PE(t, top_delay = 1, left_delay = 1))
        }
    }
  }
}

object FPOperator_GEMV {

  implicit object FPmatNxN_FPvecN extends OperatorGEMV[FPmatNxN, FPvecN] {
    def addition(l: FPmatNxN, r: FPvecN)(implicit p: Parameters): FPmatNxN = {
      //    require((l.N & (l.N - 1)) == 0, "left operand not a power of 2")
      val x = Wire(new FPmatNxN(l.N, l.t))
      for (i <- 0 until l.N) {
        for (j <- 0 until l.N) {
          val FPadd = Module(new FPUALU(p(XLEN), "Add", l.t))
          FPadd.io.in1 := l.data(i)(j)
          if (r.isCol == 0) {
            FPadd.io.in2 := r.data(j)
          } else {
            FPadd.io.in2 := r.data(i)
          }
          x.data(i)(j) := FPadd.io.out
        }
      }
      x
    }

    def subtraction(l: FPmatNxN, r: FPvecN)(implicit p: Parameters): FPmatNxN = {
      val x = Wire(new FPmatNxN(l.N, l.t))
      for (i <- 0 until l.N) {
        for (j <- 0 until l.N) {
          val FPadd = Module(new FPUALU(p(XLEN), "Sub", l.t))
          FPadd.io.in1 := l.data(i)(j)
          if (r.isCol == 0) {
            FPadd.io.in2 := r.data(j)
          } else {
            FPadd.io.in2 := r.data(i)
          }
          x.data(i)(j) := FPadd.io.out
        }
      }
      x
    }

    def multiplication(l: FPmatNxN, r: FPvecN)(implicit p: Parameters): FPvecN = {
      require(r.isCol == 1, "Right vector should be a column vector")
      val x = Wire(new FPvecN(r.N, r.t))


      x
    }

    def getfns(l: Numbers, r: Numbers)(implicit p: Parameters): Array[(Int, Numbers)] = {
      Array(
        GEMV_OpCode.Add -> addition(l.asInstanceOf[FPmatNxN], r.asInstanceOf[FPvecN]),
        GEMV_OpCode.Sub -> subtraction(l.asInstanceOf[FPmatNxN], r.asInstanceOf[FPvecN]),
        GEMV_OpCode.Mul -> multiplication(l.asInstanceOf[FPmatNxN], r.asInstanceOf[FPvecN])
      )
    }

  }

}