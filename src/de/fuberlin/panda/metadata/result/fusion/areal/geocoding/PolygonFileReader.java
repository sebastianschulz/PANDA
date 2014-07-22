package de.fuberlin.panda.metadata.result.fusion.areal.geocoding;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import de.fuberlin.panda.metadata.exceptions.PolygonReadingException;
import de.fuberlin.panda.metadata.result.fusion.areal.GeoArea;

/**
 * Temporal Class for reading in .poly files. Has to be removed in 
 * case of applying a geocoding api. Dirty version but works :)<br><br>
 * 
 * Orginal code by Brett Henderson from <a href="http://svn.openstreetmap.org/applications/utils/osmosis/trunk.orig/areafilter/src/org/openstreetmap/osmosis/areafilter/common/PolygonFileReader.java">Open Street Map</a>
 * project. 
 * 
 * @author Sebastian Schulz
 * @since 24.02.2014
 */
public class PolygonFileReader {
	private Reader fileReader;
	private String polygonFile;
	private String myPolygonName;
	
	public PolygonFileReader(final File polygonFile) throws PolygonReadingException {
		try {
			this.polygonFile = polygonFile.getName();
			this.fileReader = new FileReader(polygonFile);
		} catch (IOException e) {
			throw new PolygonReadingException("Unable to read from polygon file " + polygonFile + ". " +
					e.getMessage());
		}
	}
	
	private void cleanup() {
		if (fileReader != null) {
			try {
				fileReader.close();
			} catch (Exception e) {
				//nothing to do here
			} finally {
				fileReader = null;
			}
		}
	}
	
	/**
	 * Builds an Area configured with the polygon information defined in the
	 * file.
	 * 
	 * @return A fully configured area.
	 * @throws PolygonReadingException 
	 */
	public GeoArea loadPolygon() throws PolygonReadingException {
		try {
			GeoArea resultArea = new GeoArea();
			BufferedReader bufferedReader;

            bufferedReader = new BufferedReader(fileReader);
            myPolygonName = bufferedReader.readLine();
            if (myPolygonName == null || myPolygonName.trim().length() == 0) {
            	 throw new PolygonReadingException("The file must begin with a header naming the polygon file.");
            }

			// We now loop until no more sections are available.
			while (true) {
				String sectionHeader;
				boolean positivePolygon;
				GeoArea sectionArea;
				
				do {
					sectionHeader = bufferedReader.readLine();
					
					// It is invalid for the file to end without a global "END" record.
					if (sectionHeader == null) {
						throw new PolygonReadingException("File terminated prematurely without a section END record.");
					}
					
					// Remove any whitespace.
					sectionHeader = sectionHeader.trim();
					
				} while (sectionHeader.length() == 0);
				
				// Stop reading when the global END record is reached.
				if ("END".equals(sectionHeader)) {
					break;
				}
				
				// If the section header begins with a ! then the polygon is to
				// be subtracted from the result area.
				positivePolygon = (sectionHeader.charAt(0) != '!');
				
				// Create an area for this polygon.
				sectionArea = loadSectionPolygon(bufferedReader);
				
				// Add or subtract the section area from the overall area as
				// appropriate.
				if (positivePolygon) {
					resultArea.add(sectionArea);
				} else {
					resultArea.subtract(sectionArea);
				}
			}
			
			return resultArea;
			
		} catch (Exception e) {
			throw new PolygonReadingException("Unable to read from polygon file " + polygonFile + ". " +
					  e.getMessage());
		} finally {
			cleanup();
		}
	}

	/**
	 * Copy and paste version for loading a polygonfile with just 1 pair of coordinates.
	 * 
	 * @return
	 * @throws PolygonReadingException
	 */
	public double[] loadCoordinates() throws PolygonReadingException  {
		try {
			double[] coordinates = null;
			BufferedReader bufferedReader;

            bufferedReader = new BufferedReader(fileReader);
            myPolygonName = bufferedReader.readLine();
            if (myPolygonName == null || myPolygonName.trim().length() == 0) {
            	 throw new PolygonReadingException("The file must begin with a header naming the polygon file.");
            }
 
			// We now loop until no more sections are available.
			while (true) {
				String sectionHeader;
				String sectionLine;
				
				do {
					sectionHeader = bufferedReader.readLine();
					
					// It is invalid for the file to end without a global "END" record.
					if (sectionHeader == null) {
						throw new PolygonReadingException("File terminated prematurely without a section END record.");
					}
					
					// Remove any whitespace.
					sectionHeader = sectionHeader.trim();
					
				} while (sectionHeader.length() == 0);
				
				// Stop reading when the global END record is reached.
				if ("END".equals(sectionHeader)) {
					break;
				}
				
				// Read until a non-empty line is obtained.
				do {
					sectionLine = bufferedReader.readLine();
					
					// It is invalid for the file to end without a section "END" record.
					if (sectionLine == null) {
						throw new PolygonReadingException("File terminated prematurely without a section END record.");
					}
					
					// Remove any whitespace.
					sectionLine = sectionLine.trim();
					
				} while (sectionLine.length() == 0);
				
				// Stop reading when the section END record is reached.
				if ("END".equals(sectionLine)) {
					break;
				}
				
				// Parse the line into its coordinates.
				coordinates = parseCoordinates(sectionLine);

			}
			
			return coordinates;
			
		} catch (Exception e) {
			throw new PolygonReadingException("Unable to read from polygon file " + polygonFile + ". " +
					  e.getMessage());
		} finally {
			cleanup();
		}
	}
	
	/**
	 * Loads an individual polygon from the polygon file.
	 * 
	 * @param bufferedReader
	 *            The reader connected to the polygon file placed at the first
	 *            record of a polygon section.
	 * @return An area representing the section polygon.
	 * @throws PolygonReadingException 
	 */
	private GeoArea loadSectionPolygon(BufferedReader bufferedReader) throws IOException, PolygonReadingException {
		ArrayList<Point2D.Double> polygonalBoundary = new ArrayList<Point2D.Double>();
		
		while (true) {
			String sectionLine;
			double[] coordinates;
			
			// Read until a non-empty line is obtained.
			do {
				sectionLine = bufferedReader.readLine();
				
				// It is invalid for the file to end without a section "END" record.
				if (sectionLine == null) {
					throw new PolygonReadingException("File terminated prematurely without a section END record.");
				}
				
				// Remove any whitespace.
				sectionLine = sectionLine.trim();
				
			} while (sectionLine.length() == 0);
			
			// Stop reading when the section END record is reached.
			if ("END".equals(sectionLine)) {
				break;
			}
			
			// Parse the line into its coordinates.
			coordinates = parseCoordinates(sectionLine);
			
			// Add the current point to the list.
			polygonalBoundary.add(new Point2D.Double(coordinates[0], coordinates[1]));
		}
		
		// Convert the list into an GeoArea and return.
		return new GeoArea(polygonalBoundary);
	}
	
	/**
	 * Parses a coordinate line into its constituent double precision
	 * coordinates.
	 * 
	 * @param coordinateLine
	 *            The raw file line.
	 * @return A pair of coordinate values, first is longitude, second is
	 *         latitude.
	 * @throws PolygonReadingException 
	 */
	private double[] parseCoordinates(String coordinateLine) throws PolygonReadingException {
		String[] rawTokens;
		double[] results;
		int tokenCount;
		
		// Split the line into its sub strings separated by whitespace.
		rawTokens = coordinateLine.split("\\s");
		
		// Copy the non-zero tokens into a result array.
		tokenCount = 0;
		results = new double[2];
		for (int i = 0; i < rawTokens.length; i++) {
			if (rawTokens[i].length() > 0) {
				// Ensure we have no more than 2 coordinate values.
				if (tokenCount >= 2) {
					throw new PolygonReadingException(
						"A polygon coordinate line must contain 2 numbers, not (" + coordinateLine + ")."
					);
				}
				
				// Parse the token into a double precision number.
				try {
					results[tokenCount++] = Double.parseDouble(rawTokens[i]);
				} catch (NumberFormatException e) {
					throw new PolygonReadingException(
							"Unable to parse " + rawTokens[i] + " into a double precision number.");
				}
			}
		}
		
		// Ensure we found two tokens.
		if (tokenCount < 2) {
			throw new PolygonReadingException("Could not find two coordinates on line (" + coordinateLine + ").");
		}
		
		return results;
	}

	/**
	 * This method must only be called after {@link #loadPolygon()}.
	 * @return The name of the polygon as stated in the file-header.
	 */
	public String getPolygonName() {
		return myPolygonName;
	}
}
