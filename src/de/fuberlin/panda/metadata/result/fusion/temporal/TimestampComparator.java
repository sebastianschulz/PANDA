package de.fuberlin.panda.metadata.result.fusion.temporal;

import java.util.Comparator;

/**
 * This comparator implementation is used to sort the temporal clustering objects list 
 * by it's timestamp values.
 * 
 * @author Sebastian Schulz
 * @since 10.02.2014
 */
public class TimestampComparator implements Comparator<TemporalClusteringObject> {

	@Override
	public int compare(TemporalClusteringObject x, TemporalClusteringObject y) {
		if (x.getTimestamp() < y.getTimestamp()) {
			return -1;
		} else if(x.getTimestamp() > y.getTimestamp()) {
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
