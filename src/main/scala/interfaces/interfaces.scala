package dandelion.interfaces


import chisel3._
import chisel3.util.Decoupled
import utility.Constants._
import dandelion.config._
import chipsalliance.rocketchip.config._

import scala.collection.immutable.ListMap

trait RouteID extends AccelBundle {
  val RouteID = UInt(glen.W)
}

trait TaskID extends AccelBundle {
  val taskID = UInt(tlen.W)
}

trait PredicateT extends AccelBundle {
  val predicate = Bool()
}

// Maximum of 16MB Stack Array.
class AllocaIO(implicit p: Parameters) extends AccelBundle()(p) {
  val size = UInt(xlen.W)
  val numByte = UInt(xlen.W)
  val predicate = Bool()
}

object AllocaIO {
  def default(implicit p: Parameters): AllocaIO = {
    val temp_w = Wire(new AllocaIO)
    temp_w.size := 0.U
    temp_w.numByte := 0.U
    temp_w.predicate := true.B
    temp_w
  }
}

// alloca indicates id of stack object and returns address back.
// Can be any of the 4MB regions. Size is over provisioned
class AllocaReq(implicit p: Parameters) extends AccelBundle()(p) with RouteID {
  val size = UInt(xlen.W)
  val numByte = UInt(xlen.W)
}

object AllocaReq {
  def default(implicit p: Parameters): AllocaReq = {
    val wire = Wire(new AllocaReq)
    wire.size := 0.U
    wire.numByte := 0.U
    wire.RouteID := 0.U
    wire
  }
}

// ptr is the address returned back to the alloca call.
// Valid and Data flipped.
class AllocaResp(implicit p: Parameters) extends RouteID {
  val ptr = UInt(xlen.W)
}

object AllocaResp {
  def default(implicit p: Parameters): AllocaResp = {
    val wire = Wire(new AllocaResp)
    wire.RouteID := 0.U
    wire.ptr := 0.U
    wire
  }
}


class AllocaRespTest(implicit p: Parameters) extends RouteID {
  val ptr = UInt(xlen.W)
}


/**
 * Memory read request interface
 * @param p
 */
class ReadReq(implicit p: Parameters)
  extends RouteID {
  val address = UInt(xlen.W)
  val taskID = UInt(tlen.W)
  val Typ = UInt(8.W)

}

object ReadReq {
  def default(implicit p: Parameters): ReadReq = {
    val wire = Wire(new ReadReq)
    wire.address := 0.U
    wire.taskID := 0.U
    wire.RouteID := 0.U
    wire.Typ := MT_D
    wire
  }
}


//  data : data returned from scratchpad
class ReadResp(implicit p: Parameters) extends RouteID {
  val data = UInt(xlen.W)

  override def toPrintable: Printable = {
    p"ReadResp {\n" +
      p"  RouteID: ${RouteID}\n" +
      p"  data   : 0x${Hexadecimal(data)} }"
  }
}

object ReadResp {
  def default(implicit p: Parameters): ReadResp = {
    val wire = Wire(new ReadResp)
    wire.data := 0.U
    wire
  }
}

/**
  * Write request to memory
  *
  * @param p [description]
  * @return [description]
  */
//
// Word aligned to write to
// Node performing the write
// Mask indicates which bytes to update.
class WriteReq(implicit p: Parameters)
  extends RouteID {
  val address = UInt((xlen - 10).W)
  val data = UInt(xlen.W)
  val mask = UInt((xlen / 8).W)
  val taskID = UInt(tlen.W)
  val Typ = UInt(8.W)
}

object WriteReq {
  def default(implicit p: Parameters): WriteReq = {
    val wire = Wire(new WriteReq)
    wire.address := 0.U
    wire.data := 0.U
    wire.mask := 0.U
    wire.taskID := 0.U
    wire.RouteID := 0.U
    wire.Typ := MT_D
    wire
  }
}

// Explicitly indicate done flag
class WriteResp(implicit p: Parameters) extends RouteID {
  val done = Bool()
}

//  data : data returned from scratchpad
class FUResp(implicit p: Parameters) extends AccelBundle {
  val data = UInt(xlen.W)

  override def toPrintable: Printable = {
    p"FUResp {\n" +
      p"  data   : 0x${Hexadecimal(data)} }"
  }
}

object FUResp {
  def default(implicit p: Parameters): FUResp = {
    val wire = Wire(new FUResp)
    wire.data := 0.U
    wire
  }
}

class MemReq(implicit p: Parameters) extends AccelBundle()(p) {
  val addr = UInt(xlen.W)
  val data = UInt(xlen.W)
  val mask = UInt((xlen / 8).W)
  val tag = UInt((List(1, mshrLen).max).W)
  val taskID = UInt(tlen.W)
  val iswrite = Bool()

}

object MemReq {
  def apply()(implicit p: Parameters): MemReq = {
    val wire = Wire(new MemReq())
    wire.addr := 0.U
    wire.data := 0.U
    wire.mask := 0.U
    wire.tag := 0.U
    wire.taskID := 0.U
    wire.iswrite := false.B
    wire
  }

  def default(implicit p: Parameters): MemReq = {
    val wire = Wire(new MemReq())
    wire.addr := 0.U
    wire.data := 0.U
    wire.mask := 0.U
    wire.tag := 0.U
    wire.taskID := 0.U
    wire.iswrite := false.B
    wire
  }
}

class MemResp(implicit p: Parameters) extends AccelBundle()(p) {
  val data = UInt(xlen.W)
  val tag = UInt((List(1, mshrLen).max).W)
  val iswrite = Bool()

}

object MemResp {
  def default(implicit p: Parameters): MemResp = {
    val wire = Wire(new MemResp())
    wire.data := 0.U
    wire.tag := 0.U
    wire.iswrite := false.B
    wire
  }
}

//class RelayNode output
class RelayOutput(implicit p: Parameters) extends AccelBundle()(p) {
  override def cloneType = new RelayOutput().asInstanceOf[this.type]

  val DataNode = Decoupled(UInt(xlen.W))
  val TokenNode = Input(UInt(tlen.W))
}

/**
  * Data bundle between dataflow nodes.
  *
  * @note 2 fields
  *       data : U(xlen.W)
  *       predicate : Bool
  * @param p : implicit
  * @return
  */
class DataBundle(implicit p: Parameters) extends PredicateT with TaskID {
  // Data packet
  val data = UInt(xlen.W)

  def asControlBundle(): ControlBundle = {
    val wire = Wire(new ControlBundle)
    wire.control := this.predicate
    wire.taskID := this.taskID
    wire
  }
}

//p


class LogBundle(implicit p: Parameters) extends PredicateT with TaskID {
  // Data packet
  val stateData = UInt(2.W)

}
object LogBundle {

  def apply(stateData: UInt = 0.U, taskID: UInt = 0.U)(implicit p: Parameters): LogBundle = {
    val wire = Wire(new LogBundle)
    wire.stateData := stateData
    wire.taskID := taskID
    wire.predicate := true.B
    wire
  }}


//v

object DataBundle {

  def apply(data: UInt = 0.U, taskID: UInt = 0.U)(implicit p: Parameters): DataBundle = {
    val wire = Wire(new DataBundle)
    wire.data := data
    wire.predicate := true.B
    wire.taskID := taskID
    wire
  }

  def apply(data: UInt, taskID: UInt, predicate: UInt)(implicit p: Parameters): DataBundle = {
    val wire = Wire(new DataBundle)
    wire.data := data
    wire.predicate := predicate
    wire.taskID := taskID
    wire
  }


  def default(implicit p: Parameters): DataBundle = {
    val wire = Wire(new DataBundle)
    wire.data := 0.U
    wire.predicate := false.B
    wire.taskID := 0.U
    wire
  }

  def active(data: UInt = 0.U)(implicit p: Parameters): DataBundle = {
    val wire = Wire(new DataBundle)
    wire.data := data
    wire.predicate := true.B
    wire.taskID := 0.U
    wire
  }

  def deactivate(data: UInt = 0.U)(implicit p: Parameters): DataBundle = {
    val wire = Wire(new DataBundle)
    wire.data := data
    wire.predicate := false.B
    wire.taskID := 0.U
    wire
  }
}

class TypBundle(implicit p: Parameters) extends PredicateT with TaskID {
  // Type Packet
  val data = UInt(typeSize.W)
}


object TypBundle {
  def default(implicit p: Parameters): TypBundle = {
    val wire = Wire(new TypBundle)
    wire.data := 0.U
    wire.predicate := false.B
    wire.taskID := 0.U
    wire
  }
}

/**
  * Control bundle between branch and
  * basicblock nodes
  *
  * control  : Bool
  */
class ControlBundle(implicit p: Parameters) extends AccelBundle()(p) {
  //Control packet
  val taskID = UInt(tlen.W)
  val control = Bool()
  val debug = Bool()

  def asDataBundle(): DataBundle = {
    val wire = Wire(new DataBundle)
    wire.data := this.control.asUInt()
    wire.predicate := this.control
    wire.taskID := this.taskID
    wire
  }
}

object ControlBundle {
  def default(implicit p: Parameters): ControlBundle = {
    val wire = Wire(new ControlBundle)
    wire.control := false.B
    wire.taskID := 0.U
    wire.debug := false.B
    wire
  }

  def default(control: Bool, task: UInt)(implicit p: Parameters): ControlBundle = {
    val wire = Wire(new ControlBundle)
    wire.control := control
    wire.taskID := task
    wire.debug := false.B
    wire
  }

  def default(control: Bool, task: UInt, debug: Bool)(implicit p: Parameters): ControlBundle = {
    val wire = Wire(new ControlBundle)
    wire.control := control
    wire.taskID := task
    wire.debug := debug
    wire
  }

  def active(taskID: UInt = 0.U)(implicit p: Parameters): ControlBundle = {
    val wire = Wire(new ControlBundle)
    wire.control := true.B
    wire.taskID := taskID
    wire.debug := false.B
    wire
  }

  def debug(taskID: UInt = 0.U)(implicit p: Parameters): ControlBundle = {
    val wire = Wire(new ControlBundle)
    wire.control := true.B
    wire.taskID := taskID
    wire.debug := true.B
    wire
  }


  def deactivate(taskID: UInt = 0.U)(implicit p: Parameters): ControlBundle = {
    val wire = Wire(new ControlBundle)
    wire.control := false.B
    wire.taskID := taskID
    wire.debug := false.B
    wire
  }


  def apply(control: Bool = false.B, taskID: UInt = 0.U, debug: Bool = false.B)(implicit p: Parameters): ControlBundle = {
    val wire = Wire(new ControlBundle)
    wire.control := control
    wire.taskID := taskID
    wire.debug := debug
    wire
  }

}


/**
  * Custom Data bundle between dataflow nodes.
  *
  * @note 2 fields
  *       data : U(len.W)
  *       predicate : Bool
  * @return
  */
class CustomDataBundle[T <: Data](gen: T = UInt(32.W))(implicit p: Parameters) extends AccelBundle()(p) {
  // Data packet
  val data = gen.cloneType
  //same as data = UInt(32.W)
  val predicate = Bool()
  val taskID = UInt(tlen.W)

  def asDataBundle(): DataBundle = {
    val wire = Wire(new DataBundle)
    wire.data := this.data.asUInt()
    wire.predicate := this.predicate
    wire.taskID := this.taskID
    wire
  }


  override def cloneType: this.type = new CustomDataBundle(gen).asInstanceOf[this.type]
}

object CustomDataBundle {
  def apply[T <: Data](gen: T)(implicit p: Parameters): CustomDataBundle[T] = new CustomDataBundle(gen)

  def default[T <: Data](gen: T)(implicit p: Parameters): CustomDataBundle[T] = {
    val wire = Wire(new CustomDataBundle(gen))
    wire.data := 0.U.asTypeOf(gen)
    wire.predicate := false.B
    wire.taskID := 0.U
    wire
  }
}

/**
  * Bundle with variable (parameterizable) types and/or widths
  * VariableData   - bundle of DataBundles of different widths
  * VariableCustom - bundle of completely different types
  * These classes create a record with a configurable set of fields like:
  * "data0"
  * "data1", etc.
  * The bundle fields can either be of type CustomDataBundle[T] (any type) or of
  * DataBundle depending on class used.
  *
  * Examples:
  * var foo = VariableData(List(32, 16, 8))
  * foo("field0") is DataBundle with UInt(32.W)
  * foo("field1") is DataBundle with UInt(16.W)
  * foo("field2") is DataBundle with UInt(8.W)
  * var foo = VariableCustom(List(Int(32.W), UInt(16.W), Bool())
  * foo("field0") is CustomDataBundle with Int(32.W)
  * foo("field1") is CustomDataBundle with UInt(16.W)
  * foo("field2") is CustomDataBundle with Bool()
  *
  */

// Bundle of types specified by the argTypes parameter
class VariableCustom(val argTypes: Seq[Bits])(implicit p: Parameters) extends Record {
  var elts = Seq.tabulate(argTypes.length) {
    i => s"field$i" -> CustomDataBundle(argTypes(i))
  }
  override val elements = ListMap(elts map { case (field, elt) => field -> elt.cloneType }: _*)

  def apply(elt: String) = elements(elt)

  override def cloneType = new VariableCustom(argTypes).asInstanceOf[this.type]
}

// Bundle of decoupled types specified by the argTypes parameter
class VariableDecoupledCustom(val argTypes: Seq[Bits])(implicit p: Parameters) extends Record {
  var elts = Seq.tabulate(argTypes.length) {
    i => s"field$i" -> (Decoupled(CustomDataBundle(argTypes(i))))
  }
  override val elements = ListMap(elts map { case (field, elt) => field -> elt.cloneType }: _*)

  def apply(elt: String) = elements(elt)

  override def cloneType = new VariableDecoupledCustom(argTypes).asInstanceOf[this.type]
}

// Bundle of DataBundles with data width specified by the argTypes parameter
class VariableData(val argTypes: Seq[Int])(implicit p: Parameters) extends Record {

  var elts = Seq.tabulate(argTypes.length) {
    i =>
      s"field$i" -> new DataBundle()(
        p.alterPartial({ case DandelionConfigKey => p(DandelionConfigKey).copy(dataLen = argTypes(i)) })
      )
  }
  override val elements = ListMap(elts map { case (field, elt) => field -> elt.cloneType }: _*)

  def apply(elt: String) = elements(elt)

  override def cloneType = new VariableData(argTypes).asInstanceOf[this.type]
}

// Bundle of Decoupled DataBundles with data width specified by the argTypes parameter
class VariableDecoupledData(val argTypes: Seq[Int])(implicit p: Parameters) extends Record {
  var elts = Seq.tabulate(argTypes.length) {
    i =>
      s"field$i" -> Decoupled(new DataBundle()(
        p.alterPartial({ case DandelionConfigKey => p(DandelionConfigKey).copy(dataLen = argTypes(i)) })
      )
      )
  }
  override val elements = ListMap(elts map { case (field, elt) => field -> elt.cloneType }: _*)

  def apply(elt: String) = elements(elt)

  override def cloneType = new VariableDecoupledData(argTypes).asInstanceOf[this.type]
}

// Bundle of Decoupled DataBundle Vectors. Data width is default. Intended for use on outputs
// of a block (i.e. configurable number of output with configurable number of copies of each output)
class VariableDecoupledVec(val argTypes: Seq[Int])(implicit p: Parameters) extends Record {
  var elts = Seq.tabulate(argTypes.length) {
    i => s"field$i" -> Vec(argTypes(i), Decoupled(new DataBundle()(p)))
  }
  override val elements = ListMap(elts map { case (field, elt) => field -> elt.cloneType }: _*)

  def apply(elt: String) = elements(elt)

  override def cloneType = new VariableDecoupledVec(argTypes).asInstanceOf[this.type]
}

// Call type that wraps an enable and variable DataBundle together
class Call(val argTypes: Seq[Int])(implicit p: Parameters) extends AccelBundle() {
  val enable = new ControlBundle
  val data = new VariableData(argTypes)

  override def cloneType = new Call(argTypes).asInstanceOf[this.type]
}

// Call type that wraps an enable and variable DataBundle together
class CallDCR(val ptrsArgTypes: Seq[Int],
              val valsArgTypes: Seq[Int])(implicit p: Parameters) extends AccelBundle() {
  val enable   = new ControlBundle
  val dataPtrs = new VariableData(ptrsArgTypes)
  val dataVals = new VariableData(valsArgTypes)

  override def cloneType = new CallDCR(ptrsArgTypes, valsArgTypes).asInstanceOf[this.type]
}

// Call type that wraps a decoupled enable and decoupled variable data bundle together
class CallDecoupled(val argTypes: Seq[Int])(implicit p: Parameters) extends AccelBundle() {
  val enable = Decoupled(new ControlBundle)
  val data = new VariableDecoupledData(argTypes)

  override def cloneType = new CallDecoupled(argTypes).asInstanceOf[this.type]
}

// Call type that wraps a decoupled enable and decoupled vector DataBundle together
class CallDecoupledVec(val argTypes: Seq[Int])(implicit p: Parameters) extends AccelBundle() {
  val enable = Decoupled(new ControlBundle)
  val data = new VariableDecoupledVec(argTypes)

  override def cloneType = new CallDecoupledVec(argTypes).asInstanceOf[this.type]
}


// Call type that wraps a decoupled enable and decoupled variable data bundle together
class CallDCRDecoupled(val ptrsArgTypes: Seq[Int],
                       val valsArgTypes: Seq[Int])(implicit p: Parameters) extends AccelBundle() {
  val enable = Decoupled(new ControlBundle)
  val dataPtrs = new VariableDecoupledData(ptrsArgTypes)
  val dataVals = new VariableDecoupledData(valsArgTypes)

  override def cloneType = new CallDCRDecoupled(ptrsArgTypes, valsArgTypes).asInstanceOf[this.type]
}

// Call type that wraps a decoupled enable and decoupled vector DataBundle together
class CallDCRDecoupledVec(val ptrsArgTypes: Seq[Int],
                          val valsArgTypes: Seq[Int])(implicit p: Parameters) extends AccelBundle() {
  val enable = Decoupled(new ControlBundle)
  val dataPtrs = new VariableDecoupledVec(ptrsArgTypes)
  val dataVals = new VariableDecoupledVec(valsArgTypes)

  override def cloneType = new CallDCRDecoupledVec(ptrsArgTypes, valsArgTypes).asInstanceOf[this.type]
}

// Function unit request type
class FUReq(implicit p: Parameters) extends RouteID {
  val data_a = UInt(xlen.W)
  val data_b = UInt(xlen.W)
  val sqrt = Bool()

  override def cloneType = new FUReq().asInstanceOf[this.type]
}
