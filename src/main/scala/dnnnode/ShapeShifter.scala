package dnnnode

import Chisel.Enum
import chisel3.util.{Decoupled, Queue}
import chisel3.{Flipped, Module, UInt, _}
import config.{Parameters, XLEN}
import dnn.types.{OperatorDot, OperatorReduction}
import interfaces.CustomDataBundle
import node.{HandShakingIONPS, HandShakingNPS, Shapes, matNxN, vecN}

class ShapeShifterIO[gen <: vecN , gen2 <: Shapes](NumIns: Int)(shapeIn: => gen)(shapeOut: => gen2)(implicit p: Parameters)
  extends HandShakingIONPS(1)(new CustomDataBundle(UInt(shapeOut.getWidth.W))) {
    val in = Vec(NumIns, Flipped(Decoupled(new CustomDataBundle(UInt(shapeIn.getWidth.W)))))
    override def cloneType = new ShapeShifterIO(NumIns)(shapeIn)(shapeOut).asInstanceOf[this.type]
  }

class ShapeShifter[L <: vecN, K <: Shapes](NumIns: Int, ID: Int, lanes: Int)(shapeIn: => L)(shapeOut: => K)(implicit p: Parameters)
  extends HandShakingNPS(1, ID)(new CustomDataBundle(UInt(shapeOut.getWidth.W)))(p) {
  override lazy val io = IO(new ShapeShifterIO(NumIns)(shapeIn)(shapeOut))

  val x = Wire(shapeIn)
  val y = x.toVecUInt()


  val z = x.data
  val buffer = Module(new Queue(shapeOut,40))




/*===========================================*
 *            Registers                      *
 *===========================================*/
  val dataIn_R = RegInit(Vec(NumIns, CustomDataBundle.default(0.U(shapeIn.getWidth.W))))

  val s_idle :: s_LATCH :: s_ACTIVE :: s_COMPUTE :: Nil = Enum(4)
  val state = RegInit(s_idle)

  /*==========================================*
   *           Predicate Evaluation           *
   *==========================================*/

  val predicate = dataIn_R(0).predicate &  IsEnable( )
  val start     = dataIn_R(0).valid & IsEnableValid( )

  /*===============================================*
   *            Latch inputs. Wire up left       *
   *===============================================*/

  // Predicate register
  val pred_R = RegInit(init = false.B)

  //printfInfo("start: %x\n", start)
  for (i <- 0 until NumIns) {
    io.in(i).ready := ~dataIn_R(i).valid
    when(io.in(i).fire()) {
      dataIn_R(i).data := io.in(i).bits.data
      dataIn_R(i).valid := true.B
      dataIn_R(i).predicate := io.in(i).bits.predicate
    }
  }

  //  printf(p"\n Left ${io.LeftIO.bits.data} Right: ${io.RightIO.bits.data} Output: ${reduceNode.io.Out(0).bits.data}")
}


