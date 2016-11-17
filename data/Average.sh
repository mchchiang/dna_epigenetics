
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
teq=1000000
outdir=${7}

f=$f_start

while (( $(bc <<< "$f <= $f_end") ))
do
    e=$e_start
    while (( $(bc <<< "$e <= $e_end") ))
    do
	e=$(printf "%.1f" $e)
	f=$(printf "%.1f" $f)
	name="L_${L}_N_${N}_f_${f}_e_${e}_rc_${rc}_t_${max_iter}"
	
	echo "Doing average for e = ${e} f = ${f}"
	# Average gyration radius 
	python GetAverage.py "${outdir}/thermo_${name}_run_"*.dat "${outdir}/gyr_${name}_run_avg.dat" 0 10 $teq

	# Average G factor (magnetisation)
	python GetAverage.py "${outdir}/stats_${name}_run_"*.dat "${outdir}/mag_${name}_run_avg.dat" 0 1 0

	# Average energy
#	python GetAverage.py "${outdir}/thermo_${name}_run_*.dat ${outdir}/energy_${name}_run_avg.dat" 0 10 1000000
	
	e=$(bc <<< "$e + $e_inc")
    done
    
    f=$(bc <<< "$f + $f_inc")
done
