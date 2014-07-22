package de.fuberlin.panda.metadata.result.fusion.areal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class ConvexHullTest {
	private ArrayList<ArealClusteringObject> testList;
	
	@Before
	public void setUp() {
		testList = new ArrayList<>();
		testList.add(new ArealClusteringObject(0, 7d, 4d));
		testList.add(new ArealClusteringObject(1, 4.5d, 2.5d));
		testList.add(new ArealClusteringObject(2, 6d, 1d));
		testList.add(new ArealClusteringObject(3, 2d, 2d));
		testList.add(new ArealClusteringObject(4, 4d, 4d));
		testList.add(new ArealClusteringObject(5, 3d, 5d));
		testList.add(new ArealClusteringObject(6, 5d, 5d));
		testList.add(new ArealClusteringObject(7, 5.5d, 2d));
		testList.add(new ArealClusteringObject(8, 3d, 3d));
		testList.add(new ArealClusteringObject(9, 4d, 1d));
	}
	
	@Test
	public void testConvexHull() {
		ConvexHull testHull = new ConvexHull(testList);
		GeoArea testHullPolygon = testHull.getPolygon();
		
		assertTrue("Point [2,2] was not found inside convex hull.", testHullPolygon.contains(2, 2));
		assertTrue("Point [4,1] was not found inside convex hull.", testHullPolygon.contains(4, 1));
		assertTrue("Point [6,1] was not found insiden convex hull.", testHullPolygon.contains(6, 1));
		assertTrue("Point [7,4] was not found inside convex hull.", testHullPolygon.contains(7d, 4d));
		assertTrue("Point [3,5] was not found inside convex hull.", testHullPolygon.contains(3d, 5d));
		assertTrue("Point [5,5] was not found inside convex hull.", testHullPolygon.contains(5d, 5d));
		assertFalse("Point [1.5,1.5] was found inside convex hull.", testHullPolygon.contains(1.5d, 1.5d));
		assertFalse("Point [7.1,4] was found inside convex hull.", testHullPolygon.contains(7.1d, 4d));
		assertFalse("Point [5,0.9] was found inside convex hull.", testHullPolygon.contains(5d, 0.9d));
		assertFalse("Point [6,5.6] was found inside convex hull.", testHullPolygon.contains(6d, 5.6d));
	}
	
}
