package dna_epigenetics;

/**
 * NullWriter.java
 * 
 * An empty writer that does nothing (to be passed to CellPottsModel 
 * in the case when no data need to be recorded)
 * 
 * @author Michael Chiang
 *
 */
public class NullWriter extends DataWriter {
	@Override
	public void openWriter(String filename){}
	
	@Override
	public void openWriter(String filename, boolean append){}
	
	@Override
	public void closeWriter(){}
	
	@Override
	public void writeData(DNAModel model, int time) {}

}
