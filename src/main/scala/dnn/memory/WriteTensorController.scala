package dnn.memory

import chisel3.util._
import chisel3.{Module, _}
import config._
import interfaces._
import node._

abstract class WTController[gen <: Shapes](NumOps: Int, tensorType: String = "none")(shape: => gen)(implicit val p: Parameters)
  extends Module {
  val io = IO(new Bundle {
//    val ReadIn  = Vec(NumOps, Flipped(Decoupled(new TensorReadReq())))
//    val ReadOut = Vec(NumOps, Output(new TensorReadResp(shape.getWidth)))
    val WriteIn  = Flipped(Decoupled(new TensorWriteReq(shape.getWidth)))
    val WriteOut = Output(new TensorWriteResp())
    val tensor = new TensorMaster(tensorType)
  })
}


class WriteTensorController[L <: Shapes] (NumOps: Int, tensorType: String = "none")(shape: => L)(implicit p: Parameters)
  extends WTController(NumOps, tensorType)(shape)(p) {

  /*=====================================================================
  =            Wire up incoming reads from nodes to ReadMSHR            =
  =====================================================================*/
  io.tensor.wr.bits.idx := io.WriteIn.bits.index
  io.tensor.wr.bits.data := io.WriteIn.bits.data.asTypeOf(io.tensor.wr.bits.data)

  io.WriteOut.valid := io.tensor.wr.valid

  io.tensor.wr.valid := true.B

  io.tensor.rd <> DontCare

//  io.WriteOut <> DontCare
//  io.WriteIn <> DontCare
  /*=============================================
  =           Declare Read Table                =
  =============================================*/



}

