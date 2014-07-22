package de.fuberlin.panda.metadata.config.predicates;

/**
 * Enum for sorts of operations. 
 * 
 * @since 16.09.2013
 * @author Sebastian Schulz
 */
public enum OperationalPredicates {
	CREATION("Creation"),
	ACCESS("Access"),
	DELETING("Deleting");
	
	private final String text;
	
	private OperationalPredicates(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
    	 return text;
    }

}
