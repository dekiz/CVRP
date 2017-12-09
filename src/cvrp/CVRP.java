/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cvrp;

import com.sun.webkit.PageCache;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.lang.reflect.Field;
/**
 *
 * @author dilekarikan
 */

public class CVRP {
    public static Scanner scanner;
    public static int count[];
    public static double[][] cost;
    private static final int DEPOT = 1;
    private ArrayList<Location> map; // current map
    private ArrayList<Location> mapRecord; // the original map which doesn't change
    private ArrayList<ArrayList<Integer>> population;
    private ArrayList<ArrayList<Integer>> bestRoutes;
    private float bestCost = 9999999;
        
    public static int[] demands;
    public static int customers; 
    public static int vehicles;
    public static int capacity;
    public static double[][] locations;
    
    private static int VEHICLE_CAPACITY;

    public static int getVEHICLE_CAPACITY() {
        return VEHICLE_CAPACITY;
    }

    public static int getVehicles() {
        return vehicles;
    }    

    public static double[][] getLocations() {
        return locations;
    }

    public static int[] getDemands() {
        return demands;
    }

    public static int getCustomers() {
        return customers;
    }
        
    
    public static int[] arraycopy(int array1[], int length){
        int newarray[] = new int[length+1];
        newarray[0] = 999999;
        for (int i =0; i< length; i++){
                newarray[i+1] = array1[i];
            }
        return newarray;
        }
    
   public static double[][] arraycopy2(double array1[][], int length){
    double newarray[][] = new double[length+1][length+1];
    newarray[0][1] = -1;
    newarray[0][0] = -1;
    for (int i =0; i< length; i++){
            newarray[i+1][0] = array1[i][0];
            newarray[i+1][1] = array1[i][1];
        }
    return newarray;
    }
   
     static int findCapacity(ArrayList<Integer> routeConvert) throws Exception {
 
        Field field = ArrayList.class.getDeclaredField("elementData");
 
        field.setAccessible(true);
 
        return ((Object[]) field.get(routeConvert)).length;
 
    }
 
    
	public static void main(String[] args) throws FileNotFoundException{
        DecimalFormat dec = new DecimalFormat("#0.0");                
        scanner = new Scanner(new File(args[0]));
        customers = scanner.nextInt();
        vehicles = scanner.nextInt();
        capacity = scanner.nextInt();
        VEHICLE_CAPACITY = capacity;
        count = new int[customers];          
        Arrays.fill(count, 0);
        locations = new double[customers][customers];
        demands = new int[customers];
        for(int i = 0;i < customers; i++){
                demands[i] = scanner.nextInt();
                locations[i][0] = scanner.nextDouble();
                locations[i][1] = scanner.nextDouble();
                count[i]++;                          
            }


//        CVRPData x = new CVRPData();
//        
//        x.ReadData(args);
//        double[][] pr=x.locations;
//        for(int i=0;i<x.getCustomers();i++){
//        System.out.println(pr[i][0]);
//        }
       
        int popNum = 10;
        int generation = 100;
        int crossRate = 25;
        int mutationRate = 25;
        CVRP cvrp = new CVRP();		
        cvrp.run(popNum, generation, crossRate, mutationRate);		
//        System.out.println("Best solution: ");
        
        System.out.println(dec.format(cvrp.bestCost));
        ArrayList<ArrayList<Integer>> new_position = new ArrayList<ArrayList<Integer>>(cvrp.bestRoutes);
        for (int i = 0; i < cvrp.bestRoutes.size(); i ++) {
            for(int j = 0; j<cvrp.bestRoutes.get(i).size();j++){
            new_position.get(i).set(j,cvrp.bestRoutes.get(i).get(j)-1);
        }
//        System.out.println(new_position.get(i));
//        System.out.println(Arrays.toString(new_position.get(i).toArray()).replace("[", "").replace("]", "").replace(",", ""));
        System.out.println(new_position.get(i).toString().replace("[", "").replace("]", "").replace(",", ""));
//      System.out.println(Arrays.toString(cvrp.bestRoutes.get(i).toArray()).replace("[", "").replace("]", "").replace(",", ""));
           
        }
        
	}
        

	/* operation over generation */
	private void run(int popNum, int generation, int crossRate, int mutationRate) {
            initMap();
            ArrayList<Integer> bestRoute = new ArrayList<Integer>();
            ArrayList<ArrayList<Integer>> routeMin = new ArrayList<ArrayList<Integer>>();          
            double costMin = 9999;
            float costMinTotal = 0;
            Vehicle truck;

		do {
			costMin = 9999;
			truck = new Vehicle();
			moveTruckAdvanced(truck);
			if (truck.getRouteInIndex().size() <= 2)
				break;
//                      MyArrayList routeConvert = new MyArrayList();
//                        CapacityTrackingArrayList routeConvert = new CapacityTrackingArrayList();
			ArrayList<Integer> routeConvert = new ArrayList<Integer>();
//                        routeConvert.ensureCapacity(vehicles);
			for (int i = 0; i < truck.getRoute().size(); i ++)                              
//                            if(routeConvert.getCapacity() < vehicles){                               
                        routeConvert.add(truck.getRoute().get(i).getindex()); 
//                            }
			populate(routeConvert, popNum);
			for (int i = 0; i < generation; i++) {
				crossOver(crossRate);
				mutation(mutationRate);
				for (int j = 0; j < population.size(); j++) {
					if (calcRouteInt(new ArrayList<Integer>(population.get(j))) < costMin) {
						costMin = calcRouteInt(new ArrayList<Integer>(
								population.get(j)));
						bestRoute = new ArrayList<Integer>(population.get(j));
                                                
					}
				}				
			}
			costMinTotal += costMin;
			routeMin.add(new ArrayList<Integer>(bestRoute));   
		} while (truck.getRoute().size() > 0);
                
                //} while (truck.getRoute().size() > 0 && routeMin.size() < vehicles);
//                if(routeMin.size()<vehicles){                            
//                      ArrayList<Integer> x = new ArrayList<Integer>(Arrays.asList(1,1));
//                      for(int r=0;r<vehicles-bestRoute.size()+1;r++){
//                      routeMin.add(new ArrayList<Integer>(x)); 
//                    }                        
//
//                }
                        
		if (costMinTotal < bestCost) {
			bestCost = costMinTotal;
			bestRoutes = new ArrayList<ArrayList<Integer>>(routeMin);                       
		}

	}
        
        

	/* cross over the population one time */
	private ArrayList<ArrayList<Integer>> crossOver(int crossRate) {
		ArrayList<ArrayList<Integer>> nextGen = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> parents = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> parent1 = new ArrayList<Integer>();
		ArrayList<Integer> parent2 = new ArrayList<Integer>();
		ArrayList<Integer> child1 = new ArrayList<Integer>();
		ArrayList<Integer> child2 = new ArrayList<Integer>();
		Random random = new Random();
		int position1 = 0;
		int position2 = 0;
		while (population.size() != 0) {
			parents = getParents(crossRate);
			parent1 = parents.get(0);
			parent2 = parents.get(1);
			child1 = parent1;
			child2 = parent2;
			position1 = random.nextInt(parent1.size() - 2) + 1;
			position2 = random.nextInt(parent1.size() - 2 - position1 + 1)+ position1 + 1;
			for (int j = position1; j <= position2; j++) {
				int gene1 = child1.get(j);
				int gene2 = child2.get(j);
				int tmp1 = gene2;
				int tmp2 = gene1;
				child1.set(child1.indexOf(gene2), gene1);
				child1.set(j, new Integer(tmp1));
				child2.set(child2.indexOf(gene1), gene2);
				child2.set(j, new Integer(tmp2));
			}
			nextGen.add(new ArrayList<Integer>(child1));
			nextGen.add(new ArrayList<Integer>(child2));
		}
		population = nextGen;
		return nextGen;
	}

	/* get two parents from populatio n for crossover */
        // crossover exchanges routes of a vehicle among two different two parents
	private ArrayList<ArrayList<Integer>> getParents(int crossRate) {
		Random random = new Random();
		ArrayList<ArrayList<Integer>> parent = new ArrayList<ArrayList<Integer>>();
		int prob = 0;
		double fitness = 0;
		while ((population.size() != 0) && (parent.size() < 2)) {
			prob = random.nextInt(population.size());
			if (prob < crossRate) {
				fitness = random.nextDouble() * getFitnessTotal(population);
				if (fitness < getFitness(population, prob)) {
					parent.add(new ArrayList<Integer>(population.get(prob)));
					population.remove(prob);
				}
			}
		}
		return parent;
	}

	/* calculate the fitness of a chromosome */
	private double getFitness(ArrayList<ArrayList<Integer>> pop, int index) {
		double fitness = 0;
		double totalLength = 0;
		for (int i = 0; i < pop.size(); i++) {
			totalLength += calcRouteInt(pop.get(i));
		}
		fitness = totalLength / calcRouteInt(pop.get(index));
		return fitness;
	}

	/* calculate the fitness of a population */
	private double getFitnessTotal(ArrayList<ArrayList<Integer>> pop) {
		double fitnessTotal = 0;
		double totalLength = 0;
		for (int i = 0; i < pop.size(); i++) {
			totalLength += calcRouteInt(pop.get(i));
		}
		for (int i = 0; i < pop.size(); i++) {
			fitnessTotal += totalLength / calcRouteInt(pop.get(i));
		}
		return fitnessTotal;
	}

	/* make a population based on original route, with mutation rate of 50 */
	private void populate(ArrayList<Integer> route, int popNum) {
//        private void populate(MyArrayList route, int popNum) {
		population = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> tmp = new ArrayList<Integer>();
//                MyArrayList tmp = new MyArrayList();
		for (int i = 0; i < popNum; i++) {
			tmp = mutationInt(route, 50);
			population.add(new ArrayList<Integer>(tmp));
                }
	}

	/*
	 * move the truck continuously until run out of buns how aggressive it is to
	 * choose the closest customer every time is controlled by argument
	 * aggression
	 */
	private void moveTruck(Vehicle truck, int aggression) {
		double previousDgree = 0;
		while (true) {
			Random random = new Random(); // random generator
			int counter = 0; // for random search count
			int prob; // probability of being aggressive
			int location; // random search location
			int dest = -1; // closest destination
			double distMin = 9999; // minimum distance
			int cost = 0; // cost of the route
			prob = random.nextInt(100); // get random number
			// if not being aggressive, explore the map for a random location
			if (prob > aggression) {
				do {
					counter++;
					location = random.nextInt(map.size() - 2) + 2;
					if (counter == 100) // stop the loop when no idea result
										// found
						break;
				} while ((map.get(location) == null)
						|| (truck.getRemaining() < getDemand(location)));
				// skip random location when invalid
				if ((map.get(location) != null)
						&& (truck.getRemaining() >= getDemand(location))) {
					distMin = getDist(truck.getLocation(), map.get(location));
					dest = location;
					cost = getDemand(location);
				}
			} else { // if being aggressive, use the closest location as
						// destination
				for (int i = 2; i < map.size(); i++) {
					if ((map.get(i) != null)
							&& (truck.getRemaining() >= getDemand(i))) {
						if ((getDist(truck.getLocation(), map.get(i)) + 0
								* getDist(truck.getLocation(), map.get(i))
								* (double) Math.abs((previousDgree - getDegree(
										truck.getLocation(), map.get(i)))
										/ getDegree(truck.getLocation(),
												map.get(i)))) < distMin) {
							distMin = getDist(truck.getLocation(), map.get(i))
									+ 0
									* getDist(truck.getLocation(), map.get(i))
									* (double) Math
											.abs((previousDgree - getDegree(
													truck.getLocation(),
													map.get(i)))
													/ getDegree(
															truck.getLocation(),
															map.get(i)));
							dest = i;
							cost = getDemand(i);
						}
					}
				}
			}
			/*
			 * if did not find a destination, which is due to insufficient buns
			 * break the loop, and this is the life of one truck
			 */

			if (dest == -1)
				break;
			/*
			 * update truck information and remove visited location, so that it
			 * will not be searched by another truck
			 */
			previousDgree = getDegree(truck.getLocation(), map.get(dest));
			truck.setLocation(map.get(dest));
			truck.setRemaining(truck.getRemaining() - cost);
			truck.addRoute(map.get(dest));
			truck.addRouteInIndex(dest);
			truck.addLength(distMin);
			map.set(dest, null);
		}
		truck.addRoute(map.get(DEPOT));
		truck.addRouteInIndex(DEPOT);
		truck.addLength(getDist(truck.getLocation(), map.get(DEPOT)));
                
	}

	private void moveTruckAdvanced(Vehicle truck) {
		for (int i = 2; i < map.size(); i++) {                        
			if (map.get(i) == null)
				continue;
			if (truck.getRemaining() < getDemand(i))
				break;
			if ((map.get(i) != null) && (truck.getRemaining() >= getDemand(i))) {
				truck.setRemaining(truck.getRemaining() - getDemand(i));
				truck.addLength(getDist(truck.getLocation(), map.get(i)));
				truck.setLocation(map.get(i));
				truck.addRoute(map.get(i));
				truck.addRouteInIndex(i);
				map.set(i, null);
			}
		}
		truck.addRoute(map.get(DEPOT));
		truck.addRouteInIndex(DEPOT);
		truck.addLength(getDist(truck.getLocation(), map.get(DEPOT)));
                
	}

	/* calculate degree between two locations */
	private double getDegree(Location target, Location origin) {
		double degree = Math.toDegrees(Math.atan((double) ((double) (target
				.getY_coord() - origin.getY_coord()))
				/ ((double) (target.getX_coord() - origin.getX_coord()))));
		return degree;
	}

	/* route mutation, which switches the order of locations */
	private void mutation(int mutationRate) {
		for (int i = 0; i < population.size(); i++) {
			population.set(i, mutationInt(population.get(i), mutationRate));
		}
	}

        // mutation switches two locations in a route
	private ArrayList<Integer> mutationInt(ArrayList<Integer> route,int mutationRate) {
//        private MyArrayList mutationInt(MyArrayList route,int mutationRate) {
		int tmp;
		Random random = new Random();                
		int prob;
		for (int i = 1; i < route.size() - 1; i++) {
			prob = random.nextInt(100);
			if (prob <= mutationRate) {
				tmp = 0;
				prob = random.nextInt(route.size() - 2);
				tmp = (int) route.get(i);
				route.set(i, route.get(prob + 1));
				route.set(prob + 1, tmp);
			}
		}
		return route;
	}

	/* convert location to integer */
	private ArrayList<Integer> locToInt(ArrayList<Location> route) {
		ArrayList<Integer> result = new ArrayList<Integer>();
		for (int i = 0; i < route.size(); i++) {
			result.add(route.get(i).getindex());
		}
		return result;
	}

	private double calcRouteInt(ArrayList<Integer> route) {
		double result = 0;
		for (int i = 0; i < route.size() - 1; i++){
			result += getDist(route.get(i), route.get(i + 1));
		}
		return result;
	}

	/* get the demand of a customer by its index */
	private int getDemand(int index) {
		return map.get(index).getDemand();
	}

	/* calculate distance between two locations */
	private double getDist(int from, int to) {
		double x1 = mapRecord.get(from).getX_coord();
		double y1 = mapRecord.get(from).getY_coord();
		double x2 = mapRecord.get(to).getX_coord();
		double y2 = mapRecord.get(to).getY_coord();

		return Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
	}

	public double getDist(Location from, Location to) {
		double x1 = from.getX_coord();
		double y1 = from.getY_coord();
		double x2 = to.getX_coord();
		double y2 = to.getY_coord();

		return Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
	}

	/* initialize map, put all the data into */
	private void initMap() {
		ArrayList<Location> tmpMap = new ArrayList<Location>();
		map = new ArrayList<Location>();
		mapRecord = new ArrayList<Location>();
		for (int i = 0; i <= customers; i++) {
			map.add(new Location(i));
			if (i > 0) {
				if ((map.get(i).getX_coord() > map.get(DEPOT).getX_coord())
						&& (map.get(i).getY_coord() > map.get(DEPOT)
								.getY_coord()))
					map.get(i).setDegree(getDegree(map.get(i), map.get(DEPOT)));
				else if ((map.get(i).getX_coord() < map.get(DEPOT).getX_coord())
						&& (map.get(i).getY_coord() > map.get(DEPOT)
								.getY_coord()))
					map.get(i).setDegree(
							getDegree(map.get(i), map.get(DEPOT)) + 180);
				else if ((map.get(i).getX_coord() < map.get(DEPOT).getX_coord())
						&& (map.get(i).getY_coord() < map.get(DEPOT)
								.getY_coord()))
					map.get(i).setDegree(
							getDegree(map.get(i), map.get(DEPOT)) + 180);
				else if ((map.get(i).getX_coord() > map.get(DEPOT).getX_coord())
						&& (map.get(i).getY_coord() < map.get(DEPOT)
								.getY_coord()))
					map.get(i).setDegree(
							getDegree(map.get(i), map.get(DEPOT)) + 360);
				else if ((map.get(i).getX_coord() == map.get(DEPOT)
						.getX_coord())
						&& (map.get(i).getY_coord() > map.get(DEPOT)
								.getY_coord()))
					map.get(i).setDegree(getDegree(map.get(i), map.get(DEPOT)));
				else if ((map.get(i).getX_coord() == map.get(DEPOT)
						.getX_coord())
						&& (map.get(i).getY_coord() < map.get(DEPOT)
								.getY_coord()))
					map.get(i).setDegree(
							getDegree(map.get(i), map.get(DEPOT)) + 360);
				else if ((map.get(i).getX_coord() > map.get(DEPOT).getX_coord())
						&& (map.get(i).getY_coord() == map.get(DEPOT)
								.getY_coord()))
					map.get(i).setDegree(getDegree(map.get(i), map.get(DEPOT)));
				else if ((map.get(i).getX_coord() < map.get(DEPOT).getX_coord())
						&& (map.get(i).getY_coord() == map.get(DEPOT)
								.getY_coord()))
					map.get(i).setDegree(
							getDegree(map.get(i), map.get(DEPOT)) + 180);
			}
		}

		tmpMap.add(new Location(map.get(0)));
		tmpMap.add(new Location(map.get(DEPOT)));
		while (map.size() > 2) {
			int degreeMinIndex = 0;
			double degreeMin = 999999;
			for (int j = 2; j < map.size(); j++) {
				if (map.get(j).getDegree() <= degreeMin) {
					degreeMin = map.get(j).getDegree();
					degreeMinIndex = j;
				}
			}
			tmpMap.add(new Location(map.get(degreeMinIndex)));
			map.remove(degreeMinIndex);
		}
                
		map = new ArrayList<Location>(tmpMap);

		Random random = new Random();
		int ranNum = random.nextInt(vehicles) + 2;
		ArrayList<Location> tmp1 = new ArrayList<Location>();
		ArrayList<Location> tmp2 = new ArrayList<Location>();
		ArrayList<Location> tmp3 = new ArrayList<Location>();
		for (int i = 0; i < 2; i++)
			tmp1.add(new Location(map.get(i)));
		for (int i = 2; i < ranNum; i++)
			tmp2.add(new Location(map.get(i)));
		for (int i = ranNum; i < map.size(); i++)
			tmp3.add(new Location(map.get(i)));
                
		map.clear();
                                
		for (int i = 0; i < tmp1.size(); i++)
			map.add(tmp1.get(i));
		for (int i = 0; i < tmp3.size(); i++)
			map.add(tmp3.get(i));                
		for (int i = 0; i < tmp2.size(); i++)
			map.add(tmp2.get(i));
		for (int i = 0; i < customers+1; i++)
			mapRecord.add(new Location(i));
	}
        
//  public class MyArrayList extends ArrayList<Integer> {
//  @Override
//  public boolean add(Integer e) {
//      if (this.size() <= vehicles) {
//          return super.add(e);
//      }
//      else
//      return false;
//  }
//}
//public class CapacityTrackingArrayList<T> extends ArrayList<T> {
//
//   // Now, capacity tracking stuff:
//   private int currentCapacity = vehicles;
//
//   public int getCapacity() { return currentCapacity; }
//
//   public void ensureCapacity(int arg) {
//     currentCapacity = arg;
//     super.ensureCapacity(arg);
//   }
//
//   public void trimToSize() { currentCapacity = size(); super.trimToSize(); }
//
//}
}
    