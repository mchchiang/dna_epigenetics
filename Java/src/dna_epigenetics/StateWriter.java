package dna_epigenetics;

public class StateWriter extends DataWriter {
	
	private long actualTime;
	
	public StateWriter(){
		this(0);
	}
	
	public StateWriter(long actualTime){
		this.actualTime = actualTime;
	}
	
	@Override
	public void writeData(DNAModel model, int time) {
		int n = model.getNumOfNucleosomes();
		long totalTime = actualTime + time;
		for (int i = 0; i < n; i++){
			writer.printf("%ld %d %d\n", totalTime, (i+1), model.getState(i));
		}
		writer.printf("\n\n");
	}

}
