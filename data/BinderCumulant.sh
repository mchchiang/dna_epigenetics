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
rc=2.5
max_iter=5000
tstart=3000000
freq=$7
outdir=$8

outfile="${outdir}/bincum_L_${L}_N_${N}_rc_2.5_t_${max_iter}.dat" 
> $outfile

f=$f_start

while (( $(bc <<< "$f <= $f_end") ))
do
    e=$e_start
    while (( $(bc <<< "$e <= $e_end") ))
    do
	e=$(printf "%.1f" $e)
	f=$(printf "%.1f" $f)
	name="L_${L}_N_${N}_f_${f}_e_${e}_rc_${rc}_t_${max_iter}"
	
	echo "Calculating binder cumulant for e = ${e} f = ${f}"
	
	# Distribution of gyration radius
	python GetBinderCumulant.py 0 10 $tstart $freq "${outdir}/bincum_${name}.dat" "${outdir}/thermo_${name}_run_"*.dat
	
	bincum=$(cat "${outdir}/bincum_${name}.dat")
	echo "${f} ${e} ${rc} ${bincum}" >> $outfile

       	e=$(bc <<< "$e + $e_inc")

    done
    
    f=$(bc <<< "$f + $f_inc")
done
