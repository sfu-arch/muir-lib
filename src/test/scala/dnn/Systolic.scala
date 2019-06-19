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

class SystolicBaseTests(df: SystolicSquare[UInt])(implicit p: config.Parameters) extends PeekPokeTester(df) {
  poke(df.io.activate, false.B)
  // left * right
  df.io.left.zipWithIndex.foreach { case (io, i) => poke(io, (i + 1).U) }
  df.io.right.zipWithIndex.foreach { case (io, i) => poke(io, (i + 1).U) }
  poke(df.io.activate, true.B)
  step(1)
  poke(df.io.activate, false.B)
  step(7)
  for (i <- 0 until df.N * df.N) {
    print(peek(df.io.output(i)) + ",")
  }
}


class SystolicTests(df: SystolicBLAS[UInt])(implicit p: config.Parameters) extends PeekPokeTester(df) {
  poke(df.io.activate, false.B)
  // left * right
  df.io.left.zipWithIndex.foreach { case (io, i) => poke(io, (i + 1).U) }
  df.io.right.zipWithIndex.foreach { case (io, i) => poke(io, (i + 1).U) }
  poke(df.io.activate, true.B)
  step(1)
  poke(df.io.activate, false.B)
  step(df.latency( ) - 1)
  for (i <- 0 until df.M * df.N) {
    print(peek(df.io.output(i)) + ",")
  }
}

class Systolic_Tester extends FlatSpec with Matchers {
  implicit val p = config.Parameters.root((new Mat_VecConfig).toInstance)
  it should "Typ Compute Tester" in {
    chisel3.iotesters.Driver.execute(Array("--backend-name", "verilator", "--target-dir", "test_run_dir"),
      () => new SystolicSquare(UInt(p(XLEN).W), 3)) {
      c => new SystolicBaseTests(c)
    } should be(true)

    chisel3.iotesters.Driver.execute(Array("--backend-name", "verilator", "--target-dir", "test_run_dir"),
      () => new SystolicBLAS(UInt(p(XLEN).W), 3, 3, 1)) {
      c => new SystolicTests(c)
    } should be(true)
  }
}
