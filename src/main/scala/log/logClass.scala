package log

import chisel3._

trait logClass{
  val printfLOGF = ""

  def ppf(prefix: String, message: String, args: Bits*): Unit = {
    printf(prefix + message, args:_*) }

  def printfLog (m: String, a: Bits*) { ppf("\n[INFO] ",  printfLOGF++m, a:_*) }
}
