package de.fuberlin.panda.metadata.result.fusion.temporal;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TemporalClusteringObjectTest {

	@Test
	public void testConstructor() {
		TemporalClusteringObject testObject = new TemporalClusteringObject(0, 123);
		assertNotNull("TemporalClusteringObject was null.", testObject);
		long timestamp = testObject.getTimestamp();
		assertTrue("Timestamp was not 123 as expected but: " + timestamp + ".", timestamp == 123);
	}
	
	@Test
	public void testDistance() {
		TemporalClusteringObject testObject = new TemporalClusteringObject(0, 123);
		TemporalClusteringObject distanceObject = new TemporalClusteringObject(1, 1230);
		double testDistance = testObject.getDistance(distanceObject);		
		assertTrue("Distance was not 1107 as expected but: " + testDistance + ".", testDistance == 1107);
	}
	
	@Test
	public void testCalculateCoreDistance() {
		int minNeighbors = 2;
		TemporalClusteringObject testObject = new TemporalClusteringObject(0, 123);
		TemporalClusteringObject testNeighbor1 = new TemporalClusteringObject(1, 1230);
		TemporalClusteringObject testNeighbor2 = new TemporalClusteringObject(2, 1111);
		
		double distance = testObject.getDistance(testNeighbor1);
		testNeighbor1.setHelpEpsilonDistance(distance);
		testObject.addNeighbor(testNeighbor1);
		
		distance = testObject.getDistance(testNeighbor2);
		testNeighbor2.setHelpEpsilonDistance(distance);
		testObject.addNeighbor(testNeighbor2);
		
		assertTrue("Core distance isn't undefined initially.", testObject.getCoreDistance() == -1d);
		testObject.setCoreDistance(minNeighbors);
		double testCoreDistance = testObject.getCoreDistance();
		assertTrue("Core distance was not 1107 as expected but: " + testCoreDistance + ".", 
				testCoreDistance == 1107);
	}
}
