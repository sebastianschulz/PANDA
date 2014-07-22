package de.fuberlin.panda.metadata.descriptive;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Vector;

import javax.management.modelmbean.XMLParseException;

import org.junit.Test;

public class AdministrativeScopeTest {

	@Test
	public void testCreateAdministrativeScope() {
		AdministrativeScope as = new AdministrativeScope();
		assertNotNull(as);
	}
	
	@Test
	public void testSetAndGetCorrectLicenseUri() {
		AdministrativeScope as = new AdministrativeScope();
		
		try {
			String testUri = "/my/test/uri";
			as.setLicenseUri(testUri);
			String asUri = as.getLicenceUri().toString();
			assertTrue("Couldn't retrieve correct license URI.", testUri.equals(asUri));
		} catch(XMLParseException e) {
			fail("Couldn't set license URI correctly.");
		}
	}
	
	@Test
	public void testSetIncorrectLicenseUri() {
		AdministrativeScope as = new AdministrativeScope();
		
		try {
			String testUri = "/my/test^/uri";
			as.setLicenseUri(testUri);
			fail("License URI has been set incorrectly.");
		} catch(XMLParseException e) {
			assertTrue("License URI is not null.", as.getLicenceUri() == null);
		}
	}
	
	@Test
	public void testSetAndGetCorrectRightsUri() {
		AdministrativeScope as = new AdministrativeScope();
		
		try {
			String testUri = "/my/test/uri";
			as.setRightsUri(testUri);
			String asUri = as.getRightsUri().toString();
			assertTrue("Couldn't retrieve correct license URI.", testUri.equals(asUri));
		} catch(XMLParseException e) {
			fail("Couldn't set license URI correctly.");
		}
	}
	
	@Test
	public void testSetIncorrectRightsUri() {
		AdministrativeScope as = new AdministrativeScope();
		
		try {
			String testUri = "/my/test^/uri";
			as.setRightsUri(testUri);
			fail("License URI has been set incorrectly.");
		} catch(XMLParseException e) {
			assertTrue("License URI is not null.", as.getRightsUri() == null);
		}
	}
	
	@Test
	public void testSetAndGetCorrectDutiesUri() {
		AdministrativeScope as = new AdministrativeScope();
		
		try {
			String testUri = "/my/test/uri";
			as.setDutiesUri(testUri);
			String asUri = as.getDutiesUri().toString();
			assertTrue("Couldn't retrieve correct license URI.", testUri.equals(asUri));
		} catch(XMLParseException e) {
			fail("Couldn't set license URI correctly.");
		}
	}
	
	@Test
	public void testSetIncorrectDutiesUri() {
		AdministrativeScope as = new AdministrativeScope();
		
		try {
			String testUri = "/my/test^/uri";
			as.setDutiesUri(testUri);
			fail("License URI has been set incorrectly.");
		} catch(XMLParseException e) {
			assertTrue("License URI is not null.", as.getDutiesUri() == null);
		}
	}

	@Test
	public void testGetAdministrativeScopeWith3Entries() {
		try {
			AdministrativeScope as = new AdministrativeScope();
			String testUri = "/my/test/uri";
			as.setLicenseUri(testUri);
			as.setRightsUri(testUri);
			as.setDutiesUri(testUri);
			
			Vector<String[]> asV = as.getAttributes();
			assertTrue("Administrative scope vector has wrong size", asV.size() == 3);
			
			assertTrue("License array has wrong length.", asV.get(0).length == 2);
			assertTrue("License array label was incorrect.", asV.get(0)[0].equals("License"));
			assertTrue("License array URI was incorrect.", asV.get(0)[1].equals(testUri));
			
			assertTrue("Rights array has wrong length.", asV.get(1).length == 2);
			assertTrue("Rights array label was incorrect.", asV.get(1)[0].equals("Rights"));
			assertTrue("Rights array URI was incorrect.", asV.get(1)[1].equals(testUri));
			
			assertTrue("Duties array has wrong length.", asV.get(2).length == 2);
			assertTrue("Duties array label was incorrect.", asV.get(2)[0].equals("Duties"));
			assertTrue("Duties array URI was incorrect.", asV.get(2)[1].equals(testUri));
		} catch(XMLParseException e) {
			fail("Couldn't set URIs correctly.");
		}
	}
	
	@Test
	public void testGetAdministrativeScopeWith1Entry() {
		try {
			AdministrativeScope as = new AdministrativeScope();
			String testUri = "/my/test/uri";
			as.setLicenseUri(testUri);
			
			Vector<String[]> asV = as.getAttributes();
			assertTrue("Administrative scope vector has wrong size", asV.size() == 1);
			
			assertTrue("License array has wrong length.", asV.get(0).length == 2);
			assertTrue("License array label was incorrect.", asV.get(0)[0].equals("License"));
			assertTrue("License array URI was incorrect.", asV.get(0)[1].equals(testUri));
		} catch(XMLParseException e) {
			fail("Couldn't set URIs correctly.");
		}
	}
	
	@Test
	public void testGetAdministrativeScopeWith0Entries() {
		AdministrativeScope as = new AdministrativeScope();
		Vector<String[]> asV = as.getAttributes();
		assertTrue("Administrative scope vector has wrong size", asV.size() == 0);
	}
}
