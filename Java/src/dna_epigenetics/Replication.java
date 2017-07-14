package dna_epigenetics;

import java.io.IOException;
import java.util.Random;
import java.util.ArrayList;

public class Replication {
	public static void main (String [] args) throws IOException{
		int numOfAtoms = Integer.parseInt(args[0]);
		boolean excision = Boolean.parseBoolean(args[1]);
		int numOfRemovableBookmarks = Integer.parseInt(args[2]);
		String fileFromLAMMPS = args[3];
		String fileToLAMMPS = args[4];
		LAMMPSIO io = new LAMMPSIO();
		Polymer polymer = io.readAtomData(fileFromLAMMPS);
		Random rand = new Random();
		
		ArrayList<Integer> removableBeads = new ArrayList<Integer>();
		ArrayList<Integer> staticBeads = new ArrayList<Integer>();
		
		//Get the number of static beads
		int numOfStatic = 0;
		for (int i = 0; i < numOfAtoms; i++){
			if (polymer.getType(i) <= 3){
				removableBeads.add(i);
			} else {
				staticBeads.add(i);
				numOfStatic++;
			}
		}
		
		int totalRemovable = (numOfAtoms - numOfStatic) / 2;

		/*
		 * Uncolour half of the modifiable beads to model a semi-conservative
		 * replication process
		 */
		int j, index;
		for (int i = 0; i < totalRemovable && 
				removableBeads.size() > 0; i++){
			/*
			 * Randomly pick a bead that has not been modified to remove
			 * its colour
			 */
			j = rand.nextInt(removableBeads.size());	
			index = removableBeads.remove(j);
			polymer.setType(index, 2);
		}
		
		if (excision){
			for (int i = 0; i < numOfRemovableBookmarks &&
					staticBeads.size() > 0; i++){
				j = rand.nextInt(staticBeads.size());
				index = staticBeads.remove(j);
				polymer.setType(index, 2);
			}
		}
			
		io.writeAtomData(fileToLAMMPS, polymer);
	}
}
