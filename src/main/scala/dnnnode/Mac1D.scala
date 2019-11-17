
package dnnnode

import chisel3._
import chisel3.util._
import config._
import dnn.MacNode
import dnn.memory.TensorParams
import dnn.types.{OperatorDot, OperatorReduction}
import interfaces.{ControlBundle, CustomDataBundle, TensorReadReq, TensorReadResp}
import node.{HandShakingIONPS, HandShakingNPS, Shapes, vecN}
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
class Mac1DIO[gen <: vecN, gen2 <: Shapes](NumMac: Int, wgtTensorType: String = "none", memTensorType: String = "none")
                                                (memShape: => gen)(macShape: => gen2)(implicit p: Parameters)
  extends HandShakingIONPS(NumMac)(new CustomDataBundle(UInt(p(XLEN).W))) {
  val tpWgt = new TensorParams(wgtTensorType)
  val mp = p(ShellKey).memParams

    val in = Vec(NumMac ,Flipped(Decoupled(new CustomDataBundle(UInt(memShape.getWidth.W)))))
    val wgtTensorReq = Decoupled(new TensorReadReq())
    val wgtTensorResp = Input(Flipped(new TensorReadResp(macShape.getWidth)))
    val wgtIndex = Input(UInt(tpWgt.memAddrBits.W))
    val rowWidth = Input(UInt(mp.addrBits.W))
    val last = Output(Bool())
    val start = Input(Bool())
    val done = Output(Bool())

  override def cloneType = new Mac1DIO(NumMac, wgtTensorType, memTensorType)(memShape)(macShape).asInstanceOf[this.type]
}

class Mac1D[L <: vecN, K <: Shapes : OperatorDot : OperatorReduction]
              (NumMac: Int, ChBatch: Int, bufSize: Int, wgtTensorType: String = "none", memTensorType: String = "none")
              (memShape: => L)(macShape: => K)
              (implicit p: Parameters)
  extends HandShakingNPS(NumMac, 0)(new CustomDataBundle(UInt(p(XLEN).W)))(p) {
  override lazy val io = IO(new Mac1DIO(NumMac, wgtTensorType, memTensorType)(memShape)(macShape))

  val tpMem = new TensorParams(memTensorType)
  val sIdle :: sReadWeight :: sExec :: sFinish :: Nil = Enum(4)
  val state = RegInit(sIdle)

  val readWgtCnt = Counter(ChBatch + 1)
  val outCnt = Counter(io.tpWgt.memDepth)
  io.done := false.B
  io.last := false.B

  val loadWeight = Module(new TLoad(NumPredOps = 0, NumSuccOps = 0, NumOuts = 1, ID = 0, RouteID = 0)(macShape))
  val weight = RegInit(CustomDataBundle.default(0.U(macShape.getWidth.W)))
  val weight_valid = RegInit(false.B)

  loadWeight.io.enable.bits <> ControlBundle.active()
  loadWeight.io.enable.valid := true.B
  io.wgtTensorReq <> loadWeight.io.tensorReq
  loadWeight.io.tensorResp <> io.wgtTensorResp
  loadWeight.io.GepAddr.valid := false.B
  loadWeight.io.GepAddr.bits.taskID := 0.U
  loadWeight.io.GepAddr.bits.predicate := true.B
  loadWeight.io.GepAddr.bits.data := io.wgtIndex + readWgtCnt.value

  loadWeight.io.Out(0).ready := ~weight_valid
  when(loadWeight.io.Out(0).fire()) {
    weight := loadWeight.io.Out(0).bits
    weight_valid := true.B
  }


//  val weightBuf = SyncReadMem(ChBatch, wgtShape)

  val weightQ = Module( new Queue(CustomDataBundle(UInt(macShape.getWidth.W)), ChBatch + 1))
  weightQ.io.enq.bits := Mux(state === sReadWeight, weight, weightQ.io.deq.bits)
  weightQ.io.enq.valid := Mux(state === sReadWeight, weight_valid, weightQ.io.deq.valid)
  when(weightQ.io.enq.fire()) {
    weight_valid := false.B
  }

  val mac = for (i <- 0 until NumMac) yield {
    val macNode = Module(new MacNode(NumOuts = 1, ID = 0, lanes = macShape.getLength())(macShape))
    macNode
  }

  val outData = for (i <- 0 until NumMac) yield {
    val data = RegInit(CustomDataBundle.default(0.U(xlen.W)))
    data
  }

  val accValid = for (i <- 0 until NumMac) yield {
    val accV = RegInit(false.B)
    accV
  }

  val inQueue = for (i <- 0 until NumMac) yield {
    val buffer = Module(new MIMOQueue(UInt(p(XLEN).W), bufSize, tpMem.tensorWidth, macShape.getLength()))
    buffer
  }

  val dataIn_R = RegInit(VecInit(Seq.fill(NumMac)(CustomDataBundle.default(0.U(memShape.getWidth.W)))))
  val dataIn_validR = RegInit(VecInit(Seq.fill(NumMac)(false.B)))

  for (i <- 0 until NumMac) {
    io.in(i).ready := ~dataIn_validR(i)
    when(io.in(i).fire()) {
      dataIn_R(i).data := io.in(i).bits.data
      dataIn_validR(i) := true.B
    }
  }


  val batchCnt = for (i <- 0 until NumMac) yield {
    val batchCounter = Counter(ChBatch + 1)
    batchCounter
  }


  inQueue.foreach(_.io.clear := false.B)

  for (i <- 0 until NumMac) {
    inQueue(i).io.enq.bits := dataIn_R(i).data.asTypeOf(inQueue(i).io.enq.bits)
    inQueue(i).io.enq.valid := dataIn_validR(i)

    when(inQueue(i).io.enq.fire()){
      dataIn_validR(i) := false.B
    }

    mac(i).io.enable.bits <> ControlBundle.active()
    mac(i).io.enable.valid := true.B

    mac(i).io.LeftIO.bits.data := inQueue(i).io.deq.bits.asUInt()
    mac(i).io.LeftIO.bits.valid := true.B
    mac(i).io.LeftIO.bits.predicate := true.B
    mac(i).io.LeftIO.bits.taskID := 0.U
    mac(i).io.LeftIO.valid := inQueue(i).io.deq.valid & state === sExec

    inQueue(i).io.deq.ready := mac(i).io.LeftIO.ready & state === sExec

    mac(i).io.RightIO.bits := weightQ.io.deq.bits
    mac(i).io.RightIO.valid := weightQ.io.deq.valid & state === sExec
    weightQ.io.deq.ready := mac.map(_.io.RightIO.ready).reduceLeft(_ && _) & state === sExec


    mac(i).io.Out(0).ready := ~accValid(i) & (batchCnt(i).value < ChBatch.U)
    when(mac(i).io.Out(0).fire()) {
      outData(i).data := outData(i).data + mac(i).io.Out(0).bits.data.asUInt()
      outData(i).valid := mac(i).io.Out(0).bits.valid
      outData(i).taskID := mac(i).io.Out(0).bits.taskID
      outData(i).predicate := mac(i).io.Out(0).bits.predicate
      accValid(i) := true.B
    }

    when(accValid(i)) {
      accValid(i) := false.B
    }

    io.Out(i).bits := outData(i)

    when(mac(i).io.Out(0).fire()) {
      batchCnt(i).inc()
    }
    io.Out(i).valid := false.B
    when(batchCnt(i).value === ChBatch.U) {
      io.Out(i).valid := true.B
    }
    when(io.Out(i).fire()) {
      batchCnt(i).value := 0.U
      outData(i).data := 0.U
    }
  }



  /*val memTensorRows = Mux(io.rowWidth * ChBatch.U * macShape.getLength().U  % tpMem.tensorWidth.U === 0.U,
    io.rowWidth * ChBatch.U * macShape.getLength().U / tpMem.tensorWidth.U,
    (io.rowWidth * ChBatch.U * macShape.getLength().U /tpMem.tensorWidth.U) + 1.U)

  val readTensorCnt = Counter(io.tpWgt.memDepth)
  when (dataIn_validR.reduceLeft(_ && _) && inQueue.map(_.io.enq.ready).reduceLeft(_ && _)) {
    readTensorCnt.inc()
  }*/


  when (io.Out.map(_.fire()).reduceLeft(_ && _)){
    outCnt.inc()
  }

  when (loadWeight.io.Out(0).fire()){
    readWgtCnt.inc()
  }

  switch(state) {
    is (sIdle) {
      when(io.start) {
        state := sReadWeight
      }
    }
    is (sReadWeight) {
      loadWeight.io.GepAddr.valid := true.B
      when(readWgtCnt.value === ChBatch.U){
        readWgtCnt.value := 0.U
        state := sExec
      }
    }

    is (sExec) {
        when (outCnt.value === io.rowWidth) {
        state := sFinish
        outCnt.value := 0.U
      }
    }
    is (sFinish){
        io.done := true.B
        inQueue.foreach(_.io.clear := true.B)
        io.last := true.B
        state := sIdle

    }
  }

}
