
package dnn_layers

import chisel3._
import chisel3.util._
import config._
import dnn.memory._
import dnn.types.{OperatorDot, OperatorReduction}
import dnnnode.{Mac1D, Mac2dTensor, MacPW, PWShapeTransformer, TLoad}
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
class PW_BlockIO[gen <: vecN, gen2 <: Shapes]
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

    val wgtIndex = Input(UInt(tpWgt.memAddrBits.W))
    val vme_wgt_rd = new VMEReadMaster

    val wgt_baddr = Input(UInt(mp.addrBits.W))

    val inDMA_act_time = Output(UInt(mp.addrBits.W))
    val inDMA_wgt_time = Output(UInt(mp.addrBits.W))
    val outDMA_act_time = Output(UInt(mp.addrBits.W))
    val mac_time = Output(UInt(mp.addrBits.W))
  })
}

class PW_Block[L <: vecN, K <: Shapes : OperatorDot : OperatorReduction]
(MACperCH: Int, Fx: Int, ChBatch: Int, wgtType: String = "none", memTensorType: String = "none")
(memShape: => L)(CxShape: => K)(implicit p: Parameters)
  extends PW_BlockIO(MACperCH, Fx, wgtType, memTensorType)(memShape)(CxShape)(p) {

  val inDMA_act_time = Counter(2000)
  val inDMA_wgt_time = Counter(2000)
  val outDMA_act_time = Counter(2000)
  val mac_time = Counter(2000)

  io.inDMA_act_time := inDMA_act_time.value
  io.inDMA_wgt_time := inDMA_wgt_time.value
  io.outDMA_act_time := outDMA_act_time.value
  io.mac_time := mac_time.value

  val inDMA_act =  Module(new inDMA_act_HWC(MACperCH, 1, memTensorType)(memShape))

  val PWShapeTransformer = Module(new PWShapeTransformer(MACperCH, Fx, 20, memTensorType)(CxShape))

  val mac1D = for (i <- 0 until Fx) yield {
    val mac1d = Module(new Mac1D(MACperCH, ChBatch, 20,  wgtType)(CxShape))
    mac1d
  }

  val outDMA_act = for (i <- 0 until Fx) yield {
    val outDMA = Module(new outDMA_act(MACperCH, 20, memTensorType))
    outDMA
  }

  val doneR = for (i <- 0 until Fx) yield {
    val doneReg = RegInit(init = false.B)
    doneReg
  }

  val readTensorCnt = Counter(tpMem.memDepth)

  val sIdle :: sWgtRead :: sActRead :: sMacLoadWgt :: sExec :: Nil = Enum(5)
  val state = RegInit(sIdle)

  /* ================================================================== *
   *                     Depth-wise - inDMA_weight                      *
   * ================================================================== */

  val inDMA_wgt = Module(new inDMA_wgt(20, 100, wgtType, memTensorType)(CxShape))
  val wgtCtrl = Module(new ReadTensorController(Fx, wgtType)(CxShape))
  inDMA_wgt.io.tensor <> wgtCtrl.io.tensor
  io.vme_wgt_rd <> inDMA_wgt.io.vme_rd

  inDMA_wgt.io.numWeight := 15.U
  inDMA_wgt.io.start := false.B
  inDMA_wgt.io.baddr := io.wgt_baddr

  for (i <- 0 until Fx) {
    wgtCtrl.io.ReadIn(i) <> mac1D(i).io.wgtTensorReq
    mac1D(i).io.wgtTensorResp <> wgtCtrl.io.ReadOut(i)
  }

  /* ================================================================== *
    *                      inDMA_acts & loadNodes                       *
    * ================================================================== */

  inDMA_act.io.start := io.start
  inDMA_act.io.rowWidth := io.rowWidth
  inDMA_act.io.depth := CxShape.getLength().U * ChBatch.U
  inDMA_act.io.baddr := io.inBaseAddr

  PWShapeTransformer.io.start := inDMA_act.io.done
  PWShapeTransformer.io.rowWidth := io.rowWidth
  PWShapeTransformer.io.depth := CxShape.getLength().U * ChBatch.U
  for (i <- 0 until MACperCH) {
    inDMA_act.io.tensor(i) <> PWShapeTransformer.io.tensor(i)
    io.vme_rd(i) <> inDMA_act.io.vme_rd(i)
  }
  /* ================================================================== *
    *                        loadNodes & mac1Ds                         *
    * ================================================================== */

  for (i <- 0 until Fx) {
    mac1D(i).io.enable.bits <> ControlBundle.active()
    mac1D(i).io.enable.valid := true.B
    mac1D(i).io.wgtIndex := io.wgtIndex + (i * ChBatch).U
    mac1D(i).io.rowWidth := io.rowWidth

    for (j <- 0 until MACperCH) {
      mac1D(i).io.in(j) <> PWShapeTransformer.io.Out(j)(i)

      outDMA_act(i).io.in(j) <> mac1D(i).io.Out(j)
      io.vme_wr(i*MACperCH + j) <> outDMA_act(i).io.vme_wr(j)
    }
    outDMA_act(i).io.rowWidth := io.rowWidth
    outDMA_act(i).io.baddr := io.outBaseAddr + (i.U * (MACperCH.U * io.rowWidth))

    outDMA_act(i).io.last.foreach(a => a := mac1D(i).io.last)
    outDMA_act(i).io.start := mac1D(i).io.doneMac
    when(outDMA_act(i).io.done) {
      doneR(i) := true.B
    }
  }

  mac1D.foreach(_.io.startLoadWgt := false.B)
  mac1D.foreach(_.io.startMac := false.B)

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

  when(mac1D.map(_.io.in.map(_.fire()).reduceLeft(_ && _)).reduceLeft(_ && _) & state === sExec){
    readTensorCnt.inc()
  }

  when(readTensorCnt.value === memTensorRows) {
    readTensorCnt.value := 0.U
  }

  when(state === sIdle){
    inDMA_act_time.value := 0.U
    inDMA_wgt_time.value := 0.U
    outDMA_act_time.value := 0.U
    mac_time.value := 0.U
  }

  when(state === sWgtRead) {inDMA_wgt_time.inc()}
  when(state === sActRead) {inDMA_act_time.inc()}
  when(state === sExec) {mac_time.inc()}

  switch(state) {
    is(sIdle) {
      when(io.start) {
        inDMA_wgt.io.start := true.B
        state := sWgtRead
      }
    }
    is(sWgtRead) {
      when(inDMA_wgt.io.done) {
        state := sActRead
        inDMA_act.io.start := true.B
        mac1D.foreach(_.io.startLoadWgt := true.B)
      }
    }
    is(sActRead) {
      when(inDMA_act.io.done){
        state := sMacLoadWgt
      }
    }
    is(sMacLoadWgt){
      when(mac1D.map(_.io.doneLoadWgt).reduceLeft(_ && _)){
        mac1D.foreach(_.io.startMac := true.B)
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
