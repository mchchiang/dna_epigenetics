package dna_epigenetics;

public class SingleDomainInsulation implements Bookmark{
	
	private int numOfStatic;
	private int startIndex;
	private int spacing;
	private int type;
	private int numOfInsulation;
	private int insulationPadding;
	private int insulationType;
	
	public SingleDomainInsulation(int numOfStatic, int startIndex, 
			int spacing, int type, int numOfInsulation, int insulationPadding,
			int insulationType){
		this.numOfStatic = numOfStatic;
		this.startIndex = startIndex;
		this.spacing = spacing;
		this.type = type;
		this.numOfInsulation = numOfInsulation;
		this.insulationPadding = insulationPadding;
		this.insulationType = insulationType;
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
		for (int i = 0; i < numOfInsulation; i++){
			polymer.setType(startIndex-insulationPadding-numOfInsulation+i, 
					insulationType);
			polymer.setType(startIndex+(numOfStatic-1)*spacing+insulationPadding+i,
					insulationType);
		}
	}
}
