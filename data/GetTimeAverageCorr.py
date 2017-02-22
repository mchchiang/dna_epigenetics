# 
# GetTimeAverage.py
#
# A script to compute the time average of a data set.
# It requires the user to specify the auto-correlation
# time and sample data points at this time interval.
#

import sys
import math

args = sys.argv
args.pop(0) #ignore self

tstart = int(args.pop(0)) # first sampled point
tinc = int(args.pop(0)) # auto-correlation time
time_col = int(args.pop(0)) # time column
value_col = int(args.pop(0)) # data column
data_file = args.pop(0) # data file name
output_file = args.pop(0) # output file name

# Read data file and store average
avg = 0.0
avgSq = 0.0
n = 0

with open(data_file, "r") as f:
    for line in f:
        if (not line.startswith("#")):
            data = line.strip().split()
            if (data == []): #ignore any lines start with \n
                continue
            
            t = int(data[time_col])

            if (t < tstart or ((t-tstart) % tinc) != 0):
                continue

            else:
                n += 1
                value = abs(float(data[value_col]))
                avg += value
                avgSq += value*value


n = float(n)
avg /= n
avgSq /= n

# Compute error (use unbiased estimate for standard deviation)
var = n / (n-1) * (avgSq - avg*avg)
sigma = math.sqrt(var) 
error = sigma / math.sqrt(n)

writer = open(output_file, "w")
output = "%.5f %.5f %.5f\n" % (avg, sigma, error)
writer.write(output)
writer.close()
