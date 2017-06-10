package dna_epigenetics;

import java.io.IOException;
import java.util.Random;
import java.util.ArrayList;

public class Replication {
	public static void main (String [] args) throws IOException{
		int numOfAtoms = Integer.parseInt(args[0]);
		double staticFraction = Double.parseDouble(args[1]);
		boolean excision = Boolean.parseBoolean(args[2]);
		String fileFromLAMMPS = args[3];
		String fileToLAMMPS = args[4];
		LAMMPSIO io = new LAMMPSIO();
		Polymer polymer = io.readAtomData(fileFromLAMMPS);
		Random rand = new Random();
		int numOfStatic = (int) (staticFraction * numOfAtoms);
		int total = (numOfAtoms-numOfStatic)/2;
		
		ArrayList<Integer> unmodifiable = new ArrayList<Integer>();
		
		if (!excision){
			for (int i = 0; i < numOfAtoms; i++){
				if (polymer.getType(i) > 3){
					unmodifiable.add(i);
				}
			}
		}
		
		/*
		 * Uncolour half of the modifiable beads to model a semi-conservative
		 * replication process
		 */
		int index;
		for (int i = 0; i < total; i++){
			/*
			 * Randomly pick a bead that has not been modified to remove
			 * its colour
			 */
			do {
				index = rand.nextInt(numOfAtoms);	
			} while (unmodifiable.contains(index));
			System.out.println(index);
			polymer.setType(index, 2);
			unmodifiable.add(index);
		}
		io.writeAtomData(fileToLAMMPS, polymer);
	}
}
