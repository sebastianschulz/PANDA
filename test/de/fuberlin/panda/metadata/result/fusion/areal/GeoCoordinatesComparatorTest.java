package de.fuberlin.panda.metadata.result.fusion.areal;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class GeoCoordinatesComparatorTest {

	private final GeoCoordinatesComparator geoCoordiantesComparator = new GeoCoordinatesComparator(); 
	
	@Test
	public void testDifferentGeoCoordsDistance() {
		int comparationResult;
		
		//metadata indices are not interesting in this case
		ArealClusteringObject testObject1 = new ArealClusteringObject(0, 1d, 1d);
		ArealClusteringObject testObject2 = new ArealClusteringObject(0, 2d, 1d);
		
		//test argument 2 is greater
		comparationResult = geoCoordiantesComparator.compare(testObject1, testObject2);
		assertTrue("First reachability was smaller than second: return value should be -1 but was "
				+ comparationResult + "." , comparationResult == -1);
		
		//test argument 1 is greater
		comparationResult = geoCoordiantesComparator.compare(testObject2, testObject1);
		assertTrue("Second reachability was smaller than first: return value should be 1 but was "
				+ comparationResult + "." , comparationResult == 1);
	}
	
	@Test
	public void testSameGeoCoordsDistance() {
		int comparationResult;
		
		ArealClusteringObject testObject1 = new ArealClusteringObject(0, 1d, 1d);
		ArealClusteringObject testObject2 = new ArealClusteringObject(1, 1d, 1d);
		
		//test argument 1 has smaller metadata index
		comparationResult = geoCoordiantesComparator.compare(testObject1, testObject2);
		assertTrue("First metadata index was smaller than second: return value should be -1 but was "
				+ comparationResult + "." , comparationResult == -1);
		
		//test argument 2 has smaller metadata index
		comparationResult = geoCoordiantesComparator.compare(testObject2, testObject1);
		assertTrue("Second metadata index was smaller than first: return value should be 1 but was "
				+ comparationResult + "." , comparationResult == 1);
		
		//test both arguments have same metadata index
		comparationResult = geoCoordiantesComparator.compare(testObject1, testObject1);
		assertTrue("Both arguments have same metadata index: return value should be 0 but was "
				+ comparationResult + "." , comparationResult == 0);
	}
}
