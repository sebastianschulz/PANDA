package de.fuberlin.panda.metadata.result.fusion.type;

import org.apache.log4j.Logger;

import de.fuberlin.panda.metadata.MetadataConnector;
import de.fuberlin.panda.metadata.result.fusion.type.tree.TypeNode;
import de.fuberlin.panda.metadata.result.fusion.type.tree.TypeTree;

/**
 * This class represents a group of similiar objects described by their common
 * URI. Assuming that metadata objects with similar URI are covered by the same
 * type and language the sugroup is represented by a common {@code String} value
 * for the {@code format} and the {@code language}. The class provides all 
 * functionalities to create and update such a group.
 * 
 * @see #add(String, String)
 * @see #updateFormat(String)
 * @see #updateLanguage(String)
 * 
 * @author Sebastian Schulz
 * @since 30.03.2014
 */
public class TypeSubgroup {
	private static Logger logger = Logger.getLogger(MetadataConnector.class.getName());
	
	private final TypeTree formatTree = TypeProperties.getFormatsTree();
	private final TypeTree languageTree = TypeProperties.getLanguagesTree();
	
	private String uri;
	private int members = 0;
	private String format = null;
	private String language = null;
	
	public TypeSubgroup(String uri) {
		this.uri = uri;
	}

	/**
	 * This method is responsible for updating the format and the 
	 * language of the subgroup. It calls {@link #updateFormat(String)} 
	 * and {@link #updateLanguage(String)} to perform the updates if needed.
	 * 
	 * @param format - a {@code String} which represents the format from the 
	 *  former parsed {@code ParsedMetadata} object.
	 * @param language - a {@code String} which represents the language from the 
	 *  former parsed {@code ParsedMetadata} object.
	 */
	public void add(String format, String language) {
		if (format != null) {
			updateFormat(format);
		} 
		
		if (language != null) {
			updateLanguage(language);
		}
		
		members++;
		logger.debug("Increased members of supgroup '" + uri + "' to " + members);
	}
	
	/**
	 * This method updates the {@code format} of the current subgroup. Therfore it checks
	 * the given {@code String} is not already covered by the representing {@code format}.
	 * If this is not the case the calling of {@link TypeTree#getNextParent(String, String)}
	 * retrieves the new format representation for this subgroup.
	 * 
	 * @param updateFormat - a {@code String} value which represents the format that
	 * 	should be checked to be the new format representation for this subgroup.
	 */
	private void updateFormat(String updateFormat) {
		if (format == null) {
			format = updateFormat;
		} else if (!format.equals(updateFormat)) {
			TypeNode currentFormatNode = formatTree.get(format);
			TypeNode updateFormatNode = formatTree.get(updateFormat);
			
			if (!currentFormatNode.hasInSubtree(updateFormatNode)) {
				format = formatTree.getNextParent(format, updateFormat);
				logger.debug("Updated format for subgroup '" + uri + "' to " + format);
			}
		}
	}

	/**
	 * This method updates the {@code language} of the current subgroup. Therfore it checks
	 * the given {@code String} is not already covered by the representing {@code language}.
	 * If this is not the case the calling of {@link TypeTree#getNextParent(String, String)}
	 * retrieves the new language representation for this subgroup.
	 * 
	 * @param updateLanguage - a {@code String} value which represents the language that
	 * 	should be checked to be the new language representation for this subgroup.
	 */
	private void updateLanguage(String updateLanguage) {
		if (language == null) {
			language = updateLanguage;
		} else if (!language.equals(updateLanguage)) {
			TypeNode currentLangaugeNode = languageTree.get(language);
			TypeNode updateLanguageNode = languageTree.get(updateLanguage);
			
			if (!currentLangaugeNode.hasInSubtree(updateLanguageNode)) {
				language = languageTree.getNextParent(language, updateLanguage);
				logger.debug("Updated language for subgroup '" + uri + "' to " + language);
			}
		}
	}
	
	public String getUri() {
		return uri;
	}
	
	public int getMembers() {
		return members;
	}

	public String getFormat() {
		return format;
	}
	
	public String getLanguage() {
		return language;
	}

	
}
