#!bin/bash

L=150
N=1000
phi=0.07 # fraction of static atoms
nc=7 # cluster size
f_start=$1
f_end=$2
f_inc=$3
e_start=$4
e_end=$5
e_inc=$6
rc=2.5
run_start=$7
run_end=$8
#rundir=$9
tmax=1000000
teq=10000
tcolour=10
atom_type=0
static_type="domain_a"
sim_type="swollen"
dumpxyz="dump"
dumpstate="state"
print_freq=1000
phi2=$(printf "%.2f" $phi)
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
	    echo "Getting thermo data for f = ${f} e = ${e} phi = ${phi} run = ${run}"
	    name="L_${L}_N_${N}_f_${f}_e_${e}_rc_${rc}_phi_${phi}_nc_${nc}_t_${tmax}_run_${run}"
	    dir="./phi_${phi2}/run_${run}_f_${f}_e_${e}_phi_${phi}_nc_${nc}"
	    log="${dir}/logfile.lammps"
	    thermo="${dir}/thermo_${name}.dat"
	    python GetThermoData.py $log $thermo &
	done
	
	e=$(bc <<< "$e + $e_inc")
    done
    
    f=$(bc <<< "$f + $f_inc")
done

