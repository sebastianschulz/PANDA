package de.fuberlin.panda.metadata.result.fusion.clustering;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;

import de.fuberlin.panda.metadata.result.fusion.temporal.TemporalClusterDetector;
import de.fuberlin.panda.metadata.result.fusion.temporal.TemporalClusteringObject;
import de.fuberlin.panda.metadata.result.fusion.temporal.TimestampComparator;

public class ClusterDetectorTest {

	private TreeSet<TemporalClusteringObject> testObjects; 
	private TemporalClusteringObject testObject1;
	private TemporalClusteringObject testObject2;
	private TemporalClusteringObject testObject3;
	private TemporalClusteringObject testObject4;
	private TemporalClusteringObject testObject5;
	private TemporalClusteringObject testObject6;
	private TemporalClusteringObject testObject7;
	private TemporalClusteringObject testObject8;
	private TemporalClusteringObject testObject9;
	private TemporalClusteringObject testObject10;
	private TemporalClusteringObject testObject11;
	
	private final int minNeighbors = 2;
	private final double neighborhoodRadius = 10;
	
	@Before
	public void initialzeTreeSet() {
		testObjects = new TreeSet<>(new TimestampComparator());
		testObject1 = new TemporalClusteringObject(0, 1);
		testObject2 = new TemporalClusteringObject(1, 1000);
		testObject3 = new TemporalClusteringObject(2, 985);
		testObject4 = new TemporalClusteringObject(3, 508);
		testObject5 = new TemporalClusteringObject(4, 7);
		testObject6 = new TemporalClusteringObject(5, 500);
		testObject7 = new TemporalClusteringObject(6, 1112);
		testObject8 = new TemporalClusteringObject(7, 2);
		testObject9 = new TemporalClusteringObject(8, 510);
		testObject10 = new TemporalClusteringObject(9, 505);
		testObject11 = new TemporalClusteringObject(10, 700);
		testObjects.add(testObject1);
		testObjects.add(testObject2);
		testObjects.add(testObject3);
		testObjects.add(testObject4);
		testObjects.add(testObject5);
		testObjects.add(testObject6);
		testObjects.add(testObject7);
		testObjects.add(testObject8);
		testObjects.add(testObject9);
		testObjects.add(testObject10);
		testObjects.add(testObject11);
	}
	
	@Test
	public void testClusterDetector2ParamConstructor() {
		TemporalClusterDetector testDetector = new TemporalClusterDetector(1, 1);
		assertNotNull("Object is null.", testDetector);
		assertTrue("minNeighbors should be 1.", testDetector.minNeighbors == 1);
		assertTrue("neighborhoodradius should be 1.", testDetector.neighborhoodRadius == 1);
		assertTrue("maxOutlierFactor should be 1,5.", testDetector.maxOutlierFactor == 1.5);
	}
	
	@Test
	public void testClusterDetector3ParamConstructor() {
		TemporalClusterDetector testDetector = new TemporalClusterDetector(1, 1, 2);
		assertNotNull("Object is null.", testDetector);
		assertTrue("minNeighbors should be 1.", testDetector.minNeighbors == 1);
		assertTrue("neighborhoodradius should be 1.", testDetector.neighborhoodRadius == 1);
		assertTrue("maxOutlierFactor should be 2.", testDetector.maxOutlierFactor == 2);
	}
	
	@Test
	public void testNeighborhoodBuilding() {
		TemporalClusterDetector testDetector = new TemporalClusterDetector(neighborhoodRadius, minNeighbors);
		testDetector.calculateCoreDistances(testObjects);
		assertTrue("Clustering objects are missing in the TreeSet.", testObjects.size() == 11);
		
		assertTrue("Test object 1: wrong neighbors size.", testObject1.getNeighbors().size() == 2);
		assertTrue("Test object 1: wrong object on first position.", 
				testObject1.getNeighbors().first().getMetadataIndex() == 7);
		assertTrue("Test object 1: wrong object on last position.", 
				testObject1.getNeighbors().last().getMetadataIndex() == 4);
		
		assertTrue("Test object 2: wrong neighbors size.", testObject2.getNeighbors().size() == 2);
		assertTrue("Test object 2: wrong object on first position.", 
				testObject2.getNeighbors().first().getMetadataIndex() == 2);
		assertTrue("Test object 2: wrong object on last position.", 
				testObject2.getNeighbors().last().getMetadataIndex() == 6);
		
		assertTrue("Test object 3: wrong neighbors size.", testObject3.getNeighbors().size() == 2);
		assertTrue("Test object 3: wrong object on first position.", 
				testObject3.getNeighbors().first().getMetadataIndex() == 1);
		assertTrue("Test object 3: wrong object on last position.", 
				testObject3.getNeighbors().last().getMetadataIndex() == 6);
		
		assertTrue("Test object 4: wrong neighbors size.", testObject4.getNeighbors().size() == 3);
		assertTrue("Test object 4: wrong object on first position.", 
				testObject4.getNeighbors().first().getMetadataIndex() == 8);
		assertTrue("Test object 4: wrong object on last position.", 
				testObject4.getNeighbors().last().getMetadataIndex() == 5);
		
		assertTrue("Test object 5: wrong neighbors size.", testObject5.getNeighbors().size() == 2);
		assertTrue("Test object 5: wrong object on first position.", 
				testObject5.getNeighbors().first().getMetadataIndex() == 7);
		assertTrue("Test object 5: wrong object on last position.", 
				testObject5.getNeighbors().last().getMetadataIndex() == 0);
		
		assertTrue("Test object 6: wrong neighbors size.", testObject6.getNeighbors().size() == 3);
		assertTrue("Test object 6: wrong object on first position.", 
				testObject6.getNeighbors().first().getMetadataIndex() == 9);
		assertTrue("Test object 6: wrong object on last position.", 
				testObject6.getNeighbors().last().getMetadataIndex() == 8);
		
		assertTrue("Test object 7: wrong neighbors size.", testObject7.getNeighbors().size() == 2);
		assertTrue("Test object 7: wrong object on first position.", 
				testObject7.getNeighbors().first().getMetadataIndex() == 1);
		assertTrue("Test object 7: wrong object on last position.", 
				testObject7.getNeighbors().last().getMetadataIndex() == 2);
		
		assertTrue("Test object 8: wrong neighbors size.", testObject8.getNeighbors().size() == 2);
		assertTrue("Test object 8: wrong object on first position.", 
				testObject8.getNeighbors().first().getMetadataIndex() == 0);
		assertTrue("Test object 8: wrong object on last position.", 
				testObject8.getNeighbors().last().getMetadataIndex() == 4);
		
		assertTrue("Test object 9: wrong neighbors size.", testObject9.getNeighbors().size() == 3);
		assertTrue("Test object 9: wrong object on first position.", 
				testObject9.getNeighbors().first().getMetadataIndex() == 3);
		assertTrue("Test object 9: wrong object on last position.", 
				testObject9.getNeighbors().last().getMetadataIndex() == 5);
		
		assertTrue("Test object 10: wrong neighbors size.", testObject10.getNeighbors().size() == 3);
		assertTrue("Test object 10: wrong object on first position.", 
				testObject10.getNeighbors().first().getMetadataIndex() == 3);
		assertTrue("Test object 10: wrong object on last position.", 
				testObject10.getNeighbors().last().getMetadataIndex() == 8);
		
		assertTrue("Test object 11: wrong neighbors size.", testObject11.getNeighbors().size() == 2);
		assertTrue("Test object 11: wrong object on first position.", 
				testObject11.getNeighbors().first().getMetadataIndex() == 8);
		assertTrue("Test object 11: wrong object on last position.", 
				testObject11.getNeighbors().last().getMetadataIndex() == 3);
	}
	
	@Test
	public void testCoreDistances() {
		TemporalClusterDetector testDetector = new TemporalClusterDetector(neighborhoodRadius, minNeighbors);
		testDetector.calculateCoreDistances(testObjects);
		assertTrue("Clustering objects are missing in the TreeSet.", testObjects.size() == 11);
		
		double testCoreDistance = testObject1.getCoreDistance();
		assertTrue("Test object 1: Wrong core distance. "
				+ "Should be 6 but was " + Double.toString(testCoreDistance) + ".",
				testCoreDistance == 6);
		
		testCoreDistance = testObject2.getCoreDistance();
		assertTrue("Test object 2: Wrong core distance. "
				+ "Should be 112 but was " + Double.toString(testCoreDistance) + ".",
				testCoreDistance == 112);
		
		testCoreDistance = testObject3.getCoreDistance();
		assertTrue("Test object 3: Wrong core distance. "
				+ "Should be 127 but was " + Double.toString(testCoreDistance) + ".",
				testCoreDistance == 127);
		
		testCoreDistance = testObject4.getCoreDistance();
		assertTrue("Test object 4: Wrong core distance. "
				+ "Should be 3 but was " + Double.toString(testCoreDistance) + ".",
				testCoreDistance == 3);
		
		testCoreDistance = testObject5.getCoreDistance();
		assertTrue("Test object 5: Wrong core distance. "
				+ "Should be 6 but was " + Double.toString(testCoreDistance) + ".",
				testCoreDistance == 6);
		
		testCoreDistance = testObject6.getCoreDistance();
		assertTrue("Test object 6: Wrong core distance. "
				+ "Should be 8 but was " + Double.toString(testCoreDistance) + ".",
				testCoreDistance == 8);
		
		testCoreDistance = testObject7.getCoreDistance();
		assertTrue("Test object 7: Wrong core distance. "
				+ "Should be 127 but was " + Double.toString(testCoreDistance) + ".",
				testCoreDistance == 127);
		
		testCoreDistance = testObject8.getCoreDistance();
		assertTrue("Test object 8: Wrong core distance. "
				+ "Should be 5 but was " + Double.toString(testCoreDistance) + ".",
				testCoreDistance == 5);
		
		testCoreDistance = testObject9.getCoreDistance();
		assertTrue("Test object 9: Wrong core distance. "
				+ "Should be 5 but was " + Double.toString(testCoreDistance) + ".",
				testCoreDistance == 5);
		
		testCoreDistance = testObject10.getCoreDistance();
		assertTrue("Test object 10: Wrong core distance. "
				+ "Should be 5 but was " + Double.toString(testCoreDistance) + ".",
				testCoreDistance == 5);
		
		testCoreDistance = testObject11.getCoreDistance();
		assertTrue("Test object 11: Wrong core distance. "
				+ "Should be 192 but was " + Double.toString(testCoreDistance) + ".",
				testCoreDistance == 192);
	}
	
	@Test
	public void testOrderOfResultList() {
		TemporalClusterDetector testDetector = new TemporalClusterDetector(neighborhoodRadius, minNeighbors);
		testDetector.calculateCoreDistances(testObjects);
		testDetector.calculateReachabilities(testObjects);
		
		//check correctness of order of result list
		assertTrue("Not all objects were inserted in the result list.", testDetector.augmentedClusteringObjectList.size() == 11);
		
		ClusteringObject testObject = testDetector.augmentedClusteringObjectList.get(0);
		assertTrue("Wrong object on position 1 of result list. Metadata index should be [0] "
				+ "but was [" + testObject.getMetadataIndex() + "].",
				testObject.getMetadataIndex() == 0);
		
		testObject = testDetector.augmentedClusteringObjectList.get(1);
		assertTrue("Wrong object on position 2 of result list. Metadata index should be [4] "
				+ "but was [" + testObject.getMetadataIndex() + "].",
				testObject.getMetadataIndex() == 4);
		
		testObject = testDetector.augmentedClusteringObjectList.get(2);
		assertTrue("Wrong object on position 3 of result list. Metadata index should be [7] "
				+ "but was [" + testObject.getMetadataIndex() + "].",
				testObject.getMetadataIndex() == 7);
		
		testObject = testDetector.augmentedClusteringObjectList.get(3);
		assertTrue("Wrong object on position 4 of result list. Metadata index should be [5] "
				+ "but was [" + testObject.getMetadataIndex() + "].",
				testObject.getMetadataIndex() == 5);
		
		testObject = testDetector.augmentedClusteringObjectList.get(4);
		assertTrue("Wrong object on position 5 of result list. Metadata index should be [3] "
				+ "but was [" + testObject.getMetadataIndex() + "].",
				testObject.getMetadataIndex() == 3);
		
		testObject = testDetector.augmentedClusteringObjectList.get(5);
		assertTrue("Wrong object on position 6 of result list. Metadata index should be [8] "
				+ "but was [" + testObject.getMetadataIndex() + "].",
				testObject.getMetadataIndex() == 8);
		
		testObject = testDetector.augmentedClusteringObjectList.get(6);
		assertTrue("Wrong object on position 7 of result list. Metadata index should be [9] "
				+ "but was [" + testObject.getMetadataIndex() + "].",
				testObject.getMetadataIndex() == 9);
		
		testObject = testDetector.augmentedClusteringObjectList.get(7);
		assertTrue("Wrong object on position 8 of result list. Metadata index should be [10] "
				+ "but was [" + testObject.getMetadataIndex() + "].",
				testObject.getMetadataIndex() == 10);
		
		testObject = testDetector.augmentedClusteringObjectList.get(8);
		assertTrue("Wrong object on position 9 of result list. Metadata index should be [2] "
				+ "but was [" + testObject.getMetadataIndex() + "].",
				testObject.getMetadataIndex() == 2);
		
		testObject = testDetector.augmentedClusteringObjectList.get(9);
		assertTrue("Wrong object on position 10 of result list. Metadata index should be [1] "
				+ "but was [" + testObject.getMetadataIndex() + "].",
				testObject.getMetadataIndex() == 1);
		
		testObject = testDetector.augmentedClusteringObjectList.get(10);
		assertTrue("Wrong object on position 11 of result list. Metadata index should be [6] "
				+ "but was [" + testObject.getMetadataIndex() + "].",
				testObject.getMetadataIndex() == 6);
		
		
	}
	
	@Test
	public void testReachabilityDistances() {
		TemporalClusterDetector testDetector = new TemporalClusterDetector(neighborhoodRadius, minNeighbors);
		testDetector.calculateCoreDistances(testObjects);
		testDetector.calculateReachabilities(testObjects);
		
		double testReachabilityValue = testObject1.getReachabilityDistance();
		assertTrue("Test object 1: Reachability distance should be UNDEFINED but was " + testReachabilityValue, 
				 testReachabilityValue == ClusteringObject.REACHABLE_UNDEFINED);
		
		testReachabilityValue = testObject2.getReachabilityDistance();
		assertTrue("Test object 2: Reachability distance should be 127 but was " + testReachabilityValue, 
				 testReachabilityValue == 127);
		
		testReachabilityValue = testObject3.getReachabilityDistance();
		assertTrue("Test object 3: Reachability distance should be UNDEFINED but was " + testReachabilityValue, 
				 testReachabilityValue == ClusteringObject.REACHABLE_UNDEFINED);
				 
		testReachabilityValue = testObject4.getReachabilityDistance();
		assertTrue("Test object 4: Reachability distance should be 8 but was " + testReachabilityValue, 
				 testReachabilityValue == 8);
		
		testReachabilityValue = testObject5.getReachabilityDistance();
		assertTrue("Test object 5: Reachability distance should be 6 but was " + testReachabilityValue, 
				 testReachabilityValue == 6);
		
		testReachabilityValue = testObject6.getReachabilityDistance();
		assertTrue("Test object 6: Reachability distance should be UNDEFINED but was " + testReachabilityValue, 
				 testReachabilityValue == ClusteringObject.REACHABLE_UNDEFINED);
		
		testReachabilityValue = testObject7.getReachabilityDistance();
		assertTrue("Test object 7: Reachability distance should be 112 but was " + testReachabilityValue, 
				 testReachabilityValue == 112);
		
		testReachabilityValue = testObject8.getReachabilityDistance();
		assertTrue("Test object 8: Reachability distance should be 6 but was " + testReachabilityValue, 
				 testReachabilityValue == 6);
		
		testReachabilityValue = testObject9.getReachabilityDistance();
		assertTrue("Test object 9: Reachability distance should be 3 but was " + testReachabilityValue, 
				 testReachabilityValue == 3);
		
		testReachabilityValue = testObject10.getReachabilityDistance();
		assertTrue("Test object 10: Reachability distance should be 3 but was " + testReachabilityValue, 
				 testReachabilityValue == 3);
		
		testReachabilityValue = testObject11.getReachabilityDistance();
		assertTrue("Test object 11: Reachability distance should be UNDEFINED but was " + testReachabilityValue, 
				 testReachabilityValue == ClusteringObject.REACHABLE_UNDEFINED);
	}
	
	@Test
	public void testLocalReachabilityDensity() {
		TemporalClusterDetector testDetector = new TemporalClusterDetector(neighborhoodRadius, minNeighbors);
		testDetector.calculateCoreDistances(testObjects);
		testDetector.calculateReachabilities(testObjects);
		
		double testReachabilityValue = testObject1.getLocalReachabilityDensity();
		assertTrue("Test object 1: Local reachability density should be "
				+ "0.18181818181818182 but was " + testReachabilityValue, 
				 testReachabilityValue == 0.18181818181818182);
		
		testReachabilityValue = testObject2.getLocalReachabilityDensity();
		assertTrue("Test object 2: Local reachability density should be "
				+ "0.007874015748031496 but was " + testReachabilityValue, 
				 testReachabilityValue == 0.007874015748031496);
		
		testReachabilityValue = testObject3.getLocalReachabilityDensity();
		assertTrue("Test object 3: Local reachability density should be "
				+ "0.008368200836820083 but was " + testReachabilityValue, 
				 testReachabilityValue == 0.008368200836820083);
		
		testReachabilityValue = testObject4.getLocalReachabilityDensity();
		assertTrue("Test object 4: Local reachability density should be "
				+ "0.2 but was " + testReachabilityValue, 
				 testReachabilityValue == 0.2);
		
		testReachabilityValue = testObject5.getLocalReachabilityDensity();
		assertTrue("Test object 5: Local reachability density should be "
				+ "0.18181818181818182 but was " + testReachabilityValue, 
				 testReachabilityValue == 0.18181818181818182);
		
		testReachabilityValue = testObject6.getLocalReachabilityDensity();
		assertTrue("Test object 6: Local reachability density should be "
				+ "0.15384615384615385 but was " + testReachabilityValue, 
				 testReachabilityValue == 0.15384615384615385);
		
		testReachabilityValue = testObject7.getLocalReachabilityDensity();
		assertTrue("Test object 7: Local reachability density should be "
				+ "0.008368200836820083 but was " + testReachabilityValue, 
				 testReachabilityValue == 0.008368200836820083);
		
		testReachabilityValue = testObject8.getLocalReachabilityDensity();
		assertTrue("Test object 8: Local reachability density should be "
				+ "0.16666666666666666 but was " + testReachabilityValue, 
				 testReachabilityValue == 0.16666666666666666);
		
		testReachabilityValue = testObject9.getLocalReachabilityDensity();
		assertTrue("Test object 9: Local reachability density should be "
				+ "0.25 but was " + testReachabilityValue, 
				 testReachabilityValue == 0.25);
		
		testReachabilityValue = testObject10.getLocalReachabilityDensity();
		assertTrue("Test object 10: Local reachability density should be "
				+ "0.18181818181818182 but was " + testReachabilityValue, 
				 testReachabilityValue == 0.18181818181818182);
		
		testReachabilityValue = testObject11.getLocalReachabilityDensity();
		assertTrue("Test object 11: Local reachability density should be "
				+ "0.005235602094240838 but was " + testReachabilityValue, 
				 testReachabilityValue == 0.005235602094240838);
	}
	
	@Test
	public void testCalculateOutlierFactor() {
		TemporalClusterDetector testDetector = new TemporalClusterDetector(neighborhoodRadius, minNeighbors);
		testDetector.calculateCoreDistances(testObjects);
		testDetector.calculateReachabilities(testObjects);
		testDetector.calculateOutlierFactors(testObjects);
		
		double testOutlierFactor = testObject1.getOutlierFactor();
		assertTrue("Test object 1: Outlier factor should be 0.9583333333333333 but was " + testOutlierFactor, 
				 testOutlierFactor == 0.9583333333333333);
		
		testOutlierFactor = testObject2.getOutlierFactor();
		assertTrue("Test object 2: Outlier factor should be 1.0627615062761506 but was " + testOutlierFactor, 
				 testOutlierFactor == 1.0627615062761506);
		
		testOutlierFactor = testObject3.getOutlierFactor();
		assertTrue("Test object 3: Outlier factor should be 0.9704724409448819 but was " + testOutlierFactor, 
				 testOutlierFactor == 0.9704724409448819);
		
		testOutlierFactor = testObject4.getOutlierFactor();
		assertTrue("Test object 4: Outlier factor should be 1.0795454545454546 but was " + testOutlierFactor, 
				 testOutlierFactor == 1.0795454545454546);
		
		testOutlierFactor = testObject5.getOutlierFactor();
		assertTrue("Test object 5: Outlier factor should be 0.9583333333333333 but was " + testOutlierFactor, 
				 testOutlierFactor == 0.9583333333333333);
		
		testOutlierFactor = testObject6.getOutlierFactor();
		assertTrue("Test object 6: Outlier factor should be 1.240909090909091 but was " + testOutlierFactor, 
				 testOutlierFactor == 1.240909090909091);
		
		testOutlierFactor = testObject7.getOutlierFactor();
		assertTrue("Test object 7: Outlier factor should be 0.9704724409448819 but was " + testOutlierFactor, 
				 testOutlierFactor == 0.9704724409448819);
		
		testOutlierFactor = testObject8.getOutlierFactor();
		assertTrue("Test object 8: Outlier factor should be 1.090909090909091 but was " + testOutlierFactor, 
				 testOutlierFactor == 1.090909090909091);
		
		testOutlierFactor = testObject9.getOutlierFactor();
		assertTrue("Test object 9: Outlier factor should be 0.7636363636363637 but was " + testOutlierFactor, 
				 testOutlierFactor == 0.7636363636363637);
		
		testOutlierFactor = testObject10.getOutlierFactor();
		assertTrue("Test object 10: Outlier factor should be 0.9730769230769232 but was " + testOutlierFactor, 
				 testOutlierFactor == 0.9730769230769232);
		
		testOutlierFactor = testObject11.getOutlierFactor();
		assertTrue("Test object 11: Outlier factor should be 42.975 but was " + testOutlierFactor, 
				 testOutlierFactor == 42.975);
	}

	@Test
	public void testClusterDetectionWith12mOF() {
		TemporalClusterDetector testDetector = new TemporalClusterDetector(neighborhoodRadius, minNeighbors, 1.2);
		ClusteringResult testResult = testDetector.detectClusters(testObjects);
		assertNotNull("The clustering result was null.", testResult);
		
		int testSize = testResult.getNoMetadata().size();
		assertTrue("Wrong size of noTimestamp list, should be 0 but was " + testSize + ".", testSize == 0);
		testSize = testResult.getOutliers().size();
		assertTrue("Wrong size of outliers list, should be 2 but was " + testSize + ".", 
				testResult.getOutliers().size() == 2);
		testSize = testResult.getClusters().size();
		assertTrue("Wrong size of cluster list, should be 3 but was " + testSize + ".",
				testResult.getClusters().size() == 3);
		
		//outliers
		int testMetadataIndex = testResult.getOutliers().get(0);
		assertTrue("Wrong element on position 1 of outlier list. Should be [5] but was " + testMetadataIndex + ".",
				testMetadataIndex == 5);
		
		testMetadataIndex = testResult.getOutliers().get(1);
		assertTrue("Wrong element on position 2 of outlier list. Should be [10] but was " + testMetadataIndex + ".",
				testMetadataIndex == 10);
		
		//cluster 1
		assertTrue("Wrong size of cluster 1.", testResult.getClusters().get(0).size() == 3);	
		
		testMetadataIndex = testResult.getClusters().get(0).get(0);
		assertTrue("Wrong element on position 1 of cluster 1. Should be [0] but was " + testMetadataIndex + ".",
				testMetadataIndex == 0);
		
		testMetadataIndex = testResult.getClusters().get(0).get(1);
		assertTrue("Wrong element on position 2 of cluster 1. Should be [4] but was " + testMetadataIndex + ".",
				testMetadataIndex == 4);
		
		testMetadataIndex = testResult.getClusters().get(0).get(2);
		assertTrue("Wrong element on position 3 of cluster 1. Should be [7] but was " + testMetadataIndex + ".",
				testMetadataIndex == 7);
		
		//cluster 2
		assertTrue("Wrong size of cluster 2.", testResult.getClusters().get(1).size() == 3);	
		
		testMetadataIndex = testResult.getClusters().get(1).get(0);
		assertTrue("Wrong element on position 1 of cluster 2. Should be [3] but was " + testMetadataIndex + ".",
				testMetadataIndex == 3);
		
		testMetadataIndex = testResult.getClusters().get(1).get(1);
		assertTrue("Wrong element on position 2 of cluster 2. Should be [8] but was " + testMetadataIndex + ".",
				testMetadataIndex == 8);
		
		testMetadataIndex = testResult.getClusters().get(1).get(2);
		assertTrue("Wrong element on position 3 of cluster 2. Should be [9] but was " + testMetadataIndex + ".",
				testMetadataIndex == 9);
		
		//cluster 3
		assertTrue("Wrong size of cluster 3.", testResult.getClusters().get(2).size() == 3);	
		
		testMetadataIndex = testResult.getClusters().get(2).get(0);
		assertTrue("Wrong element on position 1 of cluster 3. Should be [2] but was " + testMetadataIndex + ".",
				testMetadataIndex == 2);
		
		testMetadataIndex = testResult.getClusters().get(2).get(1);
		assertTrue("Wrong element on position 2 of cluster 3. Should be [1] but was " + testMetadataIndex + ".",
				testMetadataIndex == 1);
		
		testMetadataIndex = testResult.getClusters().get(2).get(2);
		assertTrue("Wrong element on position 3 of cluster 3. Should be [6] but was " + testMetadataIndex + ".",
				testMetadataIndex == 6);		
	}
	
	@Test
	public void testClusterDetectionWith15mOF() {
		TemporalClusterDetector testDetector = new TemporalClusterDetector(neighborhoodRadius, minNeighbors);
		ClusteringResult testResult = testDetector.detectClusters(testObjects);
		assertNotNull("The clustering result was null.", testResult);
		
		int testSize = testResult.getNoMetadata().size();
		assertTrue("Wrong size of noTimestamp list, should be 0 but was " + testSize + ".", testSize == 0);
		testSize = testResult.getOutliers().size();
		assertTrue("Wrong size of outliers list, should be 1 but was " + testSize + ".", 
				testResult.getOutliers().size() == 1);
		testSize = testResult.getClusters().size();
		assertTrue("Wrong size of cluster list, should be 3 but was " + testSize + ".",
				testResult.getClusters().size() == 3);
		
		//outliers
		int testMetadataIndex = testResult.getOutliers().get(0);		
		assertTrue("Wrong element on position 1 of outlier list. Should be [10] but was " + testMetadataIndex + ".",
				testMetadataIndex == 10);
		
		//cluster 1
		assertTrue("Wrong size of cluster 1.", testResult.getClusters().get(0).size() == 3);	
		
		testMetadataIndex = testResult.getClusters().get(0).get(0);
		assertTrue("Wrong element on position 1 of cluster 1. Should be [0] but was " + testMetadataIndex + ".",
				testMetadataIndex == 0);
		
		testMetadataIndex = testResult.getClusters().get(0).get(1);
		assertTrue("Wrong element on position 2 of cluster 1. Should be [4] but was " + testMetadataIndex + ".",
				testMetadataIndex == 4);
		
		testMetadataIndex = testResult.getClusters().get(0).get(2);
		assertTrue("Wrong element on position 3 of cluster 1. Should be [7] but was " + testMetadataIndex + ".",
				testMetadataIndex == 7);
		
		//cluster 2
		assertTrue("Wrong size of cluster 2.", testResult.getClusters().get(1).size() == 4);	
		
		testMetadataIndex = testResult.getClusters().get(1).get(0);
		assertTrue("Wrong element on position 1 of cluster 2. Should be [5] but was " + testMetadataIndex + ".",
				testMetadataIndex == 5);
		
		testMetadataIndex = testResult.getClusters().get(1).get(1);
		assertTrue("Wrong element on position 2 of cluster 2. Should be [3] but was " + testMetadataIndex + ".",
				testMetadataIndex == 3);
		
		testMetadataIndex = testResult.getClusters().get(1).get(2);
		assertTrue("Wrong element on position 3 of cluster 2. Should be [8] but was " + testMetadataIndex + ".",
				testMetadataIndex == 8);
		
		testMetadataIndex = testResult.getClusters().get(1).get(3);
		assertTrue("Wrong element on position 4 of cluster 2. Should be [9] but was " + testMetadataIndex + ".",
				testMetadataIndex == 9);
		
		//cluster 3
		assertTrue("Wrong size of cluster 3.", testResult.getClusters().get(2).size() == 3);	
		
		testMetadataIndex = testResult.getClusters().get(2).get(0);
		assertTrue("Wrong element on position 1 of cluster 3. Should be [2] but was " + testMetadataIndex + ".",
				testMetadataIndex == 2);
		
		testMetadataIndex = testResult.getClusters().get(2).get(1);
		assertTrue("Wrong element on position 2 of cluster 3. Should be [1] but was " + testMetadataIndex + ".",
				testMetadataIndex == 1);
		
		testMetadataIndex = testResult.getClusters().get(2).get(2);
		assertTrue("Wrong element on position 3 of cluster 3. Should be [6] but was " + testMetadataIndex + ".",
				testMetadataIndex == 6);		
	}
}
