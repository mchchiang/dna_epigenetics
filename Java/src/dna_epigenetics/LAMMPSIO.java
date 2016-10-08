package dna_epigenetics;

import java.io.*;
import java.util.Random;

public class LAMMPSIO {
	private int numOfAtoms;
	private final int dimension = 3;
	private double lx, ly, lz;
	private double [][] atomPosition;
	private double [][] atomVelocity;
	private int [][] atomBoundaryCount;
	private int [] atomType;
	private double [] pairwiseDistance;
	private int typesOfAtoms;
	private boolean readData = false;
	private boolean computedPairwiseDistance = false;
	private final double pi = Math.PI;
	
	public void generateAtomData(int numOfAtoms, int typesOfAtoms, 
			double lx, double ly, double lz, int seed){
		this.numOfAtoms = numOfAtoms;
		this.typesOfAtoms = typesOfAtoms;
		this.lx = lx;
		this.ly = ly;
		this.lz = lz;
		
		Random rand = new Random(seed);
		
		atomPosition = new double [numOfAtoms][dimension];
		atomVelocity = new double [numOfAtoms][dimension];
		atomBoundaryCount = new int [numOfAtoms][dimension];
		atomType = new int [numOfAtoms];
		
		//set the first atom's position to be at the origin
		for (int i = 0; i < dimension; i++){
			atomPosition[0][i] = 0.0;
		}
		
		double x, y, z, r, costheta, sintheta, phi;
		
		for (int i = 1; i < numOfAtoms; i++){
			do {
				r = rand.nextDouble();
				costheta = 1.0 - 2.0 * r;
				sintheta = Math.sqrt(1 - costheta*costheta);
				r = rand.nextDouble();
				phi = 2.0 * pi * r;
				x = atomPosition[i-1][0] + sintheta * Math.cos(phi);
				y = atomPosition[i-1][1] + sintheta * Math.sin(phi);
				z = atomPosition[i-1][2] + costheta;
			} while (Math.abs(x) > lx/2.0 ||
					 Math.abs(y) > ly/2.0 || 
					 Math.abs(z) > lz/2.0);
			atomPosition[i][0] = x;
			atomPosition[i][1] = y;
			atomPosition[i][2] = z;
		}
		
		//generate atom's type
		for (int i = 0; i < numOfAtoms; i++){
			 atomType[i] = rand.nextInt(typesOfAtoms)+1;
		}
	}

	public void readAtomData(String filename) throws IOException {	
		BufferedReader reader = new BufferedReader(new FileReader(filename));

		String line;
		String [] args;

		boolean readNumOfAtoms = false;
		boolean readTypesOfAtoms = false;
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
			}		
		} while (!line.endsWith("xlo xhi") && reader.ready());

		if (!readNumOfAtoms || !readTypesOfAtoms){
			reader.close();
			throw new IOException("Data file must specify number of atoms "
					+ "and types of atoms.");
		}

		//read in box size
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

		//System.out.printf("Number of Atoms: %d\n", numOfAtoms);
		//System.out.printf("Types of Atoms: %d\n", typesOfAtoms);
		//System.out.printf("Box size:\n");
		//System.out.printf("lx = %.5f\nly = %.5f\nlz = %.5f\n", lx, ly, lz);

		atomPosition = new double [numOfAtoms][dimension];
		atomVelocity = new double [numOfAtoms][dimension];
		atomBoundaryCount = new int [numOfAtoms][dimension];
		atomType = new int [numOfAtoms];
		pairwiseDistance = new double [numOfAtoms * (numOfAtoms-1) / 2];

		boolean readAtomPosition = false;
		boolean readAtomVelocity = false;
		
		while (reader.ready() && (!readAtomPosition || !readAtomVelocity)){
			line = reader.readLine().trim();	

			//read in atoms' positions
			if (line.equals("Atoms # angle")){

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
					atomType[index] = Integer.parseInt(args[2]);
					atomPosition[index][0] = Double.parseDouble(args[3]);
					atomPosition[index][1] = Double.parseDouble(args[4]);
					atomPosition[index][2] = Double.parseDouble(args[5]);
					atomBoundaryCount[index][0] = Integer.parseInt(args[6]);
					atomBoundaryCount[index][1] = Integer.parseInt(args[7]);
					atomBoundaryCount[index][2] = Integer.parseInt(args[8]);

					if (reader.ready()) line = reader.readLine();
					count++;

				} while (reader.ready() && count < numOfAtoms);
				readAtomPosition = true;

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
					atomVelocity[index][0] = Double.parseDouble(args[1]);
					atomVelocity[index][1] = Double.parseDouble(args[2]);
					atomVelocity[index][2] = Double.parseDouble(args[3]);

					if (reader.ready()) line = reader.readLine();
					count++;

				} while (reader.ready() && count < numOfAtoms);
				readAtomVelocity = true;
			}
		}
		
		if (!readAtomPosition || !readAtomVelocity){
			reader.close();
			throw new IOException("Unable to read atoms' positions or velocities.");
		}

		readData = true;
		reader.close();
	}

	public void writeAtomData(String filename) throws IOException{
		PrintWriter writer = new PrintWriter(
				new BufferedWriter(new FileWriter(filename)));
		//write header section of LAMMPS input files
		writer.println(
				"LAMMPS data file from restart file: timestep = 0,\tprocs = 1");
		writer.println();
		writer.printf("%d atoms\n", numOfAtoms);
		writer.printf("%d atom types\n", typesOfAtoms);
		writer.printf("%d bonds\n", numOfAtoms-1);
		writer.printf("%d bond types\n", 1);
		writer.printf("%d angles\n", numOfAtoms-2);
		writer.printf("%d angle types\n", 1);
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
					i+1, 1, atomType[i], 
					atomPosition[i][0], atomPosition[i][1], atomPosition[i][2],
					atomBoundaryCount[i][0], atomBoundaryCount[i][1],
					atomBoundaryCount[i][2]);
		}

		//print atoms' velocities
		writer.printf("\nVelocities\n\n");
		for (int i = 0; i < numOfAtoms; i++){
			writer.printf("%d %.16f %.16f %.16f\n",
					i+1, atomVelocity[i][0], 
					atomVelocity[i][1], atomVelocity[i][2]);
		}

		//print bond information
		writer.printf("\nBonds\n\n");
		for (int i = 1; i <= numOfAtoms-1; i++){
			writer.printf("%d %d %d %d\n",
					i, 1, i, i+1);
		}

		//print angle information
		writer.printf("\nAngles\n\n");
		for (int i = 1; i <= numOfAtoms-2; i++){
			writer.printf("%d %d %d %d %d\n",
					i, 1, i, i+1, i+2);
		}
		writer.close();
	}

	public void computePairwiseDistance(){
		int index = 0;
		for (int i = 1; i < numOfAtoms; i++){
			for (int j = 0; j < i; j++){
				pairwiseDistance[index] = distance(
						atomPosition[i][0] + lx * atomBoundaryCount[i][0], 
						atomPosition[i][1] + ly * atomBoundaryCount[i][1], 
						atomPosition[i][2] + lz * atomBoundaryCount[i][2],
						atomPosition[j][0] + lx * atomBoundaryCount[j][0], 
						atomPosition[j][1] + ly * atomBoundaryCount[j][1],
						atomPosition[j][2] + lz * atomBoundaryCount[j][2]);
				index++;
			}
		}
		computedPairwiseDistance = true;
	}

	//accessor methods
	public double getAtomPosition(int index, int comp){
		return atomPosition[index][comp];
	}
	
	public double getAtomVelocity(int index, int comp){
		return atomVelocity[index][comp];
	}
	
	public int getAtomType(int index){
		return atomType[index];
	}
	
	public int getAtomBoundaryCount(int index, int comp){
		return atomBoundaryCount[index][comp];
	}

	public double getPairwiseDistance(int atom1, int atom2){
		if (atom1 == atom2) return -1.0;
		int index;
		if (atom1 < atom2){
			index = atom2 * (atom2+1) / 2 + atom1;
		} else {
			index = atom1 * (atom1+1) / 2 + atom2;
		}
		return pairwiseDistance[index];
	}

	protected double distance(
			double x1, double y1, double z1, 
			double x2, double y2, double z2){
		double dx = x2-x1;
		double dy = y2-y1;
		double dz = z2-z1;
		return Math.sqrt(dx*dx + dy*dy + dz*dz);
	}
	
	public static void main (String [] args) throws IOException {
		LAMMPSIO io = new LAMMPSIO();
		io.generateAtomData(1000, 3, 50, 50, 50, -1);
		io.writeAtomData("lammps_input");
	}
}