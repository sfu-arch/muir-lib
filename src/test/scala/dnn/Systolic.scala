package dnn

import chisel3._
import chisel3.util._

import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester, OrderedDecoupledHWIOTester}
import org.scalatest.{Matchers, FlatSpec}

import node._
import dataflow._
import muxes._
import config._
import util._

class SystolicBaseTests(df: SystolicSquareBuffered[UInt])(implicit p: config.Parameters) extends PeekPokeTester(df) {
  poke(df.io.activate, false.B)
  // left * right
  df.io.left.zipWithIndex.foreach { case (io, i) => poke(io, (i + 1).U) }
  df.io.right.zipWithIndex.foreach { case (io, i) => poke(io, (i + 1).U) }
  poke(df.io.activate, true.B)
  step(1)
  poke(df.io.activate, false.B)
  step(1)
  for(i <- 0 until 10){
    if(peek(df.io.output.valid) == 1){
      for (i <- 0 until df.N * df.N) {
        print(peek(df.io.output.bits(i)) + ",")
      }
      print("\n")
    }
    step(1)
  }
  print("\n")
}


class SystolicTests(df: SystolicBLAS[UInt])(implicit p: config.Parameters) extends PeekPokeTester(df) {
  poke(df.io.activate, false.B)
  // left * right
  df.io.left.zipWithIndex.foreach { case (io, i) => poke(io, (i + 1).U) }
  df.io.right.zipWithIndex.foreach { case (io, i) => poke(io, (i + 1).U) }
  poke(df.io.activate, true.B)
  step(1)
  poke(df.io.activate, false.B)
  step(df.latency( ) - 1)
  for (i <- 0 until df.M * df.N) {
    print(peek(df.io.output(i)) + ",")
  }
}

class SystolicTestStream(df: SystolicSquareWrapper[UInt])(implicit p: config.Parameters) extends PeekPokeTester(df) {

  println("Start initilizing input value")

  val data_left  = (1 to 9).toList
  val data_right = (1 to 9).toList
  val data_input = data_left ++ data_right

  poke(df.io.output.ready, true)

  step(1)

  for( data <- data_input){
    if(peek(df.io.input_data.ready) == 1){
      poke(df.io.input_data.bits, data)
      poke(df.io.input_data.valid, true)
      step(1)
    }else{
      println("ERROR")
    }
  }

//  for( i <- 0 to 10){
//    step(1)
//    if(peek(df.io.output.valid) == 1){
//      println(s" Output($i): ${peek(df.io.output.bits)}")
//    }
//  }

  while( peek(df.io.output.valid) == 0){
    step(1)
  }

  println("Printing output")

  //while ( peek(df.io.output.valid) == 1){
  for(i <- 0 to 20){
    println(s"Output: ${peek(df.io.output.bits)}")
    step(1)
  }

}


class Systolic_Tester extends FlatSpec with Matchers {
  implicit val p = config.Parameters.root((new Mat_VecConfig).toInstance)
  it should "Typ Compute Tester" in {
    chisel3.iotesters.Driver.execute(Array("--backend-name", "verilator", "--target-dir", "test_run_dir"),
      () => new SystolicSquareBuffered(UInt(p(XLEN).W), 3)) {
      c => new SystolicBaseTests(c)
    } should be(true)

    //chisel3.iotesters.Driver.execute(Array("--backend-name", "verilator", "--target-dir", "test_run_dir"),
      //() => new SystolicBLAS(UInt(p(XLEN).W), 3, 3, 1)) {
      //c => new SystolicTests(c)
    //} should be(true)
  }
}

class SystolicWrapper_Tester extends FlatSpec with Matchers {
  implicit val p = config.Parameters.root((new Mat_VecConfig).toInstance)
  it should "Typ Compute Tester" in {
    chisel3.iotesters.Driver.execute(Array("--backend-name", "verilator", "--target-dir", "test_run_dir"),
      () => new SystolicSquareWrapper(UInt(p(XLEN).W), 3)) {
      c => new SystolicTestStream(c)
    } should be(true)

    //chisel3.iotesters.Driver.execute(Array("--backend-name", "verilator", "--target-dir", "test_run_dir"),
      //() => new SystolicBLAS(UInt(p(XLEN).W), 3, 3, 1)) {
      //c => new SystolicTests(c)
    //} should be(true)
  }
}
