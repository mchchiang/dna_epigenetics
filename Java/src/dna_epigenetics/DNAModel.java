package dna_epigenetics;

import java.nio.file.Paths;
import java.util.Random;
import java.util.ArrayList;

public class DNAModel {
	//model parameters
	private double F; //feedback ratio
	private double alpha; //recruitment conversion probability
	
	//simulation parameters
	private int n; //number of nucleosomes
	private int [] dna; //set states to be: A -> 0; U -> 1; M -> 2
	private int sweeps;
	private int seed;
	private int time;
	
	//observables
	private int [] numInState;
	
	//randomiser
	private Random rand;
	
	//list of observers
	private ArrayList<DataListener> listeners;
	
	public DNAModel (int n, double ratio, int sweeps, int seed) {
		this.n = n;
		this.F = ratio;
		this.alpha = F / (1 + F);
		this.sweeps = sweeps;
		this.seed = seed;
		init();
	}
	
	public void init(){
		rand = new Random();
		dna = new int [n];
		numInState = new int [3];
		listeners = new ArrayList<DataListener>();
	}
	
	//initialise the dna strand with random states
	public void initState(){
		int i;
		for (i = 0; i < n; i++){
			int s = rand.nextInt(3);
			dna[i] = s;
			numInState[s]++;
		}
	}
	
	//initialise the dna with specified states
	public void initState(int [] states){
		if (states.length == dna.length){
			int i;
			for (i = 0; i < states.length; i++){
				int s = states[i];
				dna[i] = s;
				numInState[s]++;
			}
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
		for (time = 0; time < sweeps; time++){
			for (i = 0; i < n; i++){
				nextStep();
			}
			//measurements
			notifyListener();
		}
	}
	
	public void nextStep(){
		//step 1: select a random nucleosome
		int n1 = rand.nextInt(n);
		double p1 = rand.nextDouble();
		
		//step 2a: recruited conversion
		if (p1 < alpha){
			//pick another nucleosome site
			int n2;
			do {
				n2 = rand.nextInt(n);
			} while (n2 == n1);
			recruitConversion(n1, n2);
		}
		
		//step 2b: noisy conversion
		else {
			noisyConversion(n1, rand.nextDouble(), rand.nextDouble());
		}
	}
	
	protected void recruitConversion(int n1, int n2){
		int s1 = dna[n1];
		int s2 = dna[n2];
		
		if (s1 != s2 && s2 != 1){
			if (s2 == 2){
				setState(n1, dna[n1]+1);
			} else if (s2 == 0){
				setState(n1, dna[n1]-1);
			}
		}
	}
	
	protected void noisyConversion(int n1, double p1, double p2){
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
	
	public static void main (String [] args){
		int n = Integer.parseInt(args[0]);
		double ratio = Double.parseDouble(args[1]);
		int sweeps = Integer.parseInt(args[2]);
		int run = Integer.parseInt(args[3]);
		String filepath = args[4];
		int seed = 1;
		String name = String.format("n_%d_F_%.2f_t_%d_run_%d.dat",
				n, ratio, sweeps, run);
		//DataWriter stateWriter = new StateWriter();
		DataWriter probWriter = new ProbabilityWriter(n, sweeps);
		//stateWriter.openWriter(Paths.get(filepath, "state_" + name).toString());
		probWriter.openWriter(Paths.get(filepath, "prob_" + name).toString());
		
		DNAModel model = new DNAModel(n, ratio, sweeps, seed);
		model.initState();
		//model.addListener(stateWriter);
		model.addListener(probWriter);
		model.run();
		//stateWriter.closeWriter();
		probWriter.closeWriter();
	}
}