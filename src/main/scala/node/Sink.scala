package dandelion.node

import chisel3._
import chisel3.iotesters.{ChiselFlatSpec, Driver, OrderedDecoupledHWIOTester, PeekPokeTester}
import chisel3.Module
import chisel3.testers._
import chisel3.util._
import chisel3.util.experimental.BoringUtils
import org.scalatest.{FlatSpec, Matchers}
import dandelion.config._
import dandelion.interfaces._
import util._
import utility.UniformPrintfs


class SinkNodeIO(Enable : Boolean = false)(implicit p: Parameters)  extends Bundle {
  val enable =  Flipped(new Bool)
  override def cloneType = new SinkNodeIO(Enable).asInstanceOf[this.type]

}

class SinkNode(Enable: Boolean = false) (implicit p: Parameters, name: sourcecode.Name, file: sourcecode.File) extends Module  with UniformPrintfs {

  override lazy val io = IO(new SinkNodeIO(Enable))

  val node_name = name.value
  val module_name = file.value.split("/").tail.last.split("\\.").head.capitalize
  val (cycleCount, _) = Counter(true.B, 32 * 1024)
  val sinkVal = Wire (UInt(6.W))
  sinkVal := 4.U
  BoringUtils.addSink(sinkVal, "uniqueId")

  /*===========================================*
   *            Registers                      *
   *===========================================*/

  /*===============================================*
   *            Latch inputs. Wire up output       *
   *===============================================*/
  if (Enable){
  printf("[LOG] " + "[" + module_name + "] " + "[TID->%d] [Sink] " +
    node_name + ": Output fired\n", sinkVal)}
  /*============================================*
   *            State Machine                   *
   *============================================*/



}



