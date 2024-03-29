package dandelion.loop

import chisel3._
import chisel3.util._

import chipsalliance.rocketchip.config._
import dandelion.config._
import dandelion.interfaces._
import util._

class LiveOutNodeIO(NumOuts: Int)
                   (implicit p: Parameters)
  extends HandShakingIONPS(NumOuts)(new DataBundle) {

  // Inputdata for Live out element
  val InData = Flipped(Decoupled(new DataBundle()))
}

class LiveOutNode(NumOuts: Int, ID: Int)
                 (implicit p: Parameters,
                  name: sourcecode.Name,
                  file: sourcecode.File)
  extends HandShakingNPS(NumOuts, ID)(new DataBundle())(p) {
  override lazy val io = IO(new LiveOutNodeIO(NumOuts))

  val node_name = name.value
  val module_name = file.value.split("/").tail.last.split("\\.").head.capitalize

  // Printf debugging
  override val printfSigil = node_name + ID + " "
  val (cycleCount, _) = Counter(true.B, 32 * 1024)

  /*===========================================*
   *            Registers                      *
   *===========================================*/
  // In data Input
  val indata_R = RegInit(DataBundle.default)
  val indata_valid_R = RegInit(false.B)

  val finish_R = RegInit(ControlBundle.default)
  val finish_valid_R = RegInit(false.B)

  val s_IDLE :: s_LATCH :: s_VALIDOUT :: Nil = Enum(3)
  //  val s_IDLE :: s_LATCH :: Nil = Enum(2)
  val state = RegInit(s_IDLE)

  /*===============================================*
   *            LATCHING INPUTS                    *
   *===============================================*/

  io.InData.ready := ~indata_valid_R
  when(io.InData.fire) {
    //Latch the data
    indata_R <> io.InData.bits
    indata_valid_R := true.B
  }

  /*===============================================*
   *            DEFINING STATES                    *
   *===============================================*/

  switch(state) {

    is(s_IDLE) {
      when(io.InData.fire) {
        state := s_LATCH
      }
    }
    is(s_LATCH) {
      when(enable_valid_R) {
        when(enable_R.control) {
          state := s_VALIDOUT
          ValidOut()
        }.otherwise {
          state := s_IDLE

          indata_R := DataBundle.default
          indata_valid_R := false.B

          enable_valid_R := false.B
        }
      }
    }
    is(s_VALIDOUT) {
      when(IsOutReady()) {
        state := s_IDLE

        indata_R := DataBundle.default
        indata_valid_R := false.B

        Reset()
      }
    }
  }

  /*==========================================*
   *             WIRING OUTPUT                *
   *==========================================*/

  // Wire up Outputs
  for (i <- 0 until NumOuts) {
    io.Out(i).bits <> indata_R
  }

}



class LiveOutControlNodeIO(NumOuts: Int)
                   (implicit p: Parameters)
  extends HandShakingIONPS(NumOuts)(new ControlBundle()) {

  // Inputdata for Live out element
  val InData = Flipped(Decoupled(new ControlBundle()))

}


class LiveOutControlNode(NumOuts: Int, ID: Int)
                 (implicit p: Parameters,
                  name: sourcecode.Name,
                  file: sourcecode.File)
  extends HandShakingNPS(NumOuts, ID)(new ControlBundle())(p) {
  override lazy val io = IO(new LiveOutControlNodeIO(NumOuts))

  val node_name = name.value
  val module_name = file.value.split("/").tail.last.split("\\.").head.capitalize

  // Printf debugging
  override val printfSigil = node_name + ID + " "
  val (cycleCount, _) = Counter(true.B, 32 * 1024)

  /*===========================================*
   *            Registers                      *
   *===========================================*/
  // In data Input
  val indata_R = RegInit(ControlBundle.default)
  val indata_valid_R = RegInit(false.B)

  val finish_R = RegInit(ControlBundle.default)
  val finish_valid_R = RegInit(false.B)

  val s_IDLE :: s_LATCH :: s_VALIDOUT :: Nil = Enum(3)
  //  val s_IDLE :: s_LATCH :: Nil = Enum(2)
  val state = RegInit(s_IDLE)

  /*===============================================*
   *            LATCHING INPUTS                    *
   *===============================================*/

  io.InData.ready := ~indata_valid_R
  when(io.InData.fire) {
    //Latch the data
    indata_R <> io.InData.bits
    indata_valid_R := true.B
  }

  /*===============================================*
   *            DEFINING STATES                    *
   *===============================================*/

  switch(state) {

    is(s_IDLE) {
      when(io.InData.fire) {
        state := s_LATCH
      }
    }
    is(s_LATCH) {
      when(enable_valid_R) {
        when(enable_R.control) {
          state := s_VALIDOUT
          ValidOut()
        }.otherwise {
          state := s_IDLE

          indata_R := ControlBundle.default
          indata_valid_R := false.B

          enable_valid_R := false.B
        }
      }
    }
    is(s_VALIDOUT) {
      when(IsOutReady()) {
        state := s_IDLE

        indata_R := ControlBundle.default
        indata_valid_R := false.B

        Reset()
      }
    }
  }

  /*==========================================*
   *             WIRING OUTPUT                *
   *==========================================*/

  // Wire up Outputs
  for (i <- 0 until NumOuts) {
    io.Out(i).bits <> indata_R
  }

}
