package de.fuberlin.panda.metadata.result.fusion.areal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class GeoAreaTest {
	private ArrayList<Point2D.Double> testList;
	
	@Before
	public void setUp() {
		testList = new ArrayList<>();
		testList.add(new Point2D.Double(1,1));
		testList.add(new Point2D.Double(1,2));
		testList.add(new Point2D.Double(2,2));
		testList.add(new Point2D.Double(2,1));
	}
	
	@Test
	public void testCreateGeoArea() {
		GeoArea geoArea = new GeoArea(testList);
		assertNotNull("GeoArea object was null", geoArea);
		assertTrue("GeoArea object doesn't contain boundary [1,1]", geoArea.contains(1, 1));
		assertTrue("GeoArea object doesn't contain boundary [1,2]", geoArea.contains(1, 2));
		assertTrue("GeoArea object doesn't contain boundary [2,2]", geoArea.contains(2, 2));
		assertTrue("GeoArea object doesn't contain boundary [2,1]", geoArea.contains(2, 1));
		assertTrue("GeoArea object doesn't contain inner point [1.5,1.5]", geoArea.contains(1.5, 1.5));
		assertFalse("GeoArea object contains outer point [0,0]", geoArea.contains(0, 0));
		assertFalse("GeoArea object contains outer point [0,3]", geoArea.contains(0, 3));
		assertFalse("GeoArea object contains outer point [3,3]", geoArea.contains(3, 3));
		assertFalse("GeoArea object contains outer point [3,0]", geoArea.contains(3, 0));
	}
	
	@Test
	public void testContainsPartOf() {
		ArrayList<Point2D.Double> testList2 = new ArrayList<>();
		testList2.add(new Point2D.Double(1.5,1.5));
		testList2.add(new Point2D.Double(1.5,2));
		testList2.add(new Point2D.Double(2,1.5));
		
		GeoArea geoArea = new GeoArea(testList);
		GeoArea secondArea = new GeoArea(testList2);
		assertTrue("GeoArea object doesn't contain boundary point of second aera ([1.5,1.5],[1.5,2],[2,1.5].",
					geoArea.containsPartOf(secondArea));
		
	}
}
