#!/bin/bash

# read in parameters
box_size=$1        # simulation box size
num_of_atoms=$2    # number of atoms
ratio=$3           # feedback ratio in Sneppen model (i.e. F = alpha / (1 - alpha) )
bond_energy=$4     # bond energy in pairwise potential
cut_off=$5         # pairwise potential cut-off parameter
frac_static=$6     # fraction of bookmark/static atoms
cluster_size=$7    # cluster size
tcolour=$8         # recolour time (in Brownian time units)
tmax=$9            # maximum simulation time (in Brownian time units), must be multiple of tcolour 
teq=${10}          # total equlibration steps
run=${11}          # trial number
atom_type=${12}    # 0 = random, 1 = active, 2 = unmark, 3 = inactive, 4 = random uniform
static_type=${13}  # configuration for static atoms (random, cluster, mixed)
simtype=${14}      # collapse, swollen, or hysteresis
dumpxyz=${15}      # dump or nodump
dumpstate=${16}    # nostate or state
rundir=${17}       # main directory where the code will be run
print_freq=${18}   # dump atoms' positions frequency (in Brownian time units)

type_of_atoms=6  # types of atoms
delta_t=0.01     # time step size in Brownian time units
colour_step=$(bc <<< "$tcolour/$delta_t")
max_iter=$(bc <<< "$tmax/$delta_t/$colour_step")
print_freq=$(bc <<< "$print_freq/$delta_t")  # actual print frequency in simulation steps

# generate a random seed for initialising dna and running lammps
seed=$(python GetRandom.py 100000)

# make execution directory
rundir="${rundir}/run_${run}_f_${ratio}_e_${bond_energy}_phi_${frac_static}_nc_${cluster_size}"
mkdir $rundir

# copy execution code to run directory
cp -r dna_epigenetics $rundir

# generate the data/output file names
name="L_${box_size}_N_${num_of_atoms}_f_${ratio}_e_${bond_energy}_rc_${cut_off}_phi_${frac_static}_nc_${cluster_size}_t_${tmax}_run_${run}"
thermo_file="thermo_${name}.dat"
xyz_file="vmd_${name}.xyz"
init_file="init_${name}.in"
in_file="dna_${name}.in"
out_file="dna_${name}.out"
stats_file="stats_${name}.dat"
state_file="none"  
pos_file="none"

if [ $dumpstate != "nostate" ]; then
    state_file="state_${name}.dat"
fi

if [ $dumpxyz != "nodump" ]; then
    pos_file="pos_${name}.dat"
fi

# create the lammps command file based on template
lammps_file="epi_${name}.lam"
file="${rundir}/${lammps_file}"

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
sed -i -- "s/LAMMPSFILE/${lammps_file}/g" $file
sed -i -- "s/STATEFILE/${state_file}/g" $file
sed -i -- "s/STATSFILE/${stats_file}/g" $file
sed -i -- "s/POSFILE/${pos_file}/g" $file
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
dumpxyz_flag='#'

#if [ $dumpxyz != "dump" ]; then
#   dumpxyz_flag='#'
#fi

sed -i -- "s/DUMPFLAG/${dumpxyz_flag}/g" $file

# initialise dna strand (ordered/disordered)
seed2=$(bc <<< "$seed+34987")

java dna_epigenetics.LAMMPSIO $num_of_atoms $type_of_atoms $box_size $box_size $box_size $seed2 $atom_type $static_type $frac_static $cluster_size "${rundir}/${init_file}"

# clear any previous entries in the state, stats, and pos file
if [ $state_file != "none" ]; then
    > "${rundir}/${state_file}"
fi

if [ $stats_file != "none" ]; then
    > "${rundir}/${stats_file}"
fi

if [ $pos_file != "none" ]; then
    > "${rundir}/${pos_file}"
fi
