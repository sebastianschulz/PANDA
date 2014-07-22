package de.fuberlin.panda.metadata.result.fusion.clustering;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import de.fuberlin.panda.metadata.MetadataConnector;

/**
 * This class is basically an implementation of the OPTICS-OF algorithm developed by
 * Mihael Ankerst, Markus M. Breunig, Hans-Peter Kriegel and Jörg Sander. It detects
 * clusters in a set of data by augmented ordering the entries and assign them to 
 * clusters regarding their outliner factors. <br><br>
 * 
 * For a closer look on the OPTICS algorithm check the paper "OPTICS: Ordering Points To 
 * Identify the Clustering Structure".
 * For more information about OPTICS OF check the paper "OPTICS-OF: Identifying Local Outliers".
 * You can find both papers by a simple google search. 
 * 
 * @see #ClusterDetector(double, int)
 * @see #detectClusters(TreeSet)
 * @see #createClusterOrdering(TreeSet)
 * @see #createClusterResult()
 * @see #isInClusterRange(ClusteringObject)
 * @see #calculateReachabilities(TreeSet, ClusteringObject)
 * @see #updateSeedQueue(TreeSet, ClusteringObject)
 * @see #calculateNeighborhood(ClusteringObject, TreeSet)
 * 
 * @author Sebastian Schulz
 * @since 27.01.2014
 */
public abstract class ClusterDetector {
	private static Logger logger = Logger.getLogger(MetadataConnector.class.getName());
	
	public static final int NO_METADATA = -1;
	public static final int OUTLIER = 0;
	
	protected List<ClusteringObject> augmentedClusteringObjectList = new ArrayList<>();
	private PriorityQueue<ClusteringObject> orderedSeedQueue;	
	
	protected double neighborhoodRadius = 0;
	protected int minNeighbors = 0;
	protected double maxOutlierFactor = 1.5d;
	
	/**
	 * Public Constructor, which sets the neighborhood radius and the minimal neighbors which 
	 * should be in the radius to detect a cluster.
	 * 
	 * @param neighborhoodRadius - a {@code double} value which represents the radius around an 
	 * 	clustering object where {@code minNeighbors} other objects has to be to detect a cluster.
	 * @param minNeighbors - a {@code int} value which represents the minimal count of neighbors
	 *  which has to be in the {@code neighborhoddRadius} to mark a group of objects as n cluster.
	 */
	public ClusterDetector(double neighborhoodRadius, int minNeighbors) {
		this.neighborhoodRadius = neighborhoodRadius;
		this.minNeighbors = minNeighbors;
	}
	
	/**
	 * Public Constructor, which sets the neighborhood radius, the minimal neighbors and the
	 * maximum outlier factor which are used to detect the clusters.
	 * 
	 * @param neighborhoodRadius - a {@code double} value which represents the radius around an 
	 * 	clustering object where {@code minNeighbors} other objects has to be to detect a cluster.
	 * @param minNeighbors - a {@code int} value which represents the minimal count of neighbors
	 *  which has to be in the {@code neighborhoddRadius} to mark a group of objects as n cluster.
	 * @param maxOutlierFactor - a {@code double} value which represents the maximum outlier factor
	 * 	for which an object still belongs to a cluster.
	 */
	public ClusterDetector(double neighborhoodRadius, int minNeighbors, double maxOutlierFactor) {
		this.neighborhoodRadius = neighborhoodRadius;
		this.minNeighbors = minNeighbors;
		this.maxOutlierFactor = maxOutlierFactor;
	}
	
	/**
	 * This method is called to perform the clustering in accord with the OPTICS OF algorithm.
	 * It calls {@link #createClusterOrdering(TreeSet)} to generate a augmented cluster-ordering of 
	 * a given {@code Set} of {@code ClusteringObject}s. With calling {@link #createClusterResult()}
	 * the ordering is iterated to obtain the clusters and return them in a {@code ClusteringResult}
	 * object.
	 * 
	 * @param clusteringObjects - a {@code TreeSet} of {@code ClusteringObject}s which should be 
	 * 	analyzed.
	 * @return ClusteringResult - a {@code ClusteringResult} object which holds the outlier indices
	 * 	as well as lists of indices of cluster members.
	 */
	public ClusteringResult detectClusters(TreeSet<? extends ClusteringObject> clusteringObjects) {
		createClusterOrdering(clusteringObjects);
		return createClusterResult();
	}

	/**
	 * This method is responsible for ordering the given {@code TreeSet} of {@code ClusteringObject}s
	 * in an augmented form. The ordering is archieved in three steps: <br>
	 * 1. calculate the infinite neighborhood and the core distance for each entry with
	 *    {@link #calculateCoreDistances(TreeSet)}<br>
	 * 2. calculate the reachabilities and generate an ordering this basis with
	 *    {@link #calculateReachabilities(TreeSet)}<br>
	 * 3. calculate the augmentation with getting the outlier factors of each entry with
	 *    {@link #calculateOutlierFactors()}
	 * 
	 * @param clusteringObjects - a {@code TreeSet} of {@code ClusteringObject}s which should be 
	 * 	analyzed.
	 */
	private void createClusterOrdering(TreeSet<? extends ClusteringObject> clusteringObjects) {
		calculateCoreDistances(clusteringObjects);
		logger.debug("OPTICS: calculated all core distances");
		calculateReachabilities(clusteringObjects);
		logger.debug("OPTICS: calculated all reachability distances");
		calculateOutlierFactors(clusteringObjects);
		logger.debug("OPTICS: calculated all outlier factor");
	}

	/**
	 * This method calculates the infinite neighborhood and sets the infinite core distance of each 
	 * {@code clusteringObject}. The infinite neighborhood is defined as either all neighbors which 
	 * are in the {@code neighborhoodRadius} range (if the {@code clusteringObject} is a real core 
	 * object) or {@code minNeighbors} objects (if the {@code clusteringObject} has less then 
	 * {@code minNeighbors} objects in the range of {@code neighborhoodRadius}.
	 * 
	 * @param clusteringObjects - a {@code TreeSet} of {@code ClusteringObject}s which should be 
	 * 	analyzed.
	 */
	protected void calculateCoreDistances(TreeSet<? extends ClusteringObject> clusteringObjects) {
		for (ClusteringObject clusteringObject : clusteringObjects) {
			calculateNeighborhood(clusteringObject, clusteringObjects);
			clusteringObject.setCoreDistance(minNeighbors);
		}
	}
	
	/**
	 * This method does most of the work of the OPTICS OF algorithm. It creates the augmented clustering
	 * order of a given {@code TreeSet} of {@code ClusteringObject}s. It iterates over every 
	 * {@code clusteringObject} which wasn't processed yet. First the reachability distance is set to 
	 * {@code UNDEFINED} because the given {@code clusteringObject} wasn't reachable from
	 * other objects (else it would be processed from the orderedSeedQueue). Afterwards it is added to 
	 * the {@code resultClusterList}.<br><br>
	 * Because the current object has at least {@code minNeighbors} neighbors the 
	 * {@link #updateSeedQueue(ClusteringObject)} method is called to insert or update the reachability 
	 * distance of all neighbors. <br><br>
	 * Now all neighbors are processed the same way and the {@code orderedSeedQueue} is updated if necessary.
	 * 
	 * @param clusteringObjects - a {@code TreeSet} of {@code ClusteringObject}s which should be 
	 * 	analyzed.
	 * @param clusteringObject - the current {@code ClusteringObject}.
	 */
	protected void calculateReachabilities(TreeSet<? extends ClusteringObject> clusteringObjects) {
		orderedSeedQueue = new PriorityQueue<>(clusteringObjects.size(), new ReachabilityComparator());
		for (ClusteringObject clusteringObject : clusteringObjects) {
			if (!clusteringObject.isProcessed()) {
				clusteringObject.setProcessed();
				clusteringObject.setReachabilityDistance(ClusteringObject.REACHABLE_UNDEFINED);
				augmentedClusteringObjectList.add(clusteringObject); 
				
				updateSeedQueue(clusteringObject);
				while (!orderedSeedQueue.isEmpty()) {
					clusteringObject = orderedSeedQueue.poll();
					clusteringObject.setProcessed();
					augmentedClusteringObjectList.add(clusteringObject); 
					updateSeedQueue(clusteringObject);
				}
			}
		}
	}

	/**
	 * This method is responsible for updating the {@code orderedSeedQueue}. A loop iterates over
	 * all unprocessed neighbors of the given {@code ClusteringObject} and checks if the 
	 * reachability distance from the current {@code neighbor} to the {@code clusteringObject} can
	 * be minimized. The call of the {@link #updateReachabilityDistance(ClusteringObject, double)} 
	 * handles the update of the reachability distance and the placement in the {@code orderedSeedQueue}.
	 * <br><br>
	 * If the current neighbor is one of the {@code minNeighbors} neighbors the reachability distance 
	 * from the {@code centerObject} to the {@code neighbor} is added up to the 
	 * {@code neighborsReachabilitySum} which is needed for calculating the local reachability denisity of 
	 * the {@code centerObject}. This value is set by calling 
	 * {@code ClusteringObject#setLocalReachabilityDensity(double, int)} after all neighbors are processed.
	 * <br>
	 * Notice that the reachability distance in this case is the max{core distance of {@code neighbor}, 
	 * distance of {@code centerObject} to {@code neighbor}}!
	 * 
	 * @param neighbors - a {@code Set} of {@code ClusteringObject}s which represent the neighbors of 
	 * 	the given {@code ClusteringObject}.
	 * @param clusteringObject - the current processed {@code ClusteringObject}.
	 */
	private void updateSeedQueue(ClusteringObject centerObject) {
		double coreDistance = centerObject.getCoreDistance();
		double neighborsReachabilitySum = 0;
		
		int neighborsCount = 0;
		for (ClusteringObject neighbor : centerObject.getNeighbors()) {
			double distance = centerObject.getDistance(neighbor);
			
			if (!neighbor.isProcessed()) {
				double newReachabilityDistance = Math.max(coreDistance, distance);
				updateReachabilityDistance(neighbor, newReachabilityDistance);
			}
			
			//sum of reachabinility distances from centerObject to neighbors
			if (neighborsCount < minNeighbors) {
				double neighborsCoreDistance = neighbor.getCoreDistance();
				double reachabilityDistance = Math.max(neighborsCoreDistance, distance); 
				neighborsReachabilitySum += reachabilityDistance;
				neighborsCount++;
			}
		}
		
		centerObject.setLocalReachabilityDensity(neighborsReachabilitySum, minNeighbors);
	}

	/**
	 * This method updates the reachability distance of a given {@code ClusteringObject} called 
	 * neighbor. If the neighbor hasn't had a reachability distance the new calculated distance
	 * is added as reachability distance and the neighbor is added to the {@code orderedSeedQueue}.
	 * Otherwise the reachability distance is updated in case the new reachability distance is smaller
	 * than the existing and the neighbor is moved in the {@code orderedSeedQueue}.
	 * 
	 * @param neighbor - a {@code ClusteringObject}, the current neighbor the reachability distance
	 *  should be updated for.
	 * @param newReachabilityDistance - a {@code double} value which represents the new reachability
	 *  distance.
	 */
	private void updateReachabilityDistance(ClusteringObject neighbor, double newReachabilityDistance) {
		if (!neighbor.hasReachabilityDistance()) {
			//insert object in orderedSeedList
			neighbor.setReachabilityDistance(newReachabilityDistance);
			orderedSeedQueue.add(neighbor);
		} else {
			//update object in orderedSeedList 
			if (newReachabilityDistance < neighbor.getReachabilityDistance()) {
				orderedSeedQueue.remove(neighbor);
				neighbor.setReachabilityDistance(newReachabilityDistance);
				orderedSeedQueue.add(neighbor);
			}
		}
	}
	
	/**
	 * This method iterates over the given {@code TreeSet} of {@code ClusteringObjects} and calls the 
	 * {@link ClusteringObject#setOutlierFactor(int)} for every entry to calculate the 
	 * outlier factors.
	 * 
	 * @param clusteringObjects - a {@code TreeSet} of {@code ClusteringObject}s which should be 
	 * 	analyzed.
	 */
	protected void calculateOutlierFactors(TreeSet<? extends ClusteringObject> clusteringObjects) {
		for (ClusteringObject clusteringObject : clusteringObjects) {
			clusteringObject.setOutlierFactor(minNeighbors);
		}
	}
	
	/**
	 * This method creates a new {@code ClusteringResult} object and adds the metadata indices 
	 * of the objects from the {@code augmentedClusteringObjectList} to the clustering result.
	 * 
	 * @return ClusteringResult - a {@code ClusteringResult} object which holds the outlier indices
	 * 	as well as lists of indices of cluster members.
	 */
	private ClusteringResult createClusterResult() {
		boolean wasPreviousObjectOutlier = false;
		ClusteringResult clusteringResult = new ClusteringResult();
		
		for (ClusteringObject clusteringObject : augmentedClusteringObjectList) {
			if (!clusteringObject.hasMetadata()) {
				clusteringResult.addNoMetadata(clusteringObject.getMetadataIndex());
				continue;
			}
			
			if (!isInClusterRange(clusteringObject)) {
				if (clusteringObject.getOutlierFactor() <= maxOutlierFactor) {
					clusteringResult.addToNewCluster(clusteringObject.getMetadataIndex());
					wasPreviousObjectOutlier = false;
				} else {
					clusteringResult.addOutlier(clusteringObject.getMetadataIndex());
					wasPreviousObjectOutlier = true;
				}
			} else if (wasPreviousObjectOutlier) {
				clusteringResult.addToNewCluster(clusteringObject.getMetadataIndex());
				wasPreviousObjectOutlier = false;
			} else {
				clusteringResult.addToExistingCluster(clusteringObject.getMetadataIndex());
			}
		}
		
		return clusteringResult;
	}

	/**
	 * This method simply checks whether the given {@code ClusteringObject} is in a cluster range with 
	 * other objects or not. There are three cases:
	 * <br>
	 * 1. Reachability distance is undefined, this means a "jump" to another cluster or to an outlier.<br>
	 * 2. Outlier factor is greater than the boundary value ({@code maxOutlierFactor}), this also means
	 * 	a "jump" to an outlier.
	 * 3. Outlier factor is smaller than the boundary, object is still in the same cluster.
	 * 
	 * @param clusteringObject - the current {@code ClusteringObject}.
	 * @return a boolean value which represents if the given {@code ClusteringObject} is in a cluster 
	 * 	range with other objects.
	 */
	private boolean isInClusterRange(ClusteringObject clusteringObject) {
		if (!(clusteringObject.hasReachabilityDistance()) ) {
			//reachabilityDistance: UNDEFINED > neighborhoodRadius --> new cluster!
			return false;
		} else if (clusteringObject.getOutlierFactor() > maxOutlierFactor) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * This abstract method has to be implemented by every sub class. It determines the neighborhood
	 * of a given {@code ClusteringObject} regarding the {@code neighborhoodRadius} and the 
	 * {@code minNeighbors}. 
	 * 
	 * @param clusteringObject - the current {@code ClusteringObject}, which is in the center of the 
	 * 	{@code neighborhoodRadius}.
	 * @param clusteringObjects - a {@code Set} of {@code ClusteringObject}s which should be 
	 *  analyzed.
	 */
	protected abstract void calculateNeighborhood(ClusteringObject clusteringObject, 
			TreeSet<? extends ClusteringObject> clusteringObjects);
	
}