// See LICENSE for license details.

package dandelion.memory.stack

import chisel3._
import chisel3.util._

import dandelion.junctions._
import chipsalliance.rocketchip.config._
import dandelion.config._
import dandelion.interfaces._
import NastiConstants._
import dandelion.config._

class StackMemIO(implicit p: Parameters) extends AccelBundle()(p) with HasAccelParams {
  val req   = Flipped(Decoupled(new MemReq))
  val resp  = Output(Valid(new MemResp))
}

class StackMem(size : Int)(implicit val p: Parameters) extends Module with HasAccelParams {
  val io = IO(new StackMemIO)

//  val mem = Mem(size*(1<<tlen), UInt(xlen.W))
  val mem = Mem(size, UInt(xlen.W))
  val xlen_bytes = xlen / 8
  val wordindex = log2Ceil(xlen_bytes)

  io.req.ready := true.B

  val addr = io.req.bits.addr(wordindex + log2Ceil(size) - 1, wordindex)
  when(io.req.fire && io.req.bits.iswrite) {
    mem.write(addr, io.req.bits.data)
  }

  val resp_R = Reg(io.resp.cloneType)
  resp_R.bits.data := mem.read(addr)
  resp_R.bits.iswrite := io.req.bits.iswrite
  resp_R.bits.tag := io.req.bits.tag
  resp_R.valid := io.req.valid
  io.resp := resp_R

}
