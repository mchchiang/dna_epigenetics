# 
# ContactMap.py
#
# A script that computes the contact map
#

import sys
import math

args = sys.argv
args.pop(0) #ignore self

n = int(args.pop(0)) # number of atoms
t = int(args.pop(0)) # which step frame to look at
cutoff = float(args.pop(0)) # threshold distance to count as contact
in_file = args.pop(0) # state file name
out_file = args.pop(0) # output file name


start_line = t * (n+2) + 3
end_line = start_line + n

count = 1

contact = [[0.0 for j in xrange(n)] for i in xrange(n)]
pos = [[0.0 for j in xrange(3)] for i in xrange(n)]
site = 0

# read the positions of the atoms
with open(in_file, "r") as f:
    for line in f:
        if (count >= start_line and count < end_line):
            print line
            data = line.strip().split()
            pos[site][0] = float(data[1])
            pos[site][1] = float(data[2])
            pos[site][2] = float(data[3])
            site += 1
        
        count += 1
        if (count >= end_line):
            break

# compute the contact map
for i in xrange(n):
    for j in xrange(n):
        diff = (pos[i][0]-pos[j][0])**2 + \
               (pos[i][1]-pos[j][1])**2 + \
               (pos[i][2]-pos[j][2])**2
        diff = math.sqrt(diff)
        if (diff <= cutoff):
            contact[i][j] += 1

writer = open(out_file, "w")

for i in xrange(n):
    for j in xrange(n):
        output = "%d %d %d\n" % (i,j,contact[i][j])
        writer.write(output)
    writer.write("\n")

writer.close()
