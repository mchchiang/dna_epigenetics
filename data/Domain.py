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

in_file = args.pop(0) # state file name
out_file = args.pop(0) # output file name

n = 1000 # num of atoms
site_col = 0 # index column

NUM_OF_STATE = 6

# perfect domain function
domain = [0.0 for i in xrange(n)]
num_of_domains = 10
domain_size = n / num_of_domains

for i in xrange(num_of_domains):
    if (i % 2 == 0):
        for j in xrange(100):
            domain[j+i*100] = 1.0

#print domain

# Read data file and compute difference

diffSq = 0.0

site = 0
val_col = 3 # default value column

with open(in_file, "r") as f:
    for line in f:
        data = line.strip().split()
        if (site == 0):
            diff1 = (float(data[3]) - domain[site]) ** 2
            diff2 = (float(data[5]) - domain[site]) ** 2
            if (diff1 > diff2):
                val_col = 5
            else:
                val_col = 3
        site = int(data[site_col])-1
        diff = float(data[val_col]) - domain[site]
        diffSq += diff * diff;

diffSq /= float(n)
diffSq = math.sqrt(diffSq)

writer = open(out_file, "w")
output = "%.5f" % (diffSq)
writer.write(output)
writer.close()
