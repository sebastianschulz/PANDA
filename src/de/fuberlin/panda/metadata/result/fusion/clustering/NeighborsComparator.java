package de.fuberlin.panda.metadata.result.fusion.clustering;

import java.util.Comparator;

/**
 * This comparator implementation is used to sort the neighbors list by it's distance 
 * to the center object.
 * 
 * @author Sebastian Schulz
 * @since 28.01.2014
 */
public class NeighborsComparator implements Comparator<ClusteringObject> {

	@Override
	public int compare(ClusteringObject x, ClusteringObject y) {
		if (x.getHelpEpsilonDistance() < y.getHelpEpsilonDistance()) {
			return -1;
		} else if(x.getHelpEpsilonDistance() > y.getHelpEpsilonDistance()) {
			return 1;
		} else {
			if (x.getMetadataIndex() < y.getMetadataIndex()) {
				return -1;
			} else if (x.getMetadataIndex() > y.getMetadataIndex()) {
				return 1;
			} else {
				return 0;
			}
		}
	}

}
