// See LICENSE for license details.

package dnn

import chisel3._
import chisel3.util._

import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester, OrderedDecoupledHWIOTester}
import org.scalatest.{Matchers, FlatSpec}

import node._
import dataflow._
import muxes._
import config._
import util._
import interfaces._


// Tester.
class Mat_X_CompTests(df: Mat_X_Compute[vecN, vecN])
                     (implicit p: config.Parameters) extends PeekPokeTester(df) {
  poke(df.io.enable.valid, true)
  poke(df.io.enable.bits.control, true)

  poke(df.io.LeftIO.bits.data, 0x0004000300020001L)
  poke(df.io.LeftIO.valid, true)
  poke(df.io.LeftIO.bits.predicate, true)


  poke(df.io.RightIO.bits.data, 0x0004000300020001L)
  poke(df.io.RightIO.valid, true)
  poke(df.io.RightIO.bits.predicate, true)

  step(5)
}


class Mat_X_CompTester extends FlatSpec with Matchers {
  implicit val p = config.Parameters.root((new Mat_VecConfig).toInstance)
  it should "Typ Compute Tester" in {
    chisel3.iotesters.Driver.execute(Array("--backend-name", "verilator", "--target-dir", "test_run_dir"),
      () => new Mat_X_Compute(NumOuts = 1, ID = 0, opCode = "Mul")(sign = false)(new matNxN(2), new vecN(2))(new vecN(2))) {
      c => new Mat_X_CompTests(c)
    } should be(true)
  }
}
