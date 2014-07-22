package de.fuberlin.panda.metadata.config.predicates;

/**
 * Enum for sorts of Administrative Scope Predicates. 
 * 
 * @since 28.11.2013
 * @author Sebastian Schulz
 */
public enum AdministrativePredicates {
	LICENSE("License"),
	DUTIES("Duties"),
	RIGHTS("Rights");
	
	private final String text;
	
	private AdministrativePredicates(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
    	 return text;
    }

}
