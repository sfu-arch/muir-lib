
package dnn_layers

import chisel3._
import chisel3.util._
import config._
import dnn.memory._
import dnn.types.{OperatorDot, OperatorReduction}
import dnnnode.{Mac1D, MacPW, PWShapeTransformer, ShapeTransformer}
import interfaces.{ControlBundle, CustomDataBundle}
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
(MACperCH: Int, Fx: Int, wgtType: String = "none", memTensorType: String = "none")
(memShape: => gen)(CxShape: => gen2)(implicit val p: Parameters)
  extends Module {
  val tpMem = new TensorParams(memTensorType)
  val tpWgt = new TensorParams(wgtType)

  val mp = p(ShellKey).memParams
  val io = IO(new Bundle {
    val start = Input(Bool())
    val done = Output(Bool())
    val inBaseAddr = Input(UInt(mp.addrBits.W))
    val outBaseAddr = Input(UInt(mp.addrBits.W))


    val rowWidth = Input(UInt(mp.addrBits.W))

    val vme_rd = Vec(MACperCH, new VMEReadMaster)
    val vme_wr = Vec(Fx * MACperCH, new VMEWriteMaster)

    val wgtPW1index = Input(UInt(tpWgt.memAddrBits.W))
    val wgtDWindex = Input(UInt(tpWgt.memAddrBits.W))
    val wgtPW2index = Input(UInt(tpWgt.memAddrBits.W))
    val vme_wgtPW1_rd = new VMEReadMaster
    val vme_wgtDW_rd = new VMEReadMaster
    val vme_wgtPW2_rd = new VMEReadMaster

    val wgtPW1_baddr = Input(UInt(mp.addrBits.W))
    val wgtDW_baddr = Input(UInt(mp.addrBits.W))
    val wgtPW2_baddr = Input(UInt(mp.addrBits.W))
  })
}

class PDP_Block[L <: vecN, K <: Shapes : OperatorDot : OperatorReduction, M <: Shapes : OperatorDot : OperatorReduction]
(MACperCH: Int, Fx: Int, ChBatch: Int, NumPWFilter: Int,
  wgtPW1Type: String = "none",
  wgtDWType: String = "none",
  wgtPW2Type: String = "none",
  memTensorType: String = "none")
  (memShape: => L)(CxShape: => K)
  (wgtDWshape: => L)(macDWshape: => M)
  (wgtPW2shape: => L)(macPW2shape: => K)(implicit p: Parameters)
  extends PDP_BlockIO(MACperCH, Fx, wgtPW1Type, memTensorType)(memShape)(CxShape)(p) {


  val inDMA_act =  Module(new inDMA_act_HWC(MACperCH, 1, memTensorType)(memShape))

  val PW1ShapeTransformer = Module(new PWShapeTransformer(MACperCH, Fx, 20, memTensorType)(CxShape))

  val macPW1 = for (i <- 0 until Fx) yield {
    val mac1d = Module(new Mac1D(MACperCH, ChBatch, 20,  wgtPW1Type)(CxShape))
    mac1d
  }

  val mac1DshapeOut = new vecN(1, 0, false)

  val DWShapeTransformer = for (i <- 0 until Fx) yield {
    for (j <- 0 until MACperCH - macDWshape.getLength() + 1) yield {
      val shapeTran = Module(new ShapeTransformer(NumIns = macDWshape.getLength(), NumOuts = 1, ID = 0)(mac1DshapeOut)(macDWshape))
      shapeTran
    }
  }

  val macDW = for (i <- 0 until Fx) yield {
    val macDW = Module(new MacPW(MACperCH, wgtPW1Type)(wgtDWshape)(macDWshape))
    macDW
  }

  val macPW2 = for (i <- 0 until NumPWFilter) yield {
    val mac2dPW = Module(new MacPW(MACperCH, wgtPW2Type)(wgtPW2shape)(macPW2shape))
    mac2dPW
  }

  val outDMA_act = for (i <- 0 until NumPWFilter) yield {
    val outDMA = Module(new outDMA_act(MACperCH, 20, memTensorType))
    outDMA
  }

  val doneR = for (i <- 0 until NumPWFilter) yield {
    val doneReg = RegInit(init = false.B)
    doneReg
  }

  val readTensorCnt = Counter(tpMem.memDepth)

  val sIdle :: sWgtRead :: sActRead :: sMacLoadWgt :: sExec :: Nil = Enum(5)
  val state = RegInit(sIdle)

  /* ================================================================== *
   *                     Point-wise1 - inDMA_weight                     *
   * ================================================================== */

  val inDMA_wgtPW1 = Module(new inDMA_wgt(20, 100, wgtPW1Type, memTensorType)(CxShape))
  val wgtCtrlPW1 = Module(new ReadTensorController(Fx, wgtPW1Type)(CxShape))
  inDMA_wgtPW1.io.tensor <> wgtCtrlPW1.io.tensor
  io.vme_wgtPW1_rd <> inDMA_wgtPW1.io.vme_rd

  inDMA_wgtPW1.io.numWeight := 15.U
  inDMA_wgtPW1.io.start := false.B
  inDMA_wgtPW1.io.baddr := io.wgtPW1_baddr

  for (i <- 0 until Fx) {
    wgtCtrlPW1.io.ReadIn(i) <> macPW1(i).io.wgtTensorReq
    macPW1(i).io.wgtTensorResp <> wgtCtrlPW1.io.ReadOut(i)
  }

  /* ================================================================== *
   *                     Depth-wise - inDMA_weight                      *
   * ================================================================== */

  val inDMA_wgtDW = Module(new inDMA_wgt(20, 100, wgtDWType, memTensorType = "inp")(wgtDWshape))
  val wgtCtrlDW = Module(new ReadTensorController(Fx, wgtDWType)(wgtDWshape))
  inDMA_wgtDW.io.tensor <> wgtCtrlDW.io.tensor
  io.vme_wgtDW_rd <> inDMA_wgtDW.io.vme_rd

  inDMA_wgtDW.io.numWeight := 7.U
  inDMA_wgtDW.io.start := false.B
  inDMA_wgtDW.io.baddr := io.wgtDW_baddr
  inDMA_wgtDW.io.start := io.start

  for (i <- 0 until Fx) {
    wgtCtrlDW.io.ReadIn(i) <> macDW(i).io.wgtTensorReq
    macDW(i).io.wgtTensorResp <> wgtCtrlDW.io.ReadOut(i)
  }

  /* ================================================================== *
     *                     Point-wise2 - inDMA_weight                   *
     * ================================================================== */
  val inDMA_wgtPW2 = Module(new inDMA_wgt(20, 100, wgtPW2Type, memTensorType)(wgtPW2shape))
  val wgtCtrlPW2 = Module(new ReadTensorController(NumPWFilter, wgtPW2Type)(wgtPW2shape))
  inDMA_wgtPW2.io.tensor <> wgtCtrlPW2.io.tensor
  io.vme_wgtPW2_rd <> inDMA_wgtPW2.io.vme_rd

  inDMA_wgtPW2.io.numWeight := 7.U
  inDMA_wgtPW2.io.start := false.B
  inDMA_wgtPW2.io.baddr := io.wgtPW2_baddr
  inDMA_wgtPW2.io.start := inDMA_wgtDW.io.done

  for (i <- 0 until NumPWFilter) {
    wgtCtrlPW2.io.ReadIn(i) <> macPW2(i).io.wgtTensorReq
    macPW2(i).io.wgtTensorResp <> wgtCtrlPW2.io.ReadOut(i)
  }

  /* ================================================================== *
    *                   inDMA_acts & PW1ShapeTransformer                 *
    * ================================================================== */

  inDMA_act.io.start := io.start
  inDMA_act.io.rowWidth := io.rowWidth
  inDMA_act.io.depth := CxShape.getLength().U * ChBatch.U
  inDMA_act.io.baddr := io.inBaseAddr

  PW1ShapeTransformer.io.start := inDMA_act.io.done
  PW1ShapeTransformer.io.rowWidth := io.rowWidth
  PW1ShapeTransformer.io.depth := CxShape.getLength().U * ChBatch.U
  for (i <- 0 until MACperCH) {
    inDMA_act.io.tensor(i) <> PW1ShapeTransformer.io.tensor(i)
    io.vme_rd(i) <> inDMA_act.io.vme_rd(i)
  }

  /* ================================================================== *
    *                  Depth-wise MACs & inDMA_acts                     *
    * ================================================================== */
  val inROWperCH =  MACperCH - macDWshape.getLength() + 1
  for (i <- 0 until Fx) {
    macDW(i).io.enable.bits <> ControlBundle.active()
    macDW(i).io.enable.valid := true.B
    macDW(i).io.wgtIndex := io.wgtDWindex + i.U
    macDW(i).io.outRowWidth := io.rowWidth - macDWshape.getLength().U + 1.U
    macDW(i).io.start := inDMA_wgtDW.io.done

    for (j <- 0 until inROWperCH) {

      macDW(i).io.in(j) <> DWShapeTransformer(i)(j).io.Out(0)

      for (k <- 0 until macDWshape.getLength()){

        DWShapeTransformer(i)(j).io.in(k) <> macPW1(i).io.Out(j + k)
      }

    }
  }

  /* ================================================================== *
     *                   Point-wise2 MACs & outDMA_acts                  *
     * ================================================================== */
  for (i <- 0 until NumPWFilter) {
    macPW2(i).io.enable.bits <> ControlBundle.active()
    macPW2(i).io.enable.valid := true.B
    macPW2(i).io.wgtIndex := io.wgtPW2index + i.U
    macPW2(i).io.outRowWidth :=  io.rowWidth - macDWshape.getLength().U + 1.U
    macPW2(i).io.start := inDMA_wgtPW2.io.done

    outDMA_act(i).io.rowWidth :=  io.rowWidth - macDWshape.getLength().U + 1.U
    outDMA_act(i).io.baddr := io.outBaseAddr + (i.U * (MACperCH.U * ( io.rowWidth - macDWshape.getLength().U + 1.U)))

    for (j <- 0 until MACperCH) {
      outDMA_act(i).io.in(j) <> macPW2(i).io.Out(j)
      io.vme_wr(i * MACperCH + j) <> outDMA_act(i).io.vme_wr(j)

      macPW2(i).io.in(j).bits.data := VecInit(macDW.map(_.io.Out(j).bits.data.asUInt())).asUInt()
      macPW2(i).io.in(j).bits.taskID := 0.U
      macPW2(i).io.in(j).bits.predicate := true.B
      macPW2(i).io.in(j).bits.valid := true.B
      macPW2(i).io.in(j).valid := macDW.map(_.io.Out(j).valid).reduceLeft(_ && _)

    }
    outDMA_act(i).io.last.foreach(a => a := macPW2(i).io.last)
    outDMA_act(i).io.start := macPW2(i).io.done
    when(outDMA_act(i).io.done) {
      doneR(i) := true.B
    }
  }

  /* ================================================================== *
    *                        loadNodes & mac1Ds                         *
    * ================================================================== */

  for (i <- 0 until Fx) {
    macPW1(i).io.enable.bits <> ControlBundle.active()
    macPW1(i).io.enable.valid := true.B
    macPW1(i).io.wgtIndex := io.wgtPW1index + (i * ChBatch).U
    macPW1(i).io.rowWidth := io.rowWidth

    for (j <- 0 until MACperCH) {
      macPW1(i).io.in(j) <> PW1ShapeTransformer.io.Out(j)(i)

      io.vme_wr(i*MACperCH + j) <> outDMA_act(i).io.vme_wr(j)
    }
  }

  macPW1.foreach(_.io.startLoadWgt := false.B)
  macPW1.foreach(_.io.startMac := false.B)

  /* ================================================================== *
      *                        Done Signal                              *
      * ================================================================== */

  io.done := false.B
  when (doneR.reduceLeft(_ && _)) {
    doneR.foreach(a => a := false.B)
  }

  val memTensorRows = Mux(io.rowWidth * ChBatch.U * CxShape.getLength().U  % tpMem.tensorWidth.U === 0.U,
    io.rowWidth * ChBatch.U * CxShape.getLength().U / tpMem.tensorWidth.U,
    (io.rowWidth * ChBatch.U * CxShape.getLength().U /tpMem.tensorWidth.U) + 1.U)

  when(macPW1.map(_.io.in.map(_.fire()).reduceLeft(_ && _)).reduceLeft(_ && _) & state === sExec){
    readTensorCnt.inc()
  }

  when(readTensorCnt.value === memTensorRows) {
    readTensorCnt.value := 0.U
  }


  switch(state) {
    is(sIdle) {
      when(io.start) {
        inDMA_wgtPW1.io.start := true.B
        state := sWgtRead
      }
    }
    is(sWgtRead) {
      when(inDMA_wgtPW1.io.done) {
        state := sActRead
        inDMA_act.io.start := true.B
        macPW1.foreach(_.io.startLoadWgt := true.B)
      }
    }
    is(sActRead) {
      when(inDMA_act.io.done){
        state := sMacLoadWgt
      }
    }
    is(sMacLoadWgt){
      when(macPW1.map(_.io.doneLoadWgt).reduceLeft(_ && _)){
        macPW1.foreach(_.io.startMac := true.B)
        state := sExec
      }
    }
    is(sExec){
      when(doneR.reduceLeft(_ && _)) {
        io.done := true.B
        state := sIdle
      }

    }
  }

}
