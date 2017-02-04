#!/bin/bash


L=100
N=100
f_start=$1
f_end=$2
f_inc=$3
e_start=$4
e_end=$5
e_inc=$6
run_start=$7
run_end=$8
rc=2.5
max_iter=12000
tstart=2000000
tinc=100000

timeAvgPyFile="../GetTimeAverage.py"
avgPyFile="../GetAverage.py"

f=$f_start

while (( $(bc <<< "$f <= $f_end") ))
do
    e=$e_start
    f=$(printf "%.2f" $f)

    dir="./data_f_${f}"
    
    while (( $(bc <<< "$e <= $e_end") ))
    do
	e=$(printf "%.2f" $e)

	name="L_${L}_N_${N}_f_${f}_e_${e}_rc_${rc}_t_${max_iter}"
	
	gyr_avg="./gyr_${name}.dat"
	mag_avg="./mag_${name}.dat"

	echo "Doing average for e = ${e} f = ${f}"
	
	for ((run=$run_start;run<=$run_end;run++))
	do
	    gyr_in="${dir}/thermo_${name}_run_${run}.dat"
	    gyr_out="${dir}/gyr_${name}_run_${run}.dat"
	    mag_in="${dir}/stats_${name}_run_${run}.dat"
	    mag_out="${dir}/mag_${name}_run_${run}.dat"

	    python $timeAvgPyFile $tstart $tinc 0 10 $gyr_in $gyr_out
	    python $timeAvgPyFile $tstart $tinc 0 2 $mag_in $mag_out
	done

	python $avgPyFile -1 0 2 -1 $gyr_avg "${dir}/gyr_${name}_run_"*.dat
	python $avgPyFile -1 0 2 -1 $mag_avg "${dir}/mag_${name}_run_"*.dat

	rm "${dir}/gyr_${name}_run_"*.dat
	rm "${dir}/mag_${name}_run_"*.dat

	e=$(bc <<< "$e + $e_inc")
    done

    f=$(bc <<< "$f + $f_inc")
done
