#!/bin/bash

# read in parameters
num_of_atoms=$1
box_size=$2
ratio=$3
bond_energy=$4
cut_off=$5
max_iter=$6
seed=$7
run=$8
exepath=$9
nproc=${10}
outdir=${11}     # output directory relative to current directory

type_of_atoms=3

name="f_${ratio}_e_${bond_energy}_rc_${cut_off}_t_${max_iter}_run_${run}"
thermo_file="thermo_${name}.dat"
xyz_file="vmd_${name}.xyz"

outdir="`pwd`/${outdir}"

# create the lammps command file based on template
file=epigenetics.lam
cp epigenetics-temp.lam ${exepath}/$file

# init dna strand
seed2=$(bc <<< "$seed+34987")

cd ${exepath}

java dna_epigenetics.LAMMPSIO $num_of_atoms $type_of_atoms $box_size $box_size $box_size $seed2 initdna.in

# replace macros in template with input values
sed -i -- "s/NUMOFATOMS/${num_of_atoms}/g" $file
sed -i -- "s/RATIO/${ratio}/g" $file
sed -i -- "s/BONDENERGY/${bond_energy}/g" $file
sed -i -- "s/CUTOFF/${cut_off}/g" $file
sed -i -- "s/MAXITER/${max_iter}/g" $file
sed -i -- "s/SEED/${seed}/g" $file
sed -i -- "s/RUN/${run}/g" $file
sed -i -- "s/XYZFILE/${xyz_file}/g" $file

# run the simulation
if (( $(bc <<< "$nproc == 1") )); then
    lmp_serial < $file
else
    mpirun -n $nproc lmp_mpi < $file
fi

# write the thermo data
python GetThermoData.py log.lammps "${outdir}/${thermo_file}"

# move other files to output directory
mv $xyz_file "${outdir}/${xyz_file}"

cd $PWD