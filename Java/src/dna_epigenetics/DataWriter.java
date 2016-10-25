package dna_epigenetics;

import java.io.*;

/**
 * DataWriter.java
 * 
 * An abstract data writer which handles the process of creating the 
 * output stream. The actual process of writing the data is delegated 
 * to the sub-classes
 * 
 * @author Michael Chiang
 *
 */
public abstract class DataWriter implements DataListener {
	
	protected PrintWriter writer = null;
	
	/**
	 * Open a new file stream for writing data
	 * @param filename name of the file
	 */
	public void openWriter(String filename){
		openWriter(filename, false);
	}
	
	/**
	 * Open a new file stream for writing data
	 * @param filename name of the file
	 * @param append whether to overwrite the file or append at
	 * the end of the file
	 */
	public void openWriter(String filename, boolean append){
		try {
			writer = new PrintWriter(new FileWriter(filename, append));
		} catch (IOException e){
			writer = null;
			System.out.println("Cannot open file: " + filename);
		}
	}
	
	/**
	 * Close the file stream
	 */
	public void closeWriter(){
		writer.flush();
		writer.close();
		writer = null;
	}
	
	/**
	 * Write data to file
	 * @param model the simulation model
	 * @param time the current time step of the simulation
	 */
	public abstract void writeData(DNAModel model, int time);
	
	
	public void update(DNAModel model, int time){
		writeData(model,time);
	}
	
}
