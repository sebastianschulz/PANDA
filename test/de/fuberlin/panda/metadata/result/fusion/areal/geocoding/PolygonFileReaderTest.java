package de.fuberlin.panda.metadata.result.fusion.areal.geocoding;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.geom.Point2D;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;

import org.junit.Test;

import de.fuberlin.panda.metadata.exceptions.PolygonReadingException;
import de.fuberlin.panda.metadata.result.fusion.areal.GeoArea;

public class PolygonFileReaderTest {

	@Test
	public void testLoadCoordinates() {
		double[] testCoords = new double[2];
		testCoords[0] = 2d;
		testCoords[1] = 5d;
		
		URL fileURI = this.getClass().getResource("testCoord.poly");
		String filePath = fileURI.toString().replace("file:/", "").replace("%20", " ");
		File testFile = new File(filePath);
		try {
			PolygonFileReader polygonReader = new PolygonFileReader(testFile);
			double[] parsedCoords = polygonReader.loadCoordinates();
			assertTrue("Coordinates parsed wrong.", (parsedCoords[0] == testCoords[0] && parsedCoords[1] == testCoords[1]));
		} catch (PolygonReadingException e) {
			fail(e.getMessage());
		}
		
	}
	
	@Test
	public void testLoadPolyogon() {
		ArrayList<Point2D.Double> testPath = new ArrayList<>();
		testPath.add(new Point2D.Double(1, 1));
		testPath.add(new Point2D.Double(1, 2));
		testPath.add(new Point2D.Double(2, 1));
		testPath.add(new Point2D.Double(1, 1));
		GeoArea testArea = new GeoArea(testPath);
		
		URL fileURI = this.getClass().getResource("testCoord3.poly");
		String filePath = fileURI.toString().replace("file:/", "").replace("%20", " ");
		File testFile = new File(filePath);
		try {
			PolygonFileReader polygonReader = new PolygonFileReader(testFile);
			GeoArea parsedArea = polygonReader.loadPolygon();
			assertTrue("Area parsed wrong.", parsedArea.getAreaObject().equals(testArea.getAreaObject()));
		} catch (PolygonReadingException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testLoadTwoPiecesPolyogon() {
		ArrayList<Point2D.Double> testPath = new ArrayList<>();
		testPath.add(new Point2D.Double(1, 1));
		testPath.add(new Point2D.Double(1, 2));
		testPath.add(new Point2D.Double(2, 1));
		GeoArea testArea = new GeoArea(testPath);
		
		ArrayList<Point2D.Double> secondPath = new ArrayList<>();
		secondPath.add(new Point2D.Double(3, 3));
		secondPath.add(new Point2D.Double(3, 4));
		secondPath.add(new Point2D.Double(4, 3));
		secondPath.add(new Point2D.Double(4, 4));
		testArea.add(new GeoArea(secondPath));
		
		URL fileURI = this.getClass().getResource("testCoordDual.poly");
		String filePath = fileURI.toString().replace("file:/", "").replace("%20", " ");
		File testFile = new File(filePath);
		try {
			PolygonFileReader polygonReader = new PolygonFileReader(testFile);
			GeoArea parsedArea = polygonReader.loadPolygon();
			assertTrue("Area parsed wrong.", parsedArea.getAreaObject().equals(testArea.getAreaObject()));
		} catch (PolygonReadingException e) {
			fail(e.getMessage());
		}
	}
}
