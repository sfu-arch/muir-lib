
package dnn.memory

import chisel3._
import chisel3.util._
import config._
import dnnnode.WeightShapeTransformer
import node.vecN
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
class WgtTensorLoadIO[gen <: vecN](tensorType: String = "none")(wgtShape: => gen)(implicit val p: Parameters)
  extends Module {
  val tp = new TensorParams(tensorType)
  val mp = p(ShellKey).memParams
  val io = IO(new Bundle {
    val start = Input(Bool())
    val done = Output(Bool())
    val inst = Input(UInt(INST_BITS.W))
    val xsize = Input(UInt(M_SIZE_BITS.W))
    val baddr = Input(UInt(mp.addrBits.W))
    val vme_rd = new VMEReadMaster
    val tensor = new TensorClient(tensorType)
  })
}


class WgtTensorLoad[L <: vecN](numWeight: Int, tensorType: String = "none")(wgtShape: => L)(implicit p: Parameters)
  extends WgtTensorLoadIO(tensorType)(wgtShape)(p) {

  val wgtTransformer = Module(new WeightShapeTransformer(numWeight, tensorType = "inp")(wgtShape))
  val tensorLoad = Module(new TensorLoad(tensorType))

  tensorLoad.io.start := io.start
  tensorLoad.io.inst := io.inst
  tensorLoad.io.baddr := io.baddr
  io.vme_rd <> tensorLoad.io.vme_rd

  wgtTransformer.io.start := tensorLoad.io.done
  io.done := wgtTransformer.io.done
  wgtTransformer.io.xsize := io.xsize

  tensorLoad.io.tensor <> wgtTransformer.io.tensorMaster
  wgtTransformer.io.tensor <> io.tensor


}
