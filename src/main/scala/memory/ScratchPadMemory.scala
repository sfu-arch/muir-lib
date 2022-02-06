package dandelion.memory

import chisel3._
import chisel3.util._

import chipsalliance.rocketchip.config._
import dandelion.interfaces._
import dandelion.config._


/**
 * This is a simple scrtachpad memory that whenever we allocate a chunk of memory
 * we create one of these scrtachpads with MemReq/MemResp interface
 * There can be multiple Load and Store connected to these memories
 * hence we have to have an arbiter in front of the memory in the case of
 * having multiple memory operation
 * @param p
 */
class ScratchPadMemoryIO(implicit p: Parameters) extends AccelBundle()(p) with HasAccelParams {
  val req = Flipped(Decoupled(new MemReq))
  val resp = Valid(new MemResp)
}

class ScratchPadMemory(Size: Int)(implicit val p: Parameters) extends Module with HasAccelParams {
  val io = IO(new ScratchPadMemoryIO())

  val mem = Mem(Size, UInt(xlen.W))
  val mem_req = RegEnable(init = MemReq.default, next = io.req.bits, enable = io.req.fire)
  val mem_resp_data = RegInit(0.U(xlen.W))


  io.req.ready := true.B

  when(io.req.fire){
    io.resp.valid := false.B
  }.otherwise{
    io.resp.valid := true.B
  }

  io.resp.bits.data := mem_resp_data
  io.resp.bits.tag:= mem_req.tag
  io.resp.bits.iswrite := mem_req.iswrite

  val req_addr = (io.req.bits.addr >> 3.U).asUInt


  when(io.req.fire){
    when(io.req.bits.iswrite){
      mem.write(req_addr, io.req.bits.data)
    }.otherwise{
      mem_resp_data := mem.read(req_addr)
    }
  }

}
