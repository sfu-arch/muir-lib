package dandelion.node

import chisel3._
import chisel3.iotesters.{ChiselFlatSpec, Driver, OrderedDecoupledHWIOTester, PeekPokeTester}
import chisel3.Module
import chisel3.testers._
import chisel3.util._
import org.scalatest.{FlatSpec, Matchers}
import dandelion.config._
import dandelion.interfaces._
import util._
import utility.UniformPrintfs
import chisel3.util.experimental.BoringUtils


class SourceNodeIO(Enable : Boolean = false)(implicit p: Parameters)  extends Bundle {
  val enable =  Flipped(new Bool)
  override def cloneType = new SinkNodeIO(Enable).asInstanceOf[this.type]

}

class SourceNode(Enable: Boolean = false) (implicit p: Parameters, name: sourcecode.Name, file: sourcecode.File) extends Module  with UniformPrintfs {

  override lazy val io = IO(new SourceNodeIO(Enable))

  val node_name = name.value
  val module_name = file.value.split("/").tail.last.split("\\.").head.capitalize
  val (cycleCount, _) = Counter(true.B, 32 * 1024)
  val sourceVal = Wire (UInt(6.W))
  sourceVal := 7.U
  BoringUtils.addSource(sourceVal, "uniqueId")
  /*===========================================*
   *            Registers                      *
   *===========================================*/

  /*===============================================*
   *            Latch inputs. Wire up output       *
   *===============================================*/


  /*============================================*
   *            State Machine                   *
   *============================================*/

}



