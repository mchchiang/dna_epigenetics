package dna_epigenetics;

import java.io.IOException;

/* 
 * A class that maps the atom type used by Davide's code to my code
 * for the Drosophila simulation. The mapping is as follows:
 * 
 * Object           Davide's code  My code
 * promoter         1              8
 * transcribed      2              9
 * gene desert      3              5
 * heterochromatin  4              3
 * PSC              5              4
 * unmarked         6              2
 * PRC protein      7              7
 * polycomb group   8              1
 * 
 * *6 is not used in my code as there is no bookmark for the heterochromatin
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
