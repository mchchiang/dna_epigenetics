#!/bin/bash

#
# Compute the average of multiple data sets
#

L=100
N=100
f_start=$1
f_end=$2
f_inc=$3
e_start=$4
e_end=$5
e_inc=$6
rc=2.5
#max_iter=100000 #old format
max_iter=1000000 #new format
#tstart=3000000 #old format
tstart=10000 #new format
#freq=100000 #old format
freq=10000 #new format
outdir=$7
#xbin=0.05
xbin=0.1
#ybin=0.1
ybin=0.5
xmin="-1.1"
xmax="1.1"
ymin=2
ymax=20
xcol=4
ycol=1
tcol=0


f=$f_start

while (( $(bc <<< "$f <= $f_end") ))
do
    e=$e_start
    while (( $(bc <<< "$e <= $e_end") ))
    do
	e=$(printf "%.2f" $e)
	f=$(printf "%.2f" $f)
	name="L_${L}_N_${N}_f_${f}_e_${e}_rc_${rc}_t_${max_iter}"
	
	echo "Calculating distribution for e = ${e} f = ${f}"
	
	# Distribution of gyration radius
	python GetDistribution2D.py $tcol $xcol $ycol $xmin $xmax $ymin $ymax $xbin $ybin $tstart $freq "${outdir}/prob_${name}.dat" "${outdir}/gyr-mag_${name}_run_"*.dat
	
       	e=$(bc <<< "$e + $e_inc")

    done
    
    f=$(bc <<< "$f + $f_inc")
done
