package dnn.memory

import chisel3._
import chisel3.util._
import config._
import interfaces.{ControlBundle, DataBundle, WriteReq, WriteResp}
import node.{TypLoad}
import shell._

/**
  * Load inputs or weights from memory (DRAM) into scratchpads (SRAMs).
  * VMELoad Architecture:
  * * * * * IO(memReq/memResp) <--> LoadType <--> Buffer <--> VMEWriteMaster * * * * *
*/
class VMEStore(debug: Boolean = false)(implicit p: Parameters) extends Module {
  val io = IO(new Bundle {
    val start = Input(Bool())
    val done = Output(Bool())
    val vme_cmd = Flipped(Decoupled(new VMECmd()))
    val vme_write = new VMEWriteMaster()
    val base_addr = Input(new DataBundle())
    val memReq = Decoupled(new WriteReq())
    val memResp = Input(Flipped(new WriteResp()))
  })

  val ReadDataCounter = Counter(math.pow(2, io.vme_cmd.bits.lenBits).toInt)
  val WriteDataCounter = Counter(math.pow(2, io.vme_cmd.bits.lenBits).toInt)
  val buffer = Module(new Queue(io.vme_write.data.bits.cloneType, 50))

  val LoadType = Module(new TypLoad(NumPredOps = 0, NumSuccOps = 0, NumOuts = 1, ID = 0, RouteID = 0))

  io.done := false.B
  LoadType.io.enable.bits := ControlBundle.active()
  LoadType.io.enable.valid := true.B
  LoadType.io.Out(0).ready := true.B
//  StoreType.io.GepAddr := io.base_addr

  io.memReq <> LoadType.io.memReq
  LoadType.io.memResp <> io.memResp

  val sIdle :: sReq :: sBusy :: Nil = Enum(3)
  val Wstate = RegInit(sIdle)

  // VME_Write state machine
  switch (Wstate) {
    is (sIdle) {
      when (io.start) {
        WriteDataCounter.value := 0.U
        Wstate := sReq
      }
    }
    is (sReq) {
      when (io.vme_write.cmd.fire()) {
        Wstate := sBusy
      }
    }
  }

  io.vme_write.cmd.bits.addr := io.vme_cmd.bits.addr
  io.vme_write.cmd.bits.len := io.vme_cmd.bits.len
  io.vme_write.cmd.valid := false.B


  when (Wstate =/= sIdle) {
    WriteDataCounter.inc( )
  }

  when(Wstate === sReq) {
    io.vme_write.cmd.valid := true.B
  }

  when(WriteDataCounter.value === io.vme_cmd.bits.len) {
    Wstate := sIdle
  }

  when(io.vme_write.ack) {
    Wstate := sIdle
  }

  io.vme_write.data <> buffer.io.deq

  val sRIdle :: sReadData :: sGepAddr :: Nil = Enum(3)
  val state = RegInit(sRIdle)

  switch(state) {
    is(sRIdle) {
      when(io.start && io.vme_cmd.fire()) {
        state := sGepAddr
        ReadDataCounter.value := 0.U
      }
    }
    is(sGepAddr) {
      when(LoadType.io.GepAddr.ready && LoadType.io.Out(0).ready) {
        buffer.io.enq.bits := LoadType.io.Out(0).bits
        LoadType.io.GepAddr := io.base_addr.data + ReadDataCounter.value
        state := sReadData
      }
    }
    is(sReadData) {
      when(LoadType.io.Out(0).fire) {
        ReadDataCounter.inc()
        state := sGepAddr
      }.elsewhen(ReadDataCounter.value === io.vme_cmd.bits.len) {
        state := sRIdle
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
    when(state === sReadData) {
      when(io.done) {
        printf("[Load] Reading data\n")
      }.otherwise {
        printf("[VME_Load] Read is done\n")
      }
    }
  }
}
