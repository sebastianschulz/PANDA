package de.fuberlin.panda.metadata.config;


/**
 * Enum for sources for metadata queries. 
 * 
 * @since 05.09.2013
 * @author Sebastian Schulz
 */
public enum MetadataSourceType {
	XML_TEST("XMLTest"),
	XML("XML"),
	VIRTUOSO("Virtuoso");
	
	private final String text;
	
	private MetadataSourceType(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
	
}
