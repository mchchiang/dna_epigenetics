package dna_epigenetics;

public class PositionWriter extends DataWriter {
	
	private long actualTime;
	private int printInt;
	
	public PositionWriter(){
		this(0);
	}

	public PositionWriter(long actualTime){
		this(actualTime, 1);
	}
	
	public PositionWriter(long actualTime, int printInt){
		this.actualTime = actualTime;
		this.printInt = printInt;
	}

	@Override
	public void writeData(DNAModel model, int time) {
		int n = model.getNumOfNucleosomes();
		long totalTime = actualTime + time;
		
		if (totalTime % printInt == 0){
			writer.printf("%d\n", n);
			writer.printf("Atoms. Timestep: %d\n", totalTime);
			Polymer polymer = model.getPolymer();
			double x, y, z;
			int xc, yc, zc;
			int type;
			for (int i = 0; i < n; i++){
				x = polymer.getPosition(i, 0);
				y = polymer.getPosition(i, 1);
				z = polymer.getPosition(i, 2);
				xc = polymer.getBoundaryCount(i, 0);
				yc = polymer.getBoundaryCount(i, 1);
				zc = polymer.getBoundaryCount(i, 2);
				type = model.getState(i);		
				writer.printf("%s %.10f %.10f %.10f %d %d %d %d\n", 
						getTypeSymbol(type), x, y, z, xc, yc, zc, type);
			}
		}
	}

	private String getTypeSymbol(int type){
		String symbol = "";
		switch (type){
		case 0: symbol = "O"; break;
		case 1: symbol = "N"; break;
		case 2: symbol = "C"; break;	
		case 3: symbol = "H"; break;
		case 4: symbol = "F"; break;
		case 5: symbol = "S"; break;
		}
		return symbol;
	}

}
