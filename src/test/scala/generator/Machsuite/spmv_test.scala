package dandelion.generator.machsuite

import java.io.{File, PrintWriter}

import dandelion.accel._
import chisel3.iotesters._
import chisel3.util._
import chisel3.{Module, _}
import chipsalliance.rocketchip.config._
import dandelion.config._
import dandelion.interfaces._
import dandelion.memory._
import dandelion.memory.cache.{HasCacheAccelParams, ReferenceCache}
import org.scalatest.{FlatSpec, Matchers}


class ellpackMainIO(implicit val p: Parameters) extends Module
  with HasAccelParams with HasAccelShellParams with HasCacheAccelParams{
  val io = IO(new Bundle {
    val in = Flipped(Decoupled(new Call(List(32, 32, 32, 32))))
    val req = Flipped(Decoupled(new MemReq))
    val resp = Output(Valid(new MemResp))
    val out = Decoupled(new Call(List()))
  })

  def cloneType = new ellpackMainIO().asInstanceOf[this.type]
}

class ellpackMain(implicit p: Parameters) extends ellpackMainIO {

  val cache = Module(new ReferenceCache()) // Simple Nasti Cache
  val memModel = Module(new NastiInitMemSlave()()) // Model of DRAM to connect to Cache
//  val memModel = Module(new NastiMemSlave) // Model of DRAM to connect to Cache


  // Connect the wrapper I/O to the memory model initialization interface so the
  // test bench can write contents at start.
  memModel.io.nasti <> cache.io.mem
  memModel.io.init.bits.addr := 0.U
  memModel.io.init.bits.data := 0.U
  memModel.io.init.valid := false.B
    cache.io.cpu.abort := false.B
  cache.io.cpu.flush := false.B

  // Wire up the cache and modules under test.
  val ellpack = Module(new ellpackDF())

  //Put an arbiter infront of cache
  val CacheArbiter = Module(new MemArbiter(2))

  // Connect input signals to cache
  CacheArbiter.io.cpu.MemReq(0) <> ellpack.io.MemReq
  ellpack.io.MemResp <> CacheArbiter.io.cpu.MemResp(0)

  //Connect main module to cache arbiter
  CacheArbiter.io.cpu.MemReq(1) <> io.req
  io.resp <> CacheArbiter.io.cpu.MemResp(1)

  //Connect cache to the arbiter
  cache.io.cpu.req <> CacheArbiter.io.cache.MemReq
  CacheArbiter.io.cache.MemResp <> cache.io.cpu.resp

  //Connect in/out ports
  ellpack.io.in <> io.in
  io.out <> ellpack.io.out

}


class ellpackTest01[T <: ellpackMainIO](c: T) extends PeekPokeTester(c) {


  def MemRead(addr: Int): BigInt = {
    while (peek(c.io.req.ready) == 0) {
      step(1)
    }
    poke(c.io.req.valid, 1)
    poke(c.io.req.bits.addr, addr)
    poke(c.io.req.bits.iswrite, 0)
    poke(c.io.req.bits.tag, 0)
    poke(c.io.req.bits.mask, 0)
    poke(c.io.req.bits.mask, -1)
    step(1)
    while (peek(c.io.resp.valid) == 0) {
      step(1)
    }
    val result = peek(c.io.resp.bits.data)
    result
  }

  def MemWrite(addr: Int, data: Int): BigInt = {
    while (peek(c.io.req.ready) == 0) {
      step(1)
    }
    poke(c.io.req.valid, 1)
    poke(c.io.req.bits.addr, addr)
    poke(c.io.req.bits.data, data)
    poke(c.io.req.bits.iswrite, 1)
    poke(c.io.req.bits.tag, 0)
    poke(c.io.req.bits.mask, 0)
    poke(c.io.req.bits.mask, -1)
    step(1)
    poke(c.io.req.valid, 0)
    1
  }

  val addr_range = 0x0

  step(1)

  // Initializing the signals
  poke(c.io.in.bits.enable.control, false.B)
  poke(c.io.in.bits.enable.taskID, 0)

  poke(c.io.in.valid, false.B)
  poke(c.io.in.bits.data("field0").data, 0)
  poke(c.io.in.bits.data("field0").taskID, 0)
  poke(c.io.in.bits.data("field0").predicate, false)
  poke(c.io.in.bits.data("field1").data, 0)
  poke(c.io.in.bits.data("field1").taskID, 0)
  poke(c.io.in.bits.data("field1").predicate, false)
  poke(c.io.in.bits.data("field2").data, 0)
  poke(c.io.in.bits.data("field2").taskID, 0)
  poke(c.io.in.bits.data("field2").predicate, false)
  poke(c.io.in.bits.data("field3").data, 0)
  poke(c.io.in.bits.data("field3").taskID, 0)
  poke(c.io.in.bits.data("field3").predicate, false)
  poke(c.io.out.ready, false)
  step(1)
  poke(c.io.in.bits.enable.control, true.B)
  poke(c.io.in.valid, true.B)
  poke(c.io.in.bits.data("field0").data, 0) //
  poke(c.io.in.bits.data("field0").predicate, true.B)
  poke(c.io.in.bits.data("field1").data, 0x9a60) //
  poke(c.io.in.bits.data("field1").predicate, true.B)
  poke(c.io.in.bits.data("field2").data, 0xe790) //
  poke(c.io.in.bits.data("field2").predicate, true.B)
  poke(c.io.in.bits.data("field3").data, 0xf700) //
  poke(c.io.in.bits.data("field3").predicate, true.B)
  poke(c.io.out.ready, true.B)
  step(1)
  poke(c.io.in.bits.enable.control, false.B)
  poke(c.io.in.valid, false.B)
  poke(c.io.in.bits.data("field0").data, 0)
  poke(c.io.in.bits.data("field0").predicate, false.B)
  poke(c.io.in.bits.data("field1").data, 0)
  poke(c.io.in.bits.data("field1").predicate, false.B)
  poke(c.io.in.bits.data("field2").data, 0)
  poke(c.io.in.bits.data("field2").predicate, false.B)
  poke(c.io.in.bits.data("field3").data, 0)
  poke(c.io.in.bits.data("field3").predicate, false.B)

  step(1)

  // NOTE: Don't use assert().  It seems to terminate the writing of VCD files
  // early (before the error) which makes debugging very difficult. Check results
  // using if() and fail command.
  var time = 0
  var result = false
  while (time < 800000) {
    time += 1
    step(1)
    if (peek(c.io.out.valid) == 1) {
      result = true
      println(Console.BLUE + s"*** Correct return result received. Run time: $time cycles." + Console.RESET)
    }
  }
  //println(Console.BLUE + s"*** Correct return result received. Run time: $time cycles." + Console.RESET)

  step(100)

  //  Peek into the CopyMem to see if the expected data is written back to the Cache
  /*var valid_data = true
  for (i <- 0 until outAddrVec.length) {
   val data = MemRead(outAddrVec(i))
    if (data != outDataVec(i).toInt) {
      println(Console.RED + s"*** Incorrect data received. Got $data. Hoping for ${outDataVec(i).toInt}" + Console.RESET)
      fail
      valid_data = false
    }
    else {
      println(Console.BLUE + s"[LOG] MEM[${outAddrVec(i).toInt}] :: $data" + Console.RESET)
    }
  }
  if (valid_data) {
    println(Console.BLUE + "*** Correct data written back." + Console.RESET)
    dumpMemory("memory.txt")
  }*/


  if (!result) {
    println(Console.RED + s"*** Timeout after $time cycles." + Console.RESET)
    fail
  }
}


class ellpackTester1 extends FlatSpec with Matchers {
  implicit val p = new WithAccelConfig
  //it should "Check that ellpack works correctly." in {
    //// iotester flags:
    //// -ll  = log level <Error|Warn|Info|Debug|Trace>
    //// -tbn = backend <firrtl|verilator|vcs>
    //// -td  = target directory
    //// -tts = seed for RNG
    //chisel3.iotesters.Driver.execute(
      //Array(
        //// "-ll", "Info",
        //"-tn", "ellpackMain",
        //"-tbn", "verilator",
        //"-td", "test_run_dir/ellpack",
        //"-tts", "0001"),
      //() => new ellpackMain()(testParams)) {
      //c => new ellpackTest01(c)
    //} should be(true)
  //}
}
