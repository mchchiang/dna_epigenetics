package dna_epigenetics;

public class SingleDomain implements Bookmark {
	
	private int numOfStatic;
	private int startIndex;
	private int spacing;
	private int type;
	
	public SingleDomain(int numOfStatic, int startIndex, int spacing, int type){
		this.numOfStatic = numOfStatic;
		this.startIndex = startIndex;
		this.spacing = spacing;
		this.type = type;
	}

	@Override
	public void generateBookmark(Polymer polymer) {
		int numOfAtoms = polymer.getNumOfAtoms();
		if (numOfStatic > numOfAtoms) numOfStatic = numOfAtoms;
		if (startIndex+numOfStatic*spacing >= numOfAtoms){
			numOfStatic = (numOfAtoms-1-startIndex)/spacing;
		}
		for (int i = 0; i < numOfStatic; i++){
			polymer.setType(startIndex+i*spacing, type);
		}
	}

}
