package dna_epigenetics;

import static org.junit.Assert.*;

import org.junit.Test;

public class DNAModelTest {
	
	private int n = 5;
	private double ratio = 1.0;
	private double radius = 1.0;
	private int sweeps = 0;
	private int seed = 1;

	@Test
	public void testGetState1(){
		DNAModel model = new DNAModel(5, 1.0, 1.0, 0, 1);
		int [] states = new int [] {2,1,1,0,2};
		model.initState(states);
		assertEquals("Return an incorrect state value.", 1, model.getState(2));
	}
	
	@Test
	public void testGetState2(){
		DNAModel model = new DNAModel(5, 1.0, 1.0, 0, 1);
		int [] states = new int [] {2,1,1,0,2};
		model.initState(states);
		assertEquals("Return an incorrect state value.", 0, model.getState(3));
	}
	
	@Test
	public void testGetState3(){
		DNAModel model = new DNAModel(5, 1.0, 1.0, 0, 1);
		int [] states = new int [] {2,1,1,0,2};
		model.initState(states);
		assertEquals("Return an incorrect state value.", 2, model.getState(4));
	}
	
	@Test
	public void testSetState1(){
		DNAModel model = new DNAModel(5, 1.0, 1.0, 0, 1);
		int [] states = new int [] {0,0,0,0,0};
		model.initState(states);
		model.setState(1, 1);
		assertEquals("Set an incorrect state value.", 1, model.getState(1));
	}
	
	@Test
	public void testSetState2(){
		DNAModel model = new DNAModel(5, 1.0, 1.0, 0, 1);
		int [] states = new int [] {1,2,0,0,1};
		model.initState(states);
		model.setState(3, 2);
		assertEquals("Set an incorrect state value.", 2, model.getState(3));
	}
	
	@Test
	public void testRecruitedConversion1(){
		DNAModel model = new DNAModel(5, ratio, radius, sweeps, seed);
		int [] states = new int [] {1,2,0,1,0};
		model.initState(states);
		model.recruitConversion(0, 1);
		assertEquals("Incorrect state after recruited conversion.",
				2, model.getState(0));
		
	}
	
	@Test
	public void testRecruitedConversion2(){
		DNAModel model = new DNAModel(5, ratio, radius, sweeps, seed);
		int [] states = new int [] {1,2,0,1,0};
		model.initState(states);
		model.recruitConversion(4, 3);
		assertEquals("Incorrect state after recruited conversion.",
				0, model.getState(4));
		
	}
	
	@Test
	public void testRecruitedConversion3(){
		DNAModel model = new DNAModel(5, ratio, radius, sweeps, seed);
		int [] states = new int [] {1,2,0,1,0};
		model.initState(states);
		model.recruitConversion(1, 2);
		assertEquals("Incorrect state after recruited conversion.",
				1, model.getState(1));
		
	}
	
	@Test
	public void testNoisyConversion1(){
		DNAModel model = new DNAModel(5, ratio, radius, sweeps, seed);
		int [] states = new int [] {1,0,2,1,0};
		model.initState(states);
		model.noisyConversion(1, 0.4352);
		assertEquals("Incorrect state after noisy conversion.",
				0, model.getState(1));
	}
	
	@Test
	public void testNoisyConversion2(){
		DNAModel model = new DNAModel(5, ratio, radius, sweeps, seed);
		int [] states = new int [] {1,0,2,1,0};
		model.initState(states);
		model.noisyConversion(1, 0.1293);
		assertEquals("Incorrect state after noisy conversion.",
				1, model.getState(1));
	}
	
	@Test
	public void testNoisyConversion3(){
		DNAModel model = new DNAModel(5, ratio, radius, sweeps, seed);
		int [] states = new int [] {1,0,2,1,0};
		model.initState(states);
		model.noisyConversion(3, 0.2343);
		assertEquals("Incorrect state after noisy conversion.",
				2, model.getState(3));
	}
	
	@Test
	public void testNoisyConversion4(){
		DNAModel model = new DNAModel(5, ratio, radius, sweeps, seed);
		int [] states = new int [] {1,0,2,1,0};
		model.initState(states);
		model.noisyConversion(3, 0.6238);
		assertEquals("Incorrect state after noisy conversion.",
				0, model.getState(3));
	}
	
	@Test
	public void testNoisyConversion5(){
		DNAModel model = new DNAModel(5, ratio, radius, sweeps, seed);
		int [] states = new int [] {1,0,2,1,0};
		model.initState(states);
		model.noisyConversion(3, 0.9823);
		assertEquals("Incorrect state after noisy conversion.",
				1, model.getState(3));
	}

	@Test
	public void testNoisyConversion6(){
		DNAModel model = new DNAModel(5, ratio, radius, sweeps, seed);
		int [] states = new int [] {1,0,2,1,0};
		model.initState(states);
		model.noisyConversion(2, 0.0234);
		assertEquals("Incorrect state after noisy conversion.",
				1, model.getState(2));
	}
	
	@Test
	public void testNoisyConversion7(){
		DNAModel model = new DNAModel(5, ratio, radius, sweeps, seed);
		int [] states = new int [] {1,0,2,1,0};
		model.initState(states);
		model.noisyConversion(1, 0.8749);
		assertEquals("Incorrect state after noisy conversion.",
				2, model.getState(2));
	}
}
