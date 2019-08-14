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


class ReductionTests(df: NCycle_Reduction[UInt])(implicit p: config.Parameters) extends PeekPokeTester(df) {
  poke(df.io.activate, false.B)
  // left * right
  df.io.input_vec.zipWithIndex.foreach { case (io, i) => poke(io, (i).U) }
  poke(df.io.activate, true.B)
  step(1)
  poke(df.io.activate, false.B)
  for (i <- 0 until df.latency( )) {
    print(peek(df.io.output))
    print("\n")
    step(1)
  }
}


class Reduction_Tester extends FlatSpec with Matchers {
  implicit val p = config.Parameters.root((new Mat_VecConfig).toInstance)
  it should "Typ Compute Tester" in {
    chisel3.iotesters.Driver.execute(Array("--backend-name", "verilator", "--target-dir", "test_run_dir"),
      () => new NCycle_Reduction(UInt(p(XLEN).W), N = 4, pipelined = true, opcode = "add")) {
      c => new ReductionTests(c)
    } should be(true)
  }
}
