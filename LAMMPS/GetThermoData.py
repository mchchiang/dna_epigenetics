import sys

args = sys.argv
args.pop(0) #ignore self

log_file = args.pop(0)
output_file = args.pop(0)


reader = open(log_file, "r")
writer = open(output_file, "w")

foundLine = False
count = 0
for line in reader:
    if (line.startswith("btime")):
        foundLine = True
    if (foundLine):
        count += 1
    if (count == 2):
        writer.write(line)
        count = 0
        foundLine = False

reader.close()
writer.close()
    
        
    
