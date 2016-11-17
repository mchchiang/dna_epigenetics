#
# GetDistribution.py
#
# A script to find the distribution of values in a 
# particular column of multiple data files.
#

import sys
import math

args = sys.argv
args.pop(0) # ignore self

data_col = int(args.pop(0))
min_val = float(args.pop(0))
max_val = float(args.pop(0))
binsize = float(args.pop(0))

n = math.ceil((max_val - min_val) / binsize)

distrb = []
for i in xrange(n):
    distrb.append(0)

# open data files

