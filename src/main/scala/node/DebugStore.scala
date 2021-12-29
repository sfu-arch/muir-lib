package dandelion.node

import chisel3._
import chisel3.util._
import chisel3.util.experimental.BoringUtils
import dandelion.config._
import dandelion.interfaces._
import dandelion.shell.DMEWriteMaster
import utility.Constants._
import utility.UniformPrintfs
import chipsalliance.rocketchip.config._
import chisel3.experimental.{DataMirror, requireIsChiselType}
import chisel3.internal.naming.chiselName
//import tensorKernels.URAM_Queue



/** A hardware module implementing a Queue
  * @param gen The type of data to queue
  * @param entries The max number of entries in the queue
  * @param pipe True if a single entry queue can run at full throughput (like a pipeline). The ''ready'' signals are
  * combinationally coupled.
  * @param flow True if the inputs can be consumed on the same cycle (the inputs "flow" through the queue immediately).
  * The ''valid'' signals are coupled.
  *
  * @example {{{
  * val q = Module(new Queue(UInt(), 16))
  * q.io.enq <> producer.io.out
  * consumer.io.in <> q.io.deq
  * }}}
  */
@chiselName
class QueueFPGA[T <: Data](gen: T,
                       val entries: Int,
                       pipe: Boolean = false,
                       flow: Boolean = false)
                      (implicit compileOptions: chisel3.CompileOptions)
  extends Module() {
  require(entries > -1, "Queue must have non-negative number of entries")
  require(entries != 0, "Use companion object Queue.apply for zero entries")
  val genType = if (compileOptions.declaredTypeMustBeUnbound) {
    requireIsChiselType(gen)
    gen
  } else {
    if (DataMirror.internal.isSynthesizable(gen)) {
      chiselTypeOf(gen)
    } else {
      gen
    }
  }

  val io = IO(new QueueIO(genType, entries))

  val ram = SyncReadMem(entries, genType)
  val enq_ptr = Counter(entries)
  val deq_ptr = Counter(entries)
  val maybe_full = RegInit(false.B)

  val ptr_match = enq_ptr.value === deq_ptr.value
  val empty = ptr_match && !maybe_full
  val full = ptr_match && maybe_full
  val do_enq = WireDefault(io.enq.fire())
  val do_deq = WireDefault(io.deq.fire())

  when (do_enq) {
    ram(enq_ptr.value) := io.enq.bits
    enq_ptr.inc()
  }
  when (do_deq) {
    deq_ptr.inc()
  }
  when (do_enq =/= do_deq) {
    maybe_full := do_enq
  }

  io.deq.valid := !empty
  io.enq.ready := !full
  io.deq.bits := ram(deq_ptr.value)

  if (flow) {
    when (io.enq.valid) { io.deq.valid := true.B }
    when (empty) {
      io.deq.bits := io.enq.bits
      do_deq := false.B
      when (io.deq.ready) { do_enq := false.B }
    }
  }

  if (pipe) {
    when (io.deq.ready) { io.enq.ready := true.B }
  }

  val ptr_diff = enq_ptr.value - deq_ptr.value
  if (isPow2(entries)) {
    io.count := Mux(maybe_full && ptr_match, entries.U, 0.U) | ptr_diff
  } else {
    io.count := Mux(ptr_match,
      Mux(maybe_full,
        entries.asUInt, 0.U),
      Mux(deq_ptr.value > enq_ptr.value,
        entries.asUInt + ptr_diff, ptr_diff))
  }
}


/**
 * @brief Store Node. Implements store operations
 * @details [long description]
 */
class DebugBufferNode(
                       NumOuts: Int = 1,
                       Typ: UInt = MT_W, ID: Int, RouteID: Int, Bore_ID: Int, node_cnt: UInt)
                     (implicit val p: Parameters,
                      name: sourcecode.Name,
                      file: sourcecode.File)
  extends Module with HasAccelParams with UniformPrintfs {


  val io = IO(new Bundle {
    //  // Memory request
    val memReq = Decoupled(new WriteReq())
    //  // Memory response.
    val memResp = Input(Flipped(new WriteResp()))

    val Enable = Input(Bool())
  })
  val node_name = name.value
  val module_name = file.value.split("/").tail.last.split("\\.").head.capitalize
  override val printfSigil = "[" + module_name + "] " + node_name + ": " + ID + " "
  val (cycleCount, _) = Counter(true.B, 32 * 1024)
  val inData = new DataBundle
  val GepAddr = new DataBundle
  //------------------------------
  val dbg_counter = Counter(1024)

  //Is the Data The Wires of the Boring Connection Will put data in.
  val LogData = Module(new QueueFPGA(UInt((xlen).W), 20))


  val st_node = Module(new UnTypDebugStore(ID = 0, RouteID = RouteID))

  LogData.io.enq.bits := 0.U
  LogData.io.enq.valid := false.B
  LogData.io.deq.ready := false.B

  val queue_data = WireInit(0.U((xlen).W))
  val queue_valid = WireInit(false.B)
  val queue_ready = WireInit(false.B)

  BoringUtils.addSink(queue_data, s"data${Bore_ID}")
  BoringUtils.addSink(queue_valid, s"valid${Bore_ID}")
  BoringUtils.addSource(queue_ready, s"Buffer_ready${Bore_ID}")

  LogData.io.enq.bits := queue_data
  LogData.io.enq.valid := queue_valid && io.Enable
  queue_ready := LogData.io.enq.ready


  st_node.io.InData.bits := DataBundle(LogData.io.deq.bits)
  st_node.io.InData.valid := LogData.io.deq.valid
  LogData.io.deq.ready := st_node.io.InData.ready

  io.memReq <> st_node.io.memReq
  st_node.io.memResp <> io.memResp


  var (addr_cnt, wrap) = Counter(st_node.io.InData.fire, 4096)

  //  addr_cnt = node_cnt * 20.U

  val addr_offset = node_cnt * 20.U
  val addr = addr_offset + (addr_cnt << 2.U).asUInt()


  st_node.io.GepAddr.bits := DataBundle(addr)
  st_node.io.GepAddr.valid := true.B


}


/**
 * This is a test for debug store node
 *
 * @brief Store Node. Implements store operations
 * @details [long description]
 */
class UnTypDebugStore(
                       NumOuts: Int = 1,
                       Typ: UInt = MT_W, ID: Int, RouteID: Int)
                     (implicit val p: Parameters,
                      name: sourcecode.Name,
                      file: sourcecode.File)
  extends Module with HasAccelParams with UniformPrintfs {

  // Set up StoreIO
  val io = IO(new Bundle {

    //Input address
    val GepAddr = Flipped(Decoupled(new DataBundle()))
    //Input data
    val InData = Flipped(Decoupled(new DataBundle()))

    // Memory request
    val memReq = Decoupled(new WriteReq())
    // Memory response.
    val memResp = Flipped(Valid(new WriteResp()))
  })
  // Printf debugging
  val node_name = name.value
  val module_name = file.value.split("/").tail.last.split("\\.").head.capitalize
  override val printfSigil = "[" + module_name + "] " + node_name + ": " + ID + " "
  val (cycleCount, _) = Counter(true.B, 32 * 1024)

  /*=============================================
  =            Register declarations            =
  =============================================*/

  // OP Inputs
  val addr_R = RegInit(DataBundle.default)
  val addr_valid_R = RegInit(false.B)

  val data_R = RegInit(DataBundle.default)
  val data_valid_R = RegInit(false.B)

  // State machine
  val s_idle :: s_RECEIVING :: s_Done :: Nil = Enum(3)
  val state = RegInit(s_idle)


  // GepAddr
  io.GepAddr.ready := ~addr_valid_R
  when(io.GepAddr.fire()) {
    addr_R := io.GepAddr.bits
    addr_valid_R := true.B
  }

  // InData
  io.InData.ready := ~data_valid_R
  when(io.InData.fire()) {
    data_R := io.InData.bits
    data_valid_R := true.B
  }

  // Default values for memory ports
  io.memReq.bits.address := addr_R.data
  io.memReq.bits.data := data_R.data
  io.memReq.bits.Typ := Typ
  io.memReq.bits.RouteID := RouteID.U
  io.memReq.bits.taskID := data_R.taskID | addr_R.taskID
  io.memReq.bits.mask := 15.U
  io.memReq.valid := false.B

  /*=============================================
  =            ACTIONS (possibly dangerous)     =
  =============================================*/
  val latch_data = addr_valid_R && data_valid_R

  switch(state) {
    is(s_idle) {
      when(latch_data) {
        io.memReq.valid := true.B
        when(io.memReq.fire) {
          state := s_RECEIVING
        }
      }
    }
    is(s_RECEIVING) {
      when(io.memResp.valid) {
        state := s_Done
      }
    }
    is(s_Done) {
      // Clear all the valid states.
      addr_R := DataBundle.default
      addr_valid_R := false.B
      // Reset data.
      data_R := DataBundle.default
      data_valid_R := false.B

      state := s_idle
      if (log) {
        printf("[LOG] " + "[" + module_name + "] [TID->%d] [STORE]" + node_name + ": Fired @ %d Mem[%d] = %d\n",
          data_R.taskID, cycleCount, addr_R.data, data_R.data)
      }
    }
  }
}


/**
 * DebugVMEBufferNode
 * The debug buffer nodes that is connected to design's node using boreID
 * and use DME interface to flush data to off-chip memory
 *
 * @param BufferLen Length of buffer memory
 * @param ID
 * @param Bore_ID
 * @param p
 * @param name
 * @param file
 */
class DebugVMEBufferNode(BufferLen: Int = 2, ID: Int, Bore_ID: Int)
                        (implicit val p: Parameters,
                         name: sourcecode.Name,
                         file: sourcecode.File)
  extends MultiIOModule with HasAccelParams with HasAccelShellParams with UniformPrintfs {


  val io = IO(new Bundle {
    val addrDebug = Input(UInt(memParams.addrBits.W))
    val vmeOut = new DMEWriteMaster
    val Enable = Input(Bool())
  })

  val buf = IO(new Bundle {
    val port = Flipped(Decoupled(UInt(xlen.W)))
  })

  val node_name = name.value
  val module_name = file.value.split("/").tail.last.split("\\.").head.capitalize
  override val printfSigil = "[" + module_name + "] " + node_name + ": " + ID + " "
  val (cycleCount, _) = Counter(true.B, 32 * 1024)
  val inData = new DataBundle
  val GepAddr = new DataBundle
  //------------------------------
  val dbg_counter = Counter(1024)

//  val N = RegInit(32.U)
  val addr_debug_reg = RegInit(0.U(xlen.W))
  val sIdel :: sReq :: sBusy :: Nil = Enum(3)
  val wState = RegInit(sIdel)

  //Is the Data The Wires of the Boring Connection Will put data in.
  val LogData = Module(new Queue(UInt((xlen).W), BufferLen))
  //val LogData = Module(new URAM_Queue(UInt((xlen).W), BufferLen))

  val queue_count = RegInit(0.U(log2Ceil(BufferLen).W))
  when(LogData.io.enq.fire) {
    queue_count := queue_count + 1.U
  }

  val writeFinished = Wire(Bool())
  writeFinished := false.B
  BoringUtils.addSink(writeFinished, s"RunFinished${ID}")

  LogData.io.enq.bits := buf.port.bits
  LogData.io.enq.valid := buf.port.valid && io.Enable && (wState === sIdel)
  buf.port.ready := LogData.io.enq.ready && (wState === sIdel)

  io.vmeOut.cmd.bits.addr := io.addrDebug + addr_debug_reg
  io.vmeOut.cmd.bits.len := queue_count - 1.U
  io.vmeOut.cmd.valid := (wState === sReq)


  switch(wState) {
    is(sIdel) {
      when((writeFinished && LogData.io.deq.valid) || (LogData.io.count.asUInt() === BufferLen.U / 2.U)) {
        wState := sReq
      }
    }
    is(sReq) {
      when(io.vmeOut.cmd.fire()) {
        wState := sBusy
      }
    }
    is(sBusy) {
      when(io.vmeOut.ack) {
        wState := sIdel
        queue_count := 0.U
        addr_debug_reg := addr_debug_reg + (queue_count * (xlen >> 3).asUInt())
      }
    }
  }


  io.vmeOut.data.bits := 0.U
  io.vmeOut.data.valid := false.B
  LogData.io.deq.ready := false.B

  when(wState === sBusy) {
    io.vmeOut.data.bits := LogData.io.deq.bits
    io.vmeOut.data.valid := LogData.io.deq.valid
    LogData.io.deq.ready := io.vmeOut.data.ready
  }

}


