package de.fuberlin.panda.metadata.result.fusion.temporal;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TimestampComparatorTest {

	private final TimestampComparator timestampComparator = new TimestampComparator(); 
	
	@Test
	public void testDifferentTimestampOrder() {
		int comparationResult;
		
		//metadata indices are not interesting in this case
		TemporalClusteringObject testObject1 = new TemporalClusteringObject(0, 1);
		TemporalClusteringObject testObject2 = new TemporalClusteringObject(0, 2);
		
		//test argument 2 is greater
		comparationResult = timestampComparator.compare(testObject1, testObject2);
		assertTrue("First reachability was smaller than second: return value should be -1 but was "
				+ comparationResult + "." , comparationResult == -1);
		
		//test argument 1 is greater
		comparationResult = timestampComparator.compare(testObject2, testObject1);
		assertTrue("Second reachability was smaller than first: return value should be 1 but was "
				+ comparationResult + "." , comparationResult == 1);
	}
	
	@Test
	public void testSameTimestampOrder() {
		int comparationResult;
		
		TemporalClusteringObject testObject1 = new TemporalClusteringObject(0, 1);
		TemporalClusteringObject testObject2 = new TemporalClusteringObject(1, 1);
		
		//test argument 1 has smaller metadata index
		comparationResult = timestampComparator.compare(testObject1, testObject2);
		assertTrue("First metadata index was smaller than second: return value should be -1 but was "
				+ comparationResult + "." , comparationResult == -1);
		
		//test argument 2 has smaller metadata index
		comparationResult = timestampComparator.compare(testObject2, testObject1);
		assertTrue("Second metadata index was smaller than first: return value should be 1 but was "
				+ comparationResult + "." , comparationResult == 1);
		
		//test both arguments have same metadata index
		comparationResult = timestampComparator.compare(testObject1, testObject1);
		assertTrue("Both arguments have same metadata index: return value should be 0 but was "
				+ comparationResult + "." , comparationResult == 0);
	}
	
}