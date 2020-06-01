package dandelion.fpu

import chisel3._
import chisel3.Module
import chipsalliance.rocketchip.config._
import dandelion.config._
import dandelion.interfaces._
import dandelion.arbiters._
import util._
import utility.UniformPrintfs
import muxes._
import hardfloat._
import chipsalliance.rocketchip.config._
import dandelion.config._

class SharedFPUIO(NumOps: Int, argTypes: Seq[Int])
                 (implicit p: Parameters) extends AccelBundle()(p) {
  val InData = Vec(NumOps, Flipped(Decoupled(new FUReq)))

  val OutData = Vec(NumOps, Valid(new FUResp))

  override def cloneType = new SharedFPUIO(NumOps, argTypes).asInstanceOf[this.type]
}

class SharedFPU(NumOps: Int, PipeDepth: Int)(t: FType)
               (implicit val p: Parameters,
                name: sourcecode.Name,
                file: sourcecode.File)
  extends Module with HasAccelParams with UniformPrintfs {
  override lazy val io = IO(new SharedFPUIO(NumOps, argTypes = List(xlen, xlen, 1)))

  // Printf debugging
  val node_name = name.value
  val module_name = file.value.split("/").tail.last.split("\\.").head.capitalize
  val (cycleCount, _) = Counter(true.B, 32 * 1024)

  // Arguments for function unit
  // The function unit
  val ds = Module(new DivSqrtRecFN_small(t.expWidth, t.sigWidth, 0))
  //  Metadata queue associated with function unit
  val RouteQ = Module(new Queue(UInt(tlen.W), PipeDepth))


  /**
    * Instantiating Arbiter module and connecting inputs to the output
    *
    * @note we fix the base size to 8
    */
  val in_arbiter = Module(new RRArbiter(new FUReq, NumOps))

  for (i <- 0 until NumOps) {
    in_arbiter.io.in(i) <> io.InData(i)
  }

  in_arbiter.io.out.ready := ds.io.inReady
  ds.io.inValid := in_arbiter.io.out.valid
  ds.io.sqrtOp := in_arbiter.io.out.bits.sqrt
  ds.io.a := t.recode(in_arbiter.io.out.bits.data_a)
  ds.io.b := t.recode(in_arbiter.io.out.bits.data_b)
  ds.io.roundingMode := "b110".U(3.W)
  ds.io.detectTininess := 0.U(1.W)

  /**
    * Register choosen arbiter select
    */
  when(in_arbiter.io.out.fire){
    RouteQ.io.enq.bits := in_arbiter.io.chosen
    RouteQ.io.enq.valid := true.B
  }.otherwise{
    RouteQ.io.enq.bits := 0.U
    RouteQ.io.enq.valid := false.B
  }

  RouteQ.io.deq.ready := ds.io.outValid_div || ds.io.outValid_sqrt
  when(ds.io.outValid_div || ds.io.outValid_sqrt){
    io.OutData(RouteQ.io.deq.bits).bits.data := fNFromRecFN(t.expWidth, t.sigWidth, ds.io.out)
    io.OutData(RouteQ.io.deq.bits).valid := true.B
  }.otherwise{
    io.OutData.foreach(_.bits.data := 0.U)
    io.OutData.foreach(_.valid := false.B)
  }

}
