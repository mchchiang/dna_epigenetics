#!bin/bash

L=100
N=100
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
max_iter=100000
teq=1000000
order=${10}
collapse=${11}
dumpxyz="dump"
dumpstate=${12}
exepath="./"
outdir=${13}
nohup=${14}
nproc=1
print_freq=10000

cmd=()
log=()
jobid=1

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

	    if [ $runid = 1 ]; then
		dumpxyz=dump
	    else
		dumpxyz=dump
	    fi

	    cmd[$jobid]="bash epigenetics.sh ${L} ${N} ${f} ${e} ${rc} ${max_iter} ${teq} ${runid} ${order} ${collapse} ${dumpxyz} ${dumpstate} ${exepath} ${nproc} ${outdir} ${print_freq}"
	    log[$jobid]="nohup_L_${L}_N_${N}_f_${f}_e_${e}_rc_${rc}_t_${max_iter}_run_${runid}.log"
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

