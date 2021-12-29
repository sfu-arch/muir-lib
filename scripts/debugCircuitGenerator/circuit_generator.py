#!/usr/bin/env python3
import argparse
import json
import sys
import re
import os.path
from os import path


def main(argv):
    parser = argparse.ArgumentParser(description='Short sample app')

    parser.add_argument('-c', '--config-file', action='store', dest='config', required=True, help = 'input config file')

    args = parser.parse_args(argv)

    if path.isfile(args.config) and args.config.lower().endswith('json'):
        print("Input IR file: {}".format(args.config))

    else:
        print("Input config file is not valid!")
        return
    n = 3 
    count_true = 0
    debug_nodes_id = []
    with open(args.config) as json_file:
        config = json.load(json_file)
        nodes = config["module"]["node"]
        for node in nodes:
            if node["debug"] == "true":
                count_true += 1
                debug_nodes_id.append(node["id"])

    header = "package dandelion.generator\n import chisel3._\n  import dandelion.config._\n import dandelion.interfaces._ \n import dandelion.memory._\n import dandelion.node._\n import util._\n\n"
    IO =  "abstract class Debug03IO(implicit val p: Parameters) extends Module with CoreParams {\n" +  "\tval io = IO(new Bundle {\n"+ "\t\tval Enable = Input(Bool())\n"+ "\t\tval MemResp = Flipped(Valid(new MemResp))\n"+ "\t\tval MemReq = Decoupled(new MemReq)\n\t})\n}\n\n"
    memory = "class Debug03DF(implicit p: Parameters) extends Debug03IO()(p) {\n"+ "\tval MemCtrl = Module(new UnifiedController(ID = 0, Size = 32, NReads = 0, NWrites ="+ str(count_true) + ")\n"+ "\t(WControl = new WriteMemoryController(NumOps ="+ str(count_true) +", BaseSize = 2, NumEntries = 2))\n"+ "\t(RControl = new ReadMemoryController(NumOps = 0, BaseSize = 2, NumEntries = 2))\n"+ "\t(RWArbiter = new ReadWriteArbiter()))\n"+ "\tio.MemReq <> MemCtrl.io.MemReq\n"+ "\tMemCtrl.io.MemResp <> io.MemResp\n\n\n"
    footer = "import java.io.{File, FileWriter}\n\n"+ "object Debug03Top extends App {\n"+"\tval dir = new File(\"RTL/Debug0{}Top\");\n".format(n)+"\tdir.mkdirs\n"+"\timplicit val p = Parameters.root((new MiniConfig).toInstance)\n"+"\tval chirrtl = firrtl.Parser.parse(chisel3.Driver.emit(() => new Debug0{}DF()))\n".format(n)+"\tval verilogFile = new File(dir, s\"$\{chirrtl.main\}.v\")\n"+"\tval verilogWriter = new FileWriter(verilogFile)\n"+"\tval compileResult = (new firrtl.VerilogCompiler).compileAndEmit(firrtl.CircuitState(chirrtl, firrtl.ChirrtlForm))\n""\tval compiledStuff = compileResult.getEmittedCircuit\n"+"\tverilogWriter.write(compiledStuff.value)\n"+"\tverilogWriter.close()\n"
        
    with open("output.scala", "w+") as output_file:
   
        output_file.write(header)
        output_file.write(IO)
        output_file.write(memory)
        for i in range(count_true):
            new_lines = "    val buf_{} = Module(new DebugBufferNode(ID = {}, RouteID = {}, Bore_ID = {}, node_cnt = {}.U))\n".format(i, i + 1, i, debug_nodes_id[i], i) + \
                "    buf_{}.io.Enable := io.Enable\n".format(i) + \
                "    MemCtrl.io.WriteIn({}) <> buf_{}.io.memReq\n".format(i, i) + \
                "    buf_{}.io.memResp <> MemCtrl.io.WriteOut({})\n\n".format(i, i)
            output_file.write(new_lines)

        output_file.write("}\n\n")
        output_file.write(footer)
        output_file.write("}")

if __name__ == "__main__":
   main(sys.argv[1:])