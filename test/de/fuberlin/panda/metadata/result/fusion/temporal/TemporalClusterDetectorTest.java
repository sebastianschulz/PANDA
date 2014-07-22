package de.fuberlin.panda.metadata.result.fusion.temporal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.TreeSet;

import org.junit.Test;

public class TemporalClusterDetectorTest {

	@Test
	public void testClusterDetectorConstructor() {
		TemporalClusterDetector testDetector = new TemporalClusterDetector(1, 1);
		assertNotNull("Object is null.", testDetector);
	}
	
	@Test
	public void testEliminateOutliersWithoutTimestamp() {
		TemporalClusterDetector testDetector = new TemporalClusterDetector(1, 1);
		
		TreeSet<TemporalClusteringObject> testSet = new TreeSet<>(new TimestampComparator());
		TemporalClusteringObject testObject1 = new TemporalClusteringObject(0, -1);
		TemporalClusteringObject testObject2 = new TemporalClusteringObject(1, 1);
		TemporalClusteringObject testObject3 = new TemporalClusteringObject(2, 0);
		TemporalClusteringObject testObject4 = new TemporalClusteringObject(3, -1);
		testSet.add(testObject1);
		testSet.add(testObject2);
		testSet.add(testObject3);
		testSet.add(testObject4);
		
		assertTrue("UNDEFINED timestamp wasn't set initially for object 1.", testObject1.hasMetadata());
		assertTrue("UNDEFINED timestamp wasn't set initially for object 4.", testObject4.hasMetadata());
		
		testDetector.eliminateOutliersWithoutTimestamp(testSet);
		
		assertTrue("Test set has wrong size.", testSet.size() == 2);
		assertFalse("NO_TIMESTAMP wasn't set for object 1.", testObject1.hasMetadata());
		assertFalse("NO_TIMESTAMP wasn't set for object 4.", testObject4.hasMetadata());
		assertTrue("Wrong object on first position of testSet.", testSet.first() == testObject3);
		assertTrue("Wrong object on last position of testSet.", testSet.last() == testObject2);
	}
	
	@Test
	public void testCalculateNeighborhoodWithMinNeighbors() {
		TemporalClusterDetector testDetector = new TemporalClusterDetector(10, 2);
		
		TreeSet<TemporalClusteringObject> testSet = new TreeSet<>(new TimestampComparator());
		TemporalClusteringObject testObject = new TemporalClusteringObject(0, 0);
		TemporalClusteringObject testNeighbor1 = new TemporalClusteringObject(1, 30);
		TemporalClusteringObject testNeighbor2 = new TemporalClusteringObject(2, 5);
		testSet.add(testObject);
		testSet.add(testNeighbor1);
		testSet.add(testNeighbor2);
		
		testDetector.calculateNeighborhood(testObject, testSet);
		assertTrue("Neighbors Set of test object has wrong size.", testObject.getNeighbors().size() == 2);
		assertTrue("Wrong object on first position of neighbors set.", 
				testObject.getNeighbors().first() == testNeighbor2);
		assertTrue("Wrong object on last position of neighbors set.", 
				testObject.getNeighbors().last() == testNeighbor1);
	}
	
	@Test
	public void testCalculateNeighborhoodWithMoreThanMinNeighbors() {
		TemporalClusterDetector testDetector = new TemporalClusterDetector(10, 2);
		
		TreeSet<TemporalClusteringObject> testSet = new TreeSet<>(new TimestampComparator());
		TemporalClusteringObject testObject = new TemporalClusteringObject(0, 0);
		TemporalClusteringObject testNeighbor1 = new TemporalClusteringObject(1, 4);
		TemporalClusteringObject testNeighbor2 = new TemporalClusteringObject(2, 1);
		TemporalClusteringObject testNeighbor3 = new TemporalClusteringObject(3, 3);
		TemporalClusteringObject testNeighbor4 = new TemporalClusteringObject(4, 2);
		testSet.add(testNeighbor1);
		testSet.add(testNeighbor2);
		testSet.add(testNeighbor3);
		testSet.add(testNeighbor4);
		
		testDetector.calculateNeighborhood(testObject, testSet);
		assertTrue("Neighbors Set of test object has wrong size.", testObject.getNeighbors().size() == 4);
		assertTrue("Wrong object on first position of neighbors set.", 
				testObject.getNeighbors().first() == testNeighbor2);
		assertTrue("Wrong object on last position of neighbors set.", 
				testObject.getNeighbors().last() == testNeighbor1);
	}
}