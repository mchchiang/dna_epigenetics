package dna_epigenetics;

import java.io.IOException;

public class Measurement {
	
	private LAMMPSIO lammps;
	
	public Measurement(LAMMPSIO lammps){
		this.lammps = lammps;
	}
	
	public double getGyrationRadius(){
		double xcm = getCM(0);
		double ycm = getCM(1);
		double zcm = getCM(2);
		int numOfAtoms = lammps.getNumOfAtoms();
		double sum = 0.0;
		for (int i = 0; i < numOfAtoms; i++){
			sum += Vector.distance2(
					lammps.getAtomPosition(i, 0), 
					lammps.getAtomPosition(i, 1), 
					lammps.getAtomPosition(i, 2), 
					xcm, ycm, zcm);		
		}
		sum /= (double) numOfAtoms;
		return Math.sqrt(sum);
	}
	
	public double getCM(int comp){
		double sum = 0.0;
		int numOfAtoms = lammps.getNumOfAtoms();
		for (int i = 0; i < numOfAtoms; i++){
			sum += lammps.getAtomPosition(i, comp);
		}
		return sum / (double) numOfAtoms;
	}
	
	public static void main (String [] args) throws IOException{
		String atomfile = args[0];
		LAMMPSIO lammps = new LAMMPSIO();
		lammps.readAtomData(atomfile);
		Measurement measure = new Measurement(lammps);
		System.out.println(measure.getGyrationRadius());
	}
}
