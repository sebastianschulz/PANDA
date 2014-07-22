package de.fuberlin.panda.metadata.result.fusion.clustering;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * This abstract class is a simple data class for sorting data by
 * the OPTICS-OF algorithm. Methods regarding the distance between
 * to {@code ClusteringObjects} have to be implemented by subclasses.
 * The only complex methods in this class are the ones for setting the local 
 * reachability density and the outlier factor used for the OPTICS OF algorithm.
 * These are set in {@link #setLocalReachabilityDensity(double, int)}
 * respectively {@link #setOutlierFactor(int)}.
 * 
 * @see #setCoreDistance(SortedSet, int)
 * @see #setLocalReachabilityDensity(double, int)
 * @see #setOutlierFactor(int)
 * @see #getDistance(ClusteringObject)
 * 
 * @author Sebastian Schulz
 * @since 27.01.2014
 */
public abstract class ClusteringObject {
	protected static final double REACHABLE_UNDEFINED = Double.MAX_VALUE;
	protected static final double UNDEFINED = -1d;
	
	protected Double coreDistance = UNDEFINED;
	private double reachabilityDistance = REACHABLE_UNDEFINED;
	private double localReachabilityDensity = UNDEFINED;
	private double outlierFactor = UNDEFINED;
	
	private int metadataIndex;
	protected TreeSet<ClusteringObject> neighbors = new TreeSet<>(new NeighborsComparator());
	
	//help distance to determine the order of neighbors
	private Double helpEpsilonDistance = 0d;
	private boolean isProcessed = false;
	
	private boolean hasMetadata = true;
	
	public ClusteringObject(int metadataIndex) {
		this.metadataIndex = metadataIndex;
	}
	
	/**
	 * This abstract method has to be implemented by a subclass. It calculates the core distance
	 * which is defined as minimal distance for which the {@code ClusteringObject} still is a 
	 * core object (has at least {@code minNeighbors} in this distance).
	 * 
	 * @param minNeighbors - an {@code int} value which represents the minimal count of neighbors for the
	 * 	object to be a core object.
	 */
	public abstract void setCoreDistance(int minNeighbors);
	
	public Double getCoreDistance() {
		if(coreDistance == null) {
			return Double.MAX_VALUE;
		} else {
			return coreDistance;	
		}
	}
	
	public void setReachabilityDistance(Double reachabilityDistance) {
		this.reachabilityDistance = reachabilityDistance;
	}
	
	public Double getReachabilityDistance() {
		return reachabilityDistance;
	}
	
	public boolean hasReachabilityDistance() {
		if (reachabilityDistance == REACHABLE_UNDEFINED) {
			return false;
		}
		return true;
	}
	
	/**
	 * This method calculates the local reachability densitiy of the {@code ClusteringObject}.
	 * This value represents the inverse of the average reachability distance from the 
	 * {@code minNeighbors}-nearest-neighbors of the {@code ClusteringObject}.
	 * 
	 * @param neighborsReachabilitySum - a {@code double} value which represents the sum of the
	 *  reachability distances of the {@code minNeighbors}-nearest-neighbors.
	 * @param minNeighbors - an {@code int} value which represents the minimal needed neighbors
	 *  for the {@code ClusteringObject} to be a core object.
	 */
	public void setLocalReachabilityDensity(double neighborsReachabilitySum, int minNeighbors) {
		localReachabilityDensity = 1 / (neighborsReachabilitySum / minNeighbors);
	}
	
	public double getLocalReachabilityDensity() {
		return localReachabilityDensity;
	}

	/**
	 * This method calculates the outlier factor of the {@code ClusteringObject}. This value 
	 * represents the average of the ratios of the local reachability densities of the 
	 * {@code minNeighbors}-nearest-neighbors and the {@code ClusteringObject}. If a cluster is
	 * of uniform density this factor should be close around 1, because the local reachability
	 * densities are identical.
	 * 
	 * @param minNeighbors - an {@code int} value which represents the minimal needed neighbors
	 *  for the {@code ClusteringObject} to be a core object.
	 */
	public void setOutlierFactor(int minNeighbors) {
		double localReachabilityDensityRatio = 0;
		int neighborsCount = 0;
		
		for (ClusteringObject neighbor : neighbors) {
			if (neighborsCount < minNeighbors) {
				neighborsCount++;
				localReachabilityDensityRatio += 
						(neighbor.getLocalReachabilityDensity() / localReachabilityDensity);
			} else {
				break;
			}
		}
		
		outlierFactor = localReachabilityDensityRatio / minNeighbors;
	}
	
	public double getOutlierFactor() {
		return outlierFactor;
	}
	
	public int getMetadataIndex() {
		return metadataIndex;
	}
	
	public void addNeighbor(ClusteringObject neighbor) {
		neighbors.add(neighbor);
	}
	
	public TreeSet<ClusteringObject> getNeighbors() {
		return neighbors;
	}

	public void setHelpEpsilonDistance(double distance) {
		helpEpsilonDistance = distance;
	}
	
	public double getHelpEpsilonDistance() {
		return helpEpsilonDistance;
	}

	public void setProcessed() {
		this.isProcessed = true;
	}
	
	public boolean isProcessed() {
		return isProcessed;
	}
	
	public void setNoMetadata() {
		hasMetadata = false;
	}
	
	public boolean hasMetadata() {
		return hasMetadata;
	}

	/**
	 * This abstract method has to be implemented by a subclass. It calculates and returns the 
	 * distance form this {@code ClusteringObject} to the given {@code destinationObject}.
	 * 
	 * @param destinationObject - a arbitrary {@code ClusteringObject}.
	 * @return the distance as an {@code double} value.
	 */
	public abstract double getDistance(ClusteringObject destinationObject);
}