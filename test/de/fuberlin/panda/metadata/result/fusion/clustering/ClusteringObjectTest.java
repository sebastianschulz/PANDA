package de.fuberlin.panda.metadata.result.fusion.clustering;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.TreeSet;

import org.junit.Test;

import de.fuberlin.panda.metadata.result.fusion.temporal.TemporalClusteringObject;

public class ClusteringObjectTest {

	@Test
	public void testClusteringObjectConstructor() {
		//since the ClusteringObject is abstract a sublcass has to be instantiated
		TemporalClusteringObject testObject = new TemporalClusteringObject(0, 0);
		assertNotNull("ClusertingObject was null.", testObject);
		assertTrue("Wrong metadata index was set." , testObject.getMetadataIndex() == 0);
	}

	@Test
	public void testGetCoreDistance() {
		TemporalClusteringObject testObject = new TemporalClusteringObject(0, 0);
		assertTrue("Initial core distance wasn't undefined.", 
				testObject.getCoreDistance() == ClusteringObject.UNDEFINED);
	}
	
	@Test
	public void testReachabilityDistance() {
		TemporalClusteringObject testObject = new TemporalClusteringObject(0, 0);
		
		//test hasreachabilityDistance()
		assertFalse("Object has unexpectedly a reachability distance.", testObject.hasReachabilityDistance());
		
		testObject.setReachabilityDistance(12d);
		double testReachabilityResult = testObject.getReachabilityDistance();
		assertTrue("Reachability distance was wrong. Should be 12 but was ",
				testReachabilityResult == 12d);
		
		assertTrue("Object has unexpectedly no reachability distance.", testObject.hasReachabilityDistance());
	}
	
	@Test
	public void testHelpEpsilonDistance() {
		TemporalClusteringObject testObject = new TemporalClusteringObject(0, 0);
		testObject.setHelpEpsilonDistance(12d);
		double testReachabilityResult = testObject.getHelpEpsilonDistance();
		assertTrue("Reachability distance was wrong. Should be 12 but was ",
				testReachabilityResult == 12d);
	}
	
	@Test
	public void testProcessedFlag() {
		TemporalClusteringObject testObject = new TemporalClusteringObject(0, 0);
		assertFalse("Processed flag was true but should be false.", testObject.isProcessed());
		testObject.setProcessed();
		assertTrue("Processed flag was false but should be true.", testObject.isProcessed());
	}
	
	@Test
	public void testHasMetadata() {
		TemporalClusteringObject testObject = new TemporalClusteringObject(0, 0);
		assertTrue("TestObject incorrectly has no metadata.", testObject.hasMetadata());
		testObject.setNoMetadata();
		assertFalse("TestObject incorrectly has metadata.", testObject.hasMetadata());
	}
	
	@Test
	public void testLocalReachabilityDensity() {
		TemporalClusteringObject testObject = new TemporalClusteringObject(0, 0);
		double neighborsReachabilitySum = 12d;
		int minNeighbors = 3;
		
		testObject.setLocalReachabilityDensity(neighborsReachabilitySum, minNeighbors);
		double result = testObject.getLocalReachabilityDensity();
		assertTrue("Local reachability density was caluculated wrong: should be 0.25 but is: "
				+ result + ".", result == 0.25d);
	}
	
	@Test
	public void testNeighbors() {
		TemporalClusteringObject testObject = new TemporalClusteringObject(0, 0);
		TemporalClusteringObject testNeighbor = new TemporalClusteringObject(1, 0);
		assertTrue("Initially more than 0 neighbors existend.", testObject.getNeighbors().size() == 0);
		
		testObject.addNeighbor(testNeighbor);
		TreeSet<ClusteringObject> neighbors = testObject.getNeighbors();
		assertTrue("No exactly 1 neighbor existend.", neighbors.size() == 1);
		assertTrue("The neighbors entry was not exactly the same object than the testNeighbor.", 
				neighbors.first().equals(testNeighbor));
	}
	
	@Test
	public void testOutlierFactorWithSameLrd() {
		double neighborsReachabilitySum = 12d;
		int minNeighbors = 3;
		
		TemporalClusteringObject testObject = new TemporalClusteringObject(0, 0);
		TemporalClusteringObject testNeighbor1 = new TemporalClusteringObject(1, 0);
		TemporalClusteringObject testNeighbor2 = new TemporalClusteringObject(2, 0);
		TemporalClusteringObject testNeighbor3 = new TemporalClusteringObject(3, 0);
		
		testObject.setLocalReachabilityDensity(neighborsReachabilitySum, minNeighbors);
		testNeighbor1.setLocalReachabilityDensity(neighborsReachabilitySum, minNeighbors);
		testNeighbor2.setLocalReachabilityDensity(neighborsReachabilitySum, minNeighbors);
		testNeighbor3.setLocalReachabilityDensity(neighborsReachabilitySum, minNeighbors);
		
		testObject.addNeighbor(testNeighbor1);
		testObject.addNeighbor(testNeighbor2);
		testObject.addNeighbor(testNeighbor3);
		
		testObject.setOutlierFactor(minNeighbors);
		double outlierFactor = testObject.getOutlierFactor();
		assertTrue("Outlier factor should be 1 but was: " + outlierFactor + ".", outlierFactor == 1d);
	}
	
	@Test
	public void testOutlierFactorWithDifferentLrds() {
		int minNeighbors = 2;
		
		TemporalClusteringObject testObject = new TemporalClusteringObject(0, 0);
		TemporalClusteringObject testNeighbor1 = new TemporalClusteringObject(1, 0);
		TemporalClusteringObject testNeighbor2 = new TemporalClusteringObject(2, 0);
		
		testObject.setLocalReachabilityDensity(4, minNeighbors);
		testNeighbor1.setLocalReachabilityDensity(4, minNeighbors);
		testNeighbor2.setLocalReachabilityDensity(2, minNeighbors);
		
		testObject.addNeighbor(testNeighbor1);
		testObject.addNeighbor(testNeighbor2);
		
		testObject.setOutlierFactor(minNeighbors);
		double outlierFactor = testObject.getOutlierFactor();
		assertTrue("Outlier factor should be 1.5 but was: " + outlierFactor + ".", outlierFactor == 1.5d);
	}
	
}
