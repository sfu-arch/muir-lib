package node

import chisel3._
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester, OrderedDecoupledHWIOTester}
import chisel3.Module
import chisel3.testers._
import chisel3.util._
import org.scalatest.{Matchers, FlatSpec}

import config._
import interfaces._
import muxes._
import util._

class IcmpNodeIO(NumOuts: Int)
                   (implicit p: Parameters)
extends HandShakingIONPS (NumOuts)(new DataBundle) {
// LeftIO: Left input data for computation
val LeftIO = Flipped (Decoupled (new DataBundle) )

// RightIO: Right input data for computation
val RightIO = Flipped (Decoupled (new DataBundle) )
}

class IcmpNode(NumOuts: Int, ID: Int, opCode: String)
                 (sign: Boolean)
                 (implicit p: Parameters)
  extends HandShakingNPS(NumOuts, ID)(new DataBundle)(p) {
  override lazy val io = IO(new ComputeNodeIO(NumOuts))
  // Printf debugging
  override val printfSigil = "Node ID: " + ID + " "

  /*===========================================*
   *            Registers                      *
   *===========================================*/
  // Left Input
  val left_R = RegInit(DataBundle.default)
  val left_valid_R = RegInit(false.B)

  // Right Input
  val right_R = RegInit(DataBundle.default)
  val right_valid_R = RegInit(false.B)

  // Output register
  val data_R = RegInit(0.U(xlen.W))

  val s_idle :: s_LATCH :: s_COMPUTE :: Nil = Enum(3)
  val state = RegInit(s_idle)

  /*==========================================*
   *           Predicate Evaluation           *
   *==========================================*/

  val predicate = left_R.predicate & right_R.predicate & IsEnable()
  val start = left_R.valid & right_R.valid & IsEnableValid()

  /*===============================================*
   *            Latch inputs. Wire up output       *
   *===============================================*/

  //Instantiate ALU with selected code
  var FU = Module(new UCMP(xlen, opCode))
  FU.io.in1 := left_R.data
  FU.io.in2 := right_R.data

  io.LeftIO.ready := ~left_valid_R
  when(io.LeftIO.fire()) {
    //printfInfo("Latch left data\n")
    state := s_LATCH
    left_R <> io.LeftIO.bits
    left_valid_R := true.B
  }

  io.RightIO.ready := ~right_valid_R
  when(io.RightIO.fire()) {
    //printfInfo("Latch right data\n")
    state := s_LATCH
    right_R <> io.RightIO.bits
    right_valid_R := true.B
  }

  // Wire up Outputs
  for (i <- 0 until NumOuts) {
    io.Out(i).bits.data := FU.io.out
    io.Out(i).bits.predicate := predicate
  }


  /*============================================*
   *            ACTIONS (possibly dangerous)    *
   *============================================*/


  when(start & state =/= s_COMPUTE) {
    state := s_COMPUTE
    ValidOut()
  }

  /*==========================================*
   *            Output Handshaking and Reset  *
   *==========================================*/

  when(IsOutReady() & (state === s_COMPUTE)) {
    // Reset data
    left_R := DataBundle.default
    right_R := DataBundle.default

    left_valid_R := false.B
    right_valid_R := false.B

    // Reset output
    //Reset state
    state := s_idle
    //Reset output
    Reset()
  }

  //printfInfo(" State: %x\n", state)


}
