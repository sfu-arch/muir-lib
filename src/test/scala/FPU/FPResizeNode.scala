// See LICENSE for license details.

package dandelion.fpu

import chisel3._
import chisel3.util._

import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester, OrderedDecoupledHWIOTester}
import org.scalatest.{Matchers, FlatSpec}

import chipsalliance.rocketchip.config._
import chipsalliance.rocketchip.config._
import dandelion.config._
import util._


// Tester.
class FPResizeTester(df: FNtoFNNode)
                    (implicit p: Parameters) extends PeekPokeTester(df) {

  poke(df.io.Input.bits.data, 0x43000000)
  poke(df.io.Input.valid, false.B)
  poke(df.io.Input.bits.predicate, false.B)

  poke(df.io.enable.bits.control, false.B)
  poke(df.io.enable.valid, false.B)
  poke(df.io.Out(0).ready, false.B)
  println(s"Output: ${peek(df.io.Out(0))}\n")


  step(1)

  poke(df.io.enable.bits.control, true.B)
  poke(df.io.enable.valid, true.B)
  poke(df.io.Out(0).ready, true.B)


  poke(df.io.Input.valid, true.B)
  poke(df.io.Input.bits.predicate, true.B)

  println(s"Output: ${peek(df.io.Out(0))}\n")

  println(s"t: -1\n -------------------------------------")
  step(1)


  for (i <- 0 until 10) {
    println(s"Output: ${peek(df.io.Out(0))}\n")

    println(s"t: ${i}\n -------------------------------------")
    step(1)
  }
}

class FPResizeTests extends FlatSpec with Matchers {
  implicit val p = new WithAccelConfig ++ new WithTestConfig
  it should "Dataflow tester" in {
    chisel3.iotesters.Driver(() => new FNtoFNNode(FType.S, FType.H, NumOuts = 1, ID = 0)) {
      c => new FPResizeTester(c)
    } should be(true)
  }
}
