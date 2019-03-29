package dataflow

import java.io.{File, PrintWriter}

import accel._
import chisel3.iotesters._
import chisel3.util._
import chisel3.{Module, _}
import config._
import dataflow.fftDF
import interfaces._
import memory._
import org.scalatest.{FlatSpec, Matchers}


class fftMainIO(implicit val p: Parameters) extends Module with CoreParams with CacheParams {
  val io = IO(new Bundle {
    val in = Flipped(Decoupled(new Call(List(32, 32, 32, 32))))
    val req = Flipped(Decoupled(new MemReq))
    val resp = Output(Valid(new MemResp))
    val out = Decoupled(new Call(List()))
  })

  def cloneType = new fftMainIO().asInstanceOf[this.type]
}

class fftMain(implicit p: Parameters) extends fftMainIO {

  val cache = Module(new Cache) // Simple Nasti Cache
  val memModel = Module(new NastiInitMemSlave()()) // Model of DRAM to connect to Cache
//  val memModel = Module(new NastiMemSlave) // Model of DRAM to connect to Cache


  // Connect the wrapper I/O to the memory model initialization interface so the
  // test bench can write contents at start.
  memModel.io.nasti <> cache.io.nasti
  memModel.io.init.bits.addr := 0.U
  memModel.io.init.bits.data := 0.U
  memModel.io.init.valid := false.B
  cache.io.cpu.abort := false.B

  // Wire up the cache and modules under test.
  val test05 = Module(new fftDF())

  //Put an arbiter infront of cache
  val CacheArbiter = Module(new MemArbiter(2))

  // Connect input signals to cache
  CacheArbiter.io.cpu.MemReq(0) <> test05.io.MemReq
  test05.io.MemResp <> CacheArbiter.io.cpu.MemResp(0)

  //Connect main module to cache arbiter
  CacheArbiter.io.cpu.MemReq(1) <> io.req
  io.resp <> CacheArbiter.io.cpu.MemResp(1)

  //Connect cache to the arbiter
  cache.io.cpu.req <> CacheArbiter.io.cache.MemReq
  CacheArbiter.io.cache.MemResp <> cache.io.cpu.resp

  //Connect in/out ports
  test05.io.in <> io.in
  io.out <> test05.io.out

}


class fftTest01[T <: fftMainIO](c: T) extends PeekPokeTester(c) {


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

//  def dumpMemory(path: String) = {
//    //Writing mem states back to the file
//    val pw = new PrintWriter(new File(path))
//    for (i <- 0 until outDataVec.length) {
//      val data = MemRead(outAddrVec(i))
//      pw.write("0X" + outAddrVec(i).toHexString + " -> " + data + "\n")
//    }
//    pw.close
//
//  }


  //  val inAddrVec = List.range(0x0037957020, 0x000037957020 + (4 * 10), 4)
  val addr_range = 0x0

  //Write initial contents to the memory model.
  //for (i <- 0 until inDataVec.length) {
   // MemWrite(inAddrVec(i), inDataVec(i))
  //}

  //  step(10)
  //dumpMemory("memory.txt")

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
  poke(c.io.in.bits.data("field0").data, addr_range) //
  poke(c.io.in.bits.data("field0").predicate, true.B)
  poke(c.io.in.bits.data("field1").data, addr_range + 0x200) //
  poke(c.io.in.bits.data("field1").predicate, true.B)
  poke(c.io.in.bits.data("field2").data, addr_range + 0x400) //
  poke(c.io.in.bits.data("field2").predicate, true.B)
  poke(c.io.in.bits.data("field3").data, addr_range + 0x600) //
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
  while (time < 1000) {
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
    dumpMemory("memory.txt")
    fail
  }
}


class fftTester1 extends FlatSpec with Matchers {
  implicit val p = config.Parameters.root((new MiniConfig).toInstance)
  val testParams = p.alterPartial({
    case XLEN => 32
    case TRACE => true
  })
  it should "Check that fft works correctly." in {
    // iotester flags:
    // -ll  = log level <Error|Warn|Info|Debug|Trace>
    // -tbn = backend <firrtl|verilator|vcs>
    // -td  = target directory
    // -tts = seed for RNG
    chisel3.iotesters.Driver.execute(
      Array(
        // "-ll", "Info",
        "-tn", "fftMain",
        "-tbn", "verilator",
        "-td", "test_run_dir/fft",
        "-tts", "0001"),
      () => new fftMain()(testParams)) {
      c => new fftTest01(c)
    } should be(true)
  }
}
