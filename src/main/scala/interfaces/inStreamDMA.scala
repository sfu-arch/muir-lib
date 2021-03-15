
package interfaces

import chipsalliance.rocketchip.config.Parameters
import chisel3._
import chisel3.util._
import interfaces.memISA.{INST_BITS, MemDecode}
import dandelion.config._
import dandelion.shell.DMEReadMaster


class inStreamDMAIO()(implicit val p: Parameters)
  extends Module with HasAccelShellParams with HasAccelParams {
  val mp = memParams
  val io = IO(new Bundle {
    val start = Input(Bool())
    val done = Output(Bool())
    val baddr = Input(UInt(mp.addrBits.W))
    val len = Input(UInt(mp.addrBits.W))
    val ume_rd = new DMEReadMaster
    val out = Decoupled(UInt(xlen.W))
  })
}

class inStreamDMA(bufSize: Int)(implicit p: Parameters)
  extends inStreamDMAIO()(p) with HasAccelShellParams {

  val strLoad = Module(new StreamLoad(bufSize))

  io.done := strLoad.io.done

  val tl_Inst = Wire(new MemDecode)
  val memTensorRows = Mux(io.len % mp.tensorWidth.U === 0.U,
    io.len / mp.tensorWidth.U(mp.addrBits.W),
    (io.len / mp.tensorWidth.U(mp.addrBits.W)) + 1.U)

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

  strLoad.io.start := io.start
  strLoad.io.inst := tl_Inst.asTypeOf(UInt(INST_BITS.W))
  strLoad.io.baddr := io.baddr
  io.out <> strLoad.io.out
  io.ume_rd <> strLoad.io.vme_rd
}
