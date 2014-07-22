package de.fuberlin.panda.metadata.config.predicates;

/**
 * Enum for sorts of Areal Scope Predicates. 
 * 
 * @since 28.11.2013
 * @author Sebastian Schulz
 */
public enum ArealPredicates {
	LATITUDE("Latitude"),
	LONGITUDE("Longitude"),
	LOCATION("Location");
	
	private final String text;
	
	private ArealPredicates(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
    	 return text;
    }

}
