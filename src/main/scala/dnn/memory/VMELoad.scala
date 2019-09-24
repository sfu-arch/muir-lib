package dnn.memory

import chisel3._
import chisel3.util._
import config._
import dnn._
import interfaces.{ControlBundle, DataBundle, TypBundle, WriteReq, WriteResp}
import node.TypStore
import shell._

/**
  * Load inputs or weights from memory (DRAM) into scratchpads (SRAMs).
  * VMELoad Architecture:
  * * * * * IO(memReq/memResp) <--> StoreType <--> Buffer <--> VMEReadMaster * * * * *
  */
class VMELoad(debug: Boolean = false)(implicit val p: Parameters) extends Module with config.CoreParams {
  val io = IO(new Bundle {
    val start = Input(Bool())
    val done = Output(Bool())
    val vme_cmd = Flipped(Decoupled(new VMECmd()))
    val vme_read = new VMEReadMaster()
    val base_addr = Input(new DataBundle())
    val memReq = Decoupled(new WriteReq())
    val memResp = Input(Flipped(new WriteResp()))
  })

  val ReadDataCounter = Counter(math.pow(2, io.vme_cmd.bits.lenBits).toInt)
  val WriteDataCounter = Counter(math.pow(2, io.vme_cmd.bits.lenBits).toInt)
  val buffer = Module(new Queue(io.vme_read.data.bits.cloneType, 50))

  val StoreType = Module(new TypStore(NumPredOps = 0, NumSuccOps = 0, NumOuts = 1, ID = 0, RouteID = 0))

  StoreType.io.inData.bits := TypBundle.default
  StoreType.io.inData.valid := false.B

  StoreType.io.GepAddr.bits := DataBundle.default
  StoreType.io.GepAddr.valid := false.B

  StoreType.io.enable.bits := ControlBundle.active()
  StoreType.io.enable.valid := true.B
  StoreType.io.Out(0).ready := true.B
  //  StoreType.io.GepAddr := io.base_addr

  io.memReq <> StoreType.io.memReq
  StoreType.io.memResp <> io.memResp

  io.vme_read.cmd <> io.vme_cmd

  val sIdle :: sReq :: sBusy :: Nil = Enum(3)
  val Rstate = RegInit(sIdle)
  val Wstate = RegInit(sIdle)

  // Read from VMEReadMaster
  switch(Rstate) {
    is(sIdle) {
      when(io.start) {
        ReadDataCounter.value := 0.U
        Rstate := sReq
      }
    }
    is(sReq) {
      when(io.vme_read.cmd.fire()) {
        Rstate := sBusy
      }
    }
  }

  when(Rstate =/= sIdle) {
    ReadDataCounter.inc()
  }

  when(Rstate === sReq) {
    io.vme_read.cmd.valid := true.B
  }

  when(ReadDataCounter.value === io.vme_cmd.bits.len) {
    Rstate := sIdle
  }


  /**
    * @todo this line is wrong!
    */
  buffer.io.enq <> io.vme_read.data
  buffer.io.deq.ready := false.B
  //  buffer.io.enq.bits := io.vme_read.data.bits + 5.U
  //  StoreType.io.inData <> buffer.io.deq
  //  StoreType.io.GepAddr := io.base_addr.data + ReadDataCounter.value


  io.done := false.B

  val sWIdle :: sWriteData :: sGepAddr :: Nil = Enum(3)
  val state = RegInit(sWIdle)

  switch(state) {
    is(sWIdle) {
      when(io.start && io.vme_cmd.fire()) {
        state := sGepAddr
        WriteDataCounter.value := 0.U
      }
    }
    is(sGepAddr) {
      when(StoreType.io.GepAddr.ready && StoreType.io.inData.ready) {

        /**
          * @todo make return type of vme_load as typebundle instead of UInt
          */
        StoreType.io.inData.bits := TypBundle.active(buffer.io.deq.bits)
        StoreType.io.inData.valid := buffer.io.deq.valid
        buffer.io.deq.ready := StoreType.io.inData.ready

        StoreType.io.GepAddr.bits.data := io.base_addr.data + WriteDataCounter.value
        StoreType.io.GepAddr.valid := true.B

        buffer.io.deq.ready := StoreType.io.inData.ready


        state := sWriteData
      }
    }
    is(sWriteData) {
      when(StoreType.io.Out(0).fire) {
        WriteDataCounter.inc()
        state := sGepAddr
      }.elsewhen(WriteDataCounter.value === io.vme_cmd.bits.len) {
        state := sWIdle
        io.done := true.B
      }
    }
  }

  // debug
  if (debug) {
    // start
    when(state === sIdle && io.start) {
      printf("[VME_Load] start\n")
    }
    // done
    when(state === sWriteData) {
      when(io.done) {
        printf("[Load] Reading data\n")
      }.otherwise {
        printf("[VME_Load] Read is done\n")
      }
    }
  }
}
