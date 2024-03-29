package dandelion.dataflow

import chisel3._
import chisel3.util._
import chisel3.Module
import chipsalliance.rocketchip.config._
import dandelion.control.{BasicBlockNoMaskFastNode, BasicBlockNode}
import util._
import dandelion.interfaces._
import dandelion.node._
import dandelion.memory.stack._
import dandelion.config._



//TODO uncomment if you remove StackCentral.scala file
//
abstract class StackDFIO(implicit val p: Parameters) extends Module with HasAccelParams {
  val io = IO(new Bundle {
    val Data0 = Flipped(Decoupled(new AllocaIO))
    val pred = Decoupled(new Bool())
    val result = Decoupled(new DataBundle)
  })
}

class StackDF(implicit p: Parameters) extends StackDFIO() {


  /**
    * @note Module's variables they should set during initialization
    */
  //BasicBlock
  val b0_entry = Module(new BasicBlockNoMaskFastNode(NumInputs = 1, NumOuts = 1, BID = 0))

  //Compute
  val m0 = Module(new AllocaNode(NumOuts = 1,ID = 0,RouteID=0))
//  val m5 = Module(new ComputeNode(NumOuts = 1, ID = 5, opCode = "Add")(sign = false))

  //Stack
  val stack = Module(new Stack(NumOps = 1))

  /**
    * Wireing control signals from BasicBlock nodes
    * to their child
    */

  //Grounding entry BasicBlock
  b0_entry.io.predicateIn.foreach(_.bits.debug := false.B)
  b0_entry.io.predicateIn(0).bits.control := true.B
  b0_entry.io.predicateIn(0).bits.taskID := 0.U
  b0_entry.io.predicateIn(0).valid := true.B

  /**
    * Wireing enable signals to the instructions
    */
  //Wiring enable signals
  m0.io.enable <> b0_entry.io.Out(0)

  /**
    * Connecting Dataflow signals
    */
  //dataflow signal
  stack.io.InData(0) <> m0.io.allocaReqIO
  m0.io.allocaRespIO <> stack.io.OutData(0)


  /**
    * Wireing constants
    */
  m0.io.allocaInputIO <> io.Data0
//  m0.io.allocaInputIO.bits.size := 3.U
//  m0.io.allocaInputIO.bits.numByte := 4.U
//  m0.io.allocaInputIO.bits.predicate := true.B
//  m0.io.allocaInputIO.valid := true.B

  //Output
  io.result <> m0.io.Out(0)
  io.pred.valid := true.B
  io.pred.bits := true.B

  //DEBUG
//  io.pred <> b1_then.io.Out(0)

}
