package dandelion.generator

import chisel3._
import chisel3.util._
import chisel3.Module
import chisel3.testers._
import chisel3.iotesters._
import org.scalatest.{FlatSpec, Matchers}
import muxes._
import dandelion.config._
import dandelion.control._
import util._
import dandelion.interfaces._
import regfile._
import dandelion.memory._
import dandelion.memory.stack._
import dandelion.arbiters._
import dandelion.loop._
import dandelion.accel._
import dandelion.junctions._
import helpers._




class test03Main(implicit p: Parameters) extends AccelIO(List(32, 32), List(32)) {

  val cache = Module(new Cache) // Simple Nasti Cache
  val memModel = Module(new NastiMemSlave) // Model of DRAM to connect to Cache

  // Connect the wrapper I/O to the memory model initialization interface so the
  // test bench can write contents at start.
  memModel.io.nasti <> cache.io.nasti
  memModel.io.init.bits.addr := 0.U
  memModel.io.init.bits.data := 0.U
  memModel.io.init.valid := false.B
  cache.io.cpu.abort := false.B


  // Wire up the cache and modules under test.
  val test03 = Module(new test03DF())
  val test03_debug = Module(new Debug03DF())

  //Put an arbiter infront of cache
  val CacheArbiter = Module(new MemArbiter(6))

  // Connect input signals to cache
  CacheArbiter.io.cpu.MemReq(0) <> test03.io.MemReq
  test03.io.MemResp <> CacheArbiter.io.cpu.MemResp(0)

  //Connect main module to cache arbiter
  CacheArbiter.io.cpu.MemReq(1) <> io.req
  io.resp <> CacheArbiter.io.cpu.MemResp(1)

  CacheArbiter.io.cpu.MemReq(2) <> test03_debug.io.MemReq
  test03_debug.io.MemResp <> CacheArbiter.io.cpu.MemResp(2)

  CacheArbiter.io.cpu.MemReq(3) <> test03_debug.io.MemReq
  test03_debug.io.MemResp <> CacheArbiter.io.cpu.MemResp(3)

  CacheArbiter.io.cpu.MemReq(4) <> test03_debug.io.MemReq
  test03_debug.io.MemResp <> CacheArbiter.io.cpu.MemResp(4)

  CacheArbiter.io.cpu.MemReq(5) <> test03_debug.io.MemReq
  test03_debug.io.MemResp <> CacheArbiter.io.cpu.MemResp(5)

  //Connect cache to the arbiter
  cache.io.cpu.req <> CacheArbiter.io.cache.MemReq
  CacheArbiter.io.cache.MemResp <> cache.io.cpu.resp

  //Connect in/out ports
  test03.io.in <> io.in
  io.out <> test03.io.out

  /**
    * Debuging states for store node
    */
  val sIdle :: sActive :: Nil = Enum(2)
  val state = RegInit(sIdle)

  test03_debug.io.Enable := (state === sActive)

  switch(state) {
    is(sIdle) {
      when(io.in.fire) {
        state := sActive
      }
    }
    is(sActive) {
      when(test03.io.out.fire) {
        state := sIdle
      }
    }

  }



  // Check if trace option is on or off
  if (log == false) {
    println(Console.RED + "****** Trace option is off. *********" + Console.RESET)
  }
  else {
    println(Console.BLUE + "****** Trace option is on. *********" + Console.RESET)
  }


}

class test03Test01[T <: AccelIO](c: T)
                                (inAddrVec: List[Int], inDataVec: List[Int],
                                 outAddrVec: List[Int], outDataVec: List[Int])
  extends AccelTesterLocal(c)(inAddrVec, inDataVec, outAddrVec, outDataVec) {


//  initMemory()
  poke(c.io.in.bits.enable.control, false.B)
  poke(c.io.in.bits.enable.taskID, 0.U)
  poke(c.io.in.bits.enable.debug, true.B)
  poke(c.io.in.valid, false.B)
  poke(c.io.in.bits.data("field0").data, 0.U)
  poke(c.io.in.bits.data("field0").predicate, false.B)
  poke(c.io.in.bits.data("field0").taskID, 0.U)
  poke(c.io.in.bits.data("field1").data, 0.U)
  poke(c.io.in.bits.data("field1").predicate, false.B)
  poke(c.io.in.bits.data("field1").taskID, 0.U)
  poke(c.io.out.ready, false.B)
  step(1)
  poke(c.io.in.bits.enable.control, true.B)
  poke(c.io.in.bits.enable.taskID, 3.U)
  poke(c.io.in.bits.enable.debug, true.B)
  poke(c.io.in.valid, true.B)
  poke(c.io.in.bits.data("field0").data, 50.U)
  poke(c.io.in.bits.data("field0").predicate, true.B)
  poke(c.io.in.bits.data("field0").taskID, 3.U)
  poke(c.io.in.bits.data("field1").data, 5.U)
  poke(c.io.in.bits.data("field1").predicate, true.B)
  poke(c.io.in.bits.data("field1").taskID, 3.U)
  poke(c.io.out.ready, true.B)
  step(1)
  poke(c.io.in.bits.enable.control, false.B)
  poke(c.io.in.bits.enable.debug, true.B)
  poke(c.io.in.valid, false.B)
  poke(c.io.in.bits.data("field0").data, 0.U)
  poke(c.io.in.bits.data("field0").predicate, false.B)
  poke(c.io.in.bits.data("field1").data, 0.U)
  poke(c.io.in.bits.data("field1").predicate, false.B)
  step(1)
  var time = 1 //Cycle counter
  var result = false
  while (time < 10) {
    time += 1
    step(1)
    if (peek(c.io.out.valid) == 1 ) {
      result = true
      val data = peek(c.io.out.bits.data("field0").data)
      val expected = 225
      if (data != expected) {
        println(s"*** Incorrect result received. Got $data. Hoping for $expected")
        fail
      } else {
        println(Console.BLUE + s"*** Correct result received @ cycle: $time." + Console.RESET)
      }
    }
  }

  step(1000)
  dumpMemory("Debug.mem",0,20)

  if (!result) {
    dumpMemory("Debug.mem",0,10)
    println("*** Timeout.")
    fail
  }

}

class test03Tester extends FlatSpec with Matchers {
  val inAddrVec = List.range(0, (4 * 10), 4)
  val inDataVec = List.fill(10)(10)

  implicit val p = new WithAccelConfig
  it should "Check that test04 works correctly." in {
    // iotester flags:
    // -ll  = log level <Error|Warn|Info|Debug|Trace>
    // -tbn = backend <firrtl|treadle|verilator|vcs>
    // -td  = target directory
    // -tts = seed for RNG
    chisel3.iotesters.Driver.execute(
      Array(
        // "-ll", "Info",
        "-tn", "test03",
        "-tbn", "verilator",
        "-td", "test_run_dir/test03",
        "-tts", "0001"),
      () => new test03Main()) {
//      c => new test03Test01(c)
      c => new test03Test01(c)(inAddrVec, inDataVec, List(), List())
    } should be(true)
  }
}

