package de.fuberlin.panda.metadata.descriptive;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.management.modelmbean.XMLParseException;

import org.junit.Test;

public class TemporalScopeTest {

	@Test
	public void testCreateTemporalScope() {
		TemporalScope ts = new TemporalScope();
		assertNotNull(ts);
	}
	
	@Test
	public void testSetAndGetCorrectExpirationDateAsString() {
		TemporalScope ts = new TemporalScope();
		String testTimestamp = "11.11.2013 11:11:11.111";
		try {
			ts.setExpirationDate(testTimestamp);
			assertTrue("ExpirationDate has been set wrong.", testTimestamp.equals(ts.getExpirationDateString()));
		} catch (XMLParseException e) {
			fail("Couldn't set ExpirationDate.");
		}
	}
	
	@Test
	public void testSetIncorrectTimestamp() {
		TemporalScope ts = new TemporalScope();
		String testTimestamp = "some incorrect timestamp";
		try {
			ts.setExpirationDate(testTimestamp);
			fail("ExpirationDate has been set incorrectly.");
		} catch (XMLParseException e) {
			assertTrue("ExpirationDate is not null.", ts.getExpirationDateString() == null);
			assertTrue("ExpirationDate is not null.", ts.getExpirationDate() == null);
		}
	}
	
	@Test
	public void testGetTimestampAsTimestamp() throws ParseException {
		TemporalScope ts = new TemporalScope();
		String testTimestampString = "11.11.2013 11:11:11.111";
		
		//create test timestamp
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss.SSS");
		Date parsedDate = sdf.parse(testTimestampString);
		Timestamp testTimestamp =  new Timestamp(parsedDate.getTime());
		
		try {
			ts.setExpirationDate(testTimestampString);
			assertTrue("ECouldn't retrieve ExpirationDate as timestamp.", testTimestamp.equals(ts.getExpirationDate()));
		} catch (XMLParseException e) {
			fail("Couldn't set ExpirationDate.");
		}
	}
}
