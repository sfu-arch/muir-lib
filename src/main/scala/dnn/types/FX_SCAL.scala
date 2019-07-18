package dnn.types

import FPU.{FPMAC, FType}
import chisel3._
import chisel3.iotesters.{ChiselFlatSpec, Driver, OrderedDecoupledHWIOTester, PeekPokeTester}
import chisel3.Module
import chisel3.experimental.FixedPoint
import chisel3.testers._
import chisel3.util._
import org.scalatest.{FlatSpec, Matchers}
import config._
import interfaces._
import muxes._
import util._
import node._
import dnn._
//import FPOperator_SCAL._

trait OperatorSCAL[T, T2] {
  def magic(l: T, r: T2, start: Bool, opcode: String)(implicit p: Parameters): T

  def getfns(l: Shapes, r: Shapes, start: Bool, opcode: String)(implicit p: Parameters): Array[(Int, Shapes)]

}

object OperatorSCAL {

  implicit object FXmatNxN_FXvecN extends OperatorSCAL[FXmatNxN, FixedPoint] {
    def magic(l: FXmatNxN, r: FixedPoint, start: Bool, opcode: String)(implicit p: Parameters): FXmatNxN = {
      val x = Wire(new FXmatNxN(l.N, l.fraction))
      val flatvec = l.toVecUInt( )
      val FU = Module(new NCycle_SCAL(UInt(l.data(0)(0).getWidth.W), flatvec.length, l.N, opcode))
      FU.io.activate := start
      l.toVecUInt( ) zip FU.io.input_vec foreach { case (a, b) => b := a }
      FU.io.scalar := r
      x.fromVecUInt(FU.io.output)
      x
    }

    def getfns(l: Shapes, r: Shapes, start: Bool, opcode: String)(implicit p: Parameters): Array[(Int, Shapes)] = {
      Array(
        0 -> magic(l.asInstanceOf[FXmatNxN], r.asInstanceOf[FixedPoint], start, opcode),
      )
    }
  }

  implicit object matNxN_vecN extends OperatorSCAL[matNxN, UInt] {
    def magic(l: matNxN, r: UInt, start: Bool, opcode: String)(implicit p: Parameters): matNxN = {
      val x = Wire(new matNxN(l.N))
      val flatvec = l.toVecUInt( )
      val FU = Module(new NCycle_SCAL(UInt(l.data(0)(0).getWidth.W), flatvec.length, lanes = l.N, opcode))
      FU.io.activate := start
      flatvec zip FU.io.input_vec foreach { case (a, b) => b := a }
      FU.io.scalar := r
      x.fromVecUInt(FU.io.output)
      x
    }

    def getfns(l: Shapes, r: Shapes, start: Bool, opcode: String)(implicit p: Parameters): Array[(Int, Shapes)] = {
      Array(
        0 -> magic(l.asInstanceOf[matNxN], r.asInstanceOf[UInt], start, opcode),
      )
    }
  }

}

object SCAL_fns {

  def getfns(l: => Shapes, r: => Shapes, start: Bool, opcode: String)(implicit p: Parameters): Array[(Int, Shapes)] = {
    val lclass = l.getClass.getSimpleName
    val rclass = r.getClass.getSimpleName
    val parse = "(.*)(mat|vec|Bit)([a-zA-Z]*)".r

    val parse(ltype, lshape, lsize) = lclass
    print(ltype + lshape + lsize)

    //    Check the type of left and right operand are the same
    if (ltype == "FX") require(rclass == "FixedPoint")
    if (ltype == "") require((rclass == "UInt" | (rclass == "SInt")))
    if (ltype == "FP") require(rclass == "FloatingPoint")

    //    Check that the left operand is matrix and the right operand is basic type.
    require(lshape == "mat")

    val aluOp =
      if (ltype == "FX") {
        implicitly[OperatorSCAL[FXmatNxN, FixedPoint]].getfns(l, r, start, opcode)
      } else if (ltype == "") {
        implicitly[OperatorSCAL[matNxN, UInt]].getfns(l, r, start, opcode)
      } else { // You should never get here. Just a default.
        require(0 == 1, "Unsupported type of SCAL Operands")
        implicitly[OperatorSCAL[matNxN, UInt]].getfns(l, r, start, opcode)
      }
    aluOp
  }
}




