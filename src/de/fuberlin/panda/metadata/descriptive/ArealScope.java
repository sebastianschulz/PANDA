package de.fuberlin.panda.metadata.descriptive;

import java.util.Vector;

/**
 * Simple Data class for the areal scope
 * 
 * @since 16.09.2013
 * @author Sebastian Schulz
 */
public class ArealScope {
	private String location = null;
	private Double longitude = null;
	private Double latitude = null;
	
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public boolean hasLocation() {
		return (location == null) ? false : true;
	}
	
	public Double getLongitude() {
		return longitude;
	}
	
	public void setLongitude(String longitude) {
		this.longitude = Double.parseDouble(longitude);
	}
	
	public Double getLatitude() {
		return latitude;
	}
	
	public void setLatitude(String latitude) {
		this.latitude = Double.parseDouble(latitude);
	}

	public boolean hasGeoCoords() {
		if ((longitude != null) && (latitude != null)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * This method returns the whole areal scope in a vector. The return 
	 * vector has as much elements as are set while parsing the metadata. 
	 * 
	 * 
	 * @return returnVector - a vector of {@code String[]} which represents all available
	 * 			information.
	 */
	@SuppressWarnings("unchecked")
	public Vector<String[]> getAttributes() {
		Vector<String[]> returnVector = new Vector<String[]>();
		
		if(location != null) {
			returnVector.add(new String[]{"Location", getLocation()});
		} 
		
		if(latitude != null) {
			returnVector.add(new String[]{"Latitude", Double.toString(getLatitude())});
		}
		
		if (longitude != null) {
			returnVector.add(new String[]{"Longitude",  Double.toString(getLongitude())});
		}
		
		return  (Vector<String[]>) returnVector.clone();
	}
}
