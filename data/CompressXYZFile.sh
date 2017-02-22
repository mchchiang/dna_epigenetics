#!/bin/bash

#
# Compress multiple xyz files
#

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
max_iter=1000
teq=1000000
indir=$9
outdir=${10}

f=$f_start

while (( $(bc <<< "$f <= $f_end") ))
do
    e=$e_start
    while (( $(bc <<< "$e <= $e_end") ))
    do
	for ((run=run_start;run<=run_end;run++)); do
	    e=$(printf "%.2f" $e)
	    f=$(printf "%.2f" $f)
	    name="L_${L}_N_${N}_f_${f}_e_${e}_rc_${rc}_t_${max_iter}"
	    infile="${indir}/vmd_${name}_run_${run}.xyz"
	    outfile="${outdir}/vmd_${name}_run_${run}_comp.xyz"

	    echo "Compressing xyz file for e = ${e} f = ${f} run = ${run}"
	    
	    # Compress XYZ file
	    python CompressXYZFile.py $N $infile $outfile
	
	    mv $outfile $infile

	done
	
	e=$(bc <<< "$e + $e_inc")
    done
    
    f=$(bc <<< "$f + $f_inc")
done
