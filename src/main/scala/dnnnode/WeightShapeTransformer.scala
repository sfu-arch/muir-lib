package dnnnode

import Chisel.Enum
import chisel3.util._
import chisel3.{Module, UInt, _}
import config.{Parameters, XLEN}
import dnn.memory.{TensorMaster, TensorParams}
import interfaces._
import node._
import shell.ShellKey

abstract class WeightShapeTransformerIO[gen <: Shapes](tensorType: String = "none")(wgtShape: => gen)(implicit val p: Parameters)
  extends Module {
  val tp = new TensorParams(tensorType)
  val mp = p(ShellKey).memParams
  val io = IO(new Bundle {
    val start = Input(Bool())
    val done = Output(Bool())
    val numWeight = Input(UInt(mp.addrBits.W))
    val xsize = Input(UInt(mp.addrBits.W))
    val tensorMaster = new TensorMaster(tensorType)
    val tensor = new TensorMaster(tensorType)
  })
}

class WeightShapeTransformer[L <: Shapes] (Kx: Int, Ky: Int, tensorType: String = "none")(wgtShape: => L)(implicit p: Parameters)
  extends WeightShapeTransformerIO(tensorType)(wgtShape)(p) {

  val buffer = Module(new WeightQueue(UInt(p(XLEN).W), 144, 16, 9))
  val readCnt = Counter(10)
  val s_idle :: s_BufferWrite :: s_Transfer :: s_Finish :: Nil = Enum(4)
  val state = RegInit(s_idle)

  io.tensor.wr <> DontCare
}

