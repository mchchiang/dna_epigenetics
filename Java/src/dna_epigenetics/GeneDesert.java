package dna_epigenetics;

public class GeneDesert implements Bookmark {
	
	private int blockLength;
	private int separation;
	private int type;
	
	public GeneDesert(int blocklength, int separation, int type){
		this.blockLength = blocklength;
		this.separation = separation;
		this.type = type;
	}

	@Override
	public void generateBookmark(Polymer polymer) {
		int numOfAtoms = polymer.getNumOfAtoms();
		int index = 0;
		do {
			index += separation;
			for (int i = 0; i < blockLength && index < numOfAtoms; i++){
				polymer.setType(index, type);
				index++;
			}
		} while (index < numOfAtoms);
	}

}
