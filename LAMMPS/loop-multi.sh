#!/bin/bash

cmd=()
log=()
max_jobs=3
count=1
jobs=8

for ((i=1;i<=$jobs;i++))
do
    echo $i
    cmd[count]="bash loop.sh $i"
    log[count]="nohup_${i}.out"
    count=$(bc <<< "$count + 1")
    if (( $(bc <<< "$count > $max_jobs") )); then
	for ((j=1;j<=$max_jobs;j++))
	do
	    echo ${cmd[j]}
	    nohup ${cmd[j]} &> ${log[j]} &
	done
	wait
	count=1
	cmd=()
	log=()
    fi
done

if (( $(bc <<< "$count > 1 && $count <= $max_jobs") )); then
    for ((j=1;j<$count;j++))
    do
	echo ${cmd[j]}
	nohup ${cmd[j]} &> ${log[j]} &
    done
    wait
fi
