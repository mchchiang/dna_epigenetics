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
rc=2.5
max_iter=1000000
tstart=10000
freq=20000
indir=$7
outdir=$8

outfile="${outdir}/bincum-ene_L_${L}_N_${N}_rc_2.5_t_${max_iter}.dat" 
#outfile="${outdir}/bincum-gyr_L_${L}_N_${N}_rc_2.5_t_${max_iter}.dat" 
#outfile="${outdir}/bincum-mag_L_${L}_N_${N}_rc_2.5_t_${max_iter}.dat" 
> $outfile

f=$f_start

while (( $(bc <<< "$f <= $f_end") ))
do
    e=$e_start
    while (( $(bc <<< "$e <= $e_end") ))
    do
	e=$(printf "%.2f" $e)
	f=$(printf "%.2f" $f)
	name="L_${L}_N_${N}_f_${f}_e_${e}_rc_${rc}_t_${max_iter}"
	
	echo "Calculating binder cumulant for e = ${e} f = ${f}"
	
	# Distribution of gyration radius
#	python GetBinderCumulant.py 0 1 $tstart $freq "${outdir}/bincum-gyr_${name}.dat" "${indir}/gyr-mag_${name}_run_"*.dat
	python GetBinderCumulant.py 0 4 $tstart $freq "${outdir}/bincum-ene_${name}.dat" "${indir}/thermo_${name}_run_"*.dat
#	python GetBinderCumulant.py 0 4 $tstart $freq "${outdir}/bincum-mag_${name}.dat" "${indir}/gyr-mag_${name}_run_"*.dat
	
	bincum=$(cat "${outdir}/bincum-ene_${name}.dat")
#	bincum=$(cat "${outdir}/bincum-gyr_${name}.dat")
#	bincum=$(cat "${outdir}/bincum-mag_${name}.dat")
	echo "${f} ${e} ${rc} ${bincum}" >> $outfile

       	e=$(bc <<< "$e + $e_inc")

    done
    
    f=$(bc <<< "$f + $f_inc")
done
