

package cvrp;

//import static cvrp.CVRPData.scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Vehicle {

	private static final int DEPOT = 1; // index of depot on the map        	
	private int remaining;
	private double length;
        private int vehiclenumber;
	private Location location;
	private ArrayList<Location> route;
	private ArrayList<Integer> routeInIndex;
        
	
	public Vehicle() {
		route = new ArrayList<Location>();
		routeInIndex = new ArrayList<Integer>();
		this.remaining = CVRP.getVEHICLE_CAPACITY();
                this.vehiclenumber = CVRP.getVehicles();
		this.length = 0;
		this.location = new Location(DEPOT);
		this.route.add(new Location(DEPOT));
		this.routeInIndex.add(DEPOT);
	}
	
	public void setRemaining(int remaining) {
		this.remaining = remaining;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public void addRoute(Location dest) {
		this.route.add(dest);
	}
	
	public void addRouteInIndex(int dest) {
		this.routeInIndex.add(dest);
	}

	public int getRemaining() {
		return remaining;
	}

	public Location getLocation() {
		return location;
	}

	public ArrayList<Location> getRoute() {
		return route;
	}
	
	public ArrayList<Integer> getRouteInIndex() {
		return routeInIndex;
	}
	
	public void addLength (double length) {
		this.length += length;
	}
	
	public double getLength () {
		return this.length;
	}
}
