package log

import chisel3._
import scala.collection.mutable.ListBuffer
import java.io._
import java.util.Base64
import java.nio.charset.StandardCharsets.UTF_8

import java.io.File
import java.io.PrintWriter

import scala.io.Source

trait logClass{


  val printfLOGF = ""




  def storeInfo(module_name :String, Node:String, node_name:String, task_ID_R: Unit, cycleCount:Unit, FUioOut: Unit,
                left_RData : Unit, right_RData: Unit):Unit={
    val WindowOfData = module_name + Node + node_name + task_ID_R.toString + cycleCount.toString + FUioOut.toString +
    left_RData.toString + right_RData.toString
    val SWindowOfData = serialise(WindowOfData)
    Write(SWindowOfData)
  }



  def serialise(value: Any): String = {
    val stream: ByteArrayOutputStream = new ByteArrayOutputStream()
    val oos = new ObjectOutputStream(stream)
    oos.writeObject(value)
    oos.close
    new String(
      Base64.getEncoder().encode(stream.toByteArray),
      UTF_8
    )
  }


  def Write(SerialisedText: String) {
      val writer = new PrintWriter(new File("Write.txt"))

      writer.write(SerialisedText + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
      writer.close()

      Source.fromFile("Write.txt").foreach { x => print(x) }


  }



  def ppf(prefix: String, message: String, args: Bits*): Unit = {
    printf(prefix + message, args:_*) }

  def printfLog (m: String, a: Bits*) { ppf("\n[INFO] ",  printfLOGF++m, a:_*)}

}
