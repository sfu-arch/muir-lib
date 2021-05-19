package dandelion.config

import chisel3._
import chipsalliance.rocketchip.config._
import dandelion.fpu.FType
import dandelion.interfaces.axi.AXIParams
import dandelion.junctions.{NastiKey, NastiParameters}
import dandelion.util.{DandelionGenericParameterizedBundle, DandelionParameterizedBundle}


trait AccelParams {

  var xlen: Int
  var ylen: Int
  val tlen: Int
  val glen: Int
  val typeSize: Int
  val beats: Int
  val mshrLen: Int
  val fType: FType

  //Cache
  val nways: Int
  val nsets: Int

  def cacheBlockBytes: Int

  // Debugging dumps
  val log: Boolean
  val clog: Boolean
  val verb: String
  val comp: String
}

/**
 * VCR parameters.
 * These parameters are used on VCR interfaces and modules.
 */
trait DCRParams {
  val nCtrl: Int
  val nECnt: Int
  val nVals: Int
  val nPtrs: Int
  val regBits: Int
}

/**
 * DME parameters.
 * These parameters are used on DME interfaces and modules.
 */
trait DMEParams {
  val nReadClients: Int
  val nWriteClients: Int
}


case class DandelionAccelParams(
                                 dataLen: Int = 32,
                                 addrLen: Int = 32,
                                 taskLen: Int = 5,
                                 groupLen: Int = 16,
                                 mshrLen: Int = 8,
                                 tSize: Int = 64,
                                 verbosity: String = "low",
                                 components: String = "",
                                 printLog: Boolean = false,
                                 printMemLog: Boolean = false,
                                 printCLog: Boolean = false,
                                 cacheNWays: Int = 1,
                                 cacheNSets: Int = 256
                               ) extends AccelParams {
  var xlen: Int = dataLen
  var ylen: Int = addrLen
  val tlen: Int = taskLen
  val glen: Int = groupLen
  val typeSize: Int = tSize
  val beats: Int = 0
  val mshrlen: Int = mshrLen
  val fType = dataLen match {
    case 64 => FType.D
    case 32 => FType.S
    case 16 => FType.H
  }

  //Cache
  val nways = cacheNWays // TODO: set-associative
  val nsets = cacheNSets

  def cacheBlockBytes: Int = 8 * (xlen >> 3) // 4 x 64 bits = 32B

  // Debugging dumps
  val log: Boolean = printLog
  val memLog: Boolean = printMemLog
  val clog: Boolean = printCLog
  val verb: String = verbosity
  val comp: String = components

}

/**
 * DCR parameters.
 * These parameters are used on DCR interfaces and modules.
 */
case class DandelionDCRParams(numCtrl: Int = 1,
                              numEvent: Int = 1,
                              numVals: Int = 2,
                              numPtrs: Int = 4,
                              numRets: Int = 0) {
  val nCtrl = numCtrl
  val nECnt = numEvent + numRets
  val nVals = numVals
  val nPtrs = numPtrs
  val regBits = 32
}

/**
 * DME parameters.
 * These parameters are used on DME interfaces and modules.
 */
case class DandelionDMEParams(numRead: Int = 3,
                              numWrite: Int = 1) extends DMEParams {
  val nReadClients: Int = numRead
  val nWriteClients: Int = numWrite
  require(nReadClients > 0,
    s"\n\n[Dandelion] [DMEParams] nReadClients must be larger than 0\n\n")
  require(
    nWriteClients > 0,
    s"\n\n[Dandelion] [DMEParams] nWriteClients must be larger than 0\n\n")
}

/**
 * Debug Parameters
 * These parameters are used on Debug nodes.
 */
case class DebugParams(len_data: Int = 64,
                       len_id: Int = 8,
                       len_code: Int = 5,
                       iteration_len: Int = 10,
                       len_guard: Int = 2) {
  val gLen = len_guard
  val idLen = len_id
  val codeLen = len_code
  val iterLen = iteration_len
  val dataLen = len_data - (gLen + idLen + codeLen + + iterLen)
  val packetLen = len_data
}


/** Shell parameters. */
case class ShellParams(
                        val hostParams: AXIParams,
                        val memParams: AXIParams,
                        val vcrParams: DandelionDCRParams,
                        val dmeParams: DandelionDMEParams,
                        val debugParams: DebugParams
                      )


case object DandelionConfigKey extends Field[DandelionAccelParams]

case object DCRKey extends Field[DandelionDCRParams]

case object DMEKey extends Field[DandelionDMEParams]

case object HostParamKey extends Field[AXIParams]

case object MemParamKey extends Field[AXIParams]

case object DebugParamKey extends Field[DebugParams]

class WithAccelConfig(inParams: DandelionAccelParams = DandelionAccelParams())
  extends Config((site, here, up) => {
    // Core
    case DandelionConfigKey => inParams
  }

  )

trait HasAccelParams {
  implicit val p: Parameters

  def accelParams: DandelionAccelParams = p(DandelionConfigKey)

  val xlen = accelParams.xlen
  val ylen = accelParams.ylen
  val tlen = accelParams.tlen
  val glen = accelParams.glen
  val mshrLen = accelParams.mshrLen
  val typeSize = accelParams.typeSize
  val beats = typeSize / xlen
  val fType = accelParams.fType
  val log = accelParams.log
  val memLog = accelParams.memLog
  val clog = accelParams.clog
  val verb = accelParams.verb
  val comp = accelParams.comp

}

trait HasAccelShellParams {
  implicit val p: Parameters

  def dcrParams: DandelionDCRParams = p(DCRKey)

  def dmeParams: DandelionDMEParams = p(DMEKey)

  def hostParams: AXIParams = p(HostParamKey)

  def memParams: AXIParams = p(MemParamKey)

  def nastiParams: NastiParameters = p(NastiKey)

  def dbgParams: DebugParams = p(DebugParamKey)

}

trait HasDebugCodes {
  implicit val p: Parameters

  def debugParams: DebugParams = p(DebugParamKey)

  val DbgLoadAddress = "b00001".U(debugParams.codeLen.W)
  val DbgLoadData = "b00010".U(debugParams.codeLen.W)
  val DbgStoreAddress = "b0011".U(debugParams.codeLen.W)
  val DbgStoreData = "b0100".U(debugParams.codeLen.W)
  val DbgComputeData = "b0101".U(debugParams.codeLen.W)
  val DbgPhiData = "b0110".U(debugParams.codeLen.W)
}


abstract class AccelBundle(implicit val p: Parameters) extends DandelionParameterizedBundle()(p)
  with HasAccelParams

abstract class AXIAccelBundle(implicit val p: Parameters) extends DandelionGenericParameterizedBundle(p)
  with HasAccelParams
