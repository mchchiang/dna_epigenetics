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
t_start = int(args.pop(0)) # starting time frame
t_end = int(args.pop(0)) # ending time frame
cutoff = float(args.pop(0)) # threshold distance to count as contact
in_file = args.pop(0) # state file name
out_file = args.pop(0) # output file name

start_line = t_start * (n+2)
end_line = t_end * (n+2)
line_num = 0
count = 0
sample = 0

contact = [[0.0 for j in xrange(i+1)] for i in xrange(n)]
pos = [[0.0 for j in xrange(3)] for i in xrange(n)]

# read the positions of the atoms
with open(in_file, "r") as f:
    for line in f:
        if (line_num >= start_line and line_num < end_line):            
            if (count >= 2 and count < n+2):
                data = line.strip().split()
                pos[count-2][0] = float(data[1])
                pos[count-2][1] = float(data[2])
                pos[count-2][2] = float(data[3])

            count += 1

            if (count == n+2):
                count = 0
                sample += 1
                
                # compute the contact map
                for i in xrange(n):
                    for j in xrange(i+1):
                        diff = (pos[i][0]-pos[j][0])**2 + \
                               (pos[i][1]-pos[j][1])**2 + \
                               (pos[i][2]-pos[j][2])**2
                        diff = math.sqrt(diff)
                        if (diff <= cutoff):
                            contact[i][j] += 1
            
        line_num += 1
        
        if (line_num >= end_line):
            break

print sample
sample = float(sample)

for i in xrange(n):
    for j in xrange(i+1):
        contact[i][j] /= sample

writer = open(out_file, "w")

for i in xrange(n):
    for j in xrange(n):
        x = i
        y = j

        if (j > i):
            x = j
            y = i

        output = "%d %d %.5f\n" % (i,j,contact[x][y])
        writer.write(output)

    writer.write("\n")

writer.close()
