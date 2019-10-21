package dnnnode

import chisel3.util.{Decoupled, Queue}
import chisel3.{Flipped, Module, UInt, _}
import config.{Parameters, XLEN}
import dnn.types.{OperatorDot, OperatorReduction}
import interfaces.CustomDataBundle
import node.{HandShakingIONPS, HandShakingNPS, Shapes, matNxN}

class ShifterIO[gen <: Shapes](NumIns: Int, NumOuts: Int)(shapeIn: => gen)(shapeOut: => gen)(implicit p: Parameters)
  extends HandShakingIONPS(NumOuts)(new CustomDataBundle(UInt(shapeOut.getWidth.W))) {
    val in = Vec(NumIns, Flipped(Decoupled(new CustomDataBundle(UInt(shapeIn.getWidth.W)))))
    override def cloneType = new ShifterIO(NumIns, NumOuts)(shapeIn)(shapeOut).asInstanceOf[this.type]
  }

class Shifter[L <: Shapes](NumIns: Int, NumOuts: Int, ID: Int, lanes: Int)(shapeIn: => L)(shapeOut: => L)(implicit p: Parameters)
  extends HandShakingNPS(NumOuts, ID)(new CustomDataBundle(UInt(shapeOut.getWidth.W)))(p) {
  override lazy val io = IO(new ShifterIO(NumIns, NumOuts)(shapeIn)(shapeOut))



  val buffer = Module(new Queue(shapeIn,40))

  val dataIn_R = RegInit(Vec(NumIns, CustomDataBundle.default(0.U(shapeIn.getWidth.W))))






  //  printf(p"\n Left ${io.LeftIO.bits.data} Right: ${io.RightIO.bits.data} Output: ${reduceNode.io.Out(0).bits.data}")
}


