#!/bin/bash

#
# Compute the average of multiple data sets
#

L=100
N=400
f_start=$1
f_end=$2
f_inc=$3
e_start=$4
e_end=$5
e_inc=$6
run_start=$7
run_end=$8
rc=2.5
#max_iter=100000
max_iter=1000000
#teq=1000000
teq=10000
#start_time=5000000
start_time=50000
max_tau=100000
#tinc=1000
tinc=10
outdir=$9

f=$f_start

while (( $(bc <<< "$f <= $f_end") ))
do
    e=$e_start
    while (( $(bc <<< "$e <= $e_end") ))
    do
	for ((run=$run_start;run<=$run_end;run++)); do
	    e=$(printf "%.2f" $e)
	    f=$(printf "%.2f" $f)
	    name="L_${L}_N_${N}_f_${f}_e_${e}_rc_${rc}_t_${max_iter}"
	
	    echo "Calculating correlation for e = ${e} f = ${f} run = ${run}"
	    
	    # Correlation for gyration radius
	    python GetCorrelation.py $start_time $max_tau $tinc 0 2 "${outdir}/stats_${name}_run_${run}.dat" "${outdir}/corr_mag_${name}_run_${run}.dat"
	
	done
	
	e=$(bc <<< "$e + $e_inc")
    done
    
    f=$(bc <<< "$f + $f_inc")
done
