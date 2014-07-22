package de.fuberlin.panda.metadata.descriptive;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Vector;

import javax.management.modelmbean.XMLParseException;

/**
 * Simple data class for the administrative scope.
 * 
 * @since 16.09.2013
 * @author Sebastian Schulz
 */
public class AdministrativeScope {
	private URI licenceUri = null;
	private URI rightsUri = null;
	private URI dutiesUri = null;
	
	public void setLicenseUri(String uri) throws XMLParseException {
		try {
			licenceUri = new URI(uri);
		} catch (URISyntaxException e) {
			throw new XMLParseException("The license URI syntax was wrong: " + e.getMessage());
		}
	}
	
	public URI getLicenceUri() {
		return licenceUri;
	}
	
	public void setRightsUri(String uri) throws XMLParseException {
		try {
			rightsUri = new URI(uri);
		} catch (URISyntaxException e) {
			throw new XMLParseException("The rights URI syntax was wrong: " + e.getMessage());
		}
	}
	
	public URI getRightsUri() {
		return rightsUri;
	}
	
	public void setDutiesUri(String uri) throws XMLParseException {
		try {
			dutiesUri = new URI(uri);
		} catch (URISyntaxException e) {
			throw new XMLParseException("The duties URI syntax was wrong: " + e.getMessage());
		}
	}
	
	public URI getDutiesUri() {
		return dutiesUri;
	}

	/**
	 * This method returns the whole administrative scope in a vector. The return 
	 * vector has as much elements as are set while parsing the metadata. 
	 * 
	 * 
	 * @return returnVector - a vector of {@code String[]} which represents all available
	 * 			information.
	 */
	@SuppressWarnings("unchecked")
	public Vector<String[]> getAttributes() {
		Vector<String[]> returnVector = new Vector<String[]>();
		
		if(licenceUri != null) {
			returnVector.add(new String[]{"License",getLicenceUri().toString()});
		} 
		
		if(rightsUri != null) {
			returnVector.add(new String[]{"Rights",getRightsUri().toString()});
		}
		
		if (dutiesUri != null) {
			returnVector.add(new String[]{"Duties",getDutiesUri().toString()});
		}
		
		return (Vector<String[]>) returnVector.clone();
	}
	
}
