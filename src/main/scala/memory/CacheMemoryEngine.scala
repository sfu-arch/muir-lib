package dandelion.memory

import chisel3._
import chisel3.util._
import chipsalliance.rocketchip.config._
import dandelion.config._
import dandelion.interfaces._
import dandelion.util._


/**
  * CMEMaster
  * Memory interface between Load/Store, Master nodes, with memory modules such as cache
  * in the accelerator
  *
  * @param p
  */
class CMEMaster(implicit val p: Parameters) extends Bundle with HasAccelShellParams {
  val MemReq = Decoupled(new MemReq)
  val MemResp = Flipped(Valid(new MemResp))

  override def cloneType =
    new CMEMaster().asInstanceOf[this.type]
}

/**
  * CMEClient
  * Memory interface between Memory modules to Load/Store nodes of accelerator
  *
  * @param p
  */
class CMEClient(implicit val p: Parameters) extends Bundle with HasAccelShellParams {
  val MemReq = Flipped(Decoupled(new MemReq))
  val MemResp = Valid(new MemResp)

  override def cloneType =
    new CMEClient().asInstanceOf[this.type]
}

/**
  * CMEClientVector
  * Vector interface
  *
  * @param NumOps
  * @param p
  */
class CMEClientVector(NumOps: Int)(implicit val p: Parameters) extends Bundle with HasAccelShellParams {
  val mem = Vec(NumOps, new CMEClient)

  override def cloneType =
    new CMEClientVector(NumOps).asInstanceOf[this.type]
}


/**
  * @TODO: Currently we have mixed memory command with with memory data
  *        the better way to implement and pipeline the design is to separate
  *        memory commands from memory data and make them independent
  *        so we can also design a simple MSHR and have multiple
  *        memory request on the fly. At this moment for the sake of simplification
  *        we have serialize all the memory accesses.
  *        By just breaking down the memory accesses into two nodes
  *        or make a centeralized memory unit with separate channel for read and write
  *        we can have better parallelization.
  * @param ID
  * @param NumRead
  * @param NumWrite
  * @param p
  */
class CacheMemoryEngine(ID: Int, NumRead: Int, NumWrite: Int)(implicit val p: Parameters) extends Module with HasAccelParams with HasAccelShellParams {

  val io = IO(new Bundle {
    val rd = new CMEClientVector(NumRead)
    val wr = new CMEClientVector(NumWrite)
    val cache = new CMEMaster
  })

  val NumOps = NumRead + NumWrite
  val in_arb = Module(new Arbiter(new MemReq, NumOps))
  val in_arb_chosen = RegEnable(in_arb.io.chosen, in_arb.io.out.fire())

  for (i <- 0 until NumRead) {
    in_arb.io.in(i) <> io.rd.mem(i).MemReq
  }

  for (i <- 0 until NumWrite) {
    in_arb.io.in(i + NumRead) <> io.wr.mem(i).MemReq
  }

  val sMemIdle :: sMemAddr :: sMemData :: Nil = Enum(3)
  val mstate = RegInit(sMemIdle)

  switch(mstate) {
    is(sMemIdle) {
      when(in_arb.io.out.valid) {
        mstate := sMemAddr
      }
    }
    is(sMemAddr) {
      when(io.cache.MemReq.ready) {
        mstate := sMemData
      }
    }
    is(sMemData) {
      when(io.cache.MemResp.valid && in_arb_chosen === io.cache.MemResp.bits.tag) {
        mstate := sMemIdle
      }
    }
  }

  for (i <- 0 until NumRead) {
    io.rd.mem(i).MemResp.valid := (in_arb_chosen === i.U) &&
      (in_arb_chosen === io.cache.MemResp.bits.tag) &&
      io.cache.MemResp.valid &&
      (mstate === sMemData)
    io.rd.mem(i).MemResp.bits <> io.cache.MemResp.bits
  }

  for (i <- NumRead until NumRead + NumWrite) {
    io.wr.mem(i - NumRead).MemResp.valid := (in_arb_chosen === i.U) &&
      (in_arb_chosen === io.cache.MemResp.bits.tag) &&
      io.cache.MemResp.valid &&
      (mstate === sMemData)
    io.wr.mem(i - NumRead).MemResp.bits <> io.cache.MemResp.bits
  }

  val in_data_reg = RegEnable(init = MemReq.default, enable = in_arb.io.out.fire, next = in_arb.io.out.bits)

  in_arb.io.out.ready := mstate === sMemIdle
  io.cache.MemReq.valid := mstate === sMemAddr
  io.cache.MemReq.bits := in_data_reg


}