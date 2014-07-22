package de.fuberlin.panda.metadata.result.fusion.clustering;

import java.util.Comparator;

/**
 * This simple {@code Comparator} is used to sort objects by their reachability
 * value. If this value is equal the metadata index is used to sort them.
 * 
 * @author Sebastian Schulz
 * @since 27.01.2014
 */
public class ReachabilityComparator implements Comparator<ClusteringObject> {

	@Override
	public int compare(ClusteringObject x, ClusteringObject y) {
		if (x.getReachabilityDistance() < y.getReachabilityDistance()) {
			return -1;
		} else if(x.getReachabilityDistance() > y.getReachabilityDistance()) {
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
