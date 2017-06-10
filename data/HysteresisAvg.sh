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
indir=$9
outdir=${10}
rc=2.5
max_iter=3000000
tstart=0
tinc=10
teinc=2500
tsample=10

timeAvgPyFile="$MProj/data/HysteresisTimeAverage.py"
avgPyFile="$MProj/data/GetAverage.py"

f=$f_start

while (( $(bc <<< "$f <= $f_end") ))
do
    e=$e_start
    f=$(printf "%.2f" $f)

    while (( $(bc <<< "$e <= $e_end") ))
    do
	e=$(printf "%.2f" $e)

	name="L_${L}_N_${N}_f_${f}_e_${e}_rc_${rc}_t_${max_iter}"
	
	gyr_avg="${outdir}/hys-gyr_${name}.dat"
	mag_avg="${outdir}/hys-mag_${name}.dat"

	echo "Doing average for e = ${e} f = ${f}"
	
#	for ((run=$run_start;run<=$run_end;run++))
	for run in 10 11 12 13 15
	do
	    gyr_in="${indir}/gyr-mag_${name}_run_${run}.dat"
	    gyr_out="${outdir}/hys-gyr_${name}_run_${run}.dat"
	    mag_in="${indir}/gyr-mag_${name}_run_${run}.dat"
	    mag_out="${outdir}/hys-mag_${name}_run_${run}.dat"
	    
	    python $timeAvgPyFile $tstart $tinc $teinc $tsample 0 1 -1 $gyr_in $gyr_out
	    python $timeAvgPyFile $tstart $tinc $teinc $tsample 0 4 -1 $mag_in $mag_out
	    
	done

	python $avgPyFile 0 1 -1 -1 $gyr_avg "${outdir}/hys-gyr_${name}_run_"*.dat
	python $avgPyFile 0 1 -1 -1 $mag_avg "${outdir}/hys-mag_${name}_run_"*.dat

	e=$(bc <<< "$e + $e_inc")
    done

    f=$(bc <<< "$f + $f_inc")
done
