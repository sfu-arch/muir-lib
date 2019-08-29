package dnn_layers

import dnn.{DotIO, DotNode, ReduceNode, SatCounterModule}
import chisel3._
import chisel3.util.{Decoupled, Enum, Valid}
import chisel3.{Bundle, Flipped, Module, Output, RegInit, UInt, assert, printf, when}
import config.{Parameters, XLEN}
import config._
import dnn.types.OperatorDot
import interfaces.CustomDataBundle
import node.FXmatNxN
//import javafx.scene.chart.PieChart.Data
import node.{AluGenerator, HandShakingIONPS, HandShakingNPS, Shapes}

class Mac[L <: Shapes : OperatorDot](NumOuts: Int, ID: Int, lanes: Int, opCode: String)(left: => L)(implicit p: Parameters)
  extends HandShakingNPS(NumOuts, ID)(new CustomDataBundle(UInt(left.getWidth.W)))(p) {
  override lazy val io = IO(new DotIO(NumOuts)(left))

  val dotNode = Module(new DotNode(NumOuts = 1, ID = ID, lanes, "Mul")(left))
  val reduceNode = Module(new ReduceNode(NumOuts = 1, ID = ID, false, "Add")(new FXmatNxN(2,4)))

  // Connect IO to dotNode
  dotNode.io.enable <> io.enable
  dotNode.io.LeftIO <> io.LeftIO
  dotNode.io.RightIO <> io.RightIO

  reduceNode.io.LeftIO <> dotNode.io.Out(0)
  reduceNode.io.enable <> io.enable


  // Wire up Outputs
  for (i <- 0 until NumOuts) {
    io.Out(i) <> reduceNode.io.Out(i)
  }


  printf(p"\n Left ${io.LeftIO.bits.data} Right: ${io.RightIO.bits.data} Output: ${reduceNode.io.Out(0).bits.data}")

//  var classname: String = (left.getClass).toString
//  var signed            = "S"
//  override val printfSigil =
//    opCode + "[" + classname.replaceAll("class node.", "") + "]_" + ID + ":"

//  if (log == true && (comp contains "TYPOP")) {
//    val x = RegInit(0.U(xlen.W))
//    x := x + 1.U
//
//    verb match {
//      case "high" => {
//      }
//      case "med" => {
//      }
//      case "low" => {
//        printfInfo("Cycle %d : { \"Inputs\": {\"Left\": %x, \"Right\": %x},", x, (left_R.valid), (right_R.valid))
//        printf("\"State\": {\"State\": \"%x\", \"(L,R)\": \"%x,%x\",  \"O(V,D,P)\": \"%x,%x,%x\" },", state, left_R.data, right_R.data, io.Out(0).valid, data_R.data, io.Out(0).bits.predicate)
//        printf("\"Outputs\": {\"Out\": %x}", io.Out(0).fire( ))
//        printf("}")
//      }
//      case everythingElse => {
//      }
//    }
//  }
}


