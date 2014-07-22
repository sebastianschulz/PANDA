package de.fuberlin.panda.metadata.descriptive;

import java.util.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.management.modelmbean.XMLParseException;

/**
 * Simple data class for the temporal scope.
 * 
 * @since 16.09.2013
 * @author Sebastian Schulz
 */
public class TemporalScope {
	private Timestamp expirationDate;
	
	public void setExpirationDate(String timestamp) throws XMLParseException {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
			Date parsedDate = sdf.parse(timestamp);
			expirationDate = new Timestamp(parsedDate.getTime());
		} catch (ParseException e) {
			throw new XMLParseException("Couldn't parse the expiration date: " + e.getMessage());
		}
	}
	
	public Timestamp getExpirationDate() {
		return expirationDate;
	}
	
	public String getExpirationDateString() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
		try {
			return sdf.format(expirationDate);
		} catch (Exception e) {
			return null;
		}
		
	}
}
