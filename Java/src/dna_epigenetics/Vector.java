package dna_epigenetics;

public class Vector {
	public static double distance(
			double x1, double y1, double z1,
			double x2, double y2, double z2){
		double dx = x2-x1;
		double dy = y2-y1;
		double dz = z2-z1;
		return Math.sqrt(dx*dx + dy*dy + dz*dz);
	}

	public static double distance2(
			double x1, double y1, double z1,
			double x2, double y2, double z2){
		double dx = x2-x1;
		double dy = y2-y1;
		double dz = z2-z1;
		return dx*dx + dy*dy + dz*dz;
	}
}
