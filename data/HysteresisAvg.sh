#!/bin/bash


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
max_iter=2000000
tstart=0
tinc=10
teinc=2500
tsample=10

timeAvgPyFile="./HysteresisTimeAverage.py"
avgPyFile="./GetAverage.py"

f=$f_start

while (( $(bc <<< "$f <= $f_end") ))
do
    e=$e_start
    f=$(printf "%.2f" $f)

    dir="./hysteresis/run3_N_1000_e_0.45-0.85_t_2000000/"
    
    while (( $(bc <<< "$e <= $e_end") ))
    do
	e=$(printf "%.2f" $e)

	name="L_${L}_N_${N}_f_${f}_e_${e}_rc_${rc}_t_${max_iter}"
	
	gyr_avg="${dir}/hys-mag_${name}.dat"

	echo "Doing average for e = ${e} f = ${f}"
	
	for ((run=$run_start;run<=$run_end;run++))
	do
	    if [ $run != 8 ]; then 
		gyr_in="${dir}/stats_${name}_run_${run}.dat"
		gyr_out="${dir}/hys-mag_${name}_run_${run}.dat"
	  
		python $timeAvgPyFile $tstart $tinc $teinc $tsample 0 2 -1 $gyr_in $gyr_out
	    fi
	done

	python $avgPyFile 0 1 3 -1 $gyr_avg "${dir}/hys-mag_${name}_run_"*.dat

	e=$(bc <<< "$e + $e_inc")
    done

    f=$(bc <<< "$f + $f_inc")
done
