#!/bin/bash

#
# Compute the average of multiple data sets
#

L=150
N=1000
f_start=$1
f_end=$2
f_inc=$3
e_start=$4
e_end=$5
e_inc=$6
run_start=$7
run_end=$8
rc=2.5
max_iter=5000
teq=1000000
start_time=$9
max_tau=${10}
tinc=1000
outdir=${11}

f=$f_start

while (( $(bc <<< "$f <= $f_end") ))
do
    e=$e_start
    while (( $(bc <<< "$e <= $e_end") ))
    do
	for ((run=run_start;run<=run_end;run++)); do
	    e=$(printf "%.1f" $e)
	    f=$(printf "%.1f" $f)
	    name="L_${L}_N_${N}_f_${f}_e_${e}_rc_${rc}_t_${max_iter}"
	
	    echo "Calculating correlation for e = ${e} f = ${f} run = ${run}"
	    
	    # Correlation for gyration radius
	    python GetCorrelation.py $start_time $max_tau $tinc 0 10 "${outdir}/thermo_${name}_run_${run}.dat" "${outdir}/corr_thermo_${name}_run_${run}.dat"
	
	done
	
	e=$(bc <<< "$e + $e_inc")
    done
    
    f=$(bc <<< "$f + $f_inc")
done
