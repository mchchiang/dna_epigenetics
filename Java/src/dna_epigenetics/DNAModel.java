package dna_epigenetics;

import java.io.IOException;
import java.util.Random;
import java.util.ArrayList;

public class DNAModel {
	//model parameters
	private double F; //feedback ratio
	private double alpha; //recruitment conversion probability
	
	//simulation parameters
	private int n; //number of nucleosomes
	//set states to be: A = 0; U = 1; M = 2; As = 3; Us = 4; Ms = 5
	private int [] dna; 
	private int sweeps;
	private int seed;
	private int time;
	private double radius;
	private final int NUMOFSTATE = 6;
	
	//observables
	private int [] numInState;
	
	//randomiser
	private Random rand;
	
	//list of observers
	private ArrayList<DataListener> listeners;
	
	//coupling with LAMMPS
	private boolean runWithLAMMPS = false;
	private LAMMPSIO lammps = null;
	
	public DNAModel (int n, double ratio, double radius, int sweeps, int seed) {
		this(n, ratio, radius, sweeps, seed, null);
	}
	
	public DNAModel (int n, double ratio, double radius, 
			int sweeps, int seed, LAMMPSIO lammps){	
		this.n = n;
		this.F = ratio;
		this.alpha = F / (1 + F);
		this.radius = radius; 
		this.sweeps = sweeps;
		this.seed = seed;
		if (lammps == null){
			runWithLAMMPS = false;
		} else {
			runWithLAMMPS = true;
			this.lammps = lammps;
		}
		init();
	}
	
	public void init(){
		rand = new Random();
		dna = new int [n];
		numInState = new int [NUMOFSTATE];
		listeners = new ArrayList<DataListener>();
	}
	
	//initialise the dna strand with random states
	public void initState(){
		for (int i = 0; i < n; i++){
			int s = rand.nextInt(3);
			dna[i] = s;
			numInState[s]++;
		}
	}
	
	//initialise the dna with specified states
	public void initState(int [] states){
		if (states.length == dna.length){
			for (int i = 0; i < states.length; i++){
				int s = states[i];
				dna[i] = s;
				numInState[s]++;
			}
		}
	}
	
	//initialise the dna with specified states from lammps file
	public void initState(LAMMPSIO lammps){
		for (int i = 0; i < n; i++){		
			int s = lammps.getAtomType(i)-1;
			dna[i] = s;
			numInState[s]++;
		}
	}
	
	public void addListener(DataListener l){
		if (!listeners.contains(l)){
			listeners.add(l);
		}
	}
	
	public void removeListener(DataListener l){
		if (listeners.contains(l)){
			listeners.remove(l);
		}
	}
	
	public void notifyListener(){
		for (DataListener l : listeners){
			l.update(this, time);
		}
	}
	
	public void run(){
		int i;
		System.out.println("Started running model");
		for (time = 0; time < sweeps; time++){
			for (i = 0; i < n; i++){
				nextStep();
			}
			//measurements
			notifyListener();
		}
		System.out.println("Finished running model");
	}
	
	public void nextStep(){
		//step 1: select a random nucleosome
		int n1 = rand.nextInt(n);
		double p1 = rand.nextDouble();
		double distance = 0.0;
		//step 2a: recruited conversion
		if (p1 < alpha){
			//pick another nucleosome site
			int n2;
			do {
				n2 = rand.nextInt(n);
				if (runWithLAMMPS){
					distance = lammps.getPairwiseDistance(n1, n2);
				}
			} while (n2 == n1 || distance > radius);
			recruitConversion(n1, n2);
		}
		
		//step 2b: noisy conversion
		else {
			noisyConversion(n1, rand.nextDouble());
		}
	}
	
	protected void recruitConversion(int n1, int n2){
		int s1 = dna[n1];
		int s2 = dna[n2];
		
		if (s1 < 3 && s1 != s2 && s2 != 1 && s2 != 4){
			if (s2 == 2 || s2 == 5){
				setState(n1, dna[n1]+1);
			} else if (s2 == 0 || s2 == 3){
				setState(n1, dna[n1]-1);
			}
		}
	}
	
	protected void noisyConversion(int n1, double p1){
		int s1 = dna[n1];		
		if (s1 == 0 && p1 < 1.0/3.0){
			setState(n1, dna[n1]+1);
		} else if (s1 == 1 && p1 < 1.0/3.0){
			setState(n1, dna[n1]+1);
		} else if (s1 == 1 && p1 < 2.0/3.0){
			setState(n1, dna[n1]-1);
		} else if (s1 == 2 && p1 < 1.0/3.0){
			setState(n1, dna[n1]-1);
		}
	}
	
	//accessor methods
	public void setState(int index, int value){
		if (index >= 0 && index < n && 
			value >= 0 && value <= 2){
			numInState[dna[index]]--;
			dna[index] = value;
			numInState[value]++;
		}
	}
	public int getState(int index){
		if (index >= 0 && index < n){
			return dna[index]; 
		}
		return -1;
	}
	
	public void setF(double ratio){
		this.F = ratio;
	}
	
	public double getF(){
		return F;
	}
	
	public int getNumOfNucleosomes(){
		return n;
	}
	
	public double getRadius(){
		return radius;
	}
	
	public int getTime(){
		return time;
	}
	
	public int getNumOfAStates(){
		return numInState[0];
	}
	
	public int getNumOfUStates(){
		return numInState[1];
	}
	
	public int getNumOfMStates(){
		return numInState[2];
	}
	
	public LAMMPSIO getLAMMPS(){
		return lammps;
	}
	
	public double getG(){
		double m = numInState[2];
		double a = numInState[0];
		return (m-a)/(m+a);
	}
	
	public static void main (String [] args) {
		int n = Integer.parseInt(args[0]);
		double ratio = Double.parseDouble(args[1]);
		double radius = Double.parseDouble(args[2]);
		int sweeps = Integer.parseInt(args[3]);
		int run = Integer.parseInt(args[4]);
		int seed = 1;
		long actualTime = Long.parseLong(args[5]);
		boolean randomStates = Boolean.parseBoolean(args[6]);
		boolean useLAMMPS = Boolean.parseBoolean(args[7]);
		String fileFromLAMMPS = args[8];
		String fileToLAMMPS = args[9];
		String stateFileName = args[10];
		String statsFileName = args[11];
		String posFileName = args[12];
		int printInt = Integer.parseInt(args[13]);
		
		System.out.println("t = " + actualTime);
		
		DataWriter stateWriter, statsWriter, posWriter;
		if (stateFileName.equalsIgnoreCase("none")){
			stateWriter = new NullWriter();
		} else {
			stateWriter = new StateWriter(actualTime, printInt);
		}
		
		if (statsFileName.equalsIgnoreCase("none")){
			statsWriter = new NullWriter();
		} else {
			statsWriter = new StatsWriter(actualTime);
		}
		
		if (posFileName.equalsIgnoreCase("none")){
			posWriter = new NullWriter();
		} else {
			posWriter = new PositionWriter(actualTime, printInt);
		}
		
		stateWriter.openWriter(stateFileName, true);
		statsWriter.openWriter(statsFileName, true);
		posWriter.openWriter(posFileName, true);
		
		DNAModel model;
		
		//init model from lammps
		if (useLAMMPS){
			LAMMPSIO lammps = new LAMMPSIO();
			try {
				lammps.readAtomData(fileFromLAMMPS);	
			} catch (IOException e){
				System.out.println("Problem reading input file from lammps!");
				e.printStackTrace();
			}
			lammps.computePairwiseDistance();			
			model = new DNAModel(n, ratio, radius, sweeps, seed, lammps);
			model.addListener(stateWriter);
			model.addListener(statsWriter);	
			model.addListener(posWriter);
			if (randomStates){
				model.initState();
			} else {
				model.initState(lammps);
			}		
			model.run();
			//update atom types in lammps
			for (int i = 0; i < n; i++){
				lammps.setAtomType(i, model.getState(i)+1);
			}
			try {
				lammps.writeAtomData(fileToLAMMPS);
			} catch (IOException e){
				System.out.println("Problem writing output file to lammps!");
				e.printStackTrace();
			}
		} else {
			model = new DNAModel(n, ratio, radius, sweeps, seed);
			model.addListener(stateWriter);
			model.addListener(statsWriter);
			model.initState();
			model.run();
		}
		
		stateWriter.closeWriter();
		statsWriter.closeWriter();
		posWriter.closeWriter();
	}
}
