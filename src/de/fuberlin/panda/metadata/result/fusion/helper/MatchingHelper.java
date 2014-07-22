package de.fuberlin.panda.metadata.result.fusion.helper;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import de.fuberlin.panda.metadata.MetadataConnector;

/**
 * This class is a helper which contains two static methods to get the end of an given
 * {@code URI} respectively the {@code subUri} which is every {@code char} from the 
 * beginning to the last occurrence of "/". It also provides a method to check if a 
 * {@code List} of {@code String} objects contains a string.
 * 
 * @see #getUriEnding(URI)
 * @see #getSubUri(URI)
 * @see #contains(List, String)
 * 
 * @author Sebastian Schulz
 * @since 30.03.2014
 */
public class MatchingHelper {
	private static Logger logger = Logger.getLogger(MetadataConnector.class.getName());
	
	private static final Pattern ENDING_PATTERN = Pattern.compile(".*/([^/]*)($)");
	private static final Pattern SUB_URI_PATTERN = Pattern.compile("(^.*)/[^/]*$");

	/**
	 * This method tries to match the given {@code URI} object  against a {@code Pattern} 
	 * called {@code ENDING_PATTERN} and so returns the last part of the given {@code URI}.
	 * 
	 * @param uri - a {@code URI} object.
	 * @return uriEnding - a {@code String} object representing the last part of the {@code URI}.
	 */
	public static String getUriEnding(URI uri) {
		String uriEnding = null;
		if (uri != null) {
		    Matcher m = ENDING_PATTERN.matcher(uri.toString());
		    if (m.find()) {
				uriEnding = m.group(1);
				logger.debug("Found matching for pattern [" + ENDING_PATTERN.toString() + "] on URI: "
						+ uri + "; ending detected as '" + uriEnding + "'");
			} else {
				logger.warn("No matching for pattern [" + ENDING_PATTERN.toString() + "] on URI: "
						+ uri);
			}
		}
		return uriEnding;
	}
	
	/**
	 * This method tries to match the given {@code URI} object  against a {@code Pattern} 
	 * called {@code ENDING_PATTERN} and so returns the first part of the given {@code URI}. 
	 * This means the whole URI until the last appearance of "/".
	 * 
	 * @param uri - a {@code URI} object.
	 * @return uriEnding - a {@code String} object representing the first part of the {@code URI}.
	 */
	public static String getSubUri(URI uri) {
		String subUri = null;
		if (uri != null) {
		    Matcher m = SUB_URI_PATTERN.matcher(uri.toString());
		    if (m.find()) {
				subUri = m.group(1);
				logger.debug("Found matching for pattern [" + SUB_URI_PATTERN.toString() + "] on URI: "
						+ uri + "; sub URI detected as '" + subUri + "'");
			} else {
				logger.warn("No matching for pattern [" + SUB_URI_PATTERN.toString() + "] on URI: "
						+ uri);
			}
		}
		return subUri;
	}
	
	/**
	 * Given a {@code List} of {@code String}s and another {@code String} this method
	 * checks if the list contains this string.
	 *  
	 * @param list - a {@code List} of {@code String} values.
	 * @param matching - a {@code String} value which is checked to be contained in the
	 * 	list.
	 * @return a {@code boolean} value.
	 */
	public static boolean contains(Collection<String> list, String matching) {
		return list.toString().matches(".*\\b" + matching + "\\b.*");
	}

	/**
	 * This method iterates the given list of {@code URI}s and creates a new 
	 * {@link NoMetadataObject} the minimize the output. 
	 * 
	 * @param list - a {@code List} of {@code URI} values.
	 * @return noMetadata - a {@code NoMetadataObject}.
	 */
	public static NoMetadataObject groupNoMetadataUris(List<URI> list) {
		NoMetadataObject noMetadata = new NoMetadataObject();
		
		if (list.size() > 1) {
			Collections.sort(list);
			
			String groupStart = null;
			String groupEnd = null;
			String groupSubUri = null;
			int groupSize = 1;
			
			for (URI uri : list) {
				String currentSubUri = getSubUri(uri);
				if (currentSubUri.equals(groupSubUri)) {
					groupEnd = uri.toString();
					groupSize++;
				} else {
					if (groupSubUri != null) {
						if (groupSize <= 1) {
							noMetadata.addSingleUri(groupStart);	
						} else {
							noMetadata.addUriGroup(groupStart, groupEnd);
						}
					}
					
					groupSubUri = currentSubUri;
					groupStart = uri.toString();
					groupEnd = uri.toString();
					groupSize = 1;
				}
			}
			
			//add the last object
			if (groupStart.equals(groupEnd)) {
				noMetadata.addSingleUri(groupStart);
			} else {
				noMetadata.addUriGroup(groupStart, groupEnd);
			}
		}
		
		
		
		
		
		
		
		return noMetadata;
	}
	
	
}
