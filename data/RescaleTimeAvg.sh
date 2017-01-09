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
outdir=$7

#max_iter=100000
max_iter=100000
max_time=1000000

f=$f_start

list="gyr mag"

while (( $(bc <<< "$f <= $f_end") ))
do
    e=$e_start
    while (( $(bc <<< "$e <= $e_end") ))
    do
	e=$(printf "%.2f" $e)
	f=$(printf "%.2f" $f)

	for suffix in $list
	do
	    echo "Rescaling f = $f e = $e"
	    name="L_${L}_N_${N}_f_${f}_e_${e}_rc_${rc}_t_${max_iter}_run_avg"
	    file="${outdir}/${suffix}_${name}.dat"
	    name="L_${L}_N_${N}_f_${f}_e_${e}_rc_${rc}_t_${max_time}_run_avg"
	    newfile="${outdir}/${suffix}_${name}.dat"
	    tmpfile="${outdir}/${suffix}_${name}.tmp"
	    mv $file $newfile
	    awk '{$1=$1*0.01-10000; print}' $newfile > $tmpfile
	    mv $tmpfile $newfile
	done
	
	e=$(bc <<< "$e + $e_inc")
    done
    
    f=$(bc <<< "$f + $f_inc")
done
