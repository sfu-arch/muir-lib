package dandelion.junctions

import chisel3._
import chisel3.util._
import chisel3.iotesters.PeekPokeTester
import org.scalatest.{FreeSpec, Matchers}
import scala.collection.immutable.ListMap
import dandelion.interfaces._
import chipsalliance.rocketchip.config._
import dandelion.config._

class CombineCustomTester(c: CombineCustom) extends PeekPokeTester(c) {

  poke(c.io.In("field0").valid, false.B)
  poke(c.io.In("field0").bits.data, 0.U)
  poke(c.io.In("field1").valid, false.B)
  poke(c.io.In("field1").bits.data, 0.U)
  poke(c.io.In("field2").valid, false.B)
  poke(c.io.In("field2").bits.data, 0.U)

  step(1)

  expect(c.io.Out.bits("field0").data, 0)
  expect(c.io.Out.bits("field1").data, 0)
  expect(c.io.Out.bits("field2").data, 0)
  step(1)
  poke(c.io.In("field0").valid, true.B)
  poke(c.io.In("field0").bits.data, 0.U)
  poke(c.io.In("field1").valid, true.B)
  poke(c.io.In("field1").bits.data, 1.U)
  poke(c.io.In("field2").valid, true.B)
  poke(c.io.In("field2").bits.data, 2.U)
  step(1)
  poke(c.io.In("field0").valid, false.B)
  poke(c.io.In("field0").bits.data, 0.U)
  poke(c.io.In("field1").valid, false.B)
  poke(c.io.In("field1").bits.data, 0.U)
  poke(c.io.In("field2").valid, false.B)
  poke(c.io.In("field2").bits.data, 0.U)
  expect(c.io.Out.valid, true)
  expect(c.io.Out.bits("field0").data, 0)
  expect(c.io.Out.bits("field1").data, 1)
  expect(c.io.Out.bits("field2").data, 2)
  step(1)
  poke(c.io.Out.ready, true.B)
  step(1)
  expect(c.io.Out.valid, false)
  expect(c.io.Out.bits("field0").data, 0)
  expect(c.io.Out.bits("field1").data, 1)
  expect(c.io.Out.bits("field2").data, 2)

  for (i <- 0 until 10) {
    step(1)
  }

}


class CombineDecoupledTests extends FreeSpec with Matchers {
  implicit val p = new WithAccelConfig
  "Combine discrete CustomDataBundle I/O into single Decoupled IO bundle" in {
    chisel3.iotesters.Driver.execute(
      Array(//"-ll", "Info",
        "-tbn", "firrtl",
        "-td", "test_run_dir",
        "-tts", "0001")
      , () => new CombineCustom(List(UInt(32.W), UInt(16.W), UInt(8.W)))) { c =>
      new CombineCustomTester(c)
    } should be (true)
  }
}
