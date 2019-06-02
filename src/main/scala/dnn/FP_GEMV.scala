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


object FPOperator_GEMV {

  implicit object FPmatNxN_FPvecN extends OperatorGEMV[FPmatNxN, FPvecN] {
    def addition(l: FPmatNxN, r: FPvecN)(implicit p: Parameters): FPmatNxN = {
      val x = Wire(new FPmatNxN(l.N, l.t))
      x
    }

    def subtraction(l: FPmatNxN, r: FPvecN)(implicit p: Parameters): FPmatNxN = {
      val x = Wire(new FPmatNxN(l.N, l.t))
      x
    }

    def multiplication(l: FPmatNxN, r: FPvecN)(implicit p: Parameters): FPvecN = {
      val x = Wire(new FPvecN(r.N, r.t))
      x
    }

  }

}