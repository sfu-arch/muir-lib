package shell


import chisel3.Module
import config._
import junctions._

/** De10Config. Shell configuration for De10 */
class De10Config extends Config((site, here, up) => {
  case ShellKey => ShellParams(
    hostParams = AXIParams(
      addrBits = 16, dataBits = 32, idBits = 13, lenBits = 4),
    memParams = AXIParams(
      addrBits = 32, dataBits = 64, userBits = 5,
      lenBits = 4, // limit to 16 beats, instead of 256 beats in AXI4
      coherent = true),
    vcrParams = VCRParams( ),
    vmeParams = VMEParams( ))
})


/** PynqConfig. Shell configuration for Pynq */
class PynqConfig extends Config((site, here, up) => {
  case ShellKey => ShellParams(
    hostParams = AXIParams(
      coherent = false,
      addrBits = 16,
      dataBits = 32,
      lenBits = 8,
      userBits = 1),
    memParams = AXIParams(
      coherent = true,
      addrBits = 32,
      dataBits = 64,
      lenBits = 8,
      userBits = 1),
    vcrParams = VCRParams( ),
    vmeParams = VMEParams( ))
})


class DefaultDe10Config extends Config(new MiniConfig ++ new De10Config)


object DefaultDe10Config extends App {
  implicit val p: Parameters = new DefaultDe10Config
  chisel3.Driver.execute(args, () => new IntelShell)
}