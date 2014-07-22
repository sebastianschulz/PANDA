package de.fuberlin.panda.metadata.result.fusion.temporal;

import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import de.fuberlin.panda.metadata.MetadataConnector;
import de.fuberlin.panda.metadata.result.fusion.clustering.ClusterDetector;
import de.fuberlin.panda.metadata.result.fusion.clustering.ClusteringObject;
import de.fuberlin.panda.metadata.result.fusion.clustering.ClusteringResult;
import de.fuberlin.panda.metadata.result.fusion.clustering.NeighborsComparator;

/**
 * This class is a subclass of {@link ClusterDetector}. It extends the 
 * {@link ClusterDetector#detectClusters(Set)} method for eliminating all temporal
 * outliers without a timestamp. It also overrides the {@link #calculateNeighborhood(ClusteringObject, Set)}
 * method.
 * 
 * @see #TemporalClusterDetector(double, int)
 * @see #detectClusters(Set)
 * @see #eliminateOutliersWithoutTimestamp(Set)
 * @see #calculateNeighborhood(ClusteringObject, Set)
 * 
 * @author Sebastian Schulz
 * @since 28.01.2014
 */
public class TemporalClusterDetector extends ClusterDetector {
	private static Logger logger = Logger.getLogger(MetadataConnector.class.getName());
	
	public TemporalClusterDetector(double neighborhoodRadius, int minNeighbors) {
		super(neighborhoodRadius, minNeighbors);
	}

	public TemporalClusterDetector(double neighborhoodRadius, int minNeighbors, double maxOutlierFactor) {
		super(neighborhoodRadius, minNeighbors, maxOutlierFactor);
	}
	
	/**
	 * This method calls {@link #eliminateOutliersWithoutTimestamp(Set)} to remove 
	 * {@code TemporalClusteringObject}s without a timestamp and calls the 
	 * {@link ClusterDetector#detectClusters(Set)} for further processing.
	 * 
	 * @return ClusteringResult - a {@code ClusteringResult} object which holds the outlier indices
	 * 	as well as lists of indices of cluster members.
	 */
	@Override
	public ClusteringResult detectClusters(TreeSet<? extends ClusteringObject> clusteringObjects) {
		eliminateOutliersWithoutTimestamp(clusteringObjects);
		return super.detectClusters(clusteringObjects);
	}
	
	/**
	 * This method removes all {@code TemporalClusteringObject}s which doesn't have an
	 * expiration date timestamp (marked by timestamp = -1) from the {@code Set} of
	 * {@code ClusteringObject}s for further processing. It also marks the object as an outlier 
	 * by setting its {@code clusterId} to {@code NO_METADATA} (-1) and adds it to the 
	 * {@code augmentedClusteringObjectList}.
	 * 
	 * @param clusteringObjects - a {@code Set} of {@code ClusteringObject}s which should be 
	 * 	analyzed.
	 */
	protected void eliminateOutliersWithoutTimestamp(TreeSet<? extends ClusteringObject> clusteringObjects) {
		Iterator<? extends ClusteringObject> iterator = clusteringObjects.iterator();
		while (iterator.hasNext()) {
			TemporalClusteringObject clusteringObject = (TemporalClusteringObject) iterator.next();
			if (clusteringObject.getTimestamp() == -1) {
				clusteringObject.setNoMetadata();
				augmentedClusteringObjectList.add(clusteringObject);
				iterator.remove();
				logger.debug("Removed clustering object without temporal metadata information");
			}
		}
	}
	
	/**
	 * This method builds {@code SortedSet} of {@code ClusteringObjects} which represent the 
	 * neighbors of a given {@code ClusteringObject}. It checks for each neighbor if its distance
	 * to the given object is less or equal the {@code neighborhoodRadius} and adds it to the set.
	 * The set is sorted by the {@code helpEpsilonDistance} which represents the distance from the 
	 * given object to the neighbor. Because of that reason it is easier to calculate the core distance 
	 * of a {@code TemporaltClusteringObject}.<br><br>
	 * This method heavily influences the runtime of the OPTICS algorithm!
	 */
	@Override
	protected void calculateNeighborhood(ClusteringObject clusteringObject,
			TreeSet<? extends ClusteringObject> clusteringObjects) {
		PriorityQueue<ClusteringObject> distancedNeighbors = 
				new PriorityQueue<>(clusteringObjects.size(), new NeighborsComparator());
		
		//FIXME Set O(n^2) -> Tree O(n *log n) (treebased approach)
		for (ClusteringObject neighbor : clusteringObjects) {
			if (!neighbor.equals(clusteringObject)) {
				TemporalClusteringObject temporalNeighbor = (TemporalClusteringObject) neighbor;
				double distance = clusteringObject.getDistance(temporalNeighbor);
				neighbor.setHelpEpsilonDistance(distance);
				if (distance <= neighborhoodRadius) {
					clusteringObject.addNeighbor(neighbor);
				} else {
					distancedNeighbors.add(neighbor);
				}
			}
		}
		
		while (clusteringObject.getNeighbors().size() < minNeighbors) {
			clusteringObject.addNeighbor(distancedNeighbors.poll());
		}
		logger.debug("--> Successfully calculated neighborhood for clustering object with metadata index '"
				+ clusteringObject.getMetadataIndex() + "'");
			
	}

}