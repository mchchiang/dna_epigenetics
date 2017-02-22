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
max_iter=100000
tstart=3000000
freq=$7
indir=$8
outdir=$9
binsize=0.1
min=1
max=20


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
	python GetDistribution.py 0 10 $min $max $binsize $tstart $freq "${outdir}/prob_gyr_${name}.dat" "${indir}/thermo_${name}_run_"*.dat
	
       	e=$(bc <<< "$e + $e_inc")

    done
    
    f=$(bc <<< "$f + $f_inc")
done
