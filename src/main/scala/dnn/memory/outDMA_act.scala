
package dnn.memory

import chisel3._
import chisel3.util.Decoupled
import config._
import dnnnode.{StoreQueue, TStore}
import interfaces.{ControlBundle, CustomDataBundle, TensorReadReq, TensorReadResp}
import node.{Shapes, vecN}
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
class outDMA_actIO[gen <: vecN](NumRows: Int, memTensorType: String = "none")(memShape: => gen)(implicit val p: Parameters)
  extends Module {
  val tp = new TensorParams(memTensorType)
  val mp = p(ShellKey).memParams
  val io = IO(new Bundle {
    val start = Input(Bool())
    val done = Output(Bool())
    val baddr = Input(UInt(mp.addrBits.W))
    val rowWidth = Input(UInt(mp.addrBits.W))
    val vme_rd = Vec(NumRows, new VMEReadMaster)
    val ReadIn  = Vec(NumRows, Vec(NumOuts, Flipped(Decoupled(new TensorReadReq()))))
    val ReadOut = Vec(NumRows, Vec(NumOuts, Output(new TensorReadResp(memShape.getWidth))))
  })
}

class outDMA_act[L <: vecN](NumRows: Int, memTensorType: String = "none")(memShape: => L)(implicit p: Parameters)
  extends outDMA_actIO(NumRows, memTensorType)(memShape)(p) {

  val tensorStore = for (i <- 0 until NumRows) yield {
    val tensorS = Module(new TensorStore(memTensorType))
    tensorS
  }

  val storeNode = for (i <- 0 until NumRows) yield {
    val sNode = Module(new TStore(NumPredOps = 0, NumSuccOps = 0, NumOuts = 1, ID = 0, RouteID = 0)(memShape))
    sNode
  }

  val WCtrl = for (i<- 0 until NumRows) yield {
    val ctrl = Module(new WriteTensorController(1, memTensorType)(memShape))
    ctrl
  }

  val storeBuffer = for (i <- 0 until NumRows) yield {
    val buf = Module(new StoreQueue(new CustomDataBundle(UInt(p(XLEN).W)), 12, tp.tensorWidth))
    buf
  }


  for (i <-0 until NumRows) {

    tensorStore(i).io.tensor.wr.valid := storeBuffer(i).io.deq.valid
    tensorStore(i).io.tensor.wr.bits.data := VecInit(storeBuffer(i).io.deq.bits.map(_.data.asUInt())).asUInt()
    

    WCtrl(i).io.WriteIn(0) <> storeNode(i).io.tensorReq
    storeNode(i).io.tensorResp <> WCtrl(i).io.WriteOut(0)
    tensorStore(i).io.tensor <> WCtrl(i).io.tensor

    storeNode(i).io.enable.bits <> ControlBundle.active()
    storeNode(i).io.enable.valid := true.B


    storeNode(i).io.inData.bits.data := VecInit(storeBuffer(i).io.deq.bits.map(_.data.asUInt())).asUInt()

    storeNode(i).io.inData.bits.valid := storeBuffer(i).io.deq.valid
    storeNode(i).io.inData.bits.taskID := 0.U
    storeNode(i).io.inData.bits.predicate := true.B
    storeNode(i).io.inData.valid := storeBuffer(i).io.deq.valid
    storeBuffer(i).io.deq.ready := storeNode(i).io.inData.ready
  }

  storeBuffer.io.enq <> macNode.io.Out(i)

  Store.io.GepAddr.valid := true.B//macNode.io.Out(0).valid
  Store.io.GepAddr.bits.taskID := 0.U
  Store.io.GepAddr.bits.data := storeIndex
  Store.io.GepAddr.bits.predicate := true.B

  Store.io.Out(0).ready := true.B




  val doneR = for (i <- 0 until NumRows) yield {
    val doneReg = RegInit(init = false.B)
    doneReg
  }

  io.done := doneR.reduceLeft(_ && _)

  when (doneR.reduceLeft(_ && _)) {
    doneR.foreach(a => a := false.B)
  }

  for (i <- 0 until NumRows) yield{
    when (tensorLoad(i).io.done) {
      doneR(i) := true.B
    }
  }

  val tl_Inst = Wire(new MemDecode)
  val memTensorRows = Mux(io.rowWidth % tp.tensorWidth.U === 0.U, io.rowWidth / tp.tensorWidth.U, (io.rowWidth /tp.tensorWidth.U) + 1.U)

  tl_Inst.xpad_0 := 0.U
  tl_Inst.xpad_1 := 0.U
  tl_Inst.ypad_0 := 0.U
  tl_Inst.ypad_1 := 0.U
  tl_Inst.xstride := memTensorRows
  tl_Inst.xsize := memTensorRows
  tl_Inst.ysize := 1.U
  tl_Inst.empty_0 := 0.U
  tl_Inst.dram_offset := 0.U
  tl_Inst.sram_offset := 0.U
  tl_Inst.id := 3.U
  tl_Inst.push_next := 0.U
  tl_Inst.push_prev := 0.U
  tl_Inst.pop_next := 0.U
  tl_Inst.pop_prev := 0.U
  tl_Inst.op := 0.U

  for (i <- 0 until NumRows) {
    tensorLoad(i).io.start := io.start
    tensorLoad(i).io.inst := tl_Inst.asTypeOf(UInt(INST_BITS.W))
    tensorLoad(i).io.baddr := io.baddr + (i.U * io.rowWidth)
    tensorLoad(i).io.tensor <> readTensorCtrl(i).io.tensor
//    tensorLoad(i).io.vme_rd <> io.vme_rd(i)
    io.vme_rd(i) <> tensorLoad(i).io.vme_rd
  }

  for (i <- 0 until NumRows) {
    for (j <- 0 until NumOuts) {
      readTensorCtrl(i).io.ReadIn(j) <> io.ReadIn(i)(j)
      io.ReadOut(i)(j) <> readTensorCtrl(i).io.ReadOut(j)
    }
  }




}
