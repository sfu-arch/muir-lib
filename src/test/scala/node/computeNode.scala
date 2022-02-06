// See LICENSE for license details.

package dandelion.node

import chisel3._
import chisel3.util._

import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester, OrderedDecoupledHWIOTester}
import org.scalatest.{Matchers, FlatSpec}

import chipsalliance.rocketchip.config._
import dandelion.config._


// Tester.
class computeTester(df: ComputeNode)
                  (implicit p: Parameters) extends PeekPokeTester(df){

  poke(df.io.LeftIO.bits.data, 2.U)
  poke(df.io.LeftIO.valid, false.B)
  poke(df.io.LeftIO.bits.predicate, false.B)

  poke(df.io.RightIO.bits.data, 1.U)
  poke(df.io.RightIO.valid, false.B)
  poke(df.io.RightIO.bits.predicate, false.B)

  poke(df.io.enable.bits.control , false.B)
  poke(df.io.enable.valid, false.B)
  poke(df.io.Out(0).ready, false.B)
  println(s"Output: ${peek(df.io.Out(0))}\n")

  //V
  step(1)

  poke(df.io.enable.bits.control , true.B)
  poke(df.io.enable.valid, true.B)
  poke(df.io.Out(0).ready, true.B)


  poke(df.io.LeftIO.valid, true.B)
  poke(df.io.RightIO.valid, true.B)
  poke(df.io.LeftIO.bits.predicate, true.B)
  poke(df.io.RightIO.bits.predicate, true.B)


  println(s"Output: ${peek(df.io.Out(0))}\n")
  //P

  //V
  println(s"t: -1\n -------------------------------------")
  step(1)


  for( i <- 0 until 10){
    println(s"Output: ${peek(df.io.Out(0))}\n")

    println(s"t: ${i}\n -------------------------------------")
    step(1)
  }
 }


class CompTests extends  FlatSpec with Matchers {
   implicit val p = new WithAccelConfig ++ new WithTestConfig
  it should "Dataflow tester" in {
     chisel3.iotesters.Driver.execute(
       Array("-tbn", "firrtl"),
       () => new ComputeNode(NumOuts = 1, ID = 0, opCode = "Add")(sign = false, Debug = false)) {
       c => new computeTester(c)
     } should be(true)
   }
 }



