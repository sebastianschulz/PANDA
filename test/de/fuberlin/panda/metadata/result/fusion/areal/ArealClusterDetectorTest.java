package de.fuberlin.panda.metadata.result.fusion.areal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.TreeSet;

import org.junit.Test;

public class ArealClusterDetectorTest {
	
	@Test
	public void testEliminateOutliersWithoutGeoCoords() {
		ArealClusterDetector testDetector = new ArealClusterDetector(1, 1);
		
		TreeSet<ArealClusteringObject> testSet = new TreeSet<>(new GeoCoordinatesComparator());
		ArealClusteringObject testObject1 = new ArealClusteringObject(0, null, null);
		ArealClusteringObject testObject2 = new ArealClusteringObject(1, 1d, 1d);
		ArealClusteringObject testObject3 = new ArealClusteringObject(2, 0d, 0d);
		ArealClusteringObject testObject4 = new ArealClusteringObject(3, null, null);
		testSet.add(testObject1);
		testSet.add(testObject2);
		testSet.add(testObject3);
		testSet.add(testObject4);
		
		assertTrue("UNDEFINED geoCoords weren't set initially for object 1.", testObject1.hasMetadata());
		assertTrue("UNDEFINED geoCoords weren't set initially for object 4.", testObject4.hasMetadata());
		
		testDetector.eliminateOutliersWithoutGeocoords(testSet);
		
		assertTrue("Test set has wrong size.", testSet.size() == 2);
		assertFalse("NO_METADATA wasn't set for object 1.", testObject1.hasMetadata());
		assertFalse("NO_METADATA wasn't set for object 4.", testObject4.hasMetadata());
		assertTrue("Wrong object on first position of testSet.", testSet.first() == testObject3);
		assertTrue("Wrong object on last position of testSet.", testSet.last() == testObject2);
	}
	
	@Test
	public void testCalculateNeighborhoodWithMinNeighbors() {
		ArealClusterDetector testDetector = new ArealClusterDetector(10, 2);
		
		TreeSet<ArealClusteringObject> testSet = new TreeSet<>(new GeoCoordinatesComparator());
		ArealClusteringObject testObject = new ArealClusteringObject(0, 0d, 0d);
		ArealClusteringObject testNeighbor1 = new ArealClusteringObject(1, 30d, 5d);
		ArealClusteringObject testNeighbor2 = new ArealClusteringObject(2, 5d, 10d);
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
		ArealClusterDetector testDetector = new ArealClusterDetector(100, 2);
		
		TreeSet<ArealClusteringObject> testSet = new TreeSet<>(new GeoCoordinatesComparator());
		ArealClusteringObject testObject = new ArealClusteringObject(0, 0d, 0d);
		ArealClusteringObject testNeighbor1 = new ArealClusteringObject(1, 4d, 4d);
		ArealClusteringObject testNeighbor2 = new ArealClusteringObject(2, 1d, 1d);
		ArealClusteringObject testNeighbor3 = new ArealClusteringObject(3, 3d, 3d);
		ArealClusteringObject testNeighbor4 = new ArealClusteringObject(4, 2d, 2d);
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