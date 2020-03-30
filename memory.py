    
with open ("Debug.mem" ,"r") as MEM:
    lines = []
    for line in MEM:
        begin = line.find(">")
        end = line.find("\n")
        dumped = bin(int(line[(begin+1):(end+1)]))
        print dumped[2:len(dumped)].zfill(32)