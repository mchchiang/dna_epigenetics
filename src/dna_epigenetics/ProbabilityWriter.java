package dna_epigenetics;

public class ProbabilityWriter extends DataWriter {
	
	private double [] count;
	private int totalTime;

	public ProbabilityWriter(int n, int totalTime){
		this.count = new double [n+1];
		this.totalTime = totalTime;
	}
	
	@Override
	public void writeData(DNAModel model, int time) {
		count[model.getNumOfMStates()]+=1.0;
		if (time == totalTime-1){
			int i;
			for (i = 0; i < count.length; i++){
				count[i] /= (double) totalTime; 
				writer.printf("%d %.5f\n", i, count[i]);
			}
		}
	}
}
