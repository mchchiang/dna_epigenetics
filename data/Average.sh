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
teq=1000000
outdir=${7}

f=$f_start

while (( $(bc <<< "$f <= $f_end") ))
do
    e=$e_start
    while (( $(bc <<< "$e <= $e_end") ))
    do
	e=$(printf "%.2f" $e)
	f=$(printf "%.2f" $f)
	name="L_${L}_N_${N}_f_${f}_e_${e}_rc_${rc}_t_${max_iter}"
	
	echo "Doing average for e = ${e} f = ${f}"
	# Average gyration radius 
	python GetAverage.py 0 10 -1 $teq "${outdir}/gyr_${name}_run_avg.dat" "${outdir}/thermo_${name}_run_"*.dat 

	# Average G factor (magnetisation)
	python GetAverage.py 0 2 -1 0 "${outdir}/mag_${name}_run_avg.dat" "${outdir}/stats_${name}_run_"*.dat 
	
	e=$(bc <<< "$e + $e_inc")
    done
    
    f=$(bc <<< "$f + $f_inc")
done
