package dandelion.junctions

import chisel3._
import chisel3.util._
import dandelion.interfaces._
import chipsalliance.rocketchip.config._
import dandelion.config.HasAccelParams

class SplitCustomIO(argTypes: Seq[Bits])(implicit p: Parameters) extends Bundle {
  val In = Flipped(Decoupled(new VariableCustom(argTypes)))
  val Out = new VariableDecoupledCustom(argTypes)
}

class SplitCustom(val argTypes: Seq[Bits])(implicit p: Parameters) extends Module {
  val io = IO(new SplitCustomIO(argTypes))
  val inputReg  = RegInit(0.U.asTypeOf(io.In.bits))
  val inputReadyReg = RegInit(false.B)
  val outputValidReg = RegInit(VecInit(Seq.fill(argTypes.length){false.B}))

  val s_idle :: s_latch :: Nil = Enum(2)
  val state = RegInit(s_idle)

  switch(state) {
    is(s_idle) {
      when (io.In.valid) {
        state := s_latch
        inputReg := io.In.bits
      }
    }
    is(s_latch) {
      when (outputValidReg.asUInt.orR === false.B) {
        state := s_idle
      }
    }
  }
  io.In.ready := state === s_idle

  for (i <- argTypes.indices) {
    when(io.In.valid && state === s_idle) {
      outputValidReg(i) := true.B
    }
    when(outputValidReg(i) && io.Out(s"field$i").ready){
      outputValidReg(i) := false.B
    }
    io.Out(s"field$i").valid := outputValidReg(i)
    io.Out(s"field$i").bits := inputReg((s"field$i"))
  }
}

class SplitDataIO(val argTypes: Seq[Int])(implicit p: Parameters) extends Bundle {
  val In = Flipped(Decoupled(new VariableData(argTypes)))
  val Out = new VariableDecoupledData(argTypes)
}

class SplitData(val argTypes: Seq[Int])(implicit p: Parameters) extends Module {
  val io = IO(new SplitDataIO(argTypes))
  val inputReg  = RegInit(0.U.asTypeOf(io.In.bits))
  val inputReadyReg = RegInit(false.B)
  val outputValidReg = RegInit(VecInit(Seq.fill(argTypes.length){false.B}))

  val s_idle :: s_latched :: Nil = Enum(2)
  val state = RegInit(s_idle)

  switch(state) {
    is(s_idle) {
      when (io.In.valid) {
        state := s_latched
        inputReg := io.In.bits
      }
    }
    is(s_latched) {
      when (outputValidReg.asUInt.orR === false.B) {
        state := s_idle
      }
    }
  }
  io.In.ready := state === s_idle

  for (i <- argTypes.indices) {
    when(io.In.valid && state === s_idle) {
      outputValidReg(i) := true.B
    }
    when(outputValidReg(i) && io.Out(s"field$i").ready){
      outputValidReg(i) := false.B
    }
    io.Out(s"field$i").valid := outputValidReg(i)
    io.Out(s"field$i").bits := inputReg((s"field$i"))
  }

}

class SplitCallIO(val argTypes: Seq[Int])(implicit p: Parameters) extends Bundle {
  val In = Flipped(Decoupled(new Call(argTypes)))
  val Out = new CallDecoupled(argTypes)
}

class SplitCall(val argTypes: Seq[Int])(implicit p: Parameters) extends Module {
  val io = IO(new SplitCallIO(argTypes))
  val inputReg  = RegInit(0.U.asTypeOf(io.In.bits))
  val inputReadyReg = RegInit(false.B)
  val outputValidReg = RegInit(VecInit(Seq.fill(argTypes.length + 1)(false.B)))

  val s_idle :: s_latched :: Nil = Enum(2)
  val state = RegInit(s_idle)

  io.In.ready := state === s_idle

  switch(state) {
    is(s_idle) {
      when (io.In.fire) {
        state := s_latched
        inputReg <> io.In.bits
      }
    }
    is(s_latched) {
      when (outputValidReg.asUInt.orR === false.B) {
        state := s_idle
      }
    }
  }

  for (i <- argTypes.indices) {
    when(io.In.valid && state === s_idle) {
      outputValidReg(i) := true.B
    }
    when(state === s_latched && io.Out.data(s"field$i").ready){
      outputValidReg(i) := false.B
    }
    io.Out.data(s"field$i").valid := outputValidReg(i)
    io.Out.data(s"field$i").bits <> inputReg.data(s"field$i")
  }

  when(io.In.valid && state === s_idle) {
    outputValidReg(argTypes.length) := true.B
  }
  when(state === s_latched && io.Out.enable.ready){
    outputValidReg(argTypes.length) := false.B
  }
  io.Out.enable.valid := outputValidReg(argTypes.length)
  io.Out.enable.bits <> inputReg.enable

}

class SplitCallNewIO(val argTypes: Seq[Int])(implicit p: Parameters) extends Bundle {
  val In = Flipped(Decoupled(new Call(Seq.fill(argTypes.length)(32))))
  val Out = new CallDecoupledVec(argTypes)

}

class SplitCallNew(val argTypes: Seq[Int])(implicit p: Parameters) extends Module {
  val io = IO(new SplitCallNewIO(argTypes))
  val inputReg  = RegInit(0.U.asTypeOf(io.In.bits))
  val inputReadyReg = RegInit(false.B)
  val enableValidReg = RegInit(false.B)

  val outputValidReg = for(i <- argTypes.indices) yield {
    val validReg = Seq.fill(argTypes(i)){RegInit(false.B)}
    validReg
  }
  val allValid = for(i <- argTypes.indices) yield {
    val allValid = outputValidReg(i).reduceLeft(_ || _)
    allValid
  }

  val s_idle :: s_latched :: Nil = Enum(2)
  val state = RegInit(s_idle)

  io.In.ready := state === s_idle

  switch(state) {
    is(s_idle) {
      when (io.In.fire) {
        state := s_latched
        inputReg <> io.In.bits
      }
    }
    is(s_latched) {
      when (!allValid.reduceLeft(_ || _) && !enableValidReg) {
        state := s_idle
      }
    }
  }

  for (i <- argTypes.indices) {
    for (j <- 0 until argTypes(i)) {
      when(io.In.valid && state === s_idle) {
        outputValidReg(i)(j) := true.B
      }
      when(state === s_latched && io.Out.data(s"field$i")(j).ready) {
        outputValidReg(i)(j) := false.B
      }
      io.Out.data(s"field$i")(j).valid := outputValidReg(i)(j)
      io.Out.data(s"field$i")(j).bits := inputReg.data(s"field$i")
    }
  }

  when(io.In.valid && state === s_idle) {
    enableValidReg := true.B
  }
  when(state === s_latched && io.Out.enable.ready){
    enableValidReg := false.B
  }
  io.Out.enable.valid := enableValidReg
  io.Out.enable.bits := inputReg.enable

}

class SplitCallDCRIO(val ptrsArgTypes: Seq[Int],
                     val valsArgTypes: Seq[Int])(implicit val p: Parameters) extends Bundle with HasAccelParams {
  val In = Flipped(Decoupled(new CallDCR(Seq.fill(ptrsArgTypes.length)(xlen), Seq.fill(valsArgTypes.length)(xlen))))
  val Out = new CallDCRDecoupledVec(ptrsArgTypes, valsArgTypes)

}

class SplitCallDCR(val ptrsArgTypes: Seq[Int], val valsArgTypes: Seq[Int])(implicit p: Parameters) extends Module {
  val io = IO(new SplitCallDCRIO(ptrsArgTypes, valsArgTypes))

  val inputReg  = RegInit(0.U.asTypeOf(io.In.bits))
  val inputReadyReg = RegInit(false.B)
  val enableValidReg = RegInit(false.B)

  val outputPtrsValidReg = for(i <- ptrsArgTypes.indices) yield {
    val validReg = Seq.fill(ptrsArgTypes(i)){RegInit(false.B)}
    validReg
  }

  val outputValsValidReg = for(i <- valsArgTypes.indices) yield {
    val validReg = Seq.fill(valsArgTypes(i)){RegInit(false.B)}
    validReg
  }


  val ptrsValid = for(i <- ptrsArgTypes.indices) yield {
    val ptrsV = outputPtrsValidReg(i).reduceLeft(_ || _)
    ptrsV
  }

  val valsValid = for(i <- valsArgTypes.indices) yield {
    val valsV = outputValsValidReg(i).reduceLeft(_ || _)
    valsV
  }

  def isPtrsValid(): Bool ={
    if(ptrsArgTypes.length > 0)
      ~ptrsValid.reduceLeft(_ && _)
    else
      true.B
  }

  def isValsValid(): Bool ={
    if(valsArgTypes.size > 0)
      ~valsValid.reduceLeft(_ && _)
    else
      true.B
  }

  val s_idle :: s_latched :: Nil = Enum(2)
  val state = RegInit(s_idle)

  io.In.ready := state === s_idle

  switch(state) {
    is(s_idle) {
      when (io.In.fire) {
        state := s_latched
        inputReg <> io.In.bits
      }
    }
    is(s_latched) {
      when (isPtrsValid && isValsValid && ~enableValidReg) {
        state := s_idle
      }
    }
  }

  for (i <- ptrsArgTypes.indices) {
    for (j <- 0 until ptrsArgTypes(i)) {
      when(io.In.valid && state === s_idle) {
        outputPtrsValidReg(i)(j) := true.B
      }
      when(state === s_latched && io.Out.dataPtrs(s"field$i")(j).ready) {
        outputPtrsValidReg(i)(j) := false.B
      }
      io.Out.dataPtrs(s"field$i")(j).valid := outputPtrsValidReg(i)(j)
      io.Out.dataPtrs(s"field$i")(j).bits := inputReg.dataPtrs(s"field$i")
    }
  }

  for (i <- valsArgTypes.indices) {
    for (j <- 0 until valsArgTypes(i)) {
      when(io.In.valid && state === s_idle) {
        outputValsValidReg(i)(j) := true.B
      }
      when(state === s_latched && io.Out.dataVals(s"field$i")(j).ready) {
        outputValsValidReg(i)(j) := false.B
      }
      io.Out.dataVals(s"field$i")(j).valid := outputValsValidReg(i)(j)
      io.Out.dataVals(s"field$i")(j).bits := inputReg.dataVals(s"field$i")
    }
  }


  when(io.In.valid && state === s_idle) {
    enableValidReg := true.B
  }
  when(state === s_latched && io.Out.enable.ready){
    enableValidReg := false.B
  }

  io.Out.enable.valid := enableValidReg
  io.Out.enable.bits := inputReg.enable

}
