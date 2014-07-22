package de.fuberlin.panda.metadata.result.fusion.areal;

import java.util.TreeSet;

import de.fuberlin.panda.metadata.result.fusion.clustering.ClusteringObject;
import de.fuberlin.panda.metadata.result.fusion.helper.MetadataTreeSet;

/**
 * This class is a subclass of {@link ClusteringObject}. It overrides the 
 * methods to calculate a distance to another {@code ClusteringObject} and the 
 * core distance.
 * 
 * @see #ArealClusteringObject(int, long)
 * @see #setCoreDistance(int)
 * @see #getDistance(ClusteringObject)
 * 
 * @author Sebastian Schulz
 * @since 24.02.2014
 */
public class ArealClusteringObject extends ClusteringObject {
	private Double latitude = null;
	private Double longitude = null;
	
	public ArealClusteringObject(int metadataIndex, Double longitude, Double latitude) {
		super(metadataIndex);
		this.longitude = longitude;
		this.latitude = latitude;
	}
	
	public Double getLatitude() {
		return latitude;
	}
	
	public Double getLongitude() {
		return longitude;
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
	 * the euclidean distance between the geographic coordinates of both objects.
	 */
	@Override
	public double getDistance(ClusteringObject destinationObject) {
		ArealClusteringObject arealDestination = (ArealClusteringObject) destinationObject;
		double euclideanDistance = Math.sqrt(
				Math.pow(latitude - arealDestination.getLatitude(),2) +
				Math.pow(longitude - arealDestination.getLongitude(), 2));
		return euclideanDistance;
	}

}
