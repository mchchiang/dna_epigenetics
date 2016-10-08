package dna_epigenetics;

public class Vector3D {
	private double x;
	private double y;
	private double z;
	
	public Vector3D(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public double get(int comp){
		if (comp == 0) return x;
		else if (comp == 1) return y;
		else if (comp == 2) return z;
		else return 0.0;
	}
	
	public static double distance(Vector3D v1, Vector3D v2){
		double dx = v2.x - v1.x;
		double dy = v2.y - v1.y;
		double dz = v2.z - v1.z;
		return Math.sqrt(dx*dx + dy*dy + dz*dz);
	}
	
	public static Vector3D displacement(Vector3D v1, Vector3D v2){
		double dx = v2.x - v1.x;
		double dy = v2.y - v1.y;
		double dz = v2.z - v1.z;
		return new Vector3D(dx,dy,dz);
	}
}
