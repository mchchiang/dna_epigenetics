package dna_epigenetics;

import static org.junit.Assert.*;

import java.io.*;

import org.junit.Test;

public class LAMMPSIOTest {

	private final double tol = 0.0000001;

	@Test
	public void testReadAtomData1_1() throws IOException {
		LAMMPSIO io = new LAMMPSIO();
		io.readAtomData("lammps_input_test1");
		double [][] correctAtomPosition = new double [][] {
				{8.0, 6.0, 5.0},
				{-5.5, 12.0, -28.0},
				{4.0, 3.0, -2.5},
				{-3.0, -17.5, 10.0},
				{-1.0, 2.0, 1.0}
		};
		for (int i = 0; i < correctAtomPosition.length; i++){
			for (int j = 0; j < correctAtomPosition[0].length; j++){
				assertEquals(String.format(
						"Return wrong position value for "
						+ "component %d of atom %d", (j+1), (i+1)), 
						correctAtomPosition[i][j], 
						io.getAtomPosition(i, j), tol);
			}
		}
	}

	@Test
	public void testReadAtomData1_2() throws IOException {
		LAMMPSIO io = new LAMMPSIO();
		io.readAtomData("lammps_input_test1");
		double [][] correctAtomVelocity = new double [][] {
				{3.0, 0.5, 4.0},
				{6.0, 0.5, 2.5},
				{-2.5, 1.5, 1.5},
				{1.0, 1.5, 3.5},
				{-2.0, 0.0, 1.5}
		};
		for (int i = 0; i < correctAtomVelocity.length; i++){
			for (int j = 0; j < correctAtomVelocity[0].length; j++){
				assertEquals(String.format(
						"Return wrong velocity value for "
						+ "component %d of atom %d", (j+1), (i+1)), 
						correctAtomVelocity[i][j], 
						io.getAtomVelocity(i, j), tol);
			}
		}
	}
	
	@Test
	public void testReadAtomData1_3() throws IOException {
		LAMMPSIO io = new LAMMPSIO();
		io.readAtomData("lammps_input_test1");
		int [][] correctAtomBoundaryCount = new int [][] {
				{0, 1, 0},
				{0, -1, -1},
				{1, 0, 0},
				{1, 0, 0},
				{0, 0, 0}
		};
		for (int i = 0; i < correctAtomBoundaryCount.length; i++){
			for (int j = 0; j < correctAtomBoundaryCount[0].length; j++){
				assertEquals(String.format(
						"Return wrong boundary count value for "
						+ "component %d of atom %d", (j+1), (i+1)), 
						correctAtomBoundaryCount[i][j], 
						io.getAtomBoundaryCount(i, j));
			}
		}
	}

	@Test
	public void testReadAtomData1_4() throws IOException {
		LAMMPSIO io = new LAMMPSIO();
		io.readAtomData("lammps_input_test1");
		int [] correctAtomType = new int [] {2, 2, 2, 1, 1};
		for (int i = 0; i < correctAtomType.length; i++){
			assertEquals(String.format(
					"Return wrong type value for atom %d", 
					(i+1)), correctAtomType[i], io.getAtomType(i));
		}
	}

}
