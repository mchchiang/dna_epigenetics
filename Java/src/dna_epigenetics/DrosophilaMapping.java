package dna_epigenetics;

import java.io.IOException;

/* 
 * A class that maps the bead type used by Davide's code to my code
 * for the Drosophila simulation. The mapping is as follows:
 * 
 * Object           Davide's output  My output      My model code
 * promoter         1      O         8      Li      7
 * transcribed      2      Ca        9      Ag      8
 * gene desert      3      Ag        5      F       4
 * heterochromatin  4      N         3      C       2
 * PSC              5      He        4      H       3
 * unmarked         6      C         2      N       1
 * PRC protein      7      B         7      B       6
 * polycomb group   8      F         1      O       0
 * 
 * *6 (or 5) is not used in my code as there is no bookmark for the heterochromatin
 */

public class DrosophilaMapping {
	public static void main (String [] args) throws IOException{
		String input = args[0];
		String output = args[1];
		LAMMPSIO lammps = new LAMMPSIO();

		Polymer polymer = lammps.readAtomData(input);	

		//do the mapping
		int n = polymer.getNumOfAtoms();
		int type;
		for (int i = 0; i < n; i++){
			type = polymer.getType(i);
			switch (type){
			case 1: polymer.setType(i, 8); break;
			case 2: polymer.setType(i, 9); break;
			case 3: polymer.setType(i, 5); break;
			case 4: polymer.setType(i, 3); break;
			case 5: polymer.setType(i, 4); break;
			case 6: polymer.setType(i, 2); break;
			case 7: polymer.setType(i, 7); break;
			case 8: polymer.setType(i, 1); break;
			}
		}

		lammps.writeAtomData(output, polymer);
	}
}
