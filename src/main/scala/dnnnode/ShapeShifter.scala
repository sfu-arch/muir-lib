package dnnnode

import chisel3.util.{Decoupled, Queue}
import chisel3.{Flipped, Module, UInt, _}
import config.{Parameters, XLEN}
import dnn.types.{OperatorDot, OperatorReduction}
import interfaces.CustomDataBundle
import node.{HandShakingIONPS, HandShakingNPS, Shapes, matNxN, vecN}

class ShapeShifterIO[gen <: vecN , gen2 <: Shapes](NumIns: Int, NumOuts: Int)(shapeIn: => gen)(shapeOut: => gen2)(implicit p: Parameters)
  extends HandShakingIONPS(NumOuts)(new CustomDataBundle(UInt(shapeOut.getWidth.W))) {
    val in = Vec(NumIns, Flipped(Decoupled(new CustomDataBundle(UInt(shapeIn.getWidth.W)))))
    override def cloneType = new ShapeShifterIO(NumIns, NumOuts)(shapeIn)(shapeOut).asInstanceOf[this.type]
  }

class ShapeShifter[L <: vecN, K <: Shapes](NumIns: Int, NumOuts: Int, ID: Int, lanes: Int)(shapeIn: => L)(shapeOut: => K)(implicit p: Parameters)
  extends HandShakingNPS(NumOuts, ID)(new CustomDataBundle(UInt(shapeOut.getWidth.W)))(p) {
  override lazy val io = IO(new ShapeShifterIO(NumIns, NumOuts)(shapeIn)(shapeOut))

  val x = Wire(shapeIn)
  val y = x.toVecUInt()


  val z = x.data
  val buffer = Module(new Queue(shapeOut,40))

  val dataIn_R = RegInit(Vec(NumIns, CustomDataBundle.default(0.U(shapeIn.getWidth.W))))

  for (i <- 0 until NumIns) {
    dataIn_R(i) <> io.in(i)
  }





  //  printf(p"\n Left ${io.LeftIO.bits.data} Right: ${io.RightIO.bits.data} Output: ${reduceNode.io.Out(0).bits.data}")
}


