package de.fuberlin.panda.metadata.config.predicates;

/**
 * Enum for sorts of Format predicates. 
 * 
 * @since 28.11.2013
 * @author Sebastian Schulz
 */
public enum TypePredicates {
	FORMAT("Format"),
	LANGUAGE("Language");
	
	private final String text;
	
	private TypePredicates(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
    	 return text;
    }

}
