#!/bin/bash

#
# Compute the average of multiple data sets
#

L=100
N=200
f_start=$1
f_end=$2
f_inc=$3
e_start=$4
e_end=$5
e_inc=$6
rc=2.5
max_iter=100000
tstart=3000000
freq=100000
outdir=$7
xbin=0.05
ybin=0.1
xmin="-1.1"
xmax="1.1"
ymin=2
ymax=25
xcol=4
ycol=1
tcol=0

#python GetDistribution2D.py 0 1 4 2 16 -1.1 1.1 0.1 0.1 3000000 100000 data_f_2.0/run10_N_100_t_100000/prob_L_100_N_100_f_2.00_e_0.66_rc_2.5_t_100000.dat data_f_2.0/run10_N_100_t_100000/gyr-mag_L_100_N_100_f_2.00_e_0.66_rc_2.5_t_100000_run_*.dat

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