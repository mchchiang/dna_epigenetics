package dna_epigenetics;

import java.io.*;

public class LAMMPSIO {
	
	private String header = 
			"LAMMPS data file from restart file: timestep = 0,\tprocs = 1";

	
	public Polymer readAtomData(String filename) throws IOException {	
		System.out.println("Started reading");
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		header = reader.readLine();

		String line;
		String [] args;
		
		int numOfAtoms = 0;
		int typesOfAtoms = 0;
		int numOfBonds = 0;
		int typesOfBonds = 0;
		int numOfAngles = 0;
		int typesOfAngles = 0;
		int dimension = 3;
		double lx = 0.0; 
		double ly = 0.0;
		double lz = 0.0;
		boolean readNumOfAtoms = false;
		boolean readTypesOfAtoms = false;
		boolean readNumOfBonds = false;
		boolean readTypesOfBonds = false;
		boolean readNumOfAngles = false;
		boolean readTypesOfAngles = false;
		
		//read polymer info (atoms, bonds, angles)
		do {
			line = reader.readLine().trim();
			if (line.endsWith("atoms")){
				args = line.split("\\s+");
				numOfAtoms = Integer.parseInt(args[0]);
				readNumOfAtoms = true;
			} else if (line.endsWith("atom types")){
				args = line.split("\\s+");
				typesOfAtoms = Integer.parseInt(args[0]);
				readTypesOfAtoms = true;
			} else if (line.endsWith("bonds")){
				args = line.split("\\s+");
				numOfBonds = Integer.parseInt(args[0]);
				readNumOfBonds = true;
			} else if (line.endsWith("bond types")){
				args = line.split("\\s+");
				typesOfBonds = Integer.parseInt(args[0]);
				readTypesOfBonds = true;
			} else if (line.endsWith("angles")){
				args = line.split("\\s+");
				numOfAngles = Integer.parseInt(args[0]);
				readNumOfAngles = true;
			} else if (line.endsWith("angle types")){
				args = line.split("\\s+");
				typesOfAngles = Integer.parseInt(args[0]);
				readTypesOfAngles = true;
			}
		} while (!line.endsWith("xlo xhi") && reader.ready());

		if (!readNumOfAtoms || !readTypesOfAtoms ||
			!readNumOfBonds || !readTypesOfBonds ||
			!readNumOfAngles || !readTypesOfAngles){
			reader.close();
			throw new IOException("Data file must specify number and types of"
					+ " atoms, bonds, and angles.");
		}

		//read box size
		boolean readLx = false;
		boolean readLy = false;
		boolean readLz = false;

		while (reader.ready() && (!readLx || !readLy || !readLz)){
			if (line.endsWith("xlo xhi")){
				args = line.split("\\s+");
				lx = Double.parseDouble(args[1]) - 
						Double.parseDouble(args[0]);
				readLx = true;
			} else if (line.endsWith("ylo yhi")){
				args = line.split("\\s+");
				ly = Double.parseDouble(args[1]) -
						Double.parseDouble(args[0]);
				readLy = true;
			} else if (line.endsWith("zlo zhi")){
				args = line.split("\\s+");
				lz = Double.parseDouble(args[1]) -
						Double.parseDouble(args[0]);
				readLz = true;
			}
			line = reader.readLine().trim();
		} 

		if (!readLx || !readLy || !readLz){
			reader.close();
			throw new IOException("Data file must specify box size in all"
					+ " three dimensions.");
		}
		
		Polymer polymer = new Polymer(numOfAtoms, typesOfAtoms, numOfBonds,
				typesOfBonds, numOfAngles, typesOfAngles, 
				dimension, lx, ly, lz);

		boolean readAtomPosition = false;
		boolean readAtomVelocity = false;
		boolean readBond = false;
		boolean readAngle = false;

		while (reader.ready() && 
				(!readAtomPosition || !readAtomVelocity ||
				 !readBond || !readAngle)){
			
			line = reader.readLine().trim();	

			//read atoms' positions
			if (line.startsWith("Atoms")){

				//skip any empty lines
				while (reader.ready()){
					line = reader.readLine().trim();			
					if (line.length() > 0) break;
				}

				int index;
				int count = 0;

				do {
					args = line.split("\\s+");

					if (args.length < 9){
						reader.close();
						throw new IOException(
								"Not enough arguments for each atom.");
					}

					index = Integer.parseInt(args[0])-1;
					polymer.setLabel(index, Integer.parseInt(args[1]));
					polymer.setType(index, Integer.parseInt(args[2]));
					polymer.setPosition(index, 0, Double.parseDouble(args[3]));
					polymer.setPosition(index, 1, Double.parseDouble(args[4]));
					polymer.setPosition(index, 2, Double.parseDouble(args[5]));
					polymer.setBoundaryCount(index, 0, Integer.parseInt(args[6]));
					polymer.setBoundaryCount(index, 1, Integer.parseInt(args[7]));
					polymer.setBoundaryCount(index, 2, Integer.parseInt(args[8]));

					if (reader.ready()) line = reader.readLine();
					count++;

				} while (reader.ready() && count < numOfAtoms);
				readAtomPosition = true;

			//read atoms' velocities
			} else if (line.equals("Velocities")){

				//skip any empty lines
				while (reader.ready()){
					line = reader.readLine().trim();			
					if (line.length() > 0) break;
				}

				int index;
				int count = 0;

				do {
					args = line.split("\\s+");

					if (args.length < 4){
						reader.close();
						throw new IOException(
								"Not enough arguments for each atom's velocity.");
					}

					index = Integer.parseInt(args[0])-1;
					polymer.setVelocity(index, 0, Double.parseDouble(args[1]));
					polymer.setVelocity(index, 1, Double.parseDouble(args[2]));
					polymer.setVelocity(index, 2, Double.parseDouble(args[3]));

					if (reader.ready()) line = reader.readLine();
					count++;

				} while (reader.ready() && count < numOfAtoms);
				readAtomVelocity = true;
			
			//read bond information
			} else if (line.equals("Bonds")){
				
				//skip any empty lines
				while (reader.ready()){
					line = reader.readLine().trim();			
					if (line.length() > 0) break;
				}
				
				int index;
				int count = 0;

				do {
					args = line.split("\\s+");

					if (args.length < 4){
						reader.close();
						throw new IOException(
								"Not enough arguments for each bond.");
					}

					index = Integer.parseInt(args[0])-1;
					polymer.setBondLabel(index, Integer.parseInt(args[1]));
					polymer.setBondStartAtom(index, Integer.parseInt(args[2]));
					if (reader.ready()) line = reader.readLine();
					count++;

				} while (reader.ready() && count < numOfBonds);
				readBond = true;
				
			//read angle information
			} else if (line.equals("Angles")){
				
				//skip any empty lines
				while (reader.ready()){
					line = reader.readLine().trim();			
					if (line.length() > 0) break;
				}
				
				int index;
				int count = 0;

				do {
					args = line.split("\\s+");

					if (args.length < 5){
						reader.close();
						throw new IOException(
								"Not enough arguments for each angle.");
					}

					index = Integer.parseInt(args[0])-1;
					polymer.setAngleLabel(index, Integer.parseInt(args[1]));
					polymer.setAngleStartAtom(index, Integer.parseInt(args[2]));
					if (reader.ready()) line = reader.readLine();
					count++;

				} while (reader.ready() && count < numOfAngles);
				readAngle = true;
			}
		}

		if (!readAtomPosition || !readAtomVelocity ||
			!readBond || !readAngle){
			reader.close();
			throw new IOException("Unable to read atoms' positions "
					+ "or velocities, or bonds or angles.");
		}

		reader.close();
		System.out.println("Finished reading");
		return polymer;
	}

	public void writeAtomData(String filename, Polymer polymer) throws IOException{
		System.out.println("Started writing");
		PrintWriter writer = new PrintWriter(
				new BufferedWriter(new FileWriter(filename)));
		int numOfAtoms = polymer.getNumOfAtoms();
		int typesOfAtoms = polymer.getTypesOfAtoms();
		int numOfBonds = polymer.getNumOfBonds();
		int typesOfBonds = polymer.getTypesOfBonds();
		int numOfAngles = polymer.getNumOfAngles();
		int typesOfAngles = polymer.getTypesOfAngles();
		double lx = polymer.getLx();
		double ly = polymer.getLy();
		double lz = polymer.getLz();
		//write header section of LAMMPS input files
		writer.println(header);
		writer.println();
		writer.printf("%d atoms\n", numOfAtoms);
		writer.printf("%d atom types\n", typesOfAtoms);
		writer.printf("%d bonds\n", numOfBonds);
		writer.printf("%d bond types\n", typesOfBonds);
		writer.printf("%d angles\n", numOfAngles);
		writer.printf("%d angle types\n", typesOfAngles);
		writer.println();
		writer.printf("%.16f %.16f xlo xhi\n", -lx/2.0, lx/2.0);
		writer.printf("%.16f %.16f ylo yhi\n", -ly/2.0, ly/2.0);
		writer.printf("%.16f %.16f zlo zhi\n", -lz/2.0, lz/2.0);

		writer.printf("\nMasses\n\n");
		for (int i = 1; i <= typesOfAtoms; i++){
			writer.printf("%d %d\n", i, 1);
		}

		//print atoms' positions
		writer.printf("\nAtoms\n\n");
		for (int i = 0; i < numOfAtoms; i++){
			writer.printf("%d %d %d %.16f %.16f %.16f %d %d %d\n",
					i+1, polymer.getLabel(i),
					polymer.getType(i), 
					polymer.getPosition(i,0), 
					polymer.getPosition(i,1), 
					polymer.getPosition(i,2),
					polymer.getBoundaryCount(i,0), 
					polymer.getBoundaryCount(i,1),
					polymer.getBoundaryCount(i,2));
		}

		//print atoms' velocities
		writer.printf("\nVelocities\n\n");
		for (int i = 0; i < numOfAtoms; i++){
			writer.printf("%d %.16f %.16f %.16f\n",
					i+1, polymer.getVelocity(i,0), 
					polymer.getVelocity(i,1), 
					polymer.getVelocity(i,2));
		}

		//print bond information
		writer.printf("\nBonds\n\n");
		int startAtom;
		for (int i = 0; i < numOfBonds; i++){
			startAtom = polymer.getBondStartAtom(i);
			writer.printf("%d %d %d %d\n",
					i+1, polymer.getBondLabel(i), 
					startAtom, startAtom+1);
		}

		//print angle information
		writer.printf("\nAngles\n\n");
		for (int i = 0; i < numOfAngles; i++){
			startAtom = polymer.getAngleStartAtom(i);
			writer.printf("%d %d %d %d %d\n",
					i+1, polymer.getAngleLabel(i), 
					startAtom, startAtom+1, startAtom+2);
		}
		writer.println();
		writer.close();
		System.out.println("Finished writing");
	}
}
