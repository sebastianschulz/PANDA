package de.fuberlin.panda.metadata.result.fusion.clustering;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.fuberlin.panda.metadata.result.fusion.temporal.TemporalClusteringObject;

public class NeighborsComparatorTest {
	
	private final NeighborsComparator neighborsComparator = new NeighborsComparator(); 
	
	@Test
	public void testDifferentNeighborsHelpEpsilonDistanceOrder() {
		int comparationResult;
		
		//metadata index and timestamps are not interesting in this case
		TemporalClusteringObject testObject1 = new TemporalClusteringObject(0, 0);
		TemporalClusteringObject testObject2 = new TemporalClusteringObject(0, 0);
		
		//test argument 2 is greater
		testObject1.setHelpEpsilonDistance(1d);
		testObject2.setHelpEpsilonDistance(2d);
		comparationResult = neighborsComparator.compare(testObject1, testObject2);
		assertTrue("First helpEpsilonDistance was smaller than second: return value should be -1 but was "
				+ comparationResult + "." , comparationResult == -1);
		
		//test argument 1 is greater
		testObject1.setHelpEpsilonDistance(2d);
		testObject2.setHelpEpsilonDistance(1d);
		comparationResult = neighborsComparator.compare(testObject1, testObject2);
		assertTrue("Second helpEpsilonDistance was smaller than first: return value should be 1 but was "
				+ comparationResult + "." , comparationResult == 1);
	}
	
	@Test
	public void testSameNeighborsHelpEpsilonDistanceOrder() {
		int comparationResult;
		
		//just different metadata indices, timestamps are not interesting in this case
		TemporalClusteringObject testObject1 = new TemporalClusteringObject(0, 0);
		TemporalClusteringObject testObject2 = new TemporalClusteringObject(1, 0);
		
		testObject1.setHelpEpsilonDistance(1d);
		testObject2.setHelpEpsilonDistance(1d);
		
		//test argument 1 has smaller metadata index
		comparationResult = neighborsComparator.compare(testObject1, testObject2);
		assertTrue("First metadata index was smaller than second: return value should be -1 but was "
				+ comparationResult + "." , comparationResult == -1);
		
		//test argument 2 has smaller metadata index
		comparationResult = neighborsComparator.compare(testObject2, testObject1);
		assertTrue("Second metadata index was smaller than first: return value should be 1 but was "
				+ comparationResult + "." , comparationResult == 1);
		
		//test both arguments have same metadata index
		comparationResult = neighborsComparator.compare(testObject1, testObject1);
		assertTrue("Both arguments have same metadata index: return value should be 0 but was "
				+ comparationResult + "." , comparationResult == 0);
	}

}
