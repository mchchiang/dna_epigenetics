package dna_epigenetics;

public class SingleBookmark implements Bookmark{
	
	private int position;
	private int type;
	
	public SingleBookmark(int pos, int type){
		this.position = pos;
		this.type = type;
	}

	@Override
	public void generateBookmark(Polymer polymer) {
		if (position < polymer.getNumOfAtoms()){
			polymer.setType(position,type);
		}
	}

}
