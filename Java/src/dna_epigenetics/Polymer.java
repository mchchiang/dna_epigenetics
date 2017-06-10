package dna_epigenetics;

import java.io.IOException;
import java.util.Random;

public class Polymer {
	private int numOfAtoms;
	private int typesOfAtoms;
	private int dimension = 3;
	private double [][] atomPosition;
	private double [][] atomVelocity;
	private int [][] atomBoundaryCount;
	private int [] atomType;
	private double [] pairwiseDistance;
	private double lx, ly, lz;
	private final double pi = Math.PI;
	private Bookmark bookmark;

	public Polymer (int numOfAtoms, int typesOfAtoms, int dimension, 
			double lx, double ly, double lz){
		this.numOfAtoms = numOfAtoms;
		this.typesOfAtoms = typesOfAtoms;
		this.dimension = dimension;
		atomPosition = new double [numOfAtoms][dimension];
		atomVelocity = new double [numOfAtoms][dimension];
		atomBoundaryCount = new int [numOfAtoms][dimension];
		atomType = new int [numOfAtoms];
		pairwiseDistance = new double [numOfAtoms * (numOfAtoms-1) / 2];
		this.lx = lx;
		this.ly = ly;
		this.lz = lz;
	}

	public void setPosition(int index, int dim, double value){
		atomPosition[index][dim] = value;
	}

	public double getPosition(int index, int dim){
		return atomPosition[index][dim];
	}

	public void setVelocity(int index, int dim, double value){
		atomVelocity[index][dim] = value;
	}

	public double getVelocity(int index, int dim){
		return atomVelocity[index][dim];
	}

	public void setBoundaryCount(int index, int dim, int value){
		atomBoundaryCount[index][dim] = value;
	}

	public int getBoundaryCount(int index, int dim){
		return atomBoundaryCount[index][dim];
	}

	public void setType(int index, int value){
		atomType[index] = value;
	}

	public int getType(int index){
		return atomType[index];
	}

	public int getNumOfAtoms(){
		return numOfAtoms;
	}
	
	public int getTypesOfAtoms(){
		return typesOfAtoms;
	}

	public int getDimension(){
		return dimension;
	}

	public double getLx(){
		return lx;
	}

	public double getLy(){
		return ly;
	}

	public double getLz(){
		return lz;
	}

	public void computePairwiseDistance(){
		int index = 0;
		for (int i = 1; i < numOfAtoms; i++){
			for (int j = 0; j < i; j++){
				pairwiseDistance[index] = Vector.distance(
						atomPosition[i][0] + lx * atomBoundaryCount[i][0], 
						atomPosition[i][1] + ly * atomBoundaryCount[i][1], 
						atomPosition[i][2] + lz * atomBoundaryCount[i][2],
						atomPosition[j][0] + lx * atomBoundaryCount[j][0], 
						atomPosition[j][1] + ly * atomBoundaryCount[j][1],
						atomPosition[j][2] + lz * atomBoundaryCount[j][2]);
				index++;
			}
		}
	}
	
	public double getPairwiseDistance(int atom1, int atom2){
		if (atom1 == atom2) return -1.0;
		int index;
		if (atom1 < atom2){
			index = atom2 * (atom2-1) / 2 + atom1;
		} else {
			index = atom1 * (atom1-1) / 2 + atom2;
		}
		return pairwiseDistance[index];
	}

	public void generate(){
		Random rand = new Random();

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
	}

	public void setBookmarkType(String type, 
			double staticFraction, int clusterSize){
		
		int numOfStatic = (int) (staticFraction * numOfAtoms);
		
		if (bookmark == null){
			bookmark = new NullBookmark();
		}
		if (type.equalsIgnoreCase("random")){
			bookmark = new RandomBookmark(numOfStatic, 1);
		} else if (type.equalsIgnoreCase("cluster")){
			bookmark = new ClusteredBookmark(numOfStatic, clusterSize);
		} else if (type.equalsIgnoreCase("mixed")){
			bookmark = new ClusteredBookmark(numOfStatic, 1);
		} else if (type.equalsIgnoreCase("domain_a")){
			bookmark = new FixedDomain(numOfStatic, 10, 4);
		} else if (type.equalsIgnoreCase("domain_m")){
			bookmark = new FixedDomain(numOfStatic, 10, 6);
		} else if (type.equalsIgnoreCase("single_a")){
			bookmark = new SingleBookmark(numOfAtoms/2-1, 4);
		} else if (type.equalsIgnoreCase("single_u")){
			bookmark = new SingleBookmark(numOfAtoms/2-1, 5);
		} else if (type.equalsIgnoreCase("single_m")){
			bookmark = new SingleBookmark(numOfAtoms/2-1, 6);
		} else if (type.equalsIgnoreCase("band_a")){
			int domainSize = 200;
			int spacing = 10;
			bookmark = new SingleDomain(numOfStatic+spacing/2, 
					numOfAtoms/2-domainSize/2, spacing, 4);
		}
		
	}
	
	public void generateType(int type){
		//generate bookmarks
		bookmark.generateBookmark(this);
		
		//generate type for other atoms
		Random rand = new Random();

		//generate random types for the remaining atoms
		if (type == 0){
			for (int i = 0; i < numOfAtoms; i++){
				if (atomType[i] < 4){
					atomType[i] = rand.nextInt(3)+1;//3 normal atom types
				} 
			}

			//generate a uniform type for the remaining atoms
		} else if (type > 0 && type < 5){
			if (type == 4){//randomly
				type = rand.nextInt(3)+1;
			}
			for (int i = 0; i < numOfAtoms; i++){
				if (atomType[i] < 4){
					atomType[i] = type;
				} 	
			}
		}
	}

	public static void main (String [] args) throws IOException{
		int numOfAtoms = Integer.parseInt(args[0]);
		int typesOfAtoms = Integer.parseInt(args[1]);
		double lx = Double.parseDouble(args[2]);
		double ly = Double.parseDouble(args[3]);
		double lz = Double.parseDouble(args[4]);
		int seed = Integer.parseInt(args[5]);
		int type = Integer.parseInt(args[6]);
		String staticType = args[7];
		double fracStatic = Double.parseDouble(args[8]);
		int clusterSize = Integer.parseInt(args[9]);
		String file = args[10];
		LAMMPSIO io = new LAMMPSIO();
		Polymer polymer = new Polymer(numOfAtoms, typesOfAtoms, 3, lx, ly, lz);
		polymer.generate();
		polymer.setBookmarkType(staticType, fracStatic, clusterSize);
		polymer.generateType(type);
		io.writeAtomData(file, polymer);
	}
}
