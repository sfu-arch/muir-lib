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


class DebugNodeIO(Selection: Boolean = false, Nodes : List[Int], NumNodes : Int)(implicit p: Parameters)  extends Bundle {

  val Enable = Vec(NumNodes, Decoupled(new DataBundle))

  //override def cloneType = new DebugNodeIO(Selection, Nodes).asInstanceOf[this.type]

}

class DebugNode(Selection: Boolean = false, Nodes : List[Int] , NumNodes : Int = 0)
                 (implicit p: Parameters, name: sourcecode.Name, file: sourcecode.File) extends Module  with UniformPrintfs {

  override lazy val io = IO(new DebugNodeIO(Selection, Nodes, NumNodes = 0))

  val node_name = name.value
  val module_name = file.value.split("/").tail.last.split("\\.").head.capitalize
  val (cycleCount, _) = Counter(true.B, 32 * 1024)

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
