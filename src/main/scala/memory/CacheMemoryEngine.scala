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
 * @param NumOps
 * @param p
 */
class CMEClientVector(NumOps: Int)(implicit val p: Parameters) extends Bundle with HasAccelShellParams {
  val mem = Vec(NumOps, new CMEClient)
  override def cloneType =
    new CMEClientVector(NumOps).asInstanceOf[this.type]
}


class CacheMemoryEngine(ID: Int, NumOps: Int)(implicit val p: Parameters) extends Module with HasAccelParams with HasAccelShellParams {

  val io = IO(new Bundle {
    val accel = new CMEClientVector(NumOps)
    val cache = new CMEMaster
  })

  val in_arb = Module(new Arbiter(new MemReq, NumOps))
  val in_arb_chosen = RegEnable(in_arb.io.chosen, in_arb.io.out.fire())

  for (i <- 0 until NumOps) {
    in_arb.io.in(i) <> io.accel.mem(i).MemReq
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
        //Make sure the statice routeID is the same as arbiter index
        //The compiler in mac doesn't support time_stamp() function
        //at this moment
        val osName: String = System.getProperty("os.name").toLowerCase
        val isMacOs: Boolean = osName.startsWith("mac os x")
        if (!isMacOs) {
          assert(io.cache.MemReq.bits.tag === in_arb_chosen)
        }
      }
    }
    is(sMemData) {
      when(io.cache.MemResp.valid && in_arb_chosen === io.cache.MemResp.bits.tag) {
        mstate := sMemIdle
      }
    }
  }

  for (i <- 0 until NumOps) {
    io.accel.mem(i).MemResp.valid := (in_arb_chosen === i.U) && (in_arb_chosen === io.cache.MemResp.bits.tag) && io.cache.MemResp.valid
    io.accel.mem(i).MemResp.bits <> io.cache.MemResp.bits
  }

  val in_data_reg = RegEnable(init = MemReq.default, enable = in_arb.io.out.fire, next = in_arb.io.out.bits)

  in_arb.io.out.ready := mstate === sMemIdle
  io.cache.MemReq.valid := mstate === sMemAddr
  io.cache.MemReq.bits := in_data_reg


}