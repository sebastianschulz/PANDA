package de.fuberlin.panda.metadata.result.fusion.areal.geocoding;

import java.io.File;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import de.fuberlin.panda.api.APIHelper;
import de.fuberlin.panda.metadata.MetadataConnector;
import de.fuberlin.panda.metadata.exceptions.PolygonReadingException;
import de.fuberlin.panda.metadata.result.fusion.areal.GeoArea;

/**
 * This class is a placeholder! Since the actual geocoding algorithm
 * is out of the scope of this work. This can be performed by services
 * like the Google Geocoding API.
 * 
 * @author Sebastian Schulz
 * @since 24.02.2014
 */
public class Geocoder {
	private static Logger logger = Logger.getLogger(MetadataConnector.class.getName());
	
	private boolean isPolygon = false;
	private boolean isGeoCoord = false;
	
	GeoArea polygon;
	Double[] geoCoords = new Double[2];
	
	public Geocoder(String location) {
		try {
			resolveLocation(location);
			logger.debug("-->Successfully retrieve geographical data for '"
					+ location + "'");
		} catch (PolygonReadingException e) {
			logger.error("Unable to parse geographical data for '"
					+ location + "'");
		}
	}

	/**
	 * Easy to access example method. This method would call the online
	 * geocoding service. In this case the polygon files can be found in 
	 * the Folder {@code ...\WebContent\prefs\geocoding} (Files from:
	 * http://download.geofabrik.de/europe/germany/)
	 * 
	 * @param location - a {@code String} value wich rerpesents the location attribute.
	 * @throws PolygonReadingException 
	 */
	private void resolveLocation(String location) throws PolygonReadingException {
 		String trimmedLocation = location.replace(" ", "");
		boolean isParsablePolygon = checkPolygonParsibility(trimmedLocation.toLowerCase());
		boolean isParsableGeoCoord = checkGeoCoordParsibility(trimmedLocation.toLowerCase());
 		if (isParsablePolygon) {
			isPolygon = true;
			String fileName = APIHelper.getWebContentDirPath() + "\\prefs\\geocoding\\" + 
					trimmedLocation.toLowerCase() + ".poly";
			PolygonFileReader polygonReader = new PolygonFileReader(new File(fileName));
			polygon = polygonReader.loadPolygon();
			polygon.setName(trimmedLocation);
		} else if (isParsableGeoCoord) {
			isGeoCoord  = true;
			String fileName = APIHelper.getWebContentDirPath() + "\\prefs\\geocoding\\" + 
					trimmedLocation.toLowerCase() + ".poly";
			PolygonFileReader coordinatesReader = new PolygonFileReader(new File(fileName));
			double[] parsedCoords = coordinatesReader.loadCoordinates();
			geoCoords[0] = parsedCoords[0];
			geoCoords[1] = parsedCoords[1];
		}
	}
	
	/**
	 * Parse the preference file which contains all possible polygons. Check if 
	 * the given location is contained in this file.
	 * 
	 * @return a {@code boolean} value.
	 */
	private boolean checkPolygonParsibility(String location) {
		String possiblePolygons = APIHelper.readFileContent("prefs\\geocoding", "geoPolyPref.txt");
		StringTokenizer st = new StringTokenizer(possiblePolygons, "\r\n");
		while (st.hasMoreElements()) {
			if (st.nextToken().equals(location)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Parse the preference file which contains all possible geo locations. Check if 
	 * the given location is contained in this file.
	 * 
	 * @return a {@code boolean} value.
	 */
	private boolean checkGeoCoordParsibility(String location) {
		String possibleGeoCoords = APIHelper.readFileContent("prefs\\geocoding", "geoPref.txt");
		StringTokenizer st = new StringTokenizer(possibleGeoCoords, "\r\n");
		while (st.hasMoreElements()) {
			if (st.nextToken().equals(location)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isResultPolygon() {
		return isPolygon;
	}

	public boolean isResultGeoCoord() {
		return isGeoCoord;
	}

	public Double[] getGeoCoords() {
		return geoCoords;
	}
	
	public GeoArea getPolygon() {
		return polygon;
	}

}