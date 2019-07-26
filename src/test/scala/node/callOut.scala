package dandelion.node
//____________________________________
//              fix errors
//______________________________________
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester, OrderedDecoupledHWIOTester}
import org.scalatest.{Matchers, FlatSpec}

import dandelion.config._
import utility._
import chisel3._
import chisel3.util._
import dandelion.config._
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester, OrderedDecoupledHWIOTester}
import org.scalatest.{Matchers, FlatSpec}

class CallOutTests(c: UnTypStore) (implicit p: Parameters)  extends PeekPokeTester(df) {

    poke(c.io.enable.valid,false)
    poke(c.io.PredOp(0).valid,true)


    poke(c.io.SuccOp(0).ready,true)
    poke(c.io.Out(0).ready,false)



    step(1)

  val new_param = p.alterPartial(
    {case TRACE => true}
  )
  if(df.isDebug()){

    println(s"STATE of Call Out is : 0x${peek(df.io.LogCheck.get.bits.data.asUInt())}\n")
  }
    poke(c.io.enable.valid,true)


    printf(s"t: ${t}  io.Out: ${peek(c.io.Out(0))} \n")




}




class CallOutNodeTester extends  FlatSpec with Matchers {
  implicit val p = Parameters.root((new MiniConfig).toInstance)
  it should "Store Node tester" in {
    chisel3.iotesters.Driver(() => new CallOutNode(ID= 1, argTypes= List(1,2) , NumSuccOps = 1 , NoReturn = true.B, Debug = true)) { c =>
      new CallOutTests(c)
    } should be(true)
  }
}
