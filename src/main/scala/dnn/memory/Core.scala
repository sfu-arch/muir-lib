/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package dnn.memory


import chisel3._
import chisel3.util._
import config._
import shell._


/** Core parameters */
case class CoreParams(
    batch: Int = 1,
    blockOut: Int = 16,
    blockIn: Int = 8, //16
    inpBits: Int = 8,
    wgtBits: Int = 8,
    uopBits: Int = 32,
    accBits: Int = 32,
    outBits: Int = 8,
    uopMemDepth: Int = 512,
    inpMemDepth: Int = 512,
    wgtMemDepth: Int = 512,
    accMemDepth: Int = 512,
    outMemDepth: Int = 512,
    instQueueEntries: Int = 32
) {
  require(uopBits % 8 == 0,
          s"\n\n[VTA] [CoreParams] uopBits must be byte aligned\n\n")
}

case object CoreKey extends Field[CoreParams]

/** Core.
  *
  * The core defines the current VTA architecture by connecting memory and
  * compute modules together such as load/store and compute. Most of the
  * connections in the core are bulk (<>), and we should try to keep it this
  * way, because it is easier to understand what is going on.
  *
  * Also, the core must be instantiated by a shell using the
  * VTA Control Register (VCR) and the VTA Memory Engine (VME) interfaces.
  * More info about these interfaces and modules can be found in the shell
  * directory.
  */
class CoreConfig
    extends Config((site, here, up) => {
      case CoreKey =>
        CoreParams(
          batch = 1,
          blockOut = 16,
          blockIn = 8, //16
          inpBits = 8,
          wgtBits = 8,
          uopBits = 32,
          accBits = 32,
          outBits = 8,
          uopMemDepth = 2048,
          inpMemDepth = 2048,
          wgtMemDepth = 1024,
          accMemDepth = 2048,
          outMemDepth = 2048,
          instQueueEntries = 512
        )
    })