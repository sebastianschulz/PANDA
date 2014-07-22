package de.fuberlin.panda.metadata.descriptive;

import java.util.ArrayList;
import java.util.Vector;

import javax.management.modelmbean.XMLParseException;

import de.fuberlin.panda.metadata.result.fusion.helper.MatchingHelper;
import de.fuberlin.panda.metadata.result.fusion.type.TypeProperties;

/**
 * Simple type data class.
 * 
 * @since 16.09.2013
 * @author Sebastian Schulz
 */
public class Type {
	private String language = null;
	private String format = null;
	
	public void setLanguage(String language) throws XMLParseException {
		setLanguage(language, TypeProperties.getLanguages());
	}
	
	public void setLanguage(String language, ArrayList<String> languages) throws XMLParseException {
		language = language.toLowerCase();
		if (MatchingHelper.contains(languages,language)) {
			this.language = language;
		} else {
			throw new XMLParseException("Couldn't parse the format because it wasn't defined in the "
					+ "/WebContent/prefs/type/type.properties file.");
		}
	}
	
	public String getLanguage() {
		return language;
	}
	
	public void setFormat(String format) throws XMLParseException {
		setFormat(format, TypeProperties.getFormats());
	}
	
	public void setFormat(String format, ArrayList<String> formats) throws XMLParseException {
		format = format.toLowerCase();
		if (MatchingHelper.contains(formats, format)) {
			this.format = format;
		} else {
			throw new XMLParseException("Couldn't parse the format because it wasn't defined in the "
					+ "/WebContent/prefs/type/type.properties file.");
		}
	}
	
	public String getFormat() {
		return format;
	}

	/**
	 * This method returns the whole type information in a vector. The return 
	 * vector has as much elements as are set while parsing the metadata. 
	 * 
	 * @return returnVector - a vector of {@code String[]} which represents all available
	 * 			information.
	 */
	@SuppressWarnings("unchecked")
	public Vector<String[]> getAttributes() {
		Vector<String[]> returnVector = new Vector<String[]>();
		
		if(language != null) {
			returnVector.add(new String[]{"Language", getLanguage()});
		} 
		
		if(format != null) {
			returnVector.add(new String[]{"Format", getFormat()});
		}
		
		return (Vector<String[]>) returnVector.clone();
	}
}
