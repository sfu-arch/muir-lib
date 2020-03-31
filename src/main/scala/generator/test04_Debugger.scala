package dandelion.generator

import chisel3._
import chipsalliance.rocketchip.config._
import dandelion.accel.DandelionAccelDebugModule
import dandelion.node._


class test04DebugVMEDF(val numDebug: Int)(implicit p: Parameters) extends DandelionAccelDebugModule(numDebug)(p)  {

  /**
    * Debug node for BoreID = 4
    */
  //ID from 1, RoutID from 0, Bore_ID same as the ID of the node_to_be_logged and node_cnt for memory spaces from 0
  val buf_0 = Module(new DebugVMEBufferNode(ID = 1, Bore_ID = 4))
  buf_0.io.Enable := io.enableNode(0)
  buf_0.io.addrDebug := io.addrDebug(0)

  io.vmeOut(0) <> buf_0.io.vmeOut


}
