
package dnn_layers

import chisel3._
import chisel3.util.Decoupled
import config._
import dnn.memory.{ReadTensorController, TensorParams, inDMA_act, inDMA_wgt, outDMA_act}
import dnn.types.{OperatorDot, OperatorReduction}
import dnnnode.{Mac2dTensor, MacPW}
import interfaces.{ControlBundle, TensorReadReq, TensorReadResp}
import node.{Shapes, vecN}
import shell._
//import vta.util.config._


/** TensorLoad.
  *
  * Load 1D and 2D tensors from main memory (DRAM) to input/weight
  * scratchpads (SRAM). Also, there is support for zero padding, while
  * doing the load. Zero-padding works on the y and x axis, and it is
  * managed by TensorPadCtrl. The TensorDataCtrl is in charge of
  * handling the way tensors are stored on the scratchpads.
  */
class DW_PW_BlockIO[gen <: vecN, gen2 <: Shapes]
(MACperCH: Int, wgtDWType: String = "none", wgtPWType: String = "none", memTensorType: String = "none")
(memShape: => gen)(wgtDWShape: => gen)(macDWShape: => gen2)(implicit val p: Parameters)
  extends Module {
  val tpWgtDW = new TensorParams(wgtDWType)
  val tpWgtPW = new TensorParams(wgtPWType)
  val mp = p(ShellKey).memParams
  val io = IO(new Bundle {
    val start = Input(Bool())
    val done = Output(Bool())
    val inBaseAddr = Input(UInt(mp.addrBits.W))
    val outBaseAddr = Input(UInt(mp.addrBits.W))
    val rowWidth = Input(UInt(mp.addrBits.W))

    val wgtDWIndex = Input(UInt(tpWgtDW.memAddrBits.W))
    val vme_rd = Vec(MACperCH + macDWShape.getLength() - 1, new VMEReadMaster)
    val vme_wr = Vec(MACperCH, new VMEWriteMaster)


    val vme_wgtDW_rd = new VMEReadMaster
    val vme_wgtPW_rd = new VMEReadMaster
    val wgtDW_baddr = Input(UInt(mp.addrBits.W))
  })
}

class DW_PW_Block[L <: vecN, K <: Shapes : OperatorDot : OperatorReduction]
(NumChannel: Int, MACperCH: Int, NumPWFilter: Int, wgtDWType: String = "none", wgtPWType: String = "none", memTensorType: String = "none")
(memShape: => L)(wgtDWShape: => L)(wgtPWShape: => L)(macDWShape: => K)(macPWShape: => K)(implicit p: Parameters)
  extends DW_PW_BlockIO(MACperCH, wgtDWType, wgtPWType, memTensorType)(memShape)(wgtDWShape)(macDWShape)(p) {


  val inDMA_act = for (i <- 0 until NumChannel) yield {
    val inDMA =  Module(new inDMA_act(MACperCH + macDWShape.getLength() - 1, 1, memTensorType)(memShape))
    inDMA
  }

  val mac2dDW = for (i <- 0 until NumChannel) yield {
    val mac2d = Module(new Mac2dTensor(MACperCH, wgtDWType, memTensorType)(memShape)(wgtDWShape)(macDWShape))
    mac2d
  }

  val macPW = for (i <- 0 until NumPWFilter) yield {
    val mac2dPW = Module(new MacPW(MACperCH, wgtPWType)(wgtPWShape)(macPWShape))
    mac2dPW
  }

  val outDMA_act = for (i <- 0 until NumPWFilter) yield {
    val outDMA = Module(new outDMA_act(MACperCH, 20, memTensorType))
    outDMA
  }

  /* ================================================================== *
   *                     Depth-wise - inDMA_weight                      *
   * ================================================================== */

  val inDMA_wgtDW = Module(new inDMA_wgt(20, 100, wgtDWType, memTensorType = "inp")(wgtDWShape))
  val wgtCtrlDW = Module(new ReadTensorController(NumChannel, wgtDWType)(wgtDWShape))
  inDMA_wgtDW.io.tensor <> wgtCtrlDW.io.tensor
  io.vme_wgtDW_rd <> inDMA_wgtDW.io.vme_rd

  inDMA_wgtDW.io.numWeight := 7.U
  inDMA_wgtDW.io.start := false.B
  inDMA_wgtDW.io.baddr := io.wgtDW_baddr

  for (i <- 0 until NumChannel) {
    wgtCtrlDW.io.ReadIn(i) <> mac2dDW(i).io.wgtTensorReq
    mac2dDW(i).io.wgtTensorResp <> wgtCtrlDW.io.ReadOut(i)
  }

  /* ================================================================== *
     *                     Point-wise - inDMA_weight                    *
     * ================================================================== */
  val inDMA_wgtPW = Module(new inDMA_wgt(20, 100, wgtPWType, memTensorType)(wgtPWShape))
  val wgtCtrlPW = Module(new ReadTensorController(NumPWFilter, wgtPWType)(wgtPWShape))



  /*mac2dDW.io.enable.bits <> ControlBundle.active()
  mac2dDW.io.enable.valid := true.B

  mac2dDW.io.wgtIndex := io.wgtIndex
  mac2dDW.io.rowWidth := io.rowWidth

  inDMA_act.io.rowWidth := io.rowWidth + macShape.getLength().U - 1.U
  inDMA_act.io.baddr := io.inBaseAddr

  outDMA_act.io.rowWidth := io.rowWidth
  outDMA_act.io.baddr := io.outBaseAddr
  /* ================================================================== *
     *                           Connections                            *
     * ================================================================== */
  for (i <- 0 until NumMac) {
    outDMA_act.io.in(i) <> mac2dDW.io.Out(i)
    io.vme_wr(i) <> outDMA_act.io.vme_wr(i)
  }

  outDMA_act.io.last.foreach(a => a := mac2dDW.io.last)

  for (i <- 0 until NumMac + macShape.getLength() - 1) {
    inDMA_act.io.ReadIn(i)(0) <> mac2dDW.io.tensorReq(i)
    mac2dDW.io.tensorResp(i) <> inDMA_act.io.ReadOut(i)(0)

    io.vme_rd(i) <> inDMA_act.io.vme_rd(i)
  }

  io.wgtTensorReq <> mac2dDW.io.wgtTensorReq
  mac2dDW.io.wgtTensorResp <> io.wgtTensorResp

  inDMA_act.io.start := io.start
  mac2dDW.io.start := inDMA_act.io.done
  outDMA_act.io.start := mac2dDW.io.done
  io.done := outDMA_act.io.done*/


}
