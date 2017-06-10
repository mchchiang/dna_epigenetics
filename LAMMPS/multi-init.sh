#!bin/bash

L=150
N=1000
phi=0.10 # fraction of static atoms
nc=10 # cluster size
f_start=$1
f_end=$2
f_inc=$3
e_start=$4
e_end=$5
e_inc=$6
rc=2.5
run_start=$7
run_end=$8
rundir=$9
tmax=1000000
teq=10000
tcolour=10
atom_type=0
static_type="cluster"
sim_type="swollen"
dumpxyz="dump"
dumpstate="state"
print_freq=1000

phi=$(printf "%.3f" $phi)
f=$f_start

while (( $(bc <<< "$f <= $f_end") ))
do
    e=$e_start
    f=$(printf "%.2f" $f)

    while (( $(bc <<< "$e <= $e_end") ))
    do
	e=$(printf "%.2f" $e)

	for (( run=$run_start; run <= $run_end; run++ ))
	do
	    if [ $run != 1 ]; then
		dumpxyz="dump"
		dumpstate="state"
	    fi
	    
	    bash init.sh ${L} ${N} ${f} ${e} ${rc} ${phi} ${nc} ${tcolour} ${tmax} ${teq} ${run} ${atom_type} ${static_type} ${sim_type} ${dumpxyz} ${dumpstate} ${rundir} ${print_freq}

	done
	
	e=$(bc <<< "$e + $e_inc")
    done
    
    f=$(bc <<< "$f + $f_inc")
done

