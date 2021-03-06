0# 
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

args = sys.argv
args.pop(0) #ignore self

tstart = int(args.pop(0))
max_tau = int(args.pop(0))
tinc = int(args.pop(0))
time_col = int(args.pop(0))
value_col = int(args.pop(0))
data_file = args.pop(0)
output_file = args.pop(0)

# Read data file and store average
value = []
avg = 0.0
with open(data_file, "r") as f:
    for line in f:
        if (not line.startswith("#")):
            data = line.strip().split()
            if (data == [] or int(data[time_col]) < tstart): #ignore any lines start with \n
                continue
            m = float(data[value_col])
            value.append(m)
            avg += m

avg /= float(len(value))
avgSq = avg*avg

# Compute auto-correlation
corr = []
vlen = len(value)
#print vlen

maxi = int(max_tau / tinc)

if (maxi > vlen):
    maxi = vlen

for i in xrange(maxi):
#    print i
    cov = 0.0
    for j in xrange(vlen-i):
       cov += value[j] * value[i+j]
    cov /= float(vlen-i)
    corr.append(cov - avgSq)

# Rescale correlation by corr(0)
#for i in xrange(len(corr)):
#    corr[i] /= corr[0]

writer = open(output_file, "w")
for i in xrange(maxi):
    output = "%d %.5f %.5f\n" % (i*tinc, corr[i], corr[i]/corr[0])
    writer.write(output)


                
            

    
        
    
