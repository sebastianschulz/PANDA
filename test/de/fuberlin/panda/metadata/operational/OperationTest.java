package de.fuberlin.panda.metadata.operational;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.management.modelmbean.XMLParseException;

import org.junit.Test;

import de.fuberlin.panda.metadata.config.predicates.OperationalPredicates;

public class OperationTest {

	@Test
	public void testCreateOperation() {
		String testTimestamp = "15.09.2013 15:33:02.569";
		String testWrongFormatTimeStamp = "15.09.2013 15:33:02";
		
		Operation operation = null;
		try {
			operation = new Operation(OperationalPredicates.ACCESS, testTimestamp);
			assertNotNull(operation);
		} catch (XMLParseException e) {
			fail("Couldn't create a operation.");
		}
		
		Operation operation2 = null;
		try {
			operation2 = new Operation(OperationalPredicates.ACCESS, testWrongFormatTimeStamp);
			fail("Couldn't throw XMLParseException while trying to create a operation with wrong format timestamp.");
		} catch (XMLParseException e) {
			assertNull(operation2);
		}
	}
	
	@Test
	public void testGetType() {
		String testTimestamp = "15.09.2013 15:33:02.569";
		
		Operation operation = null;
		try {
			operation = new Operation(OperationalPredicates.ACCESS, testTimestamp);
		} catch (XMLParseException e) {
			fail("Couldn't create a operation.");
		}
		
		OperationalPredicates type = operation.getType();
		assertTrue("Wrong operation type was given back by getType() method.", type == OperationalPredicates.ACCESS);
	}
	
	@Test
	public void testGetTimestamp() throws ParseException {
		String testTimestampString = "15.09.2013 15:33:02.569";
		
		Operation operation = null;
		try {
			operation = new Operation(OperationalPredicates.ACCESS, testTimestampString);
		} catch (XMLParseException e) {
			fail("Couldn't create a operation.");
		}
	
		//create timestamp
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss.SSS");
		Date parsedDate = sdf.parse(testTimestampString);
		Timestamp testTimestamp =  new Timestamp(parsedDate.getTime());
		
		Timestamp timestamp = operation.getTimestamp();
		assertTrue("Wrong operation timestamp was given back by getTimestamp() method.", 
				timestamp.equals(testTimestamp));
	}
	
	@Test
	public void testGetTimestampString() throws ParseException {
		String testTimestampString = "15.09.2013 15:33:02.569";
		
		Operation operation = null;
		try {
			operation = new Operation(OperationalPredicates.ACCESS, testTimestampString);
		} catch (XMLParseException e) {
			fail("Couldn't create a operation.");
		}
	
		
		String timestampString = operation.getTimestampString();
		assertTrue("Wrong operation timestamp was given back by getTimestampString() method.", 
				timestampString.equals(testTimestampString));
	}
}
