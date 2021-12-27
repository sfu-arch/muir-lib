package dandelion.node

import chisel3._
import chisel3.util._
import chisel3.util.experimental.BoringUtils
import dandelion.config._
import dandelion.interfaces._
import dandelion.shell.{DMEReadMaster, DMEWriteMaster}
import utility.Constants._
import utility.UniformPrintfs
import chipsalliance.rocketchip.config._
import chisel3.experimental.{DataMirror, requireIsChiselType}
import chisel3.internal.naming.chiselName


/**
 * DebugVMEBufferNode
 * The debug buffer nodes that is connected to design's node using boreID
 * and use DME interface to flush data to off-chip memory
 *
 * @param BufferLen Length of buffer memory
 * @param ID
 * @param p
 * @param name
 * @param file
 */
class DebugVMEGuardLoadBufferNode(BufferLen: Int = 2, ID: Int)
                        (implicit val p: Parameters,
                         name: sourcecode.Name,
                         file: sourcecode.File)
  extends MultiIOModule with HasAccelParams with HasAccelShellParams with UniformPrintfs {


  val io = IO(new Bundle {
    val addrDebug = Input(UInt(memParams.addrBits.W))
    val start = Input(Bool())
    val vmeOut = new DMEReadMaster
    val done = Output(Bool())
    val out = Decoupled(UInt(xlen.W))
  })

  val node_name = name.value
  val module_name = file.value.split("/").tail.last.split("\\.").head.capitalize
  override val printfSigil = "[" + module_name + "] " + node_name + ": " + ID + " "
  val (cycleCount, _) = Counter(true.B, 32 * 1024)
  val inData = new DataBundle
  val dbg_counter = Counter(1024)

  val addr_debug_reg = RegInit(0.U(xlen.W))
  val sIdel :: sReq :: sBusy :: Nil = Enum(3)
  val rState = RegInit(sIdel)

  //Is the Data The Wires of the Boring Connection Will put data in.
  val LogData = Module(new Queue(UInt((xlen).W), BufferLen + 5))

  val queue_count = RegInit(0.U(BufferLen.W))
  

  val in_log_value = WireInit(0.U)
  val in_log_value_valid = WireInit(0.U)
  val in_log_value_ready = WireInit (0.U)
  val replace_data = WireInit(0.U)
  BoringUtils.addSink(in_log_value, s"in_log_data${ID}")
  BoringUtils.addSink(in_log_value_valid, s"in_log_Buffer_valid${ID}")
  
  BoringUtils.addSource(in_log_value_ready, s"in_log_Buffer_ready${ID}")
  BoringUtils.addSource(replace_data,s"replace_data${ID}")
     //Output log data
  

  
  when(LogData.io.enq.fire) {
    queue_count := queue_count + 1.U
  }

  LogData.io.enq <> io.vmeOut.data

  io.vmeOut.cmd.bits.addr := io.addrDebug + addr_debug_reg
  io.vmeOut.cmd.bits.len := BufferLen.U
  io.vmeOut.cmd.valid := (rState === sReq)


  switch(rState) {
    is(sIdel) {
      when(LogData.io.count === 0.U && io.start){
        rState := sReq
      }
    }
    is(sReq) {
      when(io.vmeOut.cmd.fire()) {
        rState := sBusy
      }
    }
    is(sBusy) {
      when(queue_count === BufferLen.U) {
        rState := sIdel
        addr_debug_reg := addr_debug_reg + (queue_count * (xlen >> 3).asUInt())
      }
    }
  }

  io.done := false.B

  //io.out.bits := LogData.io.deq.bits
  //io.out.valid := LogData.io.deq.valid
  //LogData.io.deq.ready := io.out.ready
  when (in_log_value =/= LogData.io.deq.bits){
    replace_data := LogData.io.deq.bits
    in_log_value_ready := true.B
  }
}


