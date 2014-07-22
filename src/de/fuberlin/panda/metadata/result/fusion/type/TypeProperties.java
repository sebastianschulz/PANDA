package de.fuberlin.panda.metadata.result.fusion.type;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;

import newick.NewickParser;
import newick.NewickParser.TreeNode;

import org.apache.log4j.Logger;

import de.fuberlin.panda.api.APIHelper;
import de.fuberlin.panda.metadata.MetadataConnector;
import de.fuberlin.panda.metadata.result.fusion.helper.MatchingHelper;
import de.fuberlin.panda.metadata.result.fusion.type.tree.TypeTree;

/**
 * This class is responsible for parsing an {@code type.properties} file into
 * lists of {@code String} values representing the possible formats respectively
 * languages. 
 * 
 * @see #init(String)
 * @see #setFormats(String)
 * @see #setLanguages(String)
 * 
 * @author Sebastian Schulz
 * @since 06.03.2014
 */
public class TypeProperties {
	private static Logger logger = Logger.getLogger(MetadataConnector.class.getName());
	
	private static final String PROPERTIES_FILE_PATH = APIHelper.getWebContentDirPath() + "prefs\\type.properties";
	
	private static ArrayList<String> validFormats = new ArrayList<String>();
	private static ArrayList<String> validLanguages = new ArrayList<String>();
	
	private static TypeTree formatsTree = null;
	private static TypeTree languagesTree = null;
	
	public static void init() {
		init(PROPERTIES_FILE_PATH);
	}
	
	/**
	 * This method reads the properties file from the given {@code filePath} and calls 
	 * {@link #parsePropertyList(String))} and {@link #parsePropertyTree(String)}
	 * to convert the properties into the addicted list and to parse the trees.
	 */
	public static void init(String filePath) {
		Properties properties = new Properties();
		InputStream input = null;
		
		try {
			input = new FileInputStream(filePath);
			properties.load(input);
	 
			validFormats = parsePropertyList(properties.getProperty("format"));
			validLanguages = parsePropertyList(properties.getProperty("language"));
			
			formatsTree = parsePropertyTree(properties.getProperty("format_tree"));
			languagesTree = parsePropertyTree(properties.getProperty("language_tree"));
			logger.debug("--> Successfully read type properties file from: " + filePath);
		} catch (Throwable ex) {
			logger.error("Error while parsing type properties file from: " + filePath + "; " + ex.getMessage());
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					logger.error("Couldn't close the inputstream for reading type properties file: " 
							+ e.getMessage());
				}
			}
		}
	}

	/**
	 * This method parses a comma separated list of properties from the given {@code String} called
	 * {@code property} and returns a {@code ArrayList} containing all values.
	 * 
	 * @param property - a comma separated {@code String} representation all possible formats.
	 * @return propertyValues - an {@code ArrayList} of {@code String} objects.
	 */
	protected static ArrayList<String> parsePropertyList(String property) {
		ArrayList<String> propertyValues = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(property, ",");
		while (st.hasMoreElements()) {
			String currentToken = st.nextToken().trim().toLowerCase();
			if(!MatchingHelper.contains(propertyValues, currentToken)) {
				propertyValues.add(currentToken);	
			}
		}
		return propertyValues;
	}
	
	/**
	 * This method parses a tree from a given {@code String} called {@code property}. Therefore it
	 * uses the free to use newick parser published on 
	 * <a href='https://bitbucket.org/djiao/a-lightweight-javacc-newick-parser/src/736f5b694f94?at=default'>bitbucket</a>.
	 * 
	 * @param property - a {@code String} object which has to be in valid Newick Format 
	 * 	(check <a href='http://en.wikipedia.org/wiki/Newick_format'>Newick Format Wikipedia</a>).
	 * @throws Exception - in case something gone wrong while parsing.
	 */
	protected static TypeTree parsePropertyTree(String property) throws Exception {
		InputStream propertyIn = new ByteArrayInputStream(property.getBytes("UTF-8"));
		try {
			TreeNode parsedTree = new NewickParser(propertyIn).tree();
			return new TypeTree(parsedTree); 
		} catch (Throwable e) {
			throw e;
		}
	}

	public static ArrayList<String> getFormats() {
		return validFormats;
	}
	
	public static ArrayList<String> getLanguages() {
		return validLanguages;
	}

	public static TypeTree getFormatsTree() {
		return formatsTree;
	}
	
	public static TypeTree getLanguagesTree() {
		return languagesTree;
	}
	
	public static void reset() {
		validFormats.clear();
		validLanguages.clear();
		formatsTree = null;
		languagesTree = null;
	}
}
