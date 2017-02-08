# 
# Kemograph.py
#
# A script to convert a state file to format that
# allows one to generate a kemograph of the states
# in gnuplot
#

import sys
import math

args = sys.argv
args.pop(0) #ignore self

n = int(args.pop(0)) # number of atoms
in_file = args.pop(0) # data file name
out_file = args.pop(0) # output file name

# Read data file and store average

count = 0
t = 0

writer = open(out_file, "w")
output = ""

with open(in_file, "r") as f:
    for line in f:
        data = line.strip().split()
        if (count == 0):
            t = int(data[0])
        elif (count <= n):
            index = int(data[0])
            type = int(data[1])
            output = "%d %d %d\n" % (t, index, type)
        
        count += 1
        
        if (count == n+2):
            count = 0
            output = "\n"

        writer.write(output)

writer.close()
