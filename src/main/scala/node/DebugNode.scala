package dandelion.node

import chisel3._
import chipsalliance.rocketchip.config._
import chisel3.Module
import chisel3.testers._
import chisel3.util._
import org.scalatest.{FlatSpec, Matchers}
import dandelion.config._
import dandelion.interfaces._
import util._
import utility.UniformPrintfs


class DebugNodeIO(Selection: Boolean = false, Nodes : List[Int], NumNodes : Int)
                 (Time_B : Boolean = false, Time : List[Int] ,  BB_B : Boolean = false , BB : List[Int])
                 (implicit p: Parameters)  extends Bundle {

  val Enable = Vec(NumNodes, (new Bool))

}

class DebugNode(Selection: Boolean = false, Nodes : List[Int] , NumNodes : Int = 0)
               (Time_B : Boolean = false, Time : List[Int] ,  BB_B : Boolean = false , BB : List[Int])
                 (implicit p: Parameters, name: sourcecode.Name, file: sourcecode.File) extends Module  with UniformPrintfs {

  val io = IO(new DebugNodeIO(Selection, Nodes, NumNodes = 0)(Time_B , Time ,  BB_B , BB))

  val node_name = name.value
  val module_name = file.value.split("/").tail.last.split("\\.").head.capitalize
  val (cycleCount, _) = Counter(true.B, 32 * 1024)

  /*===========================================*
   *            Registers                      *
   *===========================================*/
  val s_IDLE :: s_Change :: Nil = Enum(2)
  val state = RegInit(s_IDLE)
  val Change = RegInit(false.B)
  /*===============================================*
   *            Latch inputs. Wire up output       *
   *===============================================*/


  /*============================================*
   *            State Machine                   *
   *============================================*/
  switch(state) {

    is(s_IDLE) {
      if (Time_B){
        when(cycleCount > Time.head.U  && cycleCount < Time.last.U) {
          when(Change) {
            if (BB_B){}

            else{
              io.Enable.foreach(_ := true.B)
              state := s_Change}
          }
        }.otherwise{
          when(Change) {
            if (BB_B){}

            else{
              io.Enable.foreach(_ := true.B)
              state := s_Change}
          }

        }
      }

    }


    is(s_Change) {


//      when(){

        //Reset state
//        state := s_IDLE

//      }
    }
  }



}



