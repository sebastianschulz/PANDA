package de.fuberlin.panda.metadata.result.fusion.areal;

import java.util.Comparator;

/**
 * This comparator implementation is used to sort the temporal clustering objects list 
 * by it's euclidian distance to the origin.
 * 
 * @author Sebastian Schulz
 * @since 24.02.2014
 */
public class GeoCoordinatesComparator implements Comparator<ArealClusteringObject> {

	@Override
	public int compare(ArealClusteringObject x, ArealClusteringObject y) {
		double xDistance = 0;
		double yDistance = 0;
		
		try {
			//calculate distance from [0.0]
			xDistance = Math.sqrt(Math.pow(x.getLatitude(),2) + Math.pow(x.getLongitude(), 2)); 
			yDistance = Math.sqrt(Math.pow(y.getLatitude(),2) + Math.pow(y.getLongitude(), 2));
		} catch (NullPointerException e) {
			//nothing to do because distances where already set
		}
		
		if (xDistance < yDistance) {
			return -1;
		} else if(xDistance > yDistance) {
			return 1;
		} else {
			if(x.getMetadataIndex() < y.getMetadataIndex()) {
				return -1;
			} else if(x.getMetadataIndex() > y.getMetadataIndex()) {
				return 1;
			} else {
				return 0;
			}
		}
	}

}
