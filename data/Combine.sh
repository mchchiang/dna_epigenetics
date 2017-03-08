#!/bin/bash

# A script to combine the measured gyration radius and the magnetisation

L=150
N=1000
rc=2.5

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

#max_iter=12000 #old format
max_iter=3000000 #new format

f=$f_start

while (( $(bc <<< "$f <= $f_end") ))
do
    f=$(printf "%.2f" $f)    
#    outdir="phase_diagram/data_f_${f}/"
#    outdir="./"
    e=$e_start
    while (( $(bc <<< "$e <= $e_end") ))
    do
	e=$(printf "%.2f" $e)
	
#	for ((run=$run_start;run<=$run_end;run+=1))
	for run in 1 2 3 4 5 6 7 8 10 11 12 13
	do
	    echo "Combining f = $f e = $e run = $run"
	    name="L_${L}_N_${N}_f_${f}_e_${e}_rc_${rc}_t_${max_iter}_run_${run}"
	    statsfile="${indir}/stats_${name}.dat"
	    thermofile="${indir}/thermo_${name}.dat"
	    combinedfile="${outdir}/gyr-mag_${name}.dat"
	    > $combinedfile
	    
#	    paste -d ' ' <(awk 'NR>2 {print $1 " " $11}' $thermofile) <(awk '{printf "%.5f %.5f %.5f\n",$2,$3,($6-$4)/($4+$5+$6)}' $statsfile) > $combinedfile #old format
	    paste -d ' ' <(awk '{print $1 " " $11}' $thermofile) <(awk '{printf "%.5f %.5f %.5f\n",$2,$3,($6-$4)/($4+$5+$6)}' $statsfile) > $combinedfile #new format

	done
	
	e=$(bc <<< "$e + $e_inc")
    done
    
    f=$(bc <<< "$f + $f_inc")
done
