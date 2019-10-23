package dnnnode

import Chisel.Enum
import chisel3.util.{Decoupled, Queue, log2Ceil}
import chisel3.{Flipped, Module, UInt, _}
import config.{Parameters, XLEN}
import dnn.types.{OperatorDot, OperatorReduction}
import interfaces.CustomDataBundle
import node.{HandShakingIONPS, HandShakingNPS, Shapes, matNxN, vecN}

class ShapeShifterIO[gen <: vecN, gen2 <: Shapes](NumIns: Int)(shapeIn: => gen)(shapeOut: => gen2)(implicit p: Parameters)
  extends HandShakingIONPS(1)(new CustomDataBundle(UInt(shapeOut.getWidth.W))) {
  val in = Vec(NumIns, Flipped(Decoupled(new CustomDataBundle(UInt(shapeIn.getWidth.W)))))

  override def cloneType = new ShapeShifterIO(NumIns)(shapeIn)(shapeOut).asInstanceOf[this.type]
}

class ShapeShifter[L <: vecN, K <: Shapes](NumIns: Int, ID: Int)(shapeIn: => L)(shapeOut: => K)(implicit p: Parameters)
  extends HandShakingNPS(1, ID)(new CustomDataBundle(UInt(shapeOut.getWidth.W)))(p) {
  override lazy val io = IO(new ShapeShifterIO(NumIns)(shapeIn)(shapeOut))

  val buffer = Module(new Queue(shapeOut, 40))

  /*===========================================*
   *            Registers                      *
   *===========================================*/
  val dataIn_R = RegInit(VecInit(Seq.fill(NumIns)(CustomDataBundle.default(0.U(shapeIn.getWidth.W)))))
  val dataIn_Wire = Wire(Vec(NumIns, Vec(shapeIn.N, UInt(xlen.W))))

  val ratio = shapeIn.data.size / NumIns //8

  val input_data = dataIn_R.map(_.data.asUInt())

  for (i <- 0 until NumIns) {
    for (j <- 0 until shapeIn.N) {
      val index = ((j + 1) * xlen) - 1
      dataIn_Wire(i)(j) := input_data(i)(index, j * xlen) // 3, 24
    }
  }

  val dataOut_Wire = Wire(Vec(ratio, Vec(NumIns * NumIns, UInt(xlen.W)))) //8, (9)

  for (i <- 0 until ratio) { //8
    for (j <- 0 until NumIns) {
      for (k <- 0 until NumIns) {
        dataOut_Wire(i)(j * NumIns + k) := dataIn_Wire(j)(i * NumIns + k)
      }
    }
  }

//  val dataOut_R = RegNext(next = dataIn_Wire, init = VecInit(Seq.fill(NumIns)(VecInit(Seq.fill(shapeIn.N)(0.U)))))
  val dataOut_R = RegNext(next = dataOut_Wire, init = VecInit(Seq.fill(ratio)(CustomDataBundle.default(0.U(shapeOut.getWidth.W)))))

  val s_idle :: s_LATCH :: s_ACTIVE :: s_COMPUTE :: Nil = Enum(4)
  val state = RegInit(s_idle)

  /*==========================================*
   *           Predicate Evaluation           *
   *==========================================*/

  //  val predicate = dataIn_R.map(_.predicate).reduceLeft(_ && _) &  IsEnable( )
  val start = dataIn_R.map(_.valid).reduceLeft(_ && _) & IsEnableValid()

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
      //      dataIn_R(i).predicate := io.in(i).bits.predicate
    }
  }


  //  printf(p"\n Left ${io.LeftIO.bits.data} Right: ${io.RightIO.bits.data} Output: ${reduceNode.io.Out(0).bits.data}")
}


