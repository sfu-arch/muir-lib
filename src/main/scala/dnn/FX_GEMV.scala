package dnn

import FPU.{FPUALU, FType}
import chisel3._
import chisel3.iotesters.{ChiselFlatSpec, Driver, OrderedDecoupledHWIOTester, PeekPokeTester}
import chisel3.Module
import chisel3.testers._
import chisel3.util._
import org.scalatest.{FlatSpec, Matchers}
import config._
import interfaces._
import muxes._
import util._
import node._

object operation_GEMV {

  trait OperatorMatVec[T, T2] {
    def addition(l: T, r: T2)(implicit p: Parameters): T

    def subtraction(l: T, r: T2)(implicit p: Parameters): T

    def multiplication(l: T, r: T2)(implicit p: Parameters): T2

  }

  object OperatorMatVec {

    implicit object FXmatNxN_FXvecN extends OperatorMatVec[FXmatNxN, FXvecN] {
      def addition(l: FXmatNxN, r: FXvecN)(implicit p: Parameters): FXmatNxN = {
        val x = Wire(new FXmatNxN(l.N, l.fraction))
        for (i <- 0 until l.N) {
          for (j <- 0 until l.N) {
            x.data(i)(j) := l.data(i)(j) + r.data(j)
          }
        }
        x
      }

      def subtraction(l: FXmatNxN, r: FXvecN)(implicit p: Parameters): FXmatNxN = {
        val x = Wire(new FXmatNxN(l.N, l.fraction))
        for (i <- 0 until l.N) {
          for (j <- 0 until l.N) {
            x.data(i)(j) := l.data(i)(j) - r.data(j)
          }
        }
        x
      }

      def multiplication(l: FXmatNxN, r: FXvecN)(implicit p: Parameters): FXvecN = {
        val x = Wire(new FXvecN(r.N, r.fraction))
        val products = for (i <- 0 until l.N) yield {
          for (j <- 0 until l.N) yield {
            l.data(i)(j) * r.data(j)
          }
        }
        for (i <- 0 until l.N) {
          x.data(i) := products(i).reduceLeft(_ + _)
        }
        x
      }

    }

    implicit object matNxN_vecN extends OperatorMatVec[matNxN, vecN] {
      def addition(l: matNxN, r: vecN)(implicit p: Parameters): matNxN = {
        val x = Wire(new matNxN(l.N))
        for (i <- 0 until l.N) {
          for (j <- 0 until l.N) {
            x.data(i)(j) := l.data(i)(j) + r.data(j)
          }
        }
        x
      }

      def subtraction(l: matNxN, r: vecN)(implicit p: Parameters): matNxN = {
        val x = Wire(new matNxN(l.N))
        for (i <- 0 until l.N) {
          for (j <- 0 until l.N) {
            x.data(i)(j) := l.data(i)(j) - r.data(j)
          }
        }
        x
      }

      def multiplication(l: matNxN, r: vecN)(implicit p: Parameters): vecN = {
        val x = Wire(new vecN(l.N))
        val products = for (i <- 0 until l.N) yield {
          for (j <- 0 until l.N) yield {
            l.data(i)(j) * r.data(j)
          }
        }
        for (i <- 0 until l.N) {
          x.data(i) := products(i).reduceLeft(_ + _)
        }
        x
      }
    }


  }

  def getfns(l: => Numbers, r: => Numbers)(implicit p: Parameters): Array[(Int, Numbers)] = {
    val lclass = l.getClass.getSimpleName
    val rclass = r.getClass.getSimpleName
    val parse = "(.*)(mat|vec|Bit)([a-zA-Z]*)".r

    val parse(ltype, lshape, lsize) = lclass
    val parse(rtype, rshape, rsize) = rclass

    print(ltype + rtype + lshape + rshape + lsize + rsize)

    //    Check the type of left and right operand are the same
    require(ltype == rtype)
    //    Check that the left operand is matrix and the right operand is vector.
    require(lshape == "mat" && (rshape == "vec" || rshape == "Bits"))

    val aluOp =
      if (ltype == "FX") {
        Array(
          Mat_X_OpCode.Add -> (implicitly[OperatorMatVec[FXmatNxN, FXvecN]
            ].
            addition(l.asInstanceOf[FXmatNxN], r.asInstanceOf[FXvecN])
            ),
          Mat_X_OpCode.Sub -> (implicitly[OperatorMatVec[FXmatNxN, FXvecN]].
            subtraction(l.asInstanceOf[FXmatNxN], r.asInstanceOf[FXvecN]))
          ,
          Mat_X_OpCode.Mul -> (implicitly[OperatorMatVec[FXmatNxN, FXvecN]].
            multiplication(l.asInstanceOf[FXmatNxN], r.asInstanceOf[FXvecN]))
        )
      } else if (ltype == "") {
        Array(
          Mat_X_OpCode.Add -> (implicitly[OperatorMatVec[matNxN, vecN]
            ].
            addition(l.asInstanceOf[matNxN], r.asInstanceOf[vecN])
            ),
          Mat_X_OpCode.Sub -> (implicitly[OperatorMatVec[matNxN, vecN]].
            subtraction(l.asInstanceOf[matNxN], r.asInstanceOf[vecN]))
          ,
          Mat_X_OpCode.Mul -> (implicitly[OperatorMatVec[matNxN, vecN]].
            multiplication(l.asInstanceOf[matNxN], r.asInstanceOf[vecN]))
        )
      } else {
        Array(
          0 -> (implicitly[OperatorMatVec[matNxN, vecN]
            ].
            addition(l.asInstanceOf[matNxN], r.asInstanceOf[vecN])
            ),
          1 -> (implicitly[OperatorMatVec[matNxN, vecN]].
            multiplication(l.asInstanceOf[matNxN], r.asInstanceOf[vecN]))
        )
      }
    aluOp
  }
}




