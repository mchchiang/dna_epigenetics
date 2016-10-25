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
		writer.printf("%d %.5f %d %d %d\n", actualTime+time, 
				model.getG(),
				model.getNumOfAStates(),
				model.getNumOfUStates(),
				model.getNumOfMStates());
	}
	
}
