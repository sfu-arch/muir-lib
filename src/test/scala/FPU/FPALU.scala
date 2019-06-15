// See LICENSE for license details.

package FPU

import chisel3._
import chisel3.util._
import FPU._
import FType._

import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester, OrderedDecoupledHWIOTester}
import org.scalatest.{Matchers, FlatSpec}

import node._
import dataflow._
import muxes._
import config._
import util._
import interfaces._


// Tester.
class FPUALUTester(df: FPUALU)
                  (implicit p: config.Parameters) extends PeekPokeTester(df) {


  poke(df.io.in1, 0x4400.U)
  poke(df.io.in2, 0x4400.U)
  poke(df.io.in3.get, 0x4800.U)
  step(1)
  println(s"Output: ${(peek(df.io.out))}\n")
}

class FPALUTests extends FlatSpec with Matchers {
  implicit val p = config.Parameters.root((new HALFPrecisionFPConfig).toInstance)
  it should "FP MAC tester" in {
    chisel3.iotesters.Driver(
      () => new FPUALU(xlen = p(XLEN), opCode = "Mac", t = H)) {
      c => new FPUALUTester(c)
    } should be(true)
  }
}