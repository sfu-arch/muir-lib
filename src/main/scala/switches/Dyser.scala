package switches

/**
  * Created by vnaveen0 on 9/9/17.
  */

import chisel3._
import chisel3.util._
import config._
import muxes._
import scala.math._
import interfaces._
import muxes._
import node._
import util._



  /**
  * Note: This file should be autogenerated by the compiler
  */


  /**
  * Note: For example, io.inN.ready is connected to DMuxE, DMuxW etc.
    * The Compiler has to ensure that the other muxes are disabled i.e
    * En = false for the corresponding DyserMux
    * When EN= false, the corresponding output will always be invalid
  */

  //--------------------------------------------------
abstract class DyserIO(implicit val p: Parameters) extends Module with CoreParams {
  val io = IO(new Bundle {

    val inN = Flipped(Decoupled(new DataBundle))
    val inE = Flipped(Decoupled(new DataBundle))
    val inW = Flipped(Decoupled(new DataBundle))
    val inS = Flipped(Decoupled(new DataBundle))
    val inNE = Flipped(Decoupled(new DataBundle))

    val outN = Decoupled(new DataBundle)
    val outE = Decoupled(new DataBundle)
    val outW = Decoupled(new DataBundle)
    val outS = Decoupled(new DataBundle)
    val outNE = Decoupled(new DataBundle)
    val outNW = Decoupled(new DataBundle)
    val outSE = Decoupled(new DataBundle)
    val outSW = Decoupled(new DataBundle)
  })
}



class Dyser (implicit p: Parameters) extends DyserIO()(p) {
  //Five 4:1 Muxes
  val DMuxN = Module(new DyserMux(NInputs = 4, Sel = 1, En = false.B)(p))
  val DMuxE = Module(new DyserMux(NInputs = 4, Sel = 1, En = false.B)(p))
  val DMuxW = Module(new DyserMux(NInputs = 4, Sel = 1, En = false.B)(p))
  val DMuxS = Module(new DyserMux(NInputs = 4, Sel = 1, En = true.B)(p))
  val DMuxNE = Module(new DyserMux(NInputs = 4, Sel = 1, En =false.B)(p))

  //Three 5:1 Muxes

  val DMuxNW = Module(new DyserMux(NInputs = 5, Sel = 1, En = false.B)(p))
  val DMuxSW = Module(new DyserMux(NInputs = 5, Sel = 1, En = false.B)(p))
  val DMuxSE = Module(new DyserMux(NInputs = 5, Sel = 1, En = false.B)(p))


  //--------------------------------------------------

  //DMuxN

  // Inputs

  // E ->DMuxN(0)
  io.inE.ready := DMuxN.io.in(0).ready
  DMuxN.io.in(0).valid := io.inE.valid
  DMuxN.io.in(0).bits := io.inE.bits

  // W ->DMuxN(1)
  io.inW.ready := DMuxN.io.in(1).ready
  DMuxN.io.in(1).valid := io.inW.valid
  DMuxN.io.in(1).bits := io.inW.bits

  // S ->DMuxN(2)
  io.inS.ready := DMuxN.io.in(2).ready
  DMuxN.io.in(2).valid := io.inS.valid
  DMuxN.io.in(2).bits := io.inS.bits

  // NE ->DMuxN(3)
  io.inNE.ready := DMuxN.io.in(3).ready
  DMuxN.io.in(3).valid := io.inNE.valid
  DMuxN.io.in(3).bits := io.inNE.bits


  // Output

  io.outN.bits := DMuxN.io.out.bits
  io.outN.valid := DMuxN.io.out.valid
  DMuxN.io.out.ready := io.outN.ready

  //--------------------------------------------------
  //--------------------------------------------------
  //DMuxE

  // Inputs

  // N ->DMuxE(0)
  io.inN.ready := DMuxE.io.in(0).ready
  DMuxE.io.in(0).valid := io.inN.valid
  DMuxE.io.in(0).bits := io.inN.bits

  // W ->DMuxE(1)
  io.inW.ready := DMuxE.io.in(1).ready
  DMuxE.io.in(1).valid := io.inW.valid
  DMuxE.io.in(1).bits := io.inW.bits

  // S ->DMuxE(2)
  io.inS.ready := DMuxE.io.in(2).ready
  DMuxE.io.in(2).valid := io.inS.valid
  DMuxE.io.in(2).bits := io.inS.bits

  // NE ->DMuxE(3)
  io.inNE.ready := DMuxE.io.in(3).ready
  DMuxE.io.in(3).valid := io.inNE.valid
  DMuxE.io.in(3).bits := io.inNE.bits


  // Output

  //  DMuxE ->
  io.outE.bits := DMuxE.io.out.bits
  io.outE.valid := DMuxE.io.out.valid
  DMuxE.io.out.ready := io.outE.ready

  //--------------------------------------------------
  //--------------------------------------------------
  //DMuxW

  // Inputs

  // N ->DMuxW(0)
  io.inN.ready := DMuxW.io.in(0).ready
  DMuxW.io.in(0).valid := io.inN.valid
  DMuxW.io.in(0).bits := io.inN.bits

  // E ->DMuxW(1)
  io.inE.ready := DMuxW.io.in(1).ready
  DMuxW.io.in(1).valid := io.inE.valid
  DMuxW.io.in(1).bits := io.inE.bits

  // S ->DMuxW(2)
  io.inS.ready := DMuxW.io.in(2).ready
  DMuxW.io.in(2).valid := io.inS.valid
  DMuxW.io.in(2).bits := io.inS.bits

  // NE ->DMuxW(3)
  io.inNE.ready := DMuxW.io.in(3).ready
  DMuxW.io.in(3).valid := io.inNE.valid
  DMuxW.io.in(3).bits := io.inNE.bits


  // Output

  //  DMuxW ->
  io.outW.bits := DMuxW.io.out.bits
  io.outW.valid := DMuxW.io.out.valid
  DMuxW.io.out.ready := io.outW.ready

  //--------------------------------------------------


  //--------------------------------------------------
  //DMuxS

  // Inputs

  // N ->DMuxS(0)
  io.inN.ready := DMuxS.io.in(0).ready
  DMuxS.io.in(0).valid := io.inN.valid
  DMuxS.io.in(0).bits := io.inN.bits

  // E ->DMuxS(1)
  io.inE.ready := DMuxS.io.in(1).ready
  DMuxS.io.in(1).valid := io.inE.valid
  DMuxS.io.in(1).bits := io.inE.bits

  // W ->DMuxS(2)
  io.inW.ready := DMuxS.io.in(2).ready
  DMuxS.io.in(2).valid := io.inW.valid
  DMuxS.io.in(2).bits := io.inW.bits

  // NE ->DMuxS(3)
  io.inNE.ready := DMuxS.io.in(3).ready
  DMuxS.io.in(3).valid := io.inNE.valid
  DMuxS.io.in(3).bits := io.inNE.bits


  // Output

  //  DMuxS ->
  io.outS.bits := DMuxS.io.out.bits
  io.outS.valid := DMuxS.io.out.valid
  DMuxS.io.out.ready := io.outS.ready

  //--------------------------------------------------


  //--------------------------------------------------
  //DMuxNE

  // Inputs

  // N ->DMuxNE(0)
  io.inN.ready := DMuxNE.io.in(0).ready
  DMuxNE.io.in(0).valid := io.inN.valid
  DMuxNE.io.in(0).bits := io.inN.bits

  // E ->DMuxNE(1)
  io.inE.ready := DMuxNE.io.in(1).ready
  DMuxNE.io.in(1).valid := io.inE.valid
  DMuxNE.io.in(1).bits := io.inE.bits

  // W ->DMuxNE(2)
  io.inW.ready := DMuxNE.io.in(2).ready
  DMuxNE.io.in(2).valid := io.inW.valid
  DMuxNE.io.in(2).bits := io.inW.bits

  // S ->DMuxNE(3)
  io.inS.ready := DMuxNE.io.in(3).ready
  DMuxNE.io.in(3).valid := io.inS.valid
  DMuxNE.io.in(3).bits := io.inS.bits


  // Output

  //  DMuxNE ->
  io.outNE.bits := DMuxNE.io.out.bits
  io.outNE.valid := DMuxNE.io.out.valid
  DMuxNE.io.out.ready := io.outNE.ready

  //--------------------------------------------------


  //--------------------------------------------------
  //DMuxNW

  // Inputs

  // N ->DMuxNW(0)
  io.inN.ready := DMuxNW.io.in(0).ready
  DMuxNW.io.in(0).valid := io.inN.valid
  DMuxNW.io.in(0).bits := io.inN.bits

  // E ->DMuxNW(1)
  io.inE.ready := DMuxNW.io.in(1).ready
  DMuxNW.io.in(1).valid := io.inE.valid
  DMuxNW.io.in(1).bits := io.inE.bits

  // W ->DMuxNW(2)
  io.inW.ready := DMuxNW.io.in(2).ready
  DMuxNW.io.in(2).valid := io.inW.valid
  DMuxNW.io.in(2).bits := io.inW.bits

  // S ->DMuxNW(3)
  io.inS.ready := DMuxNW.io.in(3).ready
  DMuxNW.io.in(3).valid := io.inS.valid
  DMuxNW.io.in(3).bits := io.inS.bits

  // NE ->DMuxNW(4)
  io.inNE.ready := DMuxNW.io.in(4).ready
  DMuxNW.io.in(4).valid := io.inNE.valid
  DMuxNW.io.in(4).bits := io.inNE.bits


  // Output

  //  DMuxNW ->
  io.outNW.bits := DMuxNW.io.out.bits
  io.outNW.valid := DMuxNW.io.out.valid
  DMuxNW.io.out.ready := io.outNW.ready

  //--------------------------------------------------


  //--------------------------------------------------
  //DMuxSE

  // Inputs

  // N ->DMuxSE(0)
  io.inN.ready := DMuxSE.io.in(0).ready
  DMuxSE.io.in(0).valid := io.inN.valid
  DMuxSE.io.in(0).bits := io.inN.bits

  // E ->DMuxSE(1)
  io.inE.ready := DMuxSE.io.in(1).ready
  DMuxSE.io.in(1).valid := io.inE.valid
  DMuxSE.io.in(1).bits := io.inE.bits

  // W ->DMuxSE(2)
  io.inW.ready := DMuxSE.io.in(2).ready
  DMuxSE.io.in(2).valid := io.inW.valid
  DMuxSE.io.in(2).bits := io.inW.bits

  // S ->DMuxSE(3)
  io.inS.ready := DMuxSE.io.in(3).ready
  DMuxSE.io.in(3).valid := io.inS.valid
  DMuxSE.io.in(3).bits := io.inS.bits

  // NE ->DMuxSE(4)
  io.inNE.ready := DMuxSE.io.in(4).ready
  DMuxSE.io.in(4).valid := io.inNE.valid
  DMuxSE.io.in(4).bits := io.inNE.bits


  // Output

  //  DMuxSE ->
  io.outSE.bits := DMuxSE.io.out.bits
  io.outSE.valid := DMuxSE.io.out.valid
  DMuxSE.io.out.ready := io.outSE.ready

  //--------------------------------------------------


  //--------------------------------------------------
  //DMuxSW

  // Inputs

  // N ->DMuxSW(0)
  io.inN.ready := DMuxSW.io.in(0).ready
  DMuxSW.io.in(0).valid := io.inN.valid
  DMuxSW.io.in(0).bits := io.inN.bits

  // E ->DMuxSW(1)
  io.inE.ready := DMuxSW.io.in(1).ready
  DMuxSW.io.in(1).valid := io.inE.valid
  DMuxSW.io.in(1).bits := io.inE.bits

  // W ->DMuxSW(2)
  io.inW.ready := DMuxSW.io.in(2).ready
  DMuxSW.io.in(2).valid := io.inW.valid
  DMuxSW.io.in(2).bits := io.inW.bits

  // S ->DMuxSW(3)
  io.inS.ready := DMuxSW.io.in(3).ready
  DMuxSW.io.in(3).valid := io.inS.valid
  DMuxSW.io.in(3).bits := io.inS.bits

  // NE ->DMuxSW(4)
  io.inNE.ready := DMuxSW.io.in(4).ready
  DMuxSW.io.in(4).valid := io.inNE.valid
  DMuxSW.io.in(4).bits := io.inNE.bits


  // Output

  //  DMuxSW ->
  io.outSW.bits := DMuxSW.io.out.bits
  io.outSW.valid := DMuxSW.io.out.valid
  DMuxSW.io.out.ready := io.outSW.ready

  //--------------------------------------------------


}
