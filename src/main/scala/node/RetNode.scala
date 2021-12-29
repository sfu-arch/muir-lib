package dandelion.node

import chisel3._
import chisel3.iotesters.{ChiselFlatSpec, Driver, OrderedDecoupledHWIOTester, PeekPokeTester}
import chisel3.Module
import chisel3.testers._
import chisel3.util._
import org.scalatest.{FlatSpec, Matchers}
import utility.UniformPrintfs
import chipsalliance.rocketchip.config._
import chisel3.util.experimental.BoringUtils
import dandelion.interfaces.{VariableDecoupledData, _}
import muxes._
import util._
import dandelion.config._

class RetNodeIO(val retTypes: Seq[Int])(implicit p: Parameters)
  extends Bundle {
  val enable = Flipped(Decoupled(new ControlBundle()))
  val In = Flipped(new VariableDecoupledData(retTypes)) // Data to be returned
  val Out = Decoupled(new Call(retTypes)) // Returns to calling block(s)
}

class RetNode(retTypes: Seq[Int], ID: Int)
             (implicit val p: Parameters,
              name: sourcecode.Name,
              file: sourcecode.File) extends Module
  with HasAccelParams with UniformPrintfs {

  val node_name = name.value
  val module_name = file.value.split("/").tail.last.split("\\.").head.capitalize

  override lazy val io = IO(new RetNodeIO(retTypes)(p))
  override val printfSigil = module_name + ": " + node_name + ID + " "


  val (cycleCount, _) = Counter(true.B, 32 * 1024)

  // Defining states
  val s_IDLE :: s_COMPUTE :: Nil = Enum(2)
  val state = RegInit(s_IDLE)

  // Enable signals
  //  val enable_R = RegInit(ControlBundle.default)
  val enable_valid_R = RegInit(false.B)

  // Data Inputs
  val in_data_valid_R = RegInit(VecInit(Seq.fill(retTypes.length)(false.B)))

  // Output registers
  val output_R = RegInit(0.U.asTypeOf(io.Out.bits))
  val out_ready_R = RegInit(false.B)
  val out_valid_R = RegInit(false.B)


  // Latching enable signal
  io.enable.ready := ~enable_valid_R
  when(io.enable.fire()) {
    enable_valid_R := io.enable.valid
    //    enable_R := io.enable.bits
    output_R.enable := io.enable.bits
  }

  // Latching input data
  for (i <- retTypes.indices) {
    io.In.elements(s"field$i").ready := ~in_data_valid_R(i)
    when(io.In.elements(s"field$i").fire()) {
      output_R.data(s"field$i") := io.In.elements(s"field$i").bits
      in_data_valid_R(i) := true.B
    }
  }

  // Connecting outputs
  io.Out.bits := output_R
  io.Out.valid := out_valid_R

  when(io.Out.fire()) {
    out_ready_R := io.Out.ready
    out_valid_R := false.B
  }

  switch(state) {
    is(s_IDLE) {
      when(enable_valid_R) {
        when(in_data_valid_R.reduceLeft(_ && _)) {
          out_valid_R := true.B
          state := s_COMPUTE

        }
      }
    }
    is(s_COMPUTE) {
      when(out_ready_R) {
        for (i <- retTypes.indices) {
          in_data_valid_R(i) := false.B
        }

        out_valid_R := false.B
        enable_valid_R := false.B
        out_ready_R := false.B

        state := s_IDLE
        if (log) {
          printf("[LOG] " + "[" + module_name + "] " + "[TID->%d] "
            + node_name + ": Output fired @ %d, Value: %d\n",
            output_R.enable.taskID, io.In.asUInt(), output_R.data(s"field0").data)
        }
      }
    }
  }


}

class RetNode2IO(retTypes: Seq[Int], Debug:Boolean = false , NumBores : Int = 0)(implicit val p: Parameters)
  extends Bundle {
  val In = Flipped(new CallDecoupled(retTypes))
  val Out = Decoupled(new Call(retTypes))

  override def cloneType = new RetNode2IO(retTypes, Debug, NumBores).asInstanceOf[this.type]

}

class RetNode2(retTypes: Seq[Int], ID: Int , Debug: Boolean = false, NumBores : Int = 0)
              (implicit val p: Parameters,
               name: sourcecode.Name,
               file: sourcecode.File) extends Module
  with HasAccelParams with UniformPrintfs {

  val node_name = name.value
  val module_name = file.value.split("/").tail.last.split("\\.").head.capitalize

  override lazy val io = IO(new RetNode2IO(retTypes)(p))
  override val printfSigil = module_name + ": " + node_name + ID + " "


  val (cycleCount, _) = Counter(true.B, 32 * 1024)

  // Defining states
  val s_IDLE :: s_COMPUTE :: Nil = Enum(2)
  val state = RegInit(s_IDLE)

  // Enable signals
  val enable_valid_R = RegInit(false.B)

  // Data Inputs
  val in_data_valid_R = Seq.fill(retTypes.length)(RegInit(false.B))

  // Output registers
  val output_R = RegInit(0.U.asTypeOf(io.Out.bits))
  val out_ready_R = RegInit(false.B)
  val out_valid_R = RegInit(false.B)

  def IsInValid(): Bool = {
    if (retTypes.length == 0) {
      true.B
    } else {
      in_data_valid_R.reduceLeft(_ && _)
    }
  }

  // Latching enable signal
  io.In.enable.ready := ~enable_valid_R
  when(io.In.enable.fire()) {
    enable_valid_R := io.In.enable.valid
    output_R.enable := io.In.enable.bits
  }

  // Latching input data
  for (i <- retTypes.indices) {
    io.In.data(s"field$i").ready := ~in_data_valid_R(i)
    when(io.In.data(s"field$i").fire()) {
      output_R.data(s"field$i") := io.In.data(s"field$i").bits
      in_data_valid_R(i) := true.B
    }
  }

  // Connecting outputs
  io.Out.bits := output_R
  io.Out.valid := out_valid_R


  //**********************************************************************
  val RunFinish = RegInit(false.B)
  val RunFinishBoring = WireInit(false.B)
  RunFinishBoring := RunFinish
  if (Debug) {
    for (i <- 0 until NumBores) {
      BoringUtils.addSource(RunFinishBoring, "RunFinished" + i)
    }
  }
  //*******************************************************************

  when(io.Out.fire()) {
    RunFinish := true.B
    out_ready_R := io.Out.ready
    out_valid_R := false.B
  }

  switch(state) {
    is(s_IDLE) {
      when(enable_valid_R) {
        when(IsInValid()) {
          out_valid_R := true.B
          state := s_COMPUTE
        }
      }
    }
    is(s_COMPUTE) {
      when(out_ready_R) {
        for (i <- retTypes.indices) {
          in_data_valid_R(i) := false.B
        }

        out_valid_R := false.B
        enable_valid_R := false.B
        out_ready_R := false.B

        state := s_IDLE
        if (log) {
          printf(p"[LOG] [${module_name}] " +
            p"[TID: ${output_R.enable.taskID}] " +
            p"[${node_name}] " +
            p"[Cycle: ${cycleCount}]\n")
        }
      }
    }
  }


}

/**
  * This version of RetNode2 is the buggy version. The bug happens when we make
  * compute nodes single cycle connected to RetNode
  * @param retTypes
  * @param ID
  * @param p
  * @param name
  * @param file
  */
class RetNode2Buggy(retTypes: Seq[Int], ID: Int)
              (implicit val p: Parameters,
               name: sourcecode.Name,
               file: sourcecode.File) extends Module
  with HasAccelParams with UniformPrintfs {

  val node_name = name.value
  val module_name = file.value.split("/").tail.last.split("\\.").head.capitalize

  override lazy val io = IO(new RetNode2IO(retTypes)(p))
  override val printfSigil = module_name + ": " + node_name + ID + " "


  val (cycleCount, _) = Counter(true.B, 32 * 1024)

  // Defining states
  val s_IDLE :: s_COMPUTE :: Nil = Enum(2)
  val state = RegInit(s_IDLE)

  // Enable signals
  val enable_valid_R = RegInit(false.B)

  // Data Inputs
  val in_data_valid_R = Seq.fill(retTypes.length)(RegInit(false.B))

  // Output registers
  val output_R = RegInit(0.U.asTypeOf(io.Out.bits))
  val out_ready_R = RegInit(false.B)
  val out_valid_R = RegInit(false.B)

  def IsInValid(): Bool = {
    if (retTypes.length == 0) {
      return true.B
    } else {
      in_data_valid_R.reduceLeft(_ && _)
    }
  }

  // Latching enable signal
  io.In.enable.ready := ~enable_valid_R
  when(io.In.enable.fire()) {
    enable_valid_R := io.In.enable.valid
    output_R.enable := io.In.enable.bits
  }

  // Latching input data
  for (i <- retTypes.indices) {
    io.In.data(s"field$i").ready := ~in_data_valid_R(i)
    when(io.In.data(s"field$i").fire()) {
      output_R.data(s"field$i") := io.In.data(s"field$i").bits
      in_data_valid_R(i) := true.B
    }
  }

  // Connecting outputs
  io.Out.bits := output_R
  io.Out.valid := out_valid_R

  when(io.Out.fire()) {
    out_ready_R := io.Out.ready
    out_valid_R := false.B
  }

  switch(state) {
    is(s_IDLE) {
      when(enable_valid_R) {
        when(IsInValid()) {
          out_valid_R := true.B
          state := s_COMPUTE
        }
      }
    }
    is(s_COMPUTE) {
      when(out_ready_R) {
        for (i <- retTypes.indices) {
          in_data_valid_R(i) := false.B
        }

        out_valid_R := false.B
        enable_valid_R := false.B
        out_ready_R := false.B

        state := s_IDLE
        if (log) {
          printf("[LOG] " + "[" + module_name + "] "
            + "[TID->%d] " + node_name +
            ": Output fired @ %d\n", output_R.enable.taskID, cycleCount)
        }
      }
    }
  }


}
