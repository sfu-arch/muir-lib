package dandelion.memory.stack

import chisel3._
import chisel3.util._
import chisel3.Module
import chisel3.testers._
import chisel3.iotesters.{ChiselFlatSpec, Driver, OrderedDecoupledHWIOTester, PeekPokeTester}
import org.scalatest.{FlatSpec, Matchers}
import chipsalliance.rocketchip.config._
import dandelion.config._
import dandelion.interfaces._
import dandelion.arbiters._
import util._
import utility.UniformPrintfs
import muxes._
import dandelion.node._
import dandelion.config._

class StackIO(NumOps: Int)
             (implicit p: Parameters) extends AccelBundle()(p) {
  val InData = Vec(NumOps, Flipped(Decoupled(new AllocaReq)))
  val OutData = Vec(NumOps, Valid(new AllocaResp))

  override def cloneType = new StackIO(NumOps).asInstanceOf[this.type]
}

class Stack(NumOps: Int)
           (implicit val p: Parameters) extends Module with HasAccelParams with UniformPrintfs{
  override lazy val io = IO(new StackIO(NumOps))

  /**
    * Instantiating Arbiter module and connecting inputs to the output
    * @note we fix the base size to 8
    */
  val in_arbiter = Module(new Arbiter(new AllocaReq, NumOps))
  for( i <- 0 until NumOps){
    in_arbiter.io.in(i) <> io.InData(i)
  }

  /**
    * Arbiter's output is always ready
    */

  in_arbiter.io.out.ready := true.B

  /**
    * Stack pointer Update
    */
  val SP = RegInit(0.U)
  //val old_SP = RegInit(0.U)

  when(in_arbiter.io.out.fire){
    SP := SP + (in_arbiter.io.out.bits.numByte * in_arbiter.io.out.bits.size)
  }

  // Copy arbiter output and pointer to all outputs.
  // Assert valid to the output corresponding to the arbiter grant
  for (i <- 0 until NumOps) {
    io.OutData(i).bits.ptr := SP
    io.OutData(i).bits.RouteID := in_arbiter.io.out.bits.RouteID
    io.OutData(i).valid := false.B
  }
  io.OutData(in_arbiter.io.chosen).valid := in_arbiter.io.out.valid

}

