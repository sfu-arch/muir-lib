package dandelion.junctions

import chisel3._
import chisel3.util._
import dandelion.interfaces._
import chipsalliance.rocketchip.config._
import dandelion.config._

class CombineCustomIO(argTypes: Seq[Bits])(implicit p: Parameters) extends Bundle {
  val In =  Flipped(new VariableDecoupledCustom(argTypes))
  val Out = Decoupled(new VariableCustom(argTypes))
}

class CombineCustom(val argTypes: Seq[Bits])(implicit p: Parameters) extends Module {
  val io = IO(new CombineCustomIO(argTypes))
  val inputReady = RegInit(VecInit(Seq.fill(argTypes.length){true.B}))
  val outputReg    = RegInit(0.U.asTypeOf(io.Out.bits))

  for (i <- argTypes.indices) {
    when(io.Out.valid && io.Out.ready){
      inputReady(i) := true.B
    }.elsewhen(io.In(s"field$i").valid) {
      outputReg(s"field$i") := io.In(s"field$i").bits
      inputReady(i) := false.B
    }
    io.In(s"field$i").ready := inputReady(i)
  }
  io.Out.valid := ~inputReady.asUInt.orR
  io.Out.bits := outputReg
}

class CombineDataIO(val argTypes: Seq[Int])(implicit p: Parameters) extends AccelBundle {
  val In =  Flipped(new VariableDecoupledData(argTypes))
  val Out = Decoupled(new VariableData(argTypes))

}

class CombineData(val argTypes: Seq[Int])(implicit p: Parameters) extends Module {
  val io = IO(new CombineDataIO(argTypes))
  val inputReady = RegInit(VecInit(Seq.fill(argTypes.length){true.B}))
  val outputReg  = RegInit(0.U.asTypeOf(io.Out.bits))

  for (i <- argTypes.indices) {
    when(io.Out.fire){
      inputReady(i) := true.B
    }.elsewhen(io.In(s"field$i").valid) {
      outputReg(s"field$i") := io.In(s"field$i").bits
      inputReady(i) := false.B
    }
    io.In(s"field$i").ready := inputReady(i)
  }
  io.Out.valid := ~(inputReady.asUInt.orR)
  io.Out.bits := outputReg

}

class CombineCallIO(val argTypes: Seq[Int])(implicit p: Parameters) extends AccelBundle {
  val In =  Flipped(new CallDecoupled(argTypes))
  val Out = Decoupled(new Call(argTypes))
}

class CombineCall(val argTypes: Seq[Int])(implicit p: Parameters) extends Module {
  val io = IO(new CombineCallIO(argTypes))
  val inputReady = RegInit(VecInit(Seq.fill(argTypes.length+1){true.B}))
  val outputReg  = RegInit(0.U.asTypeOf(io.Out))

  for (i <- argTypes.indices) {
    when(io.Out.fire){
      inputReady(i) := true.B
    }.elsewhen(io.In.data(s"field$i").fire) {
      outputReg.bits.data(s"field$i") := io.In.data(s"field$i").bits
      inputReady(i) := false.B
    }
    io.In.data(s"field$i").ready := inputReady(i)
  }

  when(io.Out.fire){
    inputReady(argTypes.length) := true.B
  }.elsewhen(io.In.enable.fire) {
    outputReg.bits.enable <> io.In.enable.bits
    inputReady (argTypes.length) := false.B
  }
  io.In.enable.ready := inputReady(argTypes.length)

  io.Out.valid := ~(inputReady.asUInt.orR)
  io.Out.bits := outputReg.bits

}
