package de.fuberlin.panda.metadata.operational;

import java.util.Vector;

import javax.management.modelmbean.XMLParseException;

import de.fuberlin.panda.metadata.config.predicates.OperationalPredicates;

/**
 * This class is responsible to hold all operational information of a single data set
 * in a Vector.
 * 
 *  @see #addOperation(OperationalPredicates, String)
 * 
 * @since 15.09.2013
 * @author Sebastian Schulz
 */
public class Operations {
	private Vector<Operation> operations;
	
	public Operations() {
		operations = new Vector<Operation>();
	}
	
	/**
	 * This methods tries to create a new {@link Operation} object and adds it to the
	 * operations vector
	 * 
	 * @param type - the operations type
	 * @param timestamp - the time the operation took place 
	 * @throws XMLParseException in case the operation couldn't parse the timestamp 
	 */
	public void addOperation(OperationalPredicates type, String timestamp) throws XMLParseException {
		Operation operation = new Operation(type, timestamp);
		operations.add(operation);
	}
	
	@SuppressWarnings("unchecked")
	public Vector<Operation> getAttributes() {
		return (Vector<Operation>) operations.clone();
	}
}
