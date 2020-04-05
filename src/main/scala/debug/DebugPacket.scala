package dandelion.node

import chisel3._
import chisel3.util._
import chipsalliance.rocketchip.config._
import dandelion.config._
import dandelion.util.DandelionGenericParameterizedBundle


abstract class DebugBase(params: DebugParams) extends DandelionGenericParameterizedBundle(params)

class DebugPacket(params: DebugParams) extends DebugBase(params) {
  val gFlag = UInt(params.gLen.W)
  val bID = UInt(params.idLen.W)
  val debugCode = UInt(params.codeLen.W)
  val iterationID = UInt(params.iterLen.W)
  val data = UInt(params.dataLen.W)

  def packet(): UInt = {
    val packet = WireInit(0.U(params.packetLen.W))
    packet := Cat(gFlag, bID, debugCode, iterationID, data)
    packet
  }

}

object DebugPacket{
  def apply(gflag: UInt, id :UInt, code :UInt, iteration: UInt, data: UInt)
           (params : DebugParams): DebugPacket =
  {
    val packet = Wire(new DebugPacket(params))
    packet.gFlag := gflag
    packet.bID := id
    packet.debugCode := code
    packet.iterationID := iteration
    packet.data := data
    packet
  }
}

