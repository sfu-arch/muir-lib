
package interfaces

import chipsalliance.rocketchip.config.Parameters
import chisel3._
import chisel3.util._
import dandelion.config.HasAccelShellParams
import dandelion.shell.DMEReadMaster
import interfaces.memISA._


/** StreamLoad.
  *
  */
class StreamLoad(bufSize: Int, debug: Boolean = false)(
  implicit val p: Parameters)
  extends Module with HasAccelShellParams {
  val mp = memParams
  val io = IO(new Bundle {
    val start = Input(Bool())
    val done = Output(Bool())
    val inst = Input(UInt(INST_BITS.W))
    val baddr = Input(UInt(mp.addrBits.W))
    val vme_rd = new DMEReadMaster
    val out = Decoupled(UInt(mp.tensorElemBits.W))
  })

  require(bufSize >= math.pow(2, mp.lenBits) * mp.tensorWidth, "bufSize should be greater than size of each stream chunk")


  //  val sizeFactor = 1 //width / width
  //  val strideFactor = 2//width //tp.tensorLength * tp.tensorWidth
  val sizeFactor = mp.tensorLength * mp.numMemBlock
  val strideFactor = mp.tensorLength * mp.tensorWidth

  val dec = io.inst.asTypeOf(new MemDecode)
  val dataCtrl = Module(
    new MemDataCtrl(sizeFactor, strideFactor))
  val dataCtrlDone = RegInit(false.B)

  val tag = Reg(UInt(log2Ceil(mp.numMemBlock).W))
  val set = Reg(UInt(log2Ceil(mp.tensorLength).W))


  val queue = Module(new MIMOQueue(UInt(mp.tensorElemBits.W), entries = bufSize, mp.tensorWidth, NumOuts = 1))
  queue.io.clear := false.B

  val sIdle :: sReadCmd :: sReadData :: Nil =
    Enum(3)
  val state = RegInit(sIdle)

  // control
  switch(state) {
    is(sIdle) {
      when(io.start) {
        state := sReadCmd
      }
    }
    is(sReadCmd) {
      when(io.vme_rd.cmd.fire()) {
        state := sReadData
      }
    }
    is(sReadData) {
      when(io.vme_rd.data.valid) {
        when(dataCtrl.io.done) {
          state := sIdle
        }.elsewhen(dataCtrl.io.stride || dataCtrl.io.split) {
          state := sReadCmd
        }
      }
    }
  }

  // data controller
  dataCtrl.io.start := state === sIdle & io.start
  dataCtrl.io.inst := io.inst
  dataCtrl.io.baddr := io.baddr
  dataCtrl.io.xinit := io.vme_rd.cmd.fire()
  dataCtrl.io.xupdate := io.vme_rd.data.fire()
  dataCtrl.io.yupdate := io.vme_rd.data.fire()

  when(state === sIdle) {
    dataCtrlDone := false.B
  }.elsewhen(io.vme_rd.data.fire() && dataCtrl.io.done) {
    dataCtrlDone := true.B
  }

  val reqSize = Wire(UInt(mp.tensorElemBits.W))
  reqSize := (dataCtrl.io.len * mp.tensorWidth.U) + mp.tensorWidth.U
  val check = Wire(Bool ())
  check := false.B
  when(reqSize <= (bufSize.U - queue.io.count)) {
    check := true.B
  }

  // read-from-dram
  io.vme_rd.cmd.valid := (state === sReadCmd) && check
  io.vme_rd.cmd.bits.addr := dataCtrl.io.addr
  io.vme_rd.cmd.bits.len := dataCtrl.io.len

  io.vme_rd.data.ready := queue.io.enq.ready && state === sReadData

  // write-to-sram
  queue.io.enq.bits := io.vme_rd.data.bits.asTypeOf(queue.io.enq.bits)
  queue.io.enq.valid := io.vme_rd.data.valid

  io.out.bits := queue.io.deq.bits.asUInt()
  io.out.valid := queue.io.deq.valid
  queue.io.deq.ready := io.out.ready

  // done
  val done_no_pad = io.vme_rd.data.fire() & dataCtrl.io.done & dec.xpad_1 === 0.U & dec.ypad_1 === 0.U
  io.done := done_no_pad
}



