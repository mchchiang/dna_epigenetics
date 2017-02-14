# 
# GetMagDistrb.py
#
# A script that calculates the time average distribution of magnetisation
# over the entire chromosome
#

import sys
import math

args = sys.argv
args.pop(0) #ignore self

n = int(args.pop(0)) # number of atoms
in_file = args.pop(0) # state file name
out_file = args.pop(0) # output file name

NUM_OF_STATE = 6

# Read data file and store average

count = 0
t = 0

avg = [0.0 for i in xrange(n)]
var = [0.0 for i in xrange(n)]
prob = [[0 for j in xrange(n)] for i in xrange(NUM_OF_STATE)]

sample = 0

with open(in_file, "r") as f:
    for line in f:
        data = line.strip().split()
        if (count > 0 and count <= n):
            site = int(data[0])
            state = int(data[1])

            avg[site] += state
            var[site] += state*state
            
            prob[state][site] += 1

            if (state > 2):
                prob[state-3][site] += 1

        count += 1
        
        if (count == n+2):
            count = 0
            sample += 1

print sample

sample = float(sample)

writer = open(out_file, "w")

for i in xrange(NUM_OF_STATE):
    for j in xrange(n):
        prob[i][j] /= sample

for i in xrange(n):
    avg[i] /= sample
    var[i] /= sample
    var[i] = sample / (sample-1) * (var[i] - avg[i]*avg[i])
    sigma = math.sqrt(var[i])

    output = "%d %.5f %.5f" % (i, avg[i], sigma) 

    for j in xrange(NUM_OF_STATE):
        output += " %.5f" % (prob[j][i])

    output += "\n"

    writer.write(output)
    
writer.close()
