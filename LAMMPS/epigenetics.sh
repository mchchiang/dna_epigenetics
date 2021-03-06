#!/bin/bash

# read in parameters
box_size=$1      # simulation box size
num_of_atoms=$2  # number of atoms
ratio=$3         # feedback ratio in Sneppen model (i.e. F = alpha / (1 - alpha) )
bond_energy=$4   # bond energy in pairwise potential
cut_off=$5       # pairwise potential cut-off parameter
tcolour=$6       # recolour time (in Brownian time units)
tmax=$7          # maximum simulation time (in Brownian time units), must be multiple of tcolour 
teq=$8           # total equlibration steps
run=$9           # trial number
order=${10}      # order or disorder
simtype=${11}    # collapse, swollen, or hysteresis
dumpxyz=${12}    # dump or nodump
dumpstate=${13}  # nostate or state
exepath=${14}    # execution path (../Java/build/classes/)
nproc=${15}      # number of processes to run with
outdir=${16}     # output directory relative to current directory
print_freq=${17} # dump atoms' positions frequency (in Brownian time units)

type_of_atoms=3  # types of atoms
delta_t=0.01     # time step size in Brownian time units
colour_step=$(bc <<< "$tcolour/$delta_t")
max_iter=$(bc <<< "$tmax/$delta_t/$colour_step")
print_freq=$(bc <<< "$print_freq/$delta_t")  # actual print frequency in simulation steps

# generate a random seed for initialising dna and running lammps
seed=$(python GetRandom.py 100000)

# generate the data/output file names
name="L_${box_size}_N_${num_of_atoms}_f_${ratio}_e_${bond_energy}_rc_${cut_off}_t_${tmax}_run_${run}"
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

# choose template depending on initial conditions (collasped/swollen)
if [ $simtype = "collapse" ]; then
    cp epigenetics-collapse.lam $file
    teq_collapse=10000
    equil_collapse=$(bc <<< "${teq_collapse}/${delta_t}")
    sed -i -- "s/EQUIL_COLLAPSE/${equil_collapse}/g" $file
elif [ $simtype = "hysteresis" ]; then
    cp epigenetics-hysteresis.lam $file
    teq_colour=30000
    equil_colour=$(bc <<< "${teq_colour}/${delta_t}")
    max_iter=$(bc <<< "(${tmax}+${teq_colour})/$delta_t/$colour_step")
    sed -i -- "s/EQUIL_COLOUR/${equil_colour}/g" $file
else 
    cp epigenetics-swollen.lam $file
fi

# replace macros in template with input values
sed -i -- "s/NUMOFATOMS/${num_of_atoms}/g" $file
sed -i -- "s/RATIO/${ratio}/g" $file
sed -i -- "s/BONDENERGY/${bond_energy}/g" $file
sed -i -- "s/CUTOFF/${cut_off}/g" $file
sed -i -- "s/MAXITER/${max_iter}/g" $file
sed -i -- "s/COLOURSTEP/${colour_step}/g" $file
sed -i -- "s/SEED/${seed}/g" $file
sed -i -- "s/RUN/${run}/g" $file
sed -i -- "s/XYZFILE/${xyz_file}/g" $file
sed -i -- "s/INITFILE/${init_file}/g" $file
sed -i -- "s/INFILE/${in_file}/g" $file
sed -i -- "s/OUTFILE/${out_file}/g" $file
sed -i -- "s/LAMMPSFILE/${file}/g" $file
sed -i -- "s/STATEFILE/${state_file}/g" $file
sed -i -- "s/STATSFILE/${stats_file}/g" $file
sed -i -- "s/PRINTFREQ/${print_freq}/g" $file
sed -i -- "s/DELTAT/${delta_t}/g" $file

# calculate equilibrium times
t_soft=10 # equilibrate time with soft potential (in Brownian time units)
equil_soft=$(bc <<< "$t_soft/$delta_t")
equil_total=$(bc <<< "$teq/$delta_t")
equil_fene=$(bc <<< "$equil_total-$equil_soft")

sed -i -- "s/EQUIL_SOFT/${equil_soft}/g" $file
sed -i -- "s/EQUIL_FENE/${equil_fene}/g" $file
sed -i -- "s/EQUIL_TOTAL/${equil_total}/g" $file

# set whether to dump xyz files
dumpxyz_flag=''
if [ $dumpxyz != "dump" ]; then
   dumpxyz_flag='#'
fi
sed -i -- "s/DUMPFLAG/${dumpxyz_flag}/g" $file

mv $file ${exepath}/$file

cd ${exepath}

# initialise dna strand (ordered/disordered)
seed2=$(bc <<< "$seed+34987")
random_type="true"
if [ $order = "order" ]; then
    random_type="false"
else
    random_type="true"
fi
java dna_epigenetics.LAMMPSIO $num_of_atoms $type_of_atoms $box_size $box_size $box_size $seed2 $random_type $init_file

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
