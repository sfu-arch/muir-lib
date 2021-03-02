
package interfaces

import chisel3._
import chipsalliance.rocketchip.config.Parameters
import chisel3.util._
import interfaces._
import interfaces.memISA._
import dandelion.config._


/** MemDataCtrl. Data controller for StreamLoad. */
class MemDataCtrl(sizeFactor: Int = 1,
                  strideFactor: Int = 1)(implicit val p: Parameters)
  extends Module with HasAccelShellParams {
  val mp = memParams
  val io = IO(new Bundle {
    val start = Input(Bool())
    val done = Output(Bool())
    val inst = Input(UInt(INST_BITS.W))
    val baddr = Input(UInt(mp.addrBits.W))
    val xinit = Input(Bool())
    val xupdate = Input(Bool())
    val yupdate = Input(Bool())
    val stride = Output(Bool())
    val split = Output(Bool())
    val commit = Output(Bool())
    val addr = Output(UInt(mp.addrBits.W))
    val len = Output(UInt(mp.lenBits.W))
  })

  val dec = io.inst.asTypeOf(new MemDecode)

  val caddr = Reg(UInt(mp.addrBits.W))
  val baddr = Reg(UInt(mp.addrBits.W))

  val len = Reg(UInt(mp.lenBits.W))

  val xmax_bytes = ((1 << mp.lenBits) * mp.dataBits / 8).U
  val xcnt = Reg(UInt(mp.lenBits.W))
  val xrem = Reg(chiselTypeOf(dec.xsize))
  val xsize = (dec.xsize << log2Ceil(sizeFactor))
  val xmax = (1 << mp.lenBits).U
  val ycnt = Reg(chiselTypeOf(dec.ysize))

  val stride = xcnt === len &
    xrem === 0.U &
    ycnt =/= dec.ysize - 1.U

  val split = xcnt === len & xrem =/= 0.U

  when(io.start || (io.xupdate && stride)) {
    when(xsize < xmax) {
      len := xsize - 1.U
      xrem := 0.U
    }.otherwise {
      len := xmax - 1.U
      xrem := xsize - xmax
    }
  }.elsewhen(io.xupdate && split) {
    when(xrem < xmax) {
      len := xrem - 1.U
      xrem := 0.U
    }.otherwise {
      len := xmax - 1.U
      xrem := xrem - xmax
    }
  }

  when(io.xinit) {
    xcnt := 0.U
  }.elsewhen(io.xupdate) {
    xcnt := xcnt + 1.U
  }

  when(io.start) {
    ycnt := 0.U
  }.elsewhen(io.yupdate && stride) {
    ycnt := ycnt + 1.U
  }

  val maskOffset = VecInit(Seq.fill(M_DRAM_OFFSET_BITS)(true.B)).asUInt
  val elemBytes = mp.tensorLength * mp.tensorWidth * mp.tensorElemBits


  when(io.start) {
    caddr := io.baddr | (maskOffset & (dec.dram_offset << log2Ceil(elemBytes)))
    baddr := io.baddr | (maskOffset & (dec.dram_offset << log2Ceil(elemBytes)))
  }.elsewhen(io.yupdate) {
    when(split) {
      caddr := caddr + xmax_bytes
    }.elsewhen(stride) {
      caddr := baddr + (dec.xstride << log2Ceil(strideFactor))
      baddr := baddr + (dec.xstride << log2Ceil(strideFactor))
    }
  }

  io.stride := stride
  io.split := split
  io.commit := xcnt === len
  io.addr := caddr
  io.len := len
  io.done := xcnt === len &
    xrem === 0.U &
    ycnt === dec.ysize - 1.U
}
