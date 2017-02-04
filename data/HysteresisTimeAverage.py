# 
# HysteresisTimeAverage.py
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
tinc = int(args.pop(0)) # time increment
teinc = int(args.pop(0)) # time between energy increment
tsample = int(args.pop(0)) # auto-correlation time
time_col = int(args.pop(0)) # time column
value_col = int(args.pop(0)) # data column
energy_col = int(args.pop(0)) # energy column
data_file = args.pop(0) # data file name
output_file = args.pop(0) # output file name

# Read data file and store average
avg = 0.0
avgSq = 0.0
n = 0
tconst = tstart
value = 0
count = 0
estart = 0.45
eend = 0.85
einc = 0.001
e = estart
esign = 1.0

writer = open(output_file, "w")

with open(data_file, "r") as f:
    for line in f:
        if (not line.startswith("#")):
            data = line.strip().split()

            if (data == [] or int(data[time_col]) < tstart): #ignore any lines start with \n
                continue

            t = int(data[time_col])
            #e = float(data[energy_col])
            dt = t - tconst
           
            if (dt % tsample == 0):
                value = float(data[value_col])
                avg += value
                avgSq += value*value
                n += 1
#                print t, value, e, n

            if ((t+tinc) % teinc == 0):
                tconst = t+tinc
                
                # Compute error (use unbiased estimate for standard deviation)
                n = float(n)
                avg /= n
                avgSq /= n
                var = n / (n-1) * (avgSq - avg*avg)
                sigma = math.sqrt(var) 
                error = sigma / math.sqrt(n)


                output = "%.5f %.5f %.5f %.5f\n" % (e, avg, sigma, error)
                writer.write(output)

                # Reset
                avg = 0.0
                avgSq = 0.0
                n = 0
                if (abs(e - eend) < 0.0000001):
                    esign *= -1.0
                print esign
                e += (esign*einc)

writer.close()
