package dna_epigenetics;

public class FixedDomain implements Bookmark{

	private int numOfStatic;
	private int numOfDomains;
	private int type;
	
	public FixedDomain(int numOfStatic, int numOfDomains, int type){
		this.numOfStatic = numOfStatic;
		this.numOfDomains = numOfDomains;
		this.type = type;
	}
	
	@Override
	public void generateBookmark(Polymer polymer) {
		int numOfAtoms = polymer.getNumOfAtoms();
		int domainSize = numOfAtoms / numOfDomains;
		int bookmarksPerDomain = numOfStatic/numOfDomains;
		int spacing = domainSize / bookmarksPerDomain;
		boolean divisible = (domainSize % bookmarksPerDomain == 0);
		int shift = spacing / 2;
		int typeToggle = 5 - type;
		int site;
		int spaceToggle = 1;
		for (int i = 0; i < numOfDomains; i++){
			site = i * domainSize + shift;
			for (int j = 0; j < bookmarksPerDomain; j++){
				polymer.setType(site,type);
				if (!divisible){
					spacing += spaceToggle;
					spaceToggle *= -1;	
				}
				site += spacing;
			}
			type += (typeToggle*2);
			typeToggle *= -1;
		}
	}

}
