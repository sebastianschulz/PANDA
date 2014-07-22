package de.fuberlin.panda.metadata.exceptions;

/**
 * Temporaly execption for reading in polygons.
 * 
 * @author Sebastian Schulz
 * @since 24.02.2014
 */
public class PolygonReadingException extends Exception {
	private static final long serialVersionUID = 1L; 
	
	public PolygonReadingException(String message) {
		super(message);
	}

}
