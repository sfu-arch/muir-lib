package dandelion.junctions

import chisel3._
import chisel3.util._
import chisel3.iotesters.PeekPokeTester
import org.scalatest.{FreeSpec, Matchers}
import scala.collection.immutable.ListMap
import dandelion.interfaces._
import chipsalliance.rocketchip.config._
import dandelion.config._

class SplitCustomTester(c: SplitCustom) extends PeekPokeTester(c) {

  poke(c.io.In.valid, false.B)
  poke(c.io.In.bits("field0").data, 0.U)
  poke(c.io.In.bits("field1").data, 0.U)
  poke(c.io.In.bits("field2").data, 0.U)
  poke(c.io.Out("field0").ready, false.B)
  poke(c.io.Out("field1").ready, false.B)
  poke(c.io.Out("field2").ready, false.B)
  expect(c.io.Out("field0").bits.data, 0)
  expect(c.io.Out("field1").bits.data, 0)
  expect(c.io.Out("field2").bits.data, 0)
  expect(c.io.Out("field0").valid, false)
  expect(c.io.Out("field1").valid, false)
  expect(c.io.Out("field2").valid, false)
  step(1)
  poke(c.io.In.valid, true.B)
  poke(c.io.In.bits("field0").data, 1.U)
  poke(c.io.In.bits("field1").data, 2.U)
  poke(c.io.In.bits("field2").data, 3.U)
  poke(c.io.Out("field0").ready, true.B)
  poke(c.io.Out("field1").ready, true.B)
  poke(c.io.Out("field2").ready, true.B)
  expect(c.io.Out("field0").valid, false)
  expect(c.io.Out("field1").valid, false)
  expect(c.io.Out("field2").valid, false)
  step(1)
  poke(c.io.In.valid, false.B)
  expect(c.io.Out("field0").bits.data, 1)
  expect(c.io.Out("field1").bits.data, 2)
  expect(c.io.Out("field2").bits.data, 3)
  expect(c.io.Out("field0").valid, true)
  expect(c.io.Out("field1").valid, true)
  expect(c.io.Out("field2").valid, true)
  step(1)
  expect(c.io.Out("field0").valid, false)
  expect(c.io.Out("field1").valid, false)
  expect(c.io.Out("field2").valid, false)

  for (i <- 0 until 10) {
    step(1)
  }

}

class SplitDecoupledTests extends FreeSpec with Matchers {
  implicit val p = new WithAccelConfig
  "Split discrete CustomDataBundle I/O into single Decoupled IO bundle" in {
    chisel3.iotesters.Driver.execute(
      Array(//"-ll", "Info",
        "-tbn", "firrtl",
        "-td", "test_run_dir",
        "-tts", "0001")
      , () => new SplitCustom(List(UInt(32.W), UInt(16.W), UInt(8.W)))) { c =>
      new SplitCustomTester(c)
    } should be (true)
  }

}
