package dataflow


import java.io.PrintWriter
import java.io.File
import chisel3._
import chisel3.util._
import chisel3.Module
import chisel3.testers._
import chisel3.iotesters._
import org.scalatest.{FlatSpec, Matchers}
import muxes._
import config._
import control._
import util._
import interfaces._
import regfile._
import memory._
import stack._
import arbiters._
import loop._
import accel._
import node._


class bgemmMainIO(implicit val p: Parameters) extends Module with CoreParams with CacheParams {
  val io = IO(new Bundle {
    val in = Flipped(Decoupled(new Call(List(32, 32, 32))))
    val req = Flipped(Decoupled(new MemReq))
    val resp = Output(Valid(new MemResp))
    val out = Decoupled(new Call(List()))
  })

  def cloneType = new bgemmMainIO().asInstanceOf[this.type]
}

class bgemmMainDirect(implicit p: Parameters) extends bgemmMainIO()(p) {
  val cache = Module(new Cache) // Simple Nasti Cache
  val memModel = Module(new NastiMemSlave) // Model of DRAM to connect to Cache

  // Connect the wrapper I/O to the memory model initialization interface so the
  // test bench can write contents at start.
  memModel.io.nasti <> cache.io.nasti
  memModel.io.init.bits.addr := 0.U
  memModel.io.init.bits.data := 0.U
  memModel.io.init.valid := false.B
  cache.io.cpu.abort := false.B


  val bgemm = Module(new bgemmDF())
  val bgemm_detach1 = Module(new bgemm_detach1DF())
  val bgemm_detach2 = Module(new bgemm_detach2DF())
  val bgemm_detach3 = Module(new bgemm_detach3DF())


  bgemm.io.MemResp <> DontCare
  bgemm.io.MemReq <> DontCare

  bgemm_detach1.io.MemResp <> DontCare
  bgemm_detach1.io.MemReq <> DontCare

  bgemm_detach2.io.MemResp <> DontCare
  bgemm_detach2.io.MemReq <> DontCare

  val MemArbiter = Module(new MemArbiter(2))

  MemArbiter.io.cpu.MemReq(0) <> bgemm_detach3.io.MemReq
  bgemm_detach3.io.MemResp <> MemArbiter.io.cpu.MemResp(0)

  MemArbiter.io.cpu.MemReq(1) <> io.req
  io.resp <> MemArbiter.io.cpu.MemResp(1)

  cache.io.cpu.req <> MemArbiter.io.cache.MemReq
  MemArbiter.io.cache.MemResp <> cache.io.cpu.resp

  bgemm_detach1.io.in <> bgemm.io.call_9_out
  bgemm_detach2.io.in <> bgemm_detach1.io.call_9_out
  bgemm_detach3.io.in <> bgemm_detach2.io.call_8_out
  bgemm_detach2.io.call_8_in <> bgemm_detach3.io.out
  bgemm_detach1.io.call_9_in <> bgemm_detach2.io.out
  bgemm.io.call_9_in <> bgemm_detach1.io.out

  bgemm.io.in <> io.in
  io.out <> bgemm.io.out

}

//class bgemmMainTM(implicit p: Parameters) extends bgemmMainIO()(p) {
//
//  val cache = Module(new Cache) // Simple Nasti Cache
//  val memModel = Module(new NastiMemSlave) // Model of DRAM to connect to Cache
//  val memCopy = Mem(1024, UInt(32.W)) // Local memory just to keep track of writes to cache for validation
//
//  // Store a copy of all data written to the cache.  This is done since the cache isn't
//  // 'write through' to the memory model and we have no easy way of reading the
//  // cache contents from the testbench.
//  when(cache.io.cpu.req.valid && cache.io.cpu.req.bits.iswrite) {
//    memCopy.write((cache.io.cpu.req.bits.addr >> 2).asUInt(), cache.io.cpu.req.bits.data)
//  }
//  io.dout := memCopy.read((io.addr >> 2).asUInt())
//
//  // Connect the wrapper I/O to the memory model initialization interface so the
//  // test bench can write contents at start.
//  memModel.io.nasti <> cache.io.nasti
//  memModel.io.init.bits.addr := io.addr
//  memModel.io.init.bits.data := io.din
//  memModel.io.init.valid := io.write
//  cache.io.cpu.abort := false.B
//
//  val children = 3
//  val TaskControllerModule = Module(new TaskController(List(32, 32, 32, 32), List(32), 1, children))
//  val bgemm = Module(new bgemmDF())
//
//  val bgemm_detach1 = for (i <- 0 until children) yield {
//    val detach = Module(new bgemm_detach1DF())
//    detach
//  }
//  val bgemm_detach2 = for (i <- 0 until children) yield {
//    val detach2 = Module(new bgemm_detach2DF)
//    detach2
//  }
//  val bgemm_detach3 = for (i <- 0 until children) yield {
//    val detach3 = Module(new bgemm_detach3DF)
//    detach3
//  }
//
//  // Merge requests from two children.
//  val MemArbiter = Module(new MemArbiter(children))
//  for (i <- 0 until children) {
//    MemArbiter.io.cpu.MemReq(i) <> bgemm_detach2(i).io.MemReq
//    bgemm_detach2(i).io.MemResp <> MemArbiter.io.cpu.MemResp(i)
//  }
//  cache.io.cpu.req <> MemArbiter.io.cache.MemReq
//  MemArbiter.io.cache.MemResp <> cache.io.cpu.resp
//
//  // tester to cilk_for_test02
//  bgemm.io.in <> io.in
//
//  // cilk_for_test02 to task controller
//  TaskControllerModule.io.parentIn(0) <> bgemm.io.call10_out
//
//  // task controller to sub-task bgemm_detach
//  for (i <- 0 until children) {
//    bgemm_detach1(i).io.in <> TaskControllerModule.io.childOut(i)
//    bgemm_detach2(i).io.in <> bgemm_detach1(i).io.call13_out
//    bgemm_detach3(i).io.in <> bgemm_detach2(i).io.call13_out
//    bgemm_detach2(i).io.call13_in <> bgemm_detach3(i).io.out
//    bgemm_detach1(i).io.call13_in <> bgemm_detach2(i).io.out
//    TaskControllerModule.io.childIn(i) <> bgemm_detach1(i).io.out
//  }
//
//  // Task controller to cilk_for_test02
//  bgemm.io.call10_in <> TaskControllerModule.io.parentOut(0)
//
//  // cilk_for_test02 to tester
//  io.out <> bgemm.io.out
//
//}

class bgemmTest01[T <: bgemmMainIO](c: T) extends PeekPokeTester(c) {


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


  def dumpMemory(path: String) = {
    //Writing mem states back to the file
    val pw = new PrintWriter(new File(path))
    for (i <- 0 until outDataVec.length) {
      val data = MemRead(outAddrVec(i))
      pw.write("0X" + outAddrVec(i).toHexString + " -> " + data + "\n")
    }
    pw.close

  }


  def dumpMemoryInit(path: String) = {
    //Writing mem states back to the file
    val pw = new PrintWriter(new File(path))
    for (i <- 0 until inDataVec.length) {
      val data = MemRead(inAddrVec(i))
      pw.write("0X" + inAddrVec(i).toHexString + " -> " + data + "\n")
    }
    pw.close

  }


  val inAddrVec = List.range(0, 4 * 32, 4) // byte addresses
  val inA = List.range(0, 16) // 4x4 array of uint32
  val inB = List.range(0, 16) // 4x4 array of uint32
  val inDataVec = inA ++ inB
  val outAddrVec = List.range(256, 256 + (4 * 16), 4)
  val outDataVec = List(
    56, 62, 68, 74
    , 152, 174, 196, 218
    , 248, 286, 324, 362
    , 344, 398, 452, 506
  )

  // Write initial contents to the memory model.
  for (i <- 0 until inDataVec.length) {
    MemWrite(inAddrVec(i), inDataVec(i))
  }

  step(10)
  dumpMemoryInit("memory.txt")

  step(1)


  // Initializing the signals
  poke(c.io.in.bits.enable.control, false)
  poke(c.io.in.bits.enable.taskID, 0)
  poke(c.io.in.valid, false)
  poke(c.io.in.bits.data("field0").data, 0)
  poke(c.io.in.bits.data("field0").predicate, false)
  poke(c.io.in.bits.data("field1").data, 0)
  poke(c.io.in.bits.data("field1").predicate, false)
  poke(c.io.in.bits.data("field2").data, 0)
  poke(c.io.in.bits.data("field2").predicate, false)
  poke(c.io.out.ready, false)
  step(1)
  poke(c.io.in.bits.enable.control, true)
  poke(c.io.in.valid, true)
  poke(c.io.in.bits.data("field0").data, 0)
  poke(c.io.in.bits.data("field0").predicate, true)
  poke(c.io.in.bits.data("field1").data, 64)
  poke(c.io.in.bits.data("field1").predicate, true)
  poke(c.io.in.bits.data("field2").data, 256)
  poke(c.io.in.bits.data("field2").predicate, true)
  poke(c.io.out.ready, true)
  step(1)
  poke(c.io.in.bits.enable.control, false)
  poke(c.io.in.valid, false)
  poke(c.io.in.bits.data("field0").data, 0)
  poke(c.io.in.bits.data("field0").predicate, false)
  poke(c.io.in.bits.data("field1").data, 0)
  poke(c.io.in.bits.data("field1").predicate, false)
  poke(c.io.in.bits.data("field2").data, 0)
  poke(c.io.in.bits.data("field2").predicate, false)

  step(1)

  // NOTE: Don't use assert().  It seems to terminate the writing of VCD files
  // early (before the error) which makes debugging very difficult. Check results
  // using if() and fail command.
  var time = 0 //Cycle counter
  var result = false
  while (time < 2000) {
    time += 1
    step(1)
    if (peek(c.io.out.valid) == 1) {
      result = true
      println(Console.BLUE + s"*** Bgemm finished. Run time: $time cycles." + Console.RESET)
      //      val data = peek(c.io.out.bits.data("field0").data)
      //      if (data != 1) {
      //        println(Console.RED + s"*** Incorrect result received. Got $data. Hoping for 1" + Console.RESET)
      //        fail
      //      } else {
      //        println(Console.BLUE + s"*** Correct result received. Run time: $time cycles." + Console.RESET)
      //      }
    }
  }
  //  Peek into the CopyMem to see if the expected data is written back to the Cache
  var valid_data = true
  for (i <- 0 until outDataVec.length) {
    val data = MemRead(outAddrVec(i))
    if (data != outDataVec(i).toInt) {
      if(c.log){
        println(Console.RED + s"*** Incorrect data received. Got $data. Hoping for ${outDataVec(i).toInt}" + Console.RESET)
      }
      fail
      valid_data = false
    }
    else {
      if(c.log){
        println(Console.BLUE + s"[LOG] MEM[${outAddrVec(i).toInt}] :: $data" + Console.RESET)
      }
    }
  }
  if (valid_data) {
    println(Console.BLUE + "*** Correct data written back." + Console.RESET)
  }

  if (!result) {
    println(Console.RED + "*** Timeout." + Console.RESET)
    fail
  }
}

class bgemmTester1 extends FlatSpec with Matchers {
  implicit val p = config.Parameters.root((new MiniConfig).toInstance)
  it should "Check that bgemm works correctly." in {
    // iotester flags:
    // -ll  = log level <Error|Warn|Info|Debug|Trace>
    // -tbn = backend <firrtl|verilator|vcs>
    // -td  = target directory
    // -tts = seed for RNG
    chisel3.iotesters.Driver.execute(
      Array(
        // "-ll", "Info",
        "-tbn", "verilator",
        "-td", "test_run_dir",
        "-tts", "0001"),
      () => new bgemmMainDirect()) {
      c => new bgemmTest01(c)
    } should be(true)
  }
}

//class bgemmTester2 extends FlatSpec with Matchers {
//  implicit val p = config.Parameters.root((new MiniConfig).toInstance)
//  // iotester flags:
//  // -ll  = log level <Error|Warn|Info|Debug|Trace>
//  // -tbn = backend <firrtl|verilator|vcs>
//  // -td  = target directory
//  // -tts = seed for RNG
//  it should "Check that cilk_for_test02 works when called via task manager." in {
//    chisel3.iotesters.Driver.execute(
//      Array(
//        // "-ll", "Info",
//        "-tbn", "verilator",
//        "-td", "test_run_dir",
//        "-tts", "0001"),
//      () => new bgemmMainTM()) {
//      c => new bgemmTest01(c)
//    } should be(true)
//  }
//}
