package dna_epigenetics;

public class DrosophilaPositionWriter extends PositionWriter {
	
	public DrosophilaPositionWriter(){
		super(0);
	}

	public DrosophilaPositionWriter(long actualTime){
		super(actualTime, 1);
	}
	
	public DrosophilaPositionWriter(long actualTime, int printInt){
		super(actualTime, printInt);
	}
	
	/* 
	 * Mapping between Davide's code and my code for bead's type:
	 * 
	 * Object           Davide's output  My output      My model code
	 * promoter         1      O         8      Li      7
	 * transcribed      2      Ca        9      Ag      8
	 * gene desert      3      Ag        5      F       4
	 * heterochromatin  4      N         3      C       2
	 * PSC              5      He        4      H       3
	 * unmarked         6      C         2      N       1
	 * PRC protein      7      B         7      B       6
	 * polycomb group   8      F         1      O       0
	 * 
	 * *6 (or 5) is not used in my code as there is no bookmark for the heterochromatin
	 */
	@Override
	protected int getFormattedState(int state){
		switch (state){
		case 0: return 8;
		case 1: return 6;
		case 2: return 4;
		case 3: return 5;
		case 4: return 3;
		case 6: return 7;
		case 7: return 1;
		case 8: return 2;
		}
		return 0;
	}
	
	@Override
	protected String getFormattedSymbol(int state){
		switch (state){
		case 0: return "F";
		case 1: return "C";
		case 2: return "N";
		case 3: return "He";
		case 4: return "Ag";
		case 6: return "B";
		case 7: return "O";
		case 8: return "Ca";
		}
		return "";
	}
}
