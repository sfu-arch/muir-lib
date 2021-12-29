package dandelion.node

import chisel3._
import chisel3.Module
import chipsalliance.rocketchip.config._
import dandelion.config._
import dandelion.interfaces.{ControlBundle, DataBundle}
import util._
import utility.UniformPrintfs
import dandelion.config._

class ZextNodeIO(val src: Int, val des: Int, val nout: Int)
                (implicit p: Parameters) extends AccelBundle()(p) {

  val Input = Flipped(Decoupled(new DataBundle()))
  val enable = Flipped(Decoupled(new ControlBundle()))
  val Out = Vec(nout, Decoupled(new DataBundle()))

  override def cloneType = new ZextNodeIO(src, des, nout).asInstanceOf[this.type]

}

class ZextNode(val SrcW: Int = 0, val DesW: Int = 0, val NumOuts: Int = 1, val ID: Int = 0)
              (implicit val p: Parameters,
               name: sourcecode.Name,
               file: sourcecode.File)
  extends Module with HasAccelParams with UniformPrintfs {

  lazy val io = IO(new ZextNodeIO(SrcW, DesW, NumOuts))

  // Printf debugging
  val node_name = name.value
  val module_name = file.value.split("/").tail.last.split("\\.").head.capitalize

  override val printfSigil = "[" + module_name + "] " + node_name + ": " + ID + " "
  val (cycleCount, _) = Counter(true.B, 32 * 1024)

  /*===========================================*
   *            Registers                      *
   *===========================================*/
  // Right Input
  val input_R = RegInit(DataBundle.default)
  val input_valid_R = RegInit(false.B)

  val enable_R = RegInit(ControlBundle.default)
  val enable_valid_R = RegInit(false.B)

  val output_valid_R = Seq.fill(NumOuts)(RegInit(false.B))

  val fire_R = Seq.fill(NumOuts)(RegInit(false.B))

  val task_input = (io.enable.bits.taskID | enable_R.taskID)

  /*===============================================*
   *            Latch inputs. Wire up output       *
   *===============================================*/


  io.Input.ready := ~input_valid_R
  when(io.Input.fire()) {
    input_R <> io.Input.bits
    input_valid_R := true.B
  }

  io.enable.ready := ~enable_valid_R
  when(io.enable.fire()) {
    enable_R <> io.enable.bits
    enable_valid_R := true.B
  }

  // Defalut values for output

  val output_data = Mux(io.Input.fire, io.Input.bits, input_R)
  for (i <- 0 until NumOuts) {
    io.Out(i).bits <> output_data
    io.Out(i).valid <> output_valid_R(i)
  }

  for (i <- 0 until NumOuts) {
    when(io.Out(i).fire) {
      output_valid_R(i) := false.B
      fire_R(i) := true.B
    }
  }

  val fire_mask = (fire_R zip io.Out.map(_.fire)).map { case (a, b) => a | b }

  def IsEnableValid(): Bool = {
    enable_valid_R || io.enable.fire
  }

  def IsInputValid(): Bool = {
    input_valid_R || io.Input.fire
  }


  /*============================================*
   *            ACTIONS (possibly dangerous)    *
   *============================================*/
  val s_idle :: s_fire :: Nil = Enum(2)
  val state = RegInit(s_idle)

  switch(state) {
    is(s_idle) {

      when(IsEnableValid() && IsInputValid()) {

        io.Out.foreach(_.valid := true.B)
        output_valid_R.foreach(_ := true.B)

        state := s_fire

        if (log) {
          printf(p"[LOG] [${module_name}] " +
            p"[TID: ${task_input}] " +
            p"[ZEXT]" +
            p"[${node_name}] " +
            p"[Pred: ${enable_R.control}] " +
            p"[Out: ${output_data.data}] " +
            p"[Cycle: ${cycleCount}]\n")
        }
      }
    }

    is(s_fire) {
      when(fire_mask.reduce(_ & _)) {

        input_R := DataBundle.default
        input_valid_R := false.B

        enable_R := ControlBundle.default
        enable_valid_R := false.B

        output_valid_R.foreach(_ := false.B)

        fire_R.foreach(_ := false.B)

        state := s_idle
      }
    }
  }


}

