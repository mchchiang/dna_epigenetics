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
				type = getFormattedState(model.getState(i));		
				writer.printf("%s %.10f %.10f %.10f %d %d %d %d\n", 
						getFormattedSymbol(type), x, y, z, xc, yc, zc, type);
			}
		}
	}
	
	protected int getFormattedState(int state){
		return state+1;
	}
	
	protected String getFormattedSymbol(int state){
		switch (state){
		case 0: return "O";
		case 1: return "N";
		case 2: return "C";	
		case 3: return "H";
		case 4: return "F";
		case 5: return "S";
		case 6: return "B";
		case 7: return "Li";
		case 8: return "Ag";
		}
		return "";
	}
}
