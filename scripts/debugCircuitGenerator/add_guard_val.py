#!/usr/bin/env python3
import argparse
import json
import sys
import re
import os.path
from os import path


def main(argv):
    
    parser = argparse.ArgumentParser(description='Short sample app')

    parser.add_argument('-s', '--scala-file', action='store', dest='input', required=True, help = 'input scala file')
    parser.add_argument('-c', '--config-file', action='store', dest='config', required=True, help = 'input config file')

    args = parser.parse_args(argv)

    if path.isfile(args.input) and args.input.lower().endswith('scala'):
        print("Input scala file: {}".format(args.input))
    else:
        print("Input scala file is not valid!")
        return
    
    if path.isfile(args.config) and args.config.lower().endswith('json'):
        print("Input IR file: {}".format(args.config))

    else:
        print("Input config file is not valid!")
        return

    with open(args.config) as json_file:
        config = json.load(json_file)


    new_file = args.input.replace('.scala', '-guardval.scala')
    guard_val_file = open(new_file,"w+")

    with open(args.input) as input_scala:
        for line in input_scala:

            if line.lstrip().startswith('val'):
                reg = re.findall(r'ID = (.+?), .* ', line.lstrip())
                if reg:
                    UID = reg[0]
                    node_filter = list(filter(lambda n : n['UID'] == int(UID) ,config['module']['node']))
                    if(node_filter != []):
                        for node in node_filter:
                            guard_val_file.write(line.replace('))', ', GuardVal = ' + str(node['Value']) + ')'))
                    else:
                   	    guard_val_file.write(line)
            else:
    
                guard_val_file.write(line)
    
    guard_val_file.close()



if __name__ == "__main__":
   main(sys.argv[1:])
