// See LICENSE for license details.

package dandelion.node

import chisel3._
import chisel3.util._

import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester, OrderedDecoupledHWIOTester}
import org.scalatest.{Matchers, FlatSpec}

import dandelion.config._


// Tester.
class constTester(df: ConstNode)
                  (implicit p: Parameters) extends PeekPokeTester(df){


  poke(df.io.enable.bits.control , false.B)
  poke(df.io.enable.valid, false.B)
  poke(df.io.Out(0).ready, false.B)
  println(s"Output: ${peek(df.io.Out(0))}\n")
  //p
  val new_param = p.alterPartial(
    {case TRACE => true}
  )
  if(df.isDebug()){

    println(s"STATE of ConstNode is : 0x${peek(df.io.LogCheck.get.bits.data.asUInt())}\n")
  }

  //V
  step(1)

  poke(df.io.enable.bits.control , true.B)
  poke(df.io.enable.valid, true.B)
  poke(df.io.Out(0).ready, true.B)




  println(s"Output: ${peek(df.io.Out(0))}\n")
  //P
  if(df.isDebug()){

    println(s"STATE of ConsteNode is : 0x${peek(df.io.LogCheck.get.bits.data.asUInt())}\n")
  }

 }


class ConstTests extends  FlatSpec with Matchers {
   implicit val p = Parameters.root((new MiniConfig).toInstance)
  it should "Dataflow tester" in {
     chisel3.iotesters.Driver(() => new ConstNode(value = 0 ,  NumOuts = 1, ID = 0, Debug = true)) {
       c => new constTester(c)
     } should be(true)
   }
 }



