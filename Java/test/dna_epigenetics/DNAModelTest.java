package dna_epigenetics;

import static org.junit.Assert.*;

import org.junit.Test;

public class DNAModelTest {

	@Test
	public void testGetState1(){
		DNAModel model = new DNAModel(5, 1.0, 0, 1);
		int [] states = new int [] {2,1,1,0,2};
		model.initState(states);
		assertEquals("Return an incorrect state value.", 1, model.getState(2));
	}
	
	@Test
	public void testGetState2(){
		DNAModel model = new DNAModel(5, 1.0, 0, 1);
		int [] states = new int [] {2,1,1,0,2};
		model.initState(states);
		assertEquals("Return an incorrect state value.", 0, model.getState(3));
	}
	
	@Test
	public void testGetState3(){
		DNAModel model = new DNAModel(5, 1.0, 0, 1);
		int [] states = new int [] {2,1,1,0,2};
		model.initState(states);
		assertEquals("Return an incorrect state value.", 2, model.getState(4));
	}
	
	@Test
	public void testSetState1(){
		DNAModel model = new DNAModel(5, 1.0, 0, 1);
		int [] states = new int [] {0,0,0,0,0};
		model.initState(states);
		model.setState(1, 1);
		assertEquals("Set an incorrect state value.", 1, model.getState(1));
	}
	
	@Test
	public void testSetState2(){
		DNAModel model = new DNAModel(5, 1.0, 0, 1);
		int [] states = new int [] {1,2,0,0,1};
		model.initState(states);
		model.setState(3, 2);
		assertEquals("Set an incorrect state value.", 2, model.getState(3));
	}
}
