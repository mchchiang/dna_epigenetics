#!/bin/bash

# A script to combine the measured gyration radius and the magnetisation

L=150
N=1000
rc=2.5
nc=1
phi=0.10
f_start=$1
f_end=$2
f_inc=$3
e_start=$4
e_end=$5
e_inc=$6
run_start=$7
run_end=$8
indir=$9
outdir=${10}

max_iter=1000000

f=$f_start

while (( $(bc <<< "$f <= $f_end") ))
do
    f=$(printf "%.2f" $f)    

    e=$e_start
    while (( $(bc <<< "$e <= $e_end") ))
    do
	e=$(printf "%.2f" $e)
	
	for ((run=$run_start;run<=$run_end;run+=1))
	do
	    echo "Creating kemograph for f = $f e = $e run = $run"
	    name="L_${L}_N_${N}_f_${f}_e_${e}_rc_${rc}_phi_${phi}_nc_${nc}_t_${max_iter}_run_${run}"
	    statefile="${indir}/state_${name}.dat"
	    kemofile="${outdir}/kemo_${name}.dat"
	    python Kemograph.py $N $statefile $kemofile
	done
	
	e=$(bc <<< "$e + $e_inc")
    done
    
    f=$(bc <<< "$f + $f_inc")
done
