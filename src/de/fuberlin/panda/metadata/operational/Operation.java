package de.fuberlin.panda.metadata.operational;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.management.modelmbean.XMLParseException;

import de.fuberlin.panda.metadata.config.predicates.OperationalPredicates;

/**
 * Simple data class for operations.
 * 
 * @since 16.09.2013
 * @author Sebastian Schulz
 */
public class Operation {
	private OperationalPredicates type;
	private Timestamp timestamp;
	
	public Operation(OperationalPredicates operationType, String timestamp) throws XMLParseException {
		this.type = operationType;
		
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
			Date parsedDate = sdf.parse(timestamp);
			this.timestamp = new Timestamp(parsedDate.getTime());
		} catch (ParseException e) {
			throw new XMLParseException("Couldn't parse the operation (" + type.toString() + "): " + e.getMessage());
		}
	}
	
	public OperationalPredicates getType() {
		return type;
	}
	
	public Timestamp getTimestamp() {
		return timestamp;
	}
	
	public String getTimestampString() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
		return sdf.format(timestamp);
	}
}
