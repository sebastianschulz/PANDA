package de.fuberlin.panda.metadata.result.fusion.helper;

import java.util.TreeSet;

/**
 * This class extends a {@link TreeSet} and provides an additional
 * {@link #get(int)} method to get a specific entry of this set.
 * 
 * @author Sebastian Schulz
 * @since 15.01.2014
 *
 * @param <E> - a {@code Map.Entry}
 */
public class MetadataTreeSet<E> extends TreeSet<E> {
	static final long serialVersionUID = -6659989255231145618L;
	
	public MetadataTreeSet(TreeSet<E> sortedMetadata) {
		super(sortedMetadata);
	}
	
	@SuppressWarnings("unchecked")
	public E get(int index){
        return (E) (toArray())[index];
    }

}
