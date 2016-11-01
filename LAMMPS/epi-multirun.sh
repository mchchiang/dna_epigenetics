#!bin/bash

L=100
N=1000
f_start=$1
f_end=$2
f_inc=$3
e_start=$4
e_end=$5
e_inc=$6
rc=2.5
max_job=$7
max_iter=$8
teq=1000000
dump=dump
exepath="../Java/build/classes/"
outdir=$9
nproc=1

cmd=()
log=()
count=1

f=$f_start
e=$e_start

while (( $(bc <<< "$f <= $f_end") ))
do
    while (( $(bc <<< "$e <= $e_end") ))
    do
	for (( run=1; run<$max_run; run++ ))
	do
#	    cmd[count]="bash epigenetics.sh ${L} ${N} ${f} ${e} ${rc} ${max_iter} ${teq} ${run} ${dump} ${exepath} ${nproc} ${outdir}"
	    log[count]="nohup_L_${L}_N_${N}_f_${f}_e_${e}_rc_${rc}_t_${max_iter}_run_${run}.log"
	#    count=$(bc <<< "$count + 1")
	    
	    if (( $(bc <<< "$count > $max_job") )); then
		for (( job=1; job<$max_job; job++ ))
		do
		    nohup ${cmd[job]} &> ${log[job]} &
		done
		wait
		count=1
		cmd=()
		log=()
	    fi

	done
	
	e=$(bc <<< "$e + $e_inc")
    done
    
    f=$(bc <<< "$f + $f_inc")
done

if (( $(bc <<< "$count > 1 && $count <= $max_job") )); then
    for (( job=1; job<$count; job++ ))
    do
	echo nohup ${cmd[job]} &> ${log[job]} &
    done
    wait
fi