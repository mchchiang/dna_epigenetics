# 
# GetCorrelation.py
#
# A script to compute the auto-correlation of the data. It 
# assumes the data has a structure where the first column is
# the independent variable (i.e. time) and the second column
# is the dependent variable where the correlation operation 
# should be performed.
#
# The (normalised) auto-correlation time is given by:
# corr(t) = (<(m(t')m(t+t')> - <m>^2)/ corr(0)
#

import sys
import numpy as np

args = sys.argv
args.pop(0) #ignore self

tstart = int(args.pop(0))
max_tau = int(args.pop(0))
data_file = args.pop(0)
output_file = args.pop(0)

data = np.loadtxt(data_file, comments='#', delimiter=' ')

time = data[:,0]
value = data[:,1]

tlen = len(time)
tdelta = tlen - tstart

if (max_tau > tdelta):
    max_tau = tdelta

avgSq = 0.0

for i in xrange(tstart, tlen):
    avgSq += value[i]
    
avgSq /= float(tdelta)
avgSq *= avgSq

result = []

for tau in xrange(max_tau):
    total = 0.0
    tmax = tlen - tau
    for t in xrange(tstart, tmax):
        total += value[t] * value[tau+t]
    total /= float(tmax)
    result.append(total - avgSq)

norm = result[0]

writer = open(output_file, "w")

for tau in xrange(max_tau):
    output = "%d %d %.5f\n" % (tau, time[tau], result[tau])
    writer.write(output)

writer.close()
    
    

                
            

    
        
    
