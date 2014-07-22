package de.fuberlin.panda.metadata.config;


/**
 * Enum for sorts of metadata (which represents the relevant comments in the xml files). 
 * 
 * @since 16.09.2013
 * @author Sebastian Schulz
 */
public enum MetadataType {
	OPERATIONAL("Operational Metadata"),
	ADMINISTRATIVE_SCOPE("Administrative Scope"),
	AREAL_SCOPE("Areal Scope"),
	TEMPORAL_SCOPE("Temporal Scope"),
	TYPE("Type"),
	SEMANTIC("Semantic");
	
	private final String text;
	
	private MetadataType(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
