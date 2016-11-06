import sys
from itertools import izip
import math

args = sys.argv
args.pop(0) #ignore self

startpt = int(args.pop())
avg_col = int(args.pop())
ref_col = int(args.pop())
output_file = args.pop()

files = [open(i, "r") for i in args]
writer = open(output_file, "w")

for rows in izip(*files):
    ref = 0.0
    avg = 0.0
    avgSq = 0.0
    hasData = False

    for line in rows:
        if (not line.startswith("#")):
            data = line.strip().split()
            if (data == []): #ignore any lines start with \n
                break
            
            ref = int(data[ref_col])
            if (ref >= startpt):
                value = float(data[avg_col])
                avg += value
                avgSq += value * value
                hasData = True

    if (hasData == True):
        n = float(len(rows))
        avg /= n
        avgSq /= n
        # use un-biased estimate of variance
        var = n / (n-1) * (avgSq - avg*avg)
        sigma = math.sqrt(var) 
        error = sigma / math.sqrt(n)
        output = "%d %.5f %.5f %.5f\n" % (ref, avg, sigma,  error)
        writer.write(output)

for f in files:
    f.close()
writer.close()
    
        
    
