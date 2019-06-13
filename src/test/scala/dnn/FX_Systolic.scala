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

class FX_SystolicTests(df: grid)(implicit p: config.Parameters) extends PeekPokeTester(df) {
  poke(df.io.activate, false.B)
  // left * right
  //  df.io.left.zipWithIndex.foreach { case (io, i) => poke(io, (i + 1).U) }
  //  df.io.right.zipWithIndex.foreach { case (io, i) => poke(io, (i + 1).U) }
  poke(df.io.left(0), 0x140)
  poke(df.io.left(1), 0x140)
  poke(df.io.left(2), 0x140)
  poke(df.io.left(3), 0x140)

  poke(df.io.right(0), 0x140)
  poke(df.io.right(1), 0x140)
  poke(df.io.right(2), 0x140)
  poke(df.io.right(3), 0x140)

  poke(df.io.activate, true.B)
  step(1)
  poke(df.io.activate, false.B)
  step(6)
  for (i <- 0 until df.N * df.N) {
    //  print(peek(df.io.output(i)) + ",")
  }
}

class FX_Systolic_Tester extends FlatSpec with Matchers {
  implicit val p = config.Parameters.root((new MiniConfig).toInstance)
  it should "Typ Compute Tester" in {
    chisel3.iotesters.Driver.execute(Array("--backend-name", "verilator", "--target-dir", "test_run_dir"),
      () => new grid(2, 0.BP)) {
      c => new FX_SystolicTests(c)
    } should be(true)
  }
}
