#!bin/bash

L=150
N=1000
phi=0.001 # fraction of static atoms
nc=1 # cluster size
f_start=$1
f_end=$2
f_inc=$3
e_start=$4
e_end=$5
e_inc=$6
rc=2.5
max_run=$7
run_shift=$8
max_job=$9
tmax=1000000
teq=10000
tcolour=10
atom_type="0"
static_type="single_a"
sim_type="swollen"
dumpxyz="dump"
dumpstate="state"
exepath="./"
outdir=${10}
nohup=${11}
nproc=1
print_freq=1000

cmd=()
log=()
jobid=1

phi=$(printf "%.3f" $phi)
f=$f_start

while (( $(bc <<< "$f <= $f_end") ))
do
    e=$e_start
    while (( $(bc <<< "$e <= $e_end") ))
    do
	e=$(printf "%.2f" $e)
	f=$(printf "%.2f" $f)
	for (( run=1; run <= $max_run; run++ ))
	do
	    runid=$(bc <<< "$run + $run_shift")

	    if [ $runid != 1 ]; then
		dumpxyz="dump"
		dumpstate="state"
	    fi
	    
	    cmd[$jobid]="bash epigenetics.sh ${L} ${N} ${f} ${e} ${rc} ${phi} ${nc} ${tcolour} ${tmax} ${teq} ${runid} ${atom_type} ${static_type} ${sim_type} ${dumpxyz} ${dumpstate} ${exepath} ${nproc} ${outdir} ${print_freq}"
	    log[$jobid]="nohup_L_${L}_N_${N}_f_${f}_e_${e}_rc_${rc}_phi_${phi}_nc_${nc}_t_${tmax}_run_${runid}.log"
	    jobid=$(bc <<< "$jobid + 1")
	done
	
	e=$(bc <<< "$e + $e_inc")
    done
    
    f=$(bc <<< "$f + $f_inc")
done

total_jobs=$(bc <<< "$jobid - 1")
jobid=1

while (( $(bc <<< "$jobid <= $total_jobs") ))
do
    for ((i=1; i <= $max_job && $jobid <= $total_jobs ; i++))
    do
	if [ $nohup = "true" ]; then
	    echo "nohup ${cmd[jobid]} &> ${log[jobid]} &"
	    nohup ${cmd[jobid]} &> ${log[jobid]} &
	else
	    echo "${cmd[jobid]} &"
	    ${cmd[jobid]} &
	fi
	jobid=$(bc <<< "$jobid + 1")
    done
    wait
done

