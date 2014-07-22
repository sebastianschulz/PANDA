package de.fuberlin.panda.metadata.result.fusion.areal;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ArealClusteringObjectTest {

	public void testConstructor() {
		ArealClusteringObject testObject = new ArealClusteringObject(0, 1d, 2d);
		assertNotNull("ArealClusteringObject was null.", testObject);
		double latitude = testObject.getLatitude();
		double longitude = testObject.getLongitude();
		assertTrue("Latitude was not 1 as expected but: " + latitude + ".", latitude == 1);
		assertTrue("Longitude was not 2 as expected but: " + longitude + ".", longitude == 2);
	}
	
	@Test
	public void testDistance() {
		ArealClusteringObject testObject = new ArealClusteringObject(0, 10d, -2d);
		ArealClusteringObject distanceObject = new ArealClusteringObject(1, 5d, -5d);
		double testDistance = testObject.getDistance(distanceObject);		
		assertTrue("Distance was not 5,830951894845301 as expected but: " + testDistance + ".", testDistance == 5.830951894845301);
	}
	
	@Test
	public void testCalculateCoreDistance() {
		int minNeighbors = 2;
		ArealClusteringObject testObject = new ArealClusteringObject(0, 10d, -2d);
		ArealClusteringObject testNeighbor1 = new ArealClusteringObject(1, 5d, -5d);
		ArealClusteringObject testNeighbor2 = new ArealClusteringObject(2, 10d, 1d);
		
		double distance = testObject.getDistance(testNeighbor1);
		testNeighbor1.setHelpEpsilonDistance(distance);
		testObject.addNeighbor(testNeighbor1);
		
		distance = testObject.getDistance(testNeighbor2);
		testNeighbor2.setHelpEpsilonDistance(distance);
		testObject.addNeighbor(testNeighbor2);
		
		assertTrue("Core distance isn't undefined initially.", testObject.getCoreDistance() == -1d);
		testObject.setCoreDistance(minNeighbors);
		double testCoreDistance = testObject.getCoreDistance();
		assertTrue("Core distance was not 5,830951894845301 as expected but: " + testCoreDistance + ".", 
				testCoreDistance == 5.830951894845301);
	}
	
}