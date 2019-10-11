package dnn

/**
  * Created by nvedula on 15/5/17.
  */


import chisel3.iotesters.PeekPokeTester
import config._
import dnnnode.TStore
import node.matNxN
import org.scalatest.{FlatSpec, Matchers}
import utility._

class TStoreNodeTests(df: TStore[matNxN]) (implicit p: config.Parameters) extends PeekPokeTester(df) {
    poke(df.io.GepAddr.valid,false)
    poke(df.io.enable.valid,false)
    poke(df.io.inData.valid,false)
    poke(df.io.PredOp(0).valid,true)
    poke(df.io.tensorReq.ready,false)
    poke(df.io.tensorResp.valid,false)


    poke(c.io.SuccOp(0).ready,true)
    poke(c.io.Out(0).ready,false)


    for (t <- 0 until 20) {

     step(1)

      //IF ready is set
      // send address
      if (peek(c.io.GepAddr.ready) == 1) {
        poke(c.io.GepAddr.valid, true)
        poke(c.io.GepAddr.bits.data, 12)
        poke(c.io.GepAddr.bits.predicate, true)
        poke(c.io.inData.valid, true)
        poke(c.io.inData.bits.data, t+1)
        poke(c.io.inData.bits.predicate,true)
// //         poke(c.io.inData.bits.valid,true)
        poke(c.io.enable.bits.control,true)
        poke(c.io.enable.valid,true)
      }

      if((peek(c.io.tensorReq.valid) == 1) && (t > 4))
      {
        poke(c.io.tensorReq.ready,true)
      }

      if (t > 5 && peek(c.io.tensorReq.ready) == 1)
      {
        // poke(c.io.memReq.ready,false)
        // poke(c.io.memResp.data,t)
        poke(c.io.tensorResp.valid,true)
      }
          printf(s"t: ${t}  io.Out: ${peek(c.io.Out(0))} \n")

    }


}



import utility.Constants._

class TStoreNodeTester extends  FlatSpec with Matchers {
  implicit val p = config.Parameters.root((new MiniConfig).toInstance)
  it should "TStore Node tester" in {
    chisel3.iotesters.Driver(() => new TStore(NumPredOps=1,NumSuccOps=1,NumOuts=1,ID=1,RouteID=0)(new matNxN(2, false))) { c =>
      new TStoreNodeTests(c)
    } should be(true)
  }
}
