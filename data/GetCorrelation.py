# 
# GetCorrelation.py
#
# A script to compute the auto-correlation of the data. It 
# assumes the data has a structure where the first column is
# the independent variable (i.e. time) and the second column
# is the dependent variable where the correlation operation 
# should be performed.
#

import sys
import numpy as np

args = sys.argv
args.pop(0) #ignore self

start_row = int(args.pop(0))
max_tau = int(args.pop(0))
data_file = args.pop(0)
output_file = args.pop(0)

if (max_tau > y - start_row):
    max_tau = y - start_row

data = np.loadtxt(data_file, comments='#', delimiter=' ')

time = data[:,0]
values = data[:,1]

avgSq = 0.0

for i in xrange(y-start_row):
    avgSq += values[i]
    
avgSq /= float(y-start_row)
avgSq *= avgSq

result = []

for tau in xrange(max_tau):
    if (tau % 10000 == 0):
        print tau
    total = 0.0
    tmax = y - tau - start_row
    for t in xrange(tmax):
        total += value[t] * value[tau+t]
    total /= float(tmax)
    result.append(total - avgSq)

norm = result[0]

writer = open(output_file, "w")

for tau in xrange(max_tau):
    output = "%d %.5f\n" % (tau, result[tau])
    writer.write(output)

writer.close()
    
    

                
            

    
        
    
