package dna_epigenetics;

import java.util.Random;

public class ClusteredBookmark implements Bookmark{
	
	private int numOfStatic;
	private int clusterSize;
	private int type;
	
	public ClusteredBookmark(int numOfStatic, int clusterSize){
		this.numOfStatic = numOfStatic;
		this.clusterSize = clusterSize;
		Random rand = new Random();
		this.type = 4 + rand.nextInt(2) * 2;//pick either state 4 or 6 (A or M)
	}
	
	public ClusteredBookmark(int numOfStatic, int clusterSize, int type){
		this.numOfStatic = numOfStatic;
		this.clusterSize = clusterSize;
		this.type = type;
	}
	
	@Override
	public void generateBookmark(Polymer polymer) {
		int numOfAtoms = polymer.getNumOfAtoms();
		if (numOfStatic > numOfAtoms) numOfStatic = numOfAtoms;
		if (clusterSize > numOfStatic) clusterSize = numOfStatic;
		int spacing = numOfAtoms / numOfStatic;
		int shift = spacing/2;
		int toggle = 5 - type;
		for (int i = 0; i < numOfStatic; i++){
			polymer.setType(i*spacing+shift,type);
			if ((i+1) % clusterSize == 0){
				type += (toggle*2);
				toggle *= -1;
			}
		}
	}
}
