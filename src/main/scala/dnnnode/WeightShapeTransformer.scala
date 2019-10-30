package dnnnode

import Chisel.Enum
import breeze.numerics.ceil
import chisel3._
import chisel3.util._
import chisel3.{Module, UInt}
import config.{Parameters, XLEN}
import dnn.memory.{TensorMaster, TensorParams}
import interfaces.CustomDataBundle
import node.{vecN}
import shell.ShellKey
import dnn.memory.ISA._

class WeightShapeTransformerIO[gen <: vecN](tensorType: String = "none")(wgtShape: => gen)(implicit val p: Parameters)
  extends Module {
  val tp = new TensorParams(tensorType)
  val io = IO(new Bundle {
    val start = Input(Bool())
    val done = Output(Bool())
    val xsize = Input(UInt(M_SIZE_BITS.W))
    val tensorMaster = new TensorMaster(tensorType)
    val tensor = new TensorMaster(tensorType)
  })
}

class WeightShapeTransformer[L <: vecN] (numWeight: Int, tensorType: String = "none")(wgtShape: => L)(implicit p: Parameters)
  extends WeightShapeTransformerIO(tensorType)(wgtShape)(p) {

  val buffer = Module(new WeightQueue(UInt(p(XLEN).W), 144, 16, 9))
  val wgtTensorLength = ceil(numWeight * wgtShape.N / tp.tensorWidth)

  val writeBufCntOn = RegInit(init = false.B)
  val (writeBufCnt, writeWrap) = Counter(writeBufCntOn, wgtTensorLength)

  val readWgtCnt = Counter(numWeight)


  val s_idle :: s_BufferWrite :: s_Transfer :: s_Finish :: Nil = Enum(4)
  val state = RegInit(s_idle)

  val tensorFile = SyncReadMem(numWeight, wgtShape)

  buffer.io.enq.valid := io.tensorMaster.rd.data.valid
  buffer.io.enq.bits := io.tensorMaster.rd.data.bits(0)
  io.tensorMaster.rd.idx.bits := writeBufCnt
  io.tensorMaster.rd.idx.valid := buffer.io.enq.ready
  io.tensorMaster.wr <> DontCare

  when (writeWrap) {writeBufCntOn := false.B}
  when (io.start) {writeBufCntOn := true.B}

  when (buffer.io.deq.valid & readWgtCnt.value <= numWeight.U) {
    tensorFile.write(readWgtCnt.value, buffer.io.deq.bits.asTypeOf(wgtShape))
    readWgtCnt.inc()
  }
  when (readWgtCnt.value === (numWeight - 1).U) {
    io.done := true.B
  }.otherwise{
    io.done := false.B
  }

  val rvalid = RegNext(io.tensor.rd.idx.valid)
  io.tensor.rd.data.valid := rvalid

  val rdata = tensorFile.read(io.tensor.rd.idx.bits, io.tensor.rd.idx.valid)
  io.tensor.rd.data.bits := rdata.asUInt.asTypeOf(io.tensor.rd.data.bits)

}

