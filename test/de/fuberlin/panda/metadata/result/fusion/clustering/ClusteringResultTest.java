package de.fuberlin.panda.metadata.result.fusion.clustering;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ClusteringResultTest {

	@Test
	public void testConstructor() {
		ClusteringResult testResult = new ClusteringResult();
		assertNotNull("ClusteringResult object was null.", testResult);
	}
	
	@Test
	public void testAddNoTimestampObject() {
		ClusteringResult testResult = new ClusteringResult();
		testResult.addNoMetadata(2);
		assertNotNull("NoTimestamp list was null.", testResult.getNoMetadata());
		int testInt = testResult.getNoMetadata().size();
		assertTrue("Wrong size of noTimestamp list, should be 1 but was " + testInt + ".",	testInt == 1);
		testInt = testResult.getNoMetadata().get(0);
		assertTrue("Wrong element on position 1 of noTimestamp list, should be [2] but was [" + testInt + "].", 
				testInt == 2);
	}
	
	@Test
	public void testAddOutlierObject() {
		ClusteringResult testResult = new ClusteringResult();
		testResult.addOutlier(2);
		assertNotNull("Outliers list was null.", testResult.getOutliers());
		int testInt = testResult.getOutliers().size();
		assertTrue("Wrong size of outliers list, should be 1 but was " + testInt + ".",	testInt == 1);
		testInt = testResult.getOutliers().get(0);
		assertTrue("Wrong element on position 1 of outliers list, should be [2] but was [" + testInt + "].", 
				testInt == 2);
	}
	
	@Test
	public void testAddToNewCluster() {
		ClusteringResult testResult = new ClusteringResult();
		assertNotNull("Clusters list was null.", testResult.getClusters());
		
		//1st cluster
		testResult.addToNewCluster(2);
		int testSize = testResult.getClusters().size();
		assertTrue("Wrong ammount of clusters found, should be 1 but was " + testSize + ".", testSize == 1);
		
		testSize = testResult.getClusters().get(0).size();
		assertTrue("Wrong size of cluster 1, should be 1 but was " + testSize + ".", testSize == 1);
		
		int testIndex = testResult.getClusters().get(0).get(0);
		assertTrue("Wrong element on position 1 of cluster 1, should be [2] but was [" + testIndex + "].", 
				testIndex == 2);
		
		//2nd cluster
		testResult.addToNewCluster(3);
		
		testSize = testResult.getClusters().size();
		assertTrue("Wrong ammount of clusters found, should be 2 but was " + testSize + ".", testSize == 2);
		
		testSize = testResult.getClusters().get(1).size();
		assertTrue("Wrong size of cluster 2, should be 1 but was " + testSize + ".", testSize == 1);
		
		testIndex = testResult.getClusters().get(1).get(0);
		assertTrue("Wrong element on position 1 of cluster 2, should be [3] but was [" + testIndex + "].", 
				testIndex == 3);
	}
	
	@Test(expected=IndexOutOfBoundsException.class)
	public void testExceptionByAddToExistingClusterWithoutNewCluster() {
		ClusteringResult testResult = new ClusteringResult();
		assertNotNull("Clusters list was null.", testResult.getClusters());
		testResult.addToExistingCluster(3);
	}
	
	@Test
	public void testAddToExistingCluster() {
		ClusteringResult testResult = new ClusteringResult();
		assertNotNull("Clusters list was null.", testResult.getClusters());
		
		testResult.addToNewCluster(2);
		testResult.addToExistingCluster(3);
		
		int testSize = testResult.getClusters().size();
		assertTrue("Wrong ammount of clusters found, should be 1 but was " + testSize + ".", testSize == 1);
		
		testSize = testResult.getClusters().get(0).size();
		assertTrue("Wrong size of cluster 1, should be 2 but was " + testSize + ".", testSize == 2);
		
		int testIndex = testResult.getClusters().get(0).get(0);
		assertTrue("Wrong element on position 1 of cluster 1, should be [2] but was [" + testIndex + "].", 
				testIndex == 2);
		
		testIndex = testResult.getClusters().get(0).get(1);
		assertTrue("Wrong element on position 2 of cluster 1, should be [3] but was [" + testIndex + "].", 
				testIndex == 3);
	}
}
