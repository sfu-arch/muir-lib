
package interfaces

import chipsalliance.rocketchip.config.Parameters
import chisel3._
import chisel3.util._
import dandelion.config._
import dandelion.shell.DMEWriteMaster


/** outDMA_act
  *
  * Load 1D and 2D tensors from main memory (DRAM) to input/weight
  * scratchpads (SRAM). Also, there is support for zero padding, while
  * doing the load. Zero-padding works on the y and x axis, and it is
  * managed by TensorPadCtrl. The TensorDataCtrl is in charge of
  * handling the way tensors are stored on the scratchpads.
  */
class StreamStore_IO(NumIns: Int)(implicit val p: Parameters)
  extends Module
  with HasAccelShellParams
  with HasAccelParams {
  val mp = memParams
  val io = IO(new Bundle {
    val start = Input(Bool())
    val last = Input(Bool())
    val done = Output(Bool())
    val baddr = Input(UInt(mp.addrBits.W))
    val ume_wr = new DMEWriteMaster
    val in = Flipped(Decoupled(Vec(NumIns, UInt(xlen.W))))
    val outLen = Output(UInt(mp.addrBits.W))
  })
}



class StreamStore(bufSize: Int, NumIns: Int)(implicit p: Parameters)
  extends StreamStore_IO(NumIns)(p) {
  require(bufSize > math.pow(2, mp.lenBits).toInt * mp.dataBits / xlen, "buffer size should be greater than the tensorFile width")
  require(mp.dataBits % xlen== 0, "AXI bus width should be a multiple of XLEN")

  val bus_width = mp.dataBits / xlen

  val storeQueue = Module(new StoreMIMOQueue(UInt(xlen.W), bufSize, NumIns, bus_width))

  val pushCnt = Counter(math.pow(2, xlen).toInt)
  val last = RegInit(false.B)

  val addr = Reg(chiselTypeOf(io.ume_wr.cmd.bits.addr))
  val len = Reg(chiselTypeOf(io.ume_wr.cmd.bits.len))

  val addr_hop = RegInit((math.pow(2, mp.lenBits).toInt * bus_width * xlen/8).U(mp.addrBits.W))


  when(io.last){
    last := true.B
  }

  val sIdle :: sLoading :: sSendReq :: sBusy :: sLastReq :: sLastSending :: Nil = Enum(6)
  val state = RegInit(sIdle)


  when(storeQueue.io.enq.fire){
    pushCnt.inc()
  }

  storeQueue.io.last := io.last

  io.ume_wr.cmd.bits.addr := addr
  io.ume_wr.cmd.bits.len := len
  io.ume_wr.cmd.valid := false.B

  io.done := false.B
  io.outLen := pushCnt.value

  switch(state){
    is(sIdle){
      when(io.start){
        addr := io.baddr
        state := sLoading
      }
    }
    is(sLoading){
      when(storeQueue.io.count > (math.pow(2, mp.lenBits).toInt * bus_width).asUInt){
        len := math.pow(2, mp.lenBits).toInt.asUInt - 1.U
        state := sSendReq
      }.elsewhen(last){
        len := Mux (storeQueue.io.count % bus_width.U(storeQueue.io.count.getWidth.W) === 0.U,
          storeQueue.io.count/bus_width.U(storeQueue.io.count.getWidth.W) - 1.U,
          storeQueue.io.count/bus_width.U(storeQueue.io.count.getWidth.W))
        state := sLastReq
      }
    }
    is(sSendReq){
      when(io.ume_wr.cmd.fire){
        state := sBusy
      }
    }
    is(sBusy){
      when(io.ume_wr.ack){
        addr := addr + addr_hop
        state := sLoading
      }
    }
    is(sLastReq){
      when(io.ume_wr.cmd.fire){
        state := sLastSending
      }
    }
    is(sLastSending){
      when(io.ume_wr.ack){
        io.done := true.B
        //        addr := addr + ((len + 1.U) * tp.tensorWidth.U * p(XLEN).U/8.U)
        last := false.B
        state := sIdle
      }
    }
  }

  when(state === sSendReq || state === sLastReq){
    io.ume_wr.cmd.valid := true.B
  }

  storeQueue.io.last := io.last
  storeQueue.io.enq.bits := io.in.bits.asTypeOf(storeQueue.io.enq.bits)
  storeQueue.io.enq.valid := io.in.valid
  io.in.ready := storeQueue.io.enq.ready && !last

  io.ume_wr.data.bits := storeQueue.io.deq.bits.asUInt
  io.ume_wr.data.valid := storeQueue.io.deq.valid
  storeQueue.io.deq.ready := io.ume_wr.data.ready

}

