
package dnn_layers

import chisel3._
import config._
import dnn.memory._
import dnn.types.{OperatorDot, OperatorReduction}
import dnnnode.{Mac2dTensor, MacPW, TLoad}
import interfaces.ControlBundle
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
class PDP_BlockIO[gen <: vecN, gen2 <: Shapes]
(NumChannel: Int, MACperCH: Int, NumPWFilter: Int, wgtDWType: String = "none", wgtPWType: String = "none", memTensorType: String = "none")
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
    val outRowWidth = Input(UInt(mp.addrBits.W))

    val vme_rd = Vec(NumChannel * (MACperCH + macDWShape.getLength() - 1), new VMEReadMaster)
    val vme_wr = Vec(NumPWFilter * MACperCH, new VMEWriteMaster)

    val wgtDWIndex = Input(UInt(tpWgtDW.memAddrBits.W))
    val wgtPWIndex = Input(UInt(tpWgtPW.memAddrBits.W))
    val vme_wgtDW_rd = new VMEReadMaster
    val vme_wgtPW_rd = new VMEReadMaster
    val wgtDW_baddr = Input(UInt(mp.addrBits.W))
    val wgtPW_baddr = Input(UInt(mp.addrBits.W))
  })
}

class PDP_Block[L <: vecN, K <: Shapes : OperatorDot : OperatorReduction, M <: Shapes : OperatorDot : OperatorReduction]
(NumChannel: Int, MACperCH_DW: Int, NumPWFilter: Int, Fx: Int, wgtDWType: String = "none", wgtPWType: String = "none", memTensorType: String = "none")
(memShape: => L)(wgtDWShape: => L)(wgtPWShape: => L)(macDWShape: => K)(macPWShape: => M)(implicit p: Parameters)
  extends PDP_BlockIO(NumChannel, MACperCH_DW, NumPWFilter, wgtDWType, wgtPWType, memTensorType)(memShape)(wgtDWShape)(macDWShape)(p) {


  val inDMA_act =  Module(new inDMA_act(MACperCH_DW + macDWShape.getLength() - 1, 1, memTensorType)(memShape))

  val load = for (i <- 0 until MACperCH_DW + macDWShape.getLength() -1) yield {
    val loadNode = Module(new TLoad(NumPredOps = 0, NumSuccOps = 0, NumOuts = Fx, ID = 0, RouteID = 0)(memShape))
    loadNode
  }


  val mac2dDW = for (i <- 0 until NumChannel) yield {
    val mac2d = Module(new Mac2dTensor(MACperCH_DW, wgtDWType, memTensorType)(memShape)(wgtDWShape)(macDWShape))
    mac2d
  }

  val macPW = for (i <- 0 until NumPWFilter) yield {
    val mac2dPW = Module(new MacPW(MACperCH_DW, wgtPWType)(wgtPWShape)(macPWShape))
    mac2dPW
  }

  val outDMA_act = for (i <- 0 until NumPWFilter) yield {
    val outDMA = Module(new outDMA_act(MACperCH_DW, 20, memTensorType))
    outDMA
  }

  val doneR = for (i <- 0 until NumPWFilter) yield {
    val doneReg = RegInit(init = false.B)
    doneReg
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
  inDMA_wgtDW.io.start := io.start

  for (i <- 0 until NumChannel) {
    wgtCtrlDW.io.ReadIn(i) <> mac2dDW(i).io.wgtTensorReq
    mac2dDW(i).io.wgtTensorResp <> wgtCtrlDW.io.ReadOut(i)
  }

  /* ================================================================== *
     *                     Point-wise - inDMA_weight                    *
     * ================================================================== */
  val inDMA_wgtPW = Module(new inDMA_wgt(20, 100, wgtPWType, memTensorType)(wgtPWShape))
  val wgtCtrlPW = Module(new ReadTensorController(NumPWFilter, wgtPWType)(wgtPWShape))
  inDMA_wgtPW.io.tensor <> wgtCtrlPW.io.tensor
  io.vme_wgtPW_rd <> inDMA_wgtPW.io.vme_rd

  inDMA_wgtPW.io.numWeight := 7.U
  inDMA_wgtPW.io.start := false.B
  inDMA_wgtPW.io.baddr := io.wgtPW_baddr
  inDMA_wgtPW.io.start := inDMA_wgtDW.io.done

  for (i <- 0 until NumPWFilter) {
    wgtCtrlPW.io.ReadIn(i) <> macPW(i).io.wgtTensorReq
    macPW(i).io.wgtTensorResp <> wgtCtrlPW.io.ReadOut(i)
  }

  /* ================================================================== *
    *                  Depth-wise MACs & inDMA_acts                     *
    * ================================================================== */
  val inROWperCH =  MACperCH_DW + macDWShape.getLength() - 1
  for (i <- 0 until NumChannel) {
    mac2dDW(i).io.enable.bits <> ControlBundle.active()
    mac2dDW(i).io.enable.valid := true.B
    mac2dDW(i).io.wgtIndex := io.wgtDWIndex + i.U
    mac2dDW(i).io.outRowWidth := io.outRowWidth

    inDMA_act(i).io.rowWidth := io.outRowWidth + macDWShape.getLength().U - 1.U
    inDMA_act(i).io.baddr := io.inBaseAddr + (i.U * ((io.outRowWidth + macDWShape.getLength().U - 1.U) * inROWperCH.U))

    for (j <- 0 until inROWperCH) {
      inDMA_act(i).io.ReadIn(j)(0) <> mac2dDW(i).io.tensorReq(j)
      mac2dDW(i).io.tensorResp(j) <> inDMA_act(i).io.ReadOut(j)(0)

      io.vme_rd(i * inROWperCH + j) <> inDMA_act(i).io.vme_rd(j)
    }

    inDMA_act(i).io.start := inDMA_wgtPW.io.done
    mac2dDW(i).io.start := inDMA_act(NumChannel - 1).io.done // after last inDMA_act

    for (j <- 0 until MACperCH_DW) {
      mac2dDW(i).io.Out(j).ready := macPW.map(_.io.in(j).ready).reduceLeft(_ && _)
    }

  }


  /* ================================================================== *
     *                   Point-wise MACs & outDMA_acts                  *
     * ================================================================== */
  for (i <- 0 until NumPWFilter) {
    macPW(i).io.enable.bits <> ControlBundle.active()
    macPW(i).io.enable.valid := true.B
    macPW(i).io.wgtIndex := io.wgtPWIndex + i.U
    macPW(i).io.outRowWidth := io.outRowWidth
    macPW(i).io.start := inDMA_wgtPW.io.done

    outDMA_act(i).io.rowWidth := io.outRowWidth
    outDMA_act(i).io.baddr := io.outBaseAddr + (i.U * (MACperCH_DW.U * io.outRowWidth))

    for (j <- 0 until MACperCH_DW) {
      outDMA_act(i).io.in(j) <> macPW(i).io.Out(j)
      io.vme_wr(i * MACperCH_DW + j) <> outDMA_act(i).io.vme_wr(j)

      macPW(i).io.in(j).bits.data := VecInit(mac2dDW.map(_.io.Out(j).bits.data.asUInt())).asUInt()
      macPW(i).io.in(j).bits.taskID := 0.U
      macPW(i).io.in(j).bits.predicate := true.B
      macPW(i).io.in(j).bits.valid := true.B
      macPW(i).io.in(j).valid := mac2dDW.map(_.io.Out(j).valid).reduceLeft(_ && _)

    }
    outDMA_act(i).io.last.foreach(a => a := macPW(i).io.last)
    outDMA_act(i).io.start := macPW(i).io.done
    when(outDMA_act(i).io.done) {
      doneR(i) := true.B
    }
  }

 /* ================================================================== *
     *                        Done Signal                              *
     * ================================================================== */

  io.done := doneR.reduceLeft(_ && _)
  when (doneR.reduceLeft(_ && _)) {
    doneR.foreach(a => a := false.B)
  }


}
