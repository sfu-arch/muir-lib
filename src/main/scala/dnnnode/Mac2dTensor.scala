
package dnnnode

import chisel3._
import chisel3.util._
import config._
import dnn.MacNode
import dnn.memory.TensorParams
import dnn.types.{OperatorDot, OperatorReduction}
import interfaces.{ControlBundle, CustomDataBundle, DataBundle, TensorReadReq, TensorReadResp}
import node.{HandShakingIONPS, HandShakingNPS, Shapes, matNxN, vecN}
import shell._
//import vta.util.config._
import dnn.memory.ISA._


/** TensorLoad.
  *
  * Load 1D and 2D tensors from main memory (DRAM) to input/weight
  * scratchpads (SRAM). Also, there is support for zero padding, while
  * doing the load. Zero-padding works on the y and x axis, and it is
  * managed by TensorPadCtrl. The TensorDataCtrl is in charge of
  * handling the way tensors are stored on the scratchpads.
  */
class Mac2dTensorIO[gen <: vecN, gen2 <: Shapes](NumMac: Int, wgtTensorType: String = "none")(memShape: => gen)(macShape: => gen2)(implicit p: Parameters)
  extends HandShakingIONPS(NumMac)(new CustomDataBundle(UInt(p(XLEN).W))) {
  val tp = new TensorParams(wgtTensorType)
  val mp = p(ShellKey).memParams
    val tensorReq = Vec(NumMac + macShape.getLength() - 1, Decoupled(new TensorReadReq()))
    val tensorResp = Vec(NumMac + macShape.getLength() - 1, Input(Flipped(new TensorReadResp(memShape.getWidth))))
    val wgtTensorReq = Decoupled(new TensorReadReq())
    val wgtTensorResp = Input(Flipped(new TensorReadResp(memShape.getWidth)))
    val wgtIndex = Input(UInt(tp.memAddrBits.W))
    val rowWidth = Input(UInt(mp.addrBits.W))
    val last = Input(Bool())
    val start = Input(Bool())
    val done = Output(Bool())

  override def cloneType = new Mac2dTensorIO(NumMac)(memShape)(macShape).asInstanceOf[this.type]
}

class Mac2dTensor[L <: vecN, K <: Shapes : OperatorDot : OperatorReduction](NumMac: Int)(memShape: => L)(wgtShape: => L)(macShape: => K)
                                                                           (implicit p: Parameters)
  extends HandShakingNPS(NumMac, 0)(new CustomDataBundle(UInt(p(XLEN).W)))(p) {
  override lazy val io = IO(new Mac2dTensorIO(NumMac)(memShape)(macShape))

  val indexCnt = Counter(200)

  val loadWeight = Module(new TLoad(NumPredOps = 0, NumSuccOps = 0, NumOuts = 1, ID = 0, RouteID = 0)(wgtShape))
  loadWeight.io.enable.bits <> ControlBundle.active()
  loadWeight.io.enable.valid := true.B
  io.wgtTensorReq <> loadWeight.io.tensorReq
  loadWeight.io.tensorResp <> io.wgtTensorResp

  val load = for (i <- 0 until NumMac + macShape.getLength() - 1) yield {
    val loadNode = Module(new TLoad(NumPredOps = 0, NumSuccOps = 0, NumOuts = 1, ID = 0, RouteID = 0)(memShape))
    loadNode
  }

  val mac = for (i <- 0 until NumMac) yield {
    val macNode = Module(new MacNode(NumOuts = 1, ID = 0, lanes = macShape.getLength())(macShape))
    macNode
  }

  val shapeTransformer = for (i <- 0 until NumMac) yield {
    val shapeTran = Module(new ShapeTransformer(NumIns = macShape.getLength(), NumOuts = 1, ID = 0)(memShape)(macShape))
    shapeTran
  }

  for (i <- 0 until NumMac + macShape.getLength() - 1)  {
    load(i).io.enable.bits <> ControlBundle.active()
    load(i).io.enable.valid := true.B
    io.tensorReq(i) <> load(i).io.tensorReq
    load(i).io.tensorResp <> io.tensorResp(i)
    load(i).io.GepAddr.valid := false.B
    load(i).io.GepAddr.bits.taskID := 0.U
    load(i).io.GepAddr.bits.predicate := true.B
    load(i).io.GepAddr.bits.data := indexCnt.value
  }

  for (i <- 0 until NumMac) {
    for (j <- 0 until macShape.getLength()) {
      shapeTransformer(i).io.in(j) <> load(i + j).io.Out(0)
    }
    shapeTransformer(i).io.enable.bits := ControlBundle.active()
    shapeTransformer(i).io.enable.valid := true.B

    mac(i).io.enable.bits <> ControlBundle.active()
    mac(i).io.enable.valid := true.B

    mac(i).io.LeftIO <> shapeTransformer(i).io.Out(0)
    mac(i).io.RightIO <> loadWeight.io.Out(0)
    io.Out(i) <> mac(i).io.Out(0)
  }

  val sIdle :: sReadTensor1 :: sReadTensor2 :: sMacStart :: sMacWaiting :: sNextOp :: sWriteTensor :: sFinish :: Nil = Enum(8)

  val state = RegInit(sIdle)

  


}
