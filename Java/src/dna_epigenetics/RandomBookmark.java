package dna_epigenetics;

import java.util.Random;

public class RandomBookmark implements Bookmark{
	
	private int numOfStatic;
	private int clusterSize;
	
	public RandomBookmark(int numOfStatic, int clusterSize){
		this.numOfStatic = numOfStatic;
		this.clusterSize = clusterSize;
	}
	
	@Override
	public void generateBookmark(Polymer polymer) {
		int numOfAtoms = polymer.getNumOfAtoms();
		
		if (numOfStatic > numOfAtoms) numOfStatic = numOfAtoms;
		if (clusterSize > numOfStatic) clusterSize = numOfStatic;
		int remain = numOfStatic;
		boolean ok;
		Random rand = new Random();
		int site, type;
		while (remain > 0){
			if (clusterSize > remain) clusterSize = remain;
			ok = false;
			do {
				site = rand.nextInt(numOfAtoms);	
				if (site+clusterSize >= numOfAtoms){
					continue;
				}
				ok = true;
				for (int i = site; i < site+clusterSize; i++){
					if (polymer.getType(i) > 3){
						ok = false;
						break;
					}
				}
			} while (!ok);
			type = 4 + rand.nextInt(2) * 2;//pick either state 4 or 6 (As or Ms)
			for (int i = site; i < site+clusterSize; i++){
				polymer.setType(i,type);
				remain--;
			}
		}
	}
}
