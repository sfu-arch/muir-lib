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
  extends HandShakingIONPS(NumOuts) {
    // LeftIO: Left input data for computation
    val LeftIO  = Flipped(Decoupled(new DataBundle))

    // RightIO: Right input data for computation
    val RightIO = Flipped(Decoupled(new DataBundle))
}

class IcmpNode(NumOuts: Int, ID: Int, opCode: Int)
  (implicit p: Parameters)
  extends HandShakingNPS(NumOuts, ID)(p) {
    override lazy val io = IO(new ComputeNodeIO(NumOuts))
    // Printf debugging
    override val printfSigil = "Node ID: " + ID + " "

    /*===========================================*
     *            Registers                      *
     *===========================================*/
      // OP Inputs
      val left_R  = RegInit(DataBundle.default)
    
      // Memory Response
      val right_R = RegInit(DataBundle.default)

      // Output register
      val data_R = RegInit(0.U(xlen.W))

      val s_idle :: s_LATCH :: s_COMPUTE :: Nil = Enum(3)
      val state  = RegInit(s_idle)
    
    /*==========================================*
     *           Predicate Evaluation           *
     *==========================================*/
    
      val predicate = left_R.predicate & right_R.predicate & IsEnable()
      val start     = left_R.valid & right_R.valid & IsEnableValid()
    
    /*===============================================*
     *            Latch inputs. Wire up output       *
     *===============================================*/
    
      // Predicate register
      val pred_R = RegInit(init=false.B)

      //printfInfo("start: %x\n", start)

      io.LeftIO.ready := ~left_R.valid
      when(io.LeftIO.fire()) {
        //printfInfo("Latch left data\n")
        state := s_LATCH
        left_R.data  := io.LeftIO.bits.data
        left_R.valid := true.B
        left_R.predicate := io.LeftIO.bits.predicate
      }

      io.RightIO.ready := ~right_R.valid
      when(io.RightIO.fire()) {
        //printfInfo("Latch right data\n")
        state := s_LATCH
        right_R.data  := io.RightIO.bits.data
        right_R.valid := true.B
        right_R.predicate := io.RightIO.bits.predicate
      }

      // Wire up Outputs
      for (i <- 0 until NumOuts) {
        io.Out(i).bits.data      := data_R
        io.Out(i).bits.predicate := pred_R 
      }
    
    
    /*============================================*
     *            ACTIONS (possibly dangerous)    *
     *============================================*/

      //Instantiate ALU with selected code
      val FU = Module(new UCMP(xlen, opCode))

      FU.io.in1 := left_R.data
      FU.io.in2 := right_R.data

    when(start & predicate) {
      state := s_COMPUTE
      data_R:= FU.io.out
      pred_R:= predicate
      ValidOut()
    }.elsewhen(start & ~predicate) {
      //printfInfo("Start sending data to output INVALID\n")
      state := s_COMPUTE
      pred_R:= predicate
      ValidOut()
    }

    /*==========================================*
     *            Output Handshaking and Reset  *
     *==========================================*/


    val out_ready_W = out_ready_R.asUInt.andR
    val out_valid_W = out_valid_R.asUInt.andR

    //printfInfo("out_ready: %x\n", out_ready_W)
    //printfInfo("out_valid: %x\n", out_valid_W)

    //printfInfo(" Start restarting\n")
    when(out_ready_W & out_valid_W){
      //printfInfo("Start restarting output \n")
      // Reset data
      left_R  := DataBundle.default
      right_R := DataBundle.default
      // Reset output
      data_R:= 0.U
  	  out_ready_R := Vec(Seq.fill(NumOuts) { false.B })
      //Reset state
      state := s_idle
      //Restart predicate bit
      pred_R := false.B
    }

  //printfInfo(" State: %x\n", state)


  }