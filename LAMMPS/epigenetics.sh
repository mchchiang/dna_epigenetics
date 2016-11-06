#!/bin/bash

# read in parameters
box_size=$1
num_of_atoms=$2
ratio=$3
bond_energy=$4
cut_off=$5
max_iter=$6
teq=$7
run=$8
dumpxyz=$9
dumpstate=${10}
exepath=${11}
nproc=${12}
outdir=${13}     # output directory relative to current directory

type_of_atoms=3

seed=$(python GetRandom.py 100000)

name="L_${box_size}_N_${num_of_atoms}_f_${ratio}_e_${bond_energy}_rc_${cut_off}_t_${max_iter}_run_${run}"
thermo_file="thermo_${name}.dat"
xyz_file="vmd_${name}.xyz"
init_file="init_${name}.in"
in_file="dna_${name}.in"
out_file="dna_${name}.out"
stats_file="stats_${name}.dat"
state_file="none"  
if [ $dumpstate != "nostate" ]; then
    state_file="state_${name}.dat"
fi

outdir="`pwd`/${outdir}"
basedir=`pwd`

# create the lammps command file based on template
file="epi_${name}.lam"
cp epigenetics-temp.lam ${exepath}/$file

# init dna strand
seed2=$(bc <<< "$seed+34987")

cd ${exepath}

java dna_epigenetics.LAMMPSIO $num_of_atoms $type_of_atoms $box_size $box_size $box_size $seed2 $init_file

# replace macros in template with input values
sed -i -- "s/NUMOFATOMS/${num_of_atoms}/g" $file
sed -i -- "s/RATIO/${ratio}/g" $file
sed -i -- "s/BONDENERGY/${bond_energy}/g" $file
sed -i -- "s/CUTOFF/${cut_off}/g" $file
sed -i -- "s/MAXITER/${max_iter}/g" $file
sed -i -- "s/SEED/${seed}/g" $file
sed -i -- "s/RUN/${run}/g" $file
sed -i -- "s/XYZFILE/${xyz_file}/g" $file
sed -i -- "s/INITFILE/${init_file}/g" $file
sed -i -- "s/INFILE/${in_file}/g" $file
sed -i -- "s/OUTFILE/${out_file}/g" $file
sed -i -- "s/LAMMPSFILE/${file}/g" $file
sed -i -- "s/STATEFILE/${state_file}/g" $file
sed -i -- "s/STATSFILE/${stats_file}/g" $file

# calculate equilibrium times
equil_soft=1000
equil_fene=$(bc <<< "$teq-$equil_soft")

sed -i -- "s/EQUIL_SOFT/${equil_soft}/g" $file
sed -i -- "s/EQUIL_FENE/${equil_fene}/g" $file
sed -i -- "s/EQUIL_TOTAL/${teq}/g" $file

# set whether to dump xyz files
dumpxyz_flag=''
if [ $dumpxyz != "dump" ]; then
   dumpxyz_flag='#'
fi
sed -i -- "s/DUMPFLAG/${dumpxyz_flag}/g" $file

# clear any previous entries in the state and stats file
if [ $state_file != "none" ]; then
    > $state_file
fi

if [ $stats_file != "none" ]; then
    > $stats_file
fi

# run the simulation
logfile="log_${name}.lammps"
if (( $(bc <<< "$nproc == 1") )); then
    lmp_serial -screen none -log $logfile -in $file
else
    mpirun -n $nproc -screen none lmp_mpi -log $logfile -in $file
fi

# write the thermo data
python "${basedir}/GetThermoData.py" $logfile "${outdir}/${thermo_file}"

# move other files to output directory
if [ $state_file != "none" ]; then
    mv $state_file "${outdir}/${state_file}"
fi

if [ $stats_file != "none" ]; then
    mv $stats_file "${outdir}/${stats_file}"
fi

if [ $dumpxyz = "dump" ]; then
    mv $xyz_file "${outdir}/${xyz_file}"
fi

cd $PWD
