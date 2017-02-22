#
# GetDistribution2D.py
#
# A script to find the distribution of values in a 
# particular column of multiple data files.
#

import sys
import math

args = sys.argv
args.pop(0) # ignore self

time_col = int(args.pop(0))
x_col = int(args.pop(0))
y_col = int(args.pop(0))
x_min = float(args.pop(0))
x_max = float(args.pop(0))
y_min = float(args.pop(0))
y_max = float(args.pop(0))
x_bin = float(args.pop(0))
y_bin = float(args.pop(0))
tstart = int(args.pop(0))
freq = int(args.pop(0))
output_file = args.pop(0)

max_col = time_col
if (x_col > max_col):
    max_col = x_col
if(y_col > max_col):
    max_col = y_col

nx = int(math.ceil((x_max - x_min) / x_bin))
ny = int(math.ceil((y_max - y_min) / y_bin))

distrb = [[0 for j in xrange(ny)] for i in xrange(nx)]

# Open data files
files = [open(i, "r") for i in args]

count = 0

for f in files:
    print f
    for line in f:
        if (not line.startswith("#")):
            data = line.strip().split()
            
            # Skip blank lines or if the line does not contain enough columns
            if (data == [] or len(data) < (max_col+1)):
                continue
                
            time = int(data[time_col])

            # Skip data before start time or are not at the right time interval
            if (time < tstart or time % freq != 0):
                continue

            x = float(data[x_col])
            y = float(data[y_col])
            
            i = int(math.floor((x - x_min) / x_bin))
            j = int(math.floor((y - y_min) / y_bin))

            if (i < 0):
                print "The x value %.5f is less than min = %.5f" % (x, x_min)
            elif (j < 0):
                print "The y value %.5f is less than min = %.5f" % (y, y_min)
            elif (i >= nx):
                print "The x value %.5f is greater than or equal to max = %.5f" % (x, x_max)
            elif (j >= ny):
                print "The y value %.5f is greater than or equal to max = %.5f" % (y, y_max)
            else:
                distrb[i][j] += 1
                count += 1
            
    f.close()


for i in xrange(nx):
    for j in xrange(ny):
        distrb[i][j] /= float(count)

writer = open(output_file, "w")
for i in xrange(nx):
    for j in xrange(ny):
        xlo = i * x_bin + x_min
        xhi = (i+1) * x_bin + x_min
        xmid = (xhi - xlo) / 2.0 + xlo
        ylo = j * y_bin + y_min
        yhi = (j+1) * y_bin + y_min
        ymid = (yhi - ylo) / 2.0 + ylo
        output = "%.5f %.5f %.5f %.5f %.5f %.5f %.5f\n" % (xlo, xmid, xhi, ylo, ymid, yhi, distrb[i][j])
        writer.write(output)
    writer.write("\n")
writer.close();
