package dandelion.memory

import chisel3._
import chisel3.Module
import chipsalliance.rocketchip.config._
import dandelion.config._
import util._
import dandelion.interfaces._
import utility.UniformPrintfs

/**
  * @param Size    : Size of Register file to be allocated and managed
  * @param NReads  : Number of static reads to be connected. Controls size of arbiter and Demux
  * @param NWrites : Number of static writes to be connected. Controls size of arbiter and Demux
  */

class UnifiedController(ID: Int,
                        Size: Int,
                        NReads: Int,
                        NWrites: Int)
                       (WControl: => WController)
                       (RControl: => RController)
                       (RWArbiter: => ReadWriteArbiter)(implicit val p: Parameters)
  extends Module
    with HasAccelParams
    with UniformPrintfs {

  val io = IO(new Bundle {
    val WriteIn  = Vec(NWrites, Flipped(Decoupled(new WriteReq( ))))
    val WriteOut = Vec(NWrites, Output(new WriteResp( )))
    val ReadIn   = Vec(NReads, Flipped(Decoupled(new ReadReq( ))))
    val ReadOut  = Vec(NReads, Output(new ReadResp( )))

    //orig
    val MemResp = Flipped(Valid(new MemResp))
    val MemReq  = Decoupled(new MemReq)

  })

  require(Size > 0)
  require(isPow2(Size))

  /*====================================
   =            Declarations            =
   ====================================*/
  //  val memReq_R  = RegInit(MemReq.default)
  //val memResp_R = RegInit(MemResp.default)

  // Initialize a vector of register files (as wide as type).
  val WriteController  = if(NWrites > 0) Some(Module(WControl)) else None
  val ReadController   = if(NReads > 0) Some(Module(RControl)) else None
  val ReadWriteArbiter = Module(RWArbiter)

  /*================================================
  =            Wiring up input arbiters            =
  ================================================*/

  // Connect up Write ins with arbiters
  for (i <- 0 until NWrites) {
    WriteController.get.io.WriteIn(i) <> io.WriteIn(i)
    io.WriteOut(i) <> WriteController.get.io.WriteOut(i)
  }

  // Connect up Read ins with arbiters
  for (i <- 0 until NReads) {
    ReadController.get.io.ReadIn(i) <> io.ReadIn(i)
    io.ReadOut(i) <> ReadController.get.io.ReadOut(i)
  }

  // Connect Read/Write Controllers to ReadWrite Arbiter
  if(NReads > 0){
    ReadWriteArbiter.io.ReadMemReq <> ReadController.get.io.MemReq
    ReadController.get.io.MemResp <> ReadWriteArbiter.io.ReadMemResp
  }else{
    ReadWriteArbiter.io.ReadMemReq <> DontCare
    ReadWriteArbiter.io.ReadMemResp <> DontCare
  }

  if(NWrites > 0){
    ReadWriteArbiter.io.WriteMemReq <> WriteController.get.io.MemReq
    WriteController.get.io.MemResp <> ReadWriteArbiter.io.WriteMemResp
  }else{
    WriteController.get.io.MemReq <> DontCare
    WriteController.get.io.MemResp <> DontCare
  }


  ReadWriteArbiter.io.MemReq.ready := io.MemReq.ready
  io.MemReq.bits := ReadWriteArbiter.io.MemReq.bits
  io.MemReq.valid := ReadWriteArbiter.io.MemReq.valid
  ReadWriteArbiter.io.MemResp <> io.MemResp

  //--------------------------

  //------------------------------------------------------------------------------------
  if(memLog){
    when(io.MemReq.fire){
      when(io.MemReq.bits.iswrite){
        printf("[LOG] [MemController] [MemReq]: Addr: %d, Data: %d, IsWrite: ST\n", io.MemReq.bits.addr, io.MemReq.bits.data)
      }.otherwise{
        printf("[LOG] [MemController] [MemReq]: Addr: %d, Data: %d, IsWrite: LD\n", io.MemReq.bits.addr, io.MemReq.bits.data)
      }
    }

    when(io.MemResp.fire()){
      when(io.MemResp.bits.iswrite){
        printf("[LOG] [MemController] [MemResp]: Data: %d, IsWrite: ST\n", io.MemResp.bits.data)
      }.otherwise{
        printf("[LOG] [MemController] [MemResp]: Data: %d, IsWrite: LD\n", io.MemReq.bits.data)
      }
    }
  }


  /// Printf debugging
  override val printfSigil = "Unified: " + ID + " Type " + (typeSize)

  //  verb match {
  //    case "high"  => {printf(p" .addr: $cacheReq_R.addr")}
  //    case "med"   => {printf(p" state: $state")}
  //    case "low"   => {printf(p" state: $state")}
  //  }

  // printf(p"\n : ${ReadController.io.MemReq.fire()} Tag: ${ReadReq.tag} ")
  // printf(p"\n Cache Request ${WriteController.io.MemReq}")
  //  printf(p"Demux out:  ${io.WriteOut(0)}")

}
