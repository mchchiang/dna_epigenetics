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
		writer.printf("%d %.5f %.5f %d %d %d\n", actualTime+time, 
				g, Math.abs(g), 				
				model.getNumOfAStates(),
				model.getNumOfUStates(),
				model.getNumOfMStates());
	}
	
}
