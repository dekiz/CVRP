package cvrp;

import static cvrp.CVRP.arraycopy;
import static cvrp.CVRP.customers;
import static cvrp.CVRP.*;

public class Location {

	private static final int X_COORDINATE = 0; // x-axis coordinate is dimension
	private static final int Y_COORDINATE = 1; // y-axis coordinate is dimension
	private double x_coord;
	private double y_coord;
	private int demand;
	private int index;
	private double degree;
        
        public double[][] locations = CVRP.getLocations();
        
        public static int[] demands = CVRP.arraycopy(CVRP.getDemands(), CVRP.customers);
        
        public static double[][] coords = CVRP.arraycopy2(CVRP.getLocations(), CVRP.customers);
        
//	public static int[][] coords = new int[][] {
//			{ -1, -1 }, // dummy entry 
//			{ 0,0 }, // the coordinates of node 1 (the depot)
//			{ 0,10 }, // the coordinates of node 2 ...
//			{ -10,10 },
//			{ 0,-10 },
//			{ 10,-10 },
//		 };
        
//        public static int[] demands = new int[] { 
//                 9999999, // dummy entry to make
//                0,3,3,3,3
//                
//	 };

	public double getDegree() {
		return degree;
	}

	public void setDegree(double degree) {
		this.degree = degree;
	}

	@SuppressWarnings("static-access")
	public Location(int index) {
//		CVRPData data = new CVRPData();
		this.x_coord = coords[index][X_COORDINATE];
		this.y_coord = coords[index][Y_COORDINATE];
		this.demand = demands[index];
		this.index = index;
		this.degree = 99999;
	}
	
	public Location (Location location) {
		this.x_coord = location.x_coord;
		this.y_coord = location.y_coord;
		this.demand = location.demand;
		this.index = location.index;
		this.degree = location.degree;
	}
	
	@SuppressWarnings("static-access")
//	public Location(int index, double degree) {
////		CVRPData data = new CVRPData();
//		this.x_coord = coords[index][X_COORDINATE];
//		this.y_coord = coords[index][Y_COORDINATE];
//		this.demand = demands[index];
//		this.index = index;
//		this.degree = degree;
//	}

	public String toString() {
		return x_coord + " " + y_coord + " " + demand;
	}

	public double getX_coord() {
		return x_coord;
	}

	public double getY_coord() {
		return y_coord;
	}

	public int getDemand() {
		return demand;
	}
	
	public int getindex() {
		return index;
	}


}
