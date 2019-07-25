// See LICENSE for license details.
/*---------------------------------------------
                Log Branch, -pv
---------------------------------------------*/


package dandelion.node

import chisel3._
import chisel3.util._

import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester, OrderedDecoupledHWIOTester}
import org.scalatest.{Matchers, FlatSpec}

import dandelion.config._


// Tester.
class bitCastTester(df: BitCastNode)
                  (implicit p: Parameters) extends PeekPokeTester(df){

  poke(df.io.Input.bits.data, 2.U)
  poke(df.io.Input.valid, false.B)
  poke(df.io.Input.bits.predicate, false.B)

  poke(df.io.enable.bits.control , false.B)
  poke(df.io.enable.valid, false.B)
  poke(df.io.Out(0).ready, false.B)

  println(s"Output: ${peek(df.io.Out(0))}\n")
  //p
  val new_param = p.alterPartial(
    {case TRACE => true}
  )
  if(df.isDebug()){

    println(s"STATE of BitCastNode is : 0x${peek(df.io.LogCheck.get.bits.data.asUInt())}\n")
  }

  //V
  step(1)

  poke(df.io.enable.bits.control , true.B)
  poke(df.io.enable.valid, true.B)
  poke(df.io.Out(0).ready, true.B)


  poke(df.io.Input.valid, true.B)
  poke(df.io.Input.bits.predicate, true.B)



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


class BitCastTests extends  FlatSpec with Matchers {
   implicit val p = Parameters.root((new MiniConfig).toInstance)
  it should "Dataflow tester" in {
     chisel3.iotesters.Driver(() => new BitCastNode(NumOuts = 1, ID = 0, Debug = true)){
       c => new bitCastTester(c)
     } should be(true)
   }
 }



