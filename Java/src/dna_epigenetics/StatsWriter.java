package dna_epigenetics;

public class StatsWriter extends DataWriter {
	
	private long actualTime;
	
	public StatsWriter(){
		this(0);
	}
	
	public StatsWriter(long actualTime){
		this.actualTime = actualTime;
	}
	
	@Override
	public void writeData(DNAModel model, int time) {
		double g = model.getG();
		double m =  model.getM();
		writer.printf("%d %.5f %.5f", actualTime+time, g, m);
		int numOfStates = model.getNumOfStates();
		for (int i = 0; i < numOfStates; i++){
			writer.printf(" %d", model.getNumInState(i));
		}
		writer.printf("\n");
	}
	
}
