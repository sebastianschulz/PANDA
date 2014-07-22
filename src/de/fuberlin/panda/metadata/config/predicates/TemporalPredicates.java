package de.fuberlin.panda.metadata.config.predicates;

/**
 * Enum for sorts of Temporal Scope Predicates. 
 * 
 * @since 28.11.2013
 * @author Sebastian Schulz
 */
public enum TemporalPredicates {
	EXPIRATION_DATE("ExpirationDate");
	
	private final String text;
	
	private TemporalPredicates(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
    	 return text;
    }

}
