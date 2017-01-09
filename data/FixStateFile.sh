#!/bin/bash

# A script to combine the measured gyration radius and the magnetisation

L=100
N=100
rc=2.5

f_start=$1
f_end=$2
f_inc=$3
e_start=$4
e_end=$5
e_inc=$6
run_start=$7
run_end=$8
run_inc=$9
outdir=${10}


max_time=100000

f=$f_start


while (( $(bc <<< "$f <= $f_end") ))
do
    e=$e_start
    while (( $(bc <<< "$e <= $e_end") ))
    do
	e=$(printf "%.2f" $e)
	f=$(printf "%.2f" $f)

	for ((run=$run_start;run<=$run_end;run+=$run_inc))
	do
	    echo "Rescaling f = $f e = $e run = $run"
	    name="L_${L}_N_${N}_f_${f}_e_${e}_rc_${rc}_t_${max_time}_run_${run}"
	    file="${outdir}/state_${name}.dat"
	    tmpfile="${outdir}/state_${name}.tmp"
	    awk '$1!="-10000"{print $0}$1=="-10000"{$1=""; print}' $file > $tmpfile
#	    awk '$1!="-10000"{print $0}' $file > $tmpfile
	    mv $tmpfile $file
	done
	
	e=$(bc <<< "$e + $e_inc")
    done
    
    f=$(bc <<< "$f + $f_inc")
done
