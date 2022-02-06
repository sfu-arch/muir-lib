package dandelion.accel

import chisel3._
import chipsalliance.rocketchip.config.Parameters
import chisel3.util.{Decoupled, Valid}
import dandelion.config.{AccelBundle, HasAccelParams, HasAccelShellParams}
import dandelion.interfaces.{Call, CallDCR, MemReq, MemResp}
import dandelion.shell.{DMEReadMaster, DMEWriteMaster}


/**
  * Global definition for dandelion accelerators
  *
  * @param ArgsIn
  * @param Returns
  * @param p
  * @tparam T
  */
class DandelionAccelIO[T <: Data](val ArgsIn: Seq[Int],
                                  val Returns: Seq[Int])(implicit p: Parameters)
  extends AccelBundle()(p) {
  val in = Flipped(Decoupled(new Call(ArgsIn)))
  val MemResp = Flipped(Valid(new MemResp))
  val MemReq = Decoupled(new MemReq)
  val out = Decoupled(new Call(Returns))

}


abstract class DandelionAccelModule(val ArgsIn: Seq[Int],
                                    val Returns: Seq[Int])(implicit val p: Parameters) extends Module
  with HasAccelParams
  with HasAccelShellParams {
  lazy val io = IO(new DandelionAccelIO(ArgsIn, Returns))
}


/**
  * Global definition for dandelion accelerators
  *
  * @param PtrsIn
  * @param ValsIn
  * @param RetsOut
  * @param p
  * @tparam T
  */
class DandelionAccelDCRIO[T <: Data](val PtrsIn: Seq[Int],
                                     val ValsIn: Seq[Int],
                                     val RetsOut: Seq[Int])
                                    (implicit p: Parameters)
  extends AccelBundle()(p) with HasAccelShellParams {
  val in = Flipped(Decoupled(new CallDCR(PtrsIn, ValsIn)))
  val MemResp = Flipped(Valid(new MemResp))
  val MemReq = Decoupled(new MemReq)
  val out = Decoupled(new Call(RetsOut))

}


abstract class DandelionAccelDCRModule(val PtrsIn: Seq[Int],
                                       val ValsIn: Seq[Int],
                                       val RetsOut: Seq[Int])
                                      (implicit val p: Parameters) extends MultiIOModule with HasAccelParams {
  lazy val io = IO(new DandelionAccelDCRIO(PtrsIn, ValsIn, RetsOut))
}

/**
  * Global definition for dandelion accelerators
  *
  * @param p implicit accel params
  */
class DandelionAccelDebugIO(val numDebugNode: Int)(implicit p: Parameters)
  extends AccelBundle()(p) with HasAccelShellParams {
  val addrDebug = Vec(numDebugNode, Input(UInt(memParams.addrBits.W)))
  val enableNode = Vec(numDebugNode, Input(Bool()))
  val vmeOut = Vec(numDebugNode, new DMEWriteMaster)

}


abstract class DandelionAccelDebugModule(val numDebugNode: Int, val boreIDList: Seq[Int])(implicit val p: Parameters) extends Module
  with HasAccelParams
  with HasAccelShellParams {
  lazy val io = IO(new DandelionAccelDebugIO(numDebugNode))

  require(numDebugNode == boreIDList.length,
    s"You the size boreIDs should be equalt to number debug nodes\n")
}


/**
  * Global definition for dandelion accelerators
  *
  * @param p implicit accel params
  */
class DandelionAccelReadDebugIO(val numDebugNode: Int)(implicit p: Parameters)
  extends AccelBundle()(p) with HasAccelShellParams {
  val addrDebug = Vec(numDebugNode, Input(UInt(memParams.addrBits.W)))
  val vmeIn = Vec(numDebugNode, new DMEReadMaster)

}


abstract class DandelionAccelReadDebugModule(val numDebugNode: Int, val boreIDList: Seq[Int])(implicit val p: Parameters) extends Module
  with HasAccelParams
  with HasAccelShellParams {
  lazy val io = IO(new DandelionAccelReadDebugIO(numDebugNode))

  require(numDebugNode == boreIDList.length,
    s"You the size boreIDs should be equalt to number debug nodes\n")
}
