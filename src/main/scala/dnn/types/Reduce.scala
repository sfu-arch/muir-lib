package dnn.types

import FPU.{FPMAC, FType, FloatingPoint}
import chisel3._
import chisel3.iotesters.{ChiselFlatSpec, Driver, OrderedDecoupledHWIOTester, PeekPokeTester}
import chisel3.Module
import chisel3.experimental.FixedPoint
import chisel3.testers._
import chisel3.util._
import org.scalatest.{FlatSpec, Matchers}
import config._
import interfaces._
import muxes._
import util._
import node._
import dnn._
//import FPOperator_Reduction._

trait OperatorReduction[T] {
  def magic(l: T, r: UInt, start: Bool, opcode: String)(implicit p: Parameters): (UInt, Int)

}

object OperatorReduction {

  implicit object FXmatNxN_FX extends OperatorReduction[FXmatNxN] {
    def magic(l: FXmatNxN, r: UInt, start: Bool, opcode: String)(implicit p: Parameters): (UInt, Int) = {
      val flatvec = l.toVecUInt( )
      val FU = Module(new NCycle_Reduction(l.data(0)(0), flatvec.length, opcode = opcode))
      FU.io.activate := start
      flatvec zip FU.io.input_vec foreach { case (a, b) => b := a }
      (FU.io.output,FU.latency())
    }
  }

  implicit object FXvecN_UInt extends OperatorReduction[FXvecN] {
    def magic(l: FXvecN, r: UInt, start: Bool, opcode: String)(implicit p: Parameters): ( UInt, Int) = {
      val flatvec = l.toVecUInt( )
      val FU = Module(new NCycle_Reduction(l.data(0), flatvec.length, opcode = opcode))
      FU.io.activate := start
      flatvec zip FU.io.input_vec foreach { case (a, b) => b := a }
      (FU.io.output,FU.latency())
    }
  }

  implicit object matNxN_UInt extends OperatorReduction[matNxN] {
    def magic(l: matNxN, r: UInt, start: Bool, opcode: String)(implicit p: Parameters): ( UInt, Int) = {
      val flatvec = l.toVecUInt( )
      val FU = Module(new NCycle_Reduction(l.data(0)(0), flatvec.length, opcode = opcode))
      FU.io.activate := start
      flatvec zip FU.io.input_vec foreach { case (a, b) => b := a }
      (FU.io.output,FU.latency())
    }
  }

  implicit object vecN_UInt extends OperatorReduction[vecN] {
    def magic(l: vecN, r: UInt, start: Bool, opcode: String)(implicit p: Parameters): ( UInt, Int) = {
      val flatvec = l.toVecUInt( )
      val FU = Module(new NCycle_Reduction(l.data(0), flatvec.length, opcode = opcode))
      FU.io.activate := start
      flatvec zip FU.io.input_vec foreach { case (a, b) => b := a }
      (FU.io.output,FU.latency())
    }
  }

  implicit object FPmatNxN_FX extends OperatorReduction[FPmatNxN] {
    def magic(l: FPmatNxN, r: UInt, start: Bool, opcode: String)(implicit p: Parameters): (UInt, Int) = {
      val flatvec = l.toVecUInt( )
      val FU = Module(new NCycle_Reduction(new FloatingPoint(l.t), flatvec.length, pipelined = true, opcode))
      FU.io.activate := start
      flatvec zip FU.io.input_vec foreach { case (a, b) => b := a }
      (FU.io.output, FU.latency())
    }
  }

  implicit object FPvecN_UInt extends OperatorReduction[FPvecN] {
    def magic(l: FPvecN, r: UInt, start: Bool, opcode: String)(implicit p: Parameters): ( UInt, Int) = {
      val flatvec = l.toVecUInt( )
      val FU = Module(new NCycle_Reduction(new FloatingPoint(l.t), flatvec.length, pipelined = true, opcode))
      FU.io.activate := start
      flatvec zip FU.io.input_vec foreach { case (a, b) => b := a }
      (FU.io.output, FU.latency())
    }
  }

  def magic[T](l: T, r: UInt, start: Bool, opcode: String)(implicit op: OperatorReduction[T], p: Parameters): (UInt, Int) = op.magic(l, r, start, opcode)


}




