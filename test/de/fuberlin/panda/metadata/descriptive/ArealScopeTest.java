package de.fuberlin.panda.metadata.descriptive;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.Vector;

import org.junit.Test;

public class ArealScopeTest {

	@Test
	public void testCreateArealScope() {
		ArealScope as = new ArealScope();
		assertNotNull(as);
	}
	
	@Test
	public void testSetGetLatitude() {
		ArealScope as = new ArealScope();
		double testLatitude = 52.5243700;
		as.setLatitude("52.5243700");
		assertTrue("Latitude has been set the wrong way.", testLatitude == as.getLatitude());
	}
	
	@Test
	public void testSetGetLongitude() {
		ArealScope as = new ArealScope();
		double testLongitude = 13.4105300;
		as.setLongitude("13.4105300");
		assertTrue("Longitude has been set the wrong way.", testLongitude == as.getLongitude());
	}
	
	@Test
	public void testSetGetLocation() {
		ArealScope as = new ArealScope();
		String testLocation = "TestTown";
		as.setLocation(testLocation);
		assertTrue("Location has been set the wrong way.", testLocation.equals(as.getLocation()));
	}

	@Test
	public void testHasLocation() {
		ArealScope as = new ArealScope();
		assertFalse("hasLocation should be false since no location was set.", as.hasLocation());
		String testLocation = "TestTown";
		as.setLocation(testLocation);
		assertTrue("hasLocation should be true.", as.hasLocation());
	}
	
	@Test
	public void testHasGeoCoords() {
		ArealScope as = new ArealScope();
		assertFalse("hasGeoCoords should be false since no location was set.", as.hasGeoCoords());
		as.setLatitude("52.5243700");
		assertFalse("hasGeoCoords should be false since no location was set.", as.hasGeoCoords());
		as.setLongitude("13.4105300");
		assertTrue("hasGeoCoords should be true.", as.hasGeoCoords());
	}
	
	@Test
	public void testGetArealScopeWith3Entries() {
		ArealScope as = new ArealScope();
		String testLocation = "TestTown";
		String testLatitude = "52.52437";
		String testLongitude = "13.41053";
		
		as.setLocation(testLocation);
		as.setLatitude(testLatitude);
		as.setLongitude(testLongitude);
		
		Vector<String[]> asV = as.getAttributes();
		assertTrue("Areal scope vector has wrong size", asV.size() == 3);
		
		assertTrue("Location array has wrong length.", asV.get(0).length == 2);
		assertTrue("Location array label was incorrect.", asV.get(0)[0].equals("Location"));
		assertTrue("Location array URI was incorrect.", asV.get(0)[1].equals(testLocation));
		
		assertTrue("Latitude array has wrong length.", asV.get(1).length == 2);
		assertTrue("Latitude array label was incorrect.", asV.get(1)[0].equals("Latitude"));
		assertTrue("Latitude array URI was incorrect.", asV.get(1)[1].equals(testLatitude));
		
		assertTrue("Longitude array has wrong length.", asV.get(2).length == 2);
		assertTrue("Longitude array label was incorrect.", asV.get(2)[0].equals("Longitude"));
		assertTrue("Longitude array URI was incorrect.", asV.get(2)[1].equals(testLongitude));
	}
	
	@Test
	public void testGetArealScopeWith2Entries() {
		ArealScope as = new ArealScope();
		String testLatitude = "52.52437";
		String testLongitude = "13.41053";
		
		as.setLatitude(testLatitude);
		as.setLongitude(testLongitude);
		
		Vector<String[]> asV = as.getAttributes();
		assertTrue("Areal scope vector has wrong size", asV.size() == 2);
		
		assertTrue("Latitude array has wrong length.", asV.get(0).length == 2);
		assertTrue("Latitude array label was incorrect.", asV.get(0)[0].equals("Latitude"));
		assertTrue("Latitude array URI was incorrect.", asV.get(0)[1].equals(testLatitude));
		
		assertTrue("Longitude array has wrong length.", asV.get(1).length == 2);
		assertTrue("Longitude array label was incorrect.", asV.get(1)[0].equals("Longitude"));
		assertTrue("Longitude array URI was incorrect.", asV.get(1)[1].equals(testLongitude));
	}
	
	@Test
	public void testGetArealScopeWith0Entries() {
		ArealScope as = new ArealScope();
		Vector<String[]> asV = as.getAttributes();
		assertTrue("Areal scope vector has wrong size", asV.size() == 0);
	}
	
}