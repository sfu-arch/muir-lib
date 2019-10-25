package dnnnode

import Chisel.Enum
import chisel3.util._
import chisel3.{Flipped, Module, UInt, _}
import config.{Parameters, XLEN}
import dnn.types.{OperatorDot, OperatorReduction}
import muxes._
import interfaces.CustomDataBundle
import node.{HandShakingIONPS, HandShakingNPS, Shapes, matNxN, vecN}

class ShapeShifterIO[gen <: vecN, gen2 <: Shapes](NumIns: Int, NumOuts: Int)(shapeIn: => gen)(shapeOut: => gen2)(implicit p: Parameters)
  extends HandShakingIONPS(NumOuts)(new CustomDataBundle(UInt(shapeOut.getWidth.W))) {
  val in = Vec(NumIns, Flipped(Decoupled(new CustomDataBundle(UInt(shapeIn.getWidth.W)))))

  override def cloneType = new ShapeShifterIO(NumIns, NumOuts)(shapeIn)(shapeOut).asInstanceOf[this.type]
}

class ShapeShifter[L <: vecN, K <: Shapes](NumIns: Int, NumOuts: Int, ID: Int)(shapeIn: => L)(shapeOut: => K)(implicit p: Parameters)
  extends HandShakingNPS(NumOuts, ID)(new CustomDataBundle(UInt(shapeOut.getWidth.W)))(p) {
  override lazy val io = IO(new ShapeShifterIO(NumIns, NumOuts)(shapeIn)(shapeOut))


  /*===========================================*
   *            Registers                      *
   *===========================================*/
  val dataIn_R = RegInit(VecInit(Seq.fill(NumIns)(CustomDataBundle.default(0.U(shapeIn.getWidth.W)))))
  val dataIn_valid_R = RegInit(VecInit(Seq.fill(NumIns)(false.B)))
  val dataIn_Wire = Wire(Vec(NumIns, Vec(shapeIn.N, UInt(xlen.W))))
  val data_out_R = RegInit(CustomDataBundle.default(0.U(shapeOut.getWidth.W)))

  val ratio = shapeIn.data.size / NumIns //8

  val input_data = dataIn_R.map(_.data.asUInt())

  for (i <- 0 until NumIns) {
    for (j <- 0 until shapeIn.N) {
      val index = ((j + 1) * xlen) - 1
      dataIn_Wire(i)(j) := input_data(i)(index, j * xlen) // 3, 24
    }
  }

  val dataOut_Wire = Wire(Vec(ratio, Vec(NumIns * NumIns, UInt(xlen.W)))) //8, (9)

  for (i <- 0 until ratio) { //8
    for (j <- 0 until NumIns) {
      for (k <- 0 until NumIns) {
        dataOut_Wire(i)(j * NumIns + k) := dataIn_Wire(j)(i * NumIns + k)
      }
    }
  }

  val buffer = Module(new Queue(new CustomDataBundle(UInt(shapeOut.getWidth.W)), 40))
  val mux = Module(new Mux(new CustomDataBundle(UInt(shapeOut.getWidth.W)), 8))
  val countOn = RegInit(init = false.B)
  val (cnt, wrap) = Counter(countOn, 8)

  mux.io.sel := cnt
  mux.io.en := countOn
  for (i <- 0 until ratio) {
    mux.io.inputs(i).data := dataOut_Wire(i).asTypeOf(CustomDataBundle(UInt(shapeOut.getWidth.W))).data
    mux.io.inputs(i).valid := dataIn_R.map(_.valid).reduceLeft(_ && _)
    mux.io.inputs(i).predicate := dataIn_R.map(_.valid).reduceLeft(_ && _)
    mux.io.inputs(i).taskID := dataIn_R.map(_.taskID).reduceLeft(_ | _)
  }

  buffer.io.enq.bits <> mux.io.output
  buffer.io.enq.valid := countOn
  buffer.io.enq.bits.predicate := true.B

  val s_idle :: s_BufferWrite :: s_Transfer :: s_Finish :: Nil = Enum(4)
  val state = RegInit(s_idle)

  /*===============================================*
   *            Latch inputs. Wire up left       *
   *===============================================*/
  // Predicate register
  val pred_R = RegInit(init = false.B)

  //printfInfo("start: %x\n", start)
  for (i <- 0 until NumIns) {
    io.in(i).ready := ~dataIn_valid_R(i)
    when(io.in(i).fire()) {
      dataIn_R(i).data := io.in(i).bits.data
      dataIn_valid_R(i) := true.B
    }
  }

//  countOn := (dataIn_valid_R.reduceLeft(_ && _)) && (buffer.io.enq.ready)

  for (i <- 0 until NumOuts) {
    io.Out(i).valid := buffer.io.deq.valid
    io.Out(i).bits := buffer.io.deq.bits
    io.Out(i).bits.taskID := 0.U
    io.Out(i).bits.predicate := true.B //enable_R.control
  }

//  buffer.io.deq.ready := false.B
  data_out_R <> buffer.io.deq.bits
  buffer.io.deq.ready := io.Out.map(_.ready).reduceLeft(_ && _)
//  out_valid_R.foreach(_ := buffer.io.deq.valid)

  switch(state) {
    is(s_idle) { //0
      when(dataIn_valid_R.reduceLeft(_ && _) && buffer.io.enq.ready) {
        state := s_BufferWrite
        countOn := true.B
      }
    }
    is(s_BufferWrite) { //1
      when(wrap) {
        state := s_idle
        //        data_out_R := buffer.io.deq.bits
        countOn := false.B
        dataIn_R.foreach(_ := CustomDataBundle.default(0.U(shapeIn.getWidth.W)))
        dataIn_valid_R.foreach(_ := false.B)
      }
    }
  }

   /* is(s_Transfer) {  //2
//      buffer.io.deq.ready := true.B
      when(buffer.io.deq.fire) {
        state := s_Finish
        ValidOut()
      }
    }
    is(s_Finish) {
      when(IsOutReady()) {
        when(buffer.io.deq.valid) {
          state := s_Transfer
          data_out_R := buffer.io.deq.bits
          Reset()
        }.otherwise {
//          dataIn_R.foreach(_ := CustomDataBundle.default(0.U(shapeIn.getWidth.W)))
          data_out_R := CustomDataBundle.default(0.U(shapeOut.getWidth.W))
//          dataIn_valid_R.foreach(_ := false.B)
          Reset()
          state := s_idle
        }
      }
    }*/
//  }

  //  when(IsOutReady() && wrap) {
  //    for (i <- 0 until NumIns) {
  //      dataIn_R(i) := CustomDataBundle.default(0.U(shapeIn.getWidth.W))
  //    }
  //    Reset()
  //    countOn := false.B
  //  }
  //  when(!wrap) {
  //    countOn := true.B
  //    ValidOut()
  //  }

  //  printf(p"\n Left ${io.LeftIO.bits.data} Right: ${io.RightIO.bits.data} Output: ${reduceNode.io.Out(0).bits.data}")
}


