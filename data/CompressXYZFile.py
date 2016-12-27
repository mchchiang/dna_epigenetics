#
# CompressXYZFile.py
#
# A python script to remove duplicated data
# in the xyz file.
#

import sys

args = sys.argv
args.pop(0) #ignore self

n = int(args.pop(0))
input_file = args.pop(0)
output_file = args.pop(0)

reader = open(input_file, "r")
writer = open(output_file, "w")

count = 0
iteration = 0

for line in reader:
    if (iteration == 0 or iteration % 2 == 1):
        writer.write(line)
    
    count += 1

    if (count == n+2):
        iteration += 1
        count = 0


reader.close()
writer.close()
