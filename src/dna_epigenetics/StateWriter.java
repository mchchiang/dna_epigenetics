package dna_epigenetics;

public class StateWriter extends DataWriter {

	@Override
	public void writeData(DNAModel model, int time) {
		writer.printf("%d %d %d %d\n", time, 
				model.getNumOfAStates(), 
				model.getNumOfUStates(), 
				model.getNumOfMStates());
	}

}
