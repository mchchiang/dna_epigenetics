#!/bin/bash

# A script to combine the measured gyration radius and the magnetisation

L=150
N=1000
rc=2.5
#nc=1
#phi=0.10
f_start=$1
f_end=$2
f_inc=$3
e_start=$4
e_end=$5
e_inc=$6
phi_start=$7
phi_end=$8
phi_inc=$9
run_start=${10}
run_end=${11}
indir=${12}
outdir=${13}

max_iter=1000000

f=$f_start

while (( $(bc <<< "$f <= $f_end") ))
do
    f=$(printf "%.2f" $f)    

    e=$e_start
    while (( $(bc <<< "$e <= $e_end") ))
    do
	e=$(printf "%.2f" $e)

	phi=$phi_start
	
	while (( $(bc <<< "$phi <= $phi_end") ))
	do
	    phi=$(printf "%.3f" $phi)
	    nc=$(bc <<< "100 * $phi")
	    nc=$(printf "%.0f" $nc)
	    for ((run=$run_start;run<=$run_end;run+=1))
	    do
		echo "Creating kemograph for f = $f e = $e run = $run"
		name="L_${L}_N_${N}_f_${f}_e_${e}_rc_${rc}_phi_${phi}_nc_${nc}_t_${max_iter}"
		magdistfile="${outdir}/mag-dist_${name}_run_${run}.dat"
		domainfile="${outdir}/domain_${name}_run_${run}.dat"
		python ../../Domain.py $magdistfile $domainfile

	    done
	    
	    avgfile="${outdir}/domain_${name}.dat"
	    combinefile="${outdir}/domain.dat"
	    python ../../GetAverage.py -1 0 -1 -1 $avgfile "${outdir}/domain_${name}_run_"*.dat
	    data=$(cat $avgfile)
	    echo "$phi $data" >> $combinefile
	    phi=$(bc <<< "$phi + $phi_inc")

	done
	
	e=$(bc <<< "$e + $e_inc")
    done
    
    f=$(bc <<< "$f + $f_inc")
done
