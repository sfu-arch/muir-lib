package dnn.memory

// Generic Packages
import chisel3.{Module, _}
import chisel3.util._

// Modules needed
import arbiters._
import muxes._

// Config
import config._
import interfaces._
import node._
import utility._

abstract class RTController[gen <: Shapes](NumOps: Int, tensorType: String = "none")(shape: => gen)(implicit val p: Parameters)
  extends Module {
  val io = IO(new Bundle {
//    val ReadIn  = Vec(NumOps, Flipped(Decoupled(new TensorReadReq())))
//    val ReadOut = Vec(NumOps, Output(new TensorReadResp(shape.getWidth)))
    val ReadIn  = Flipped(Decoupled(new TensorReadReq()))
    val ReadOut = Output(new TensorReadResp(shape.getWidth))
    val tensor = new TensorMaster(tensorType)
  })
}


class ReadTensorController[L <: Shapes] (NumOps: Int, tensorType: String = "none")(shape: => L)(implicit p: Parameters)
  extends RTController(NumOps, tensorType)(shape)(p) {

  /*=====================================================================
  =            Wire up incoming reads from nodes to ReadMSHR            =
  =====================================================================*/

  io.tensor.rd.idx.bits := io.ReadIn.bits.index
  io.tensor.rd.idx.valid := true.B

  io.ReadIn.ready := true.B
  io.ReadOut.data := io.tensor.rd.data.bits.asUInt()
  io.ReadOut.valid := true.B
  io.ReadOut.RouteID := io.ReadIn.bits.RouteID

  io.tensor.wr <> DontCare

  /*=============================================
  =           Declare Read Table                =
  =============================================*/



}

