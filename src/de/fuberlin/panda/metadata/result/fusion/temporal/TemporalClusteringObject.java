package de.fuberlin.panda.metadata.result.fusion.temporal;


import java.util.TreeSet;

import de.fuberlin.panda.metadata.result.fusion.clustering.ClusteringObject;
import de.fuberlin.panda.metadata.result.fusion.helper.MetadataTreeSet;

/**
 * This class is a subclass of {@link ClusteringObject}. It overrides the 
 * methods to calculate a distance to another {@code ClusteringObject} and the 
 * core distance.
 * 
 * @see #TemporalClusteringObject(int, long)
 * @see #setCoreDistance(int)
 * @see #getDistance(ClusteringObject)
 * 
 * @author Sebastian Schulz
 * @since 28.01.2014
 */
public class TemporalClusteringObject extends ClusteringObject {
	private long timestamp;
	
	/**
	 * Constructor which adds the additional information of {@code metadataIndex} and
	 * {@code timestamp} to a {@code TemporalClusteringObject}. Both values are only set once
	 * on creating an {@code TemporalClusteringObject}.
	 * 
	 * @param metadataIndex - an {@code int} value.
	 * @param timestamp - a {@code long} value.
	 */
	public TemporalClusteringObject(int metadataIndex, long timestamp) {
		super(metadataIndex);
		this.timestamp = timestamp;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	/**
	 * This method overrides the {@link ClusteringObject#setCoreDistance(TreeSet, int)} method. It calculates 
	 * the distance of the {@code minNeighbors}'s neighbor and sets this value for {@code coreDistance}.
	 */
	@Override
	public void setCoreDistance(int minNeighbors) {
		if (neighbors.size() >= minNeighbors) {
			MetadataTreeSet<ClusteringObject> tmpNeighbors = new MetadataTreeSet<>(neighbors);
			ClusteringObject minCoreObject = tmpNeighbors.get(minNeighbors-1);
			coreDistance = getDistance(minCoreObject);
		} else {
			coreDistance = null;
		}
	}

	/**
	 * This method overrides the {@link ClusteringObject#getDistance(ClusteringObject)} method. And returns 
	 * the distance between the timestamps of both objects.
	 */
	@Override
	public double getDistance(ClusteringObject destinationObject) {
		return (double) Math.abs(timestamp - ((TemporalClusteringObject) destinationObject).getTimestamp()); 
	}

}