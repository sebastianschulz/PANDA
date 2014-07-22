package de.fuberlin.panda.metadata;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import de.fuberlin.panda.metadata.config.MetadataSourceType;
import de.fuberlin.panda.metadata.config.RDFInput;
import de.fuberlin.panda.metadata.exceptions.MetadataNotFoundException;
import de.fuberlin.panda.metadata.exceptions.ProcessingUriException;
import de.fuberlin.panda.metadata.result.ListResult;
import de.fuberlin.panda.metadata.result.MetadataResult;
import de.fuberlin.panda.metadata.result.fusion.FusionResult;
import de.fuberlin.panda.metadata.result.fusion.type.TypeProperties;

/**
 * The {@code MetadataConnector} is the main entrance to the metadata fusion. It's the API
 * which calls the constructor {@link #MetadataConnector(MetadataSourceType, List, boolean)}
 * and the method {@link #getMetadata()} to retrieve the metadata information of the given 
 * URIs.
 * 
 * @see #MetadataConnector(MetadataSourceType, List, boolean)
 * @see #getMetadata()
 * 
 * @author Sebastian Schulz
 * @since 05.09.2013
 */
public class MetadataConnector {
	private static Logger logger = Logger.getLogger(MetadataConnector.class.getName());
	
	protected MetadataSourceType sourceType;
	protected List<String> uris;
	protected boolean fusion;
	
	/**
	 * The constructor just adds the given information to protected fields and calls the 
	 * {@link RDFInput#initPredicates()} to initialize the matching possibility for later
	 * processing.
	 * 
	 * @param sourceType - a {@code MetadataSourceType} which represents the source the metadata
	 * 	should be parsed from.
	 * @param uris - a {@code List} of URIs which should be processed.
	 * @param fusion - a {@code boolean} value which describes whether a fusion should be done
	 * 	or not.
	 */
	public MetadataConnector(MetadataSourceType sourceType, List<String> uris, boolean fusion) {
		this.sourceType = sourceType;
		this.uris = uris;
		this.fusion = fusion;
		createInitialLoggingOutput(sourceType, uris, fusion);
		initProperties();
	}
	
	/**
	 * This method creates a logging info which represents the key attributes given by the 
	 * parameters.
	 * 
	 * @param sourceType - a {@code MetadataSourceType} which represents the source the metadata
	 * 	should be parsed from.
	 * @param uris - a {@code List} of URIs which should be processed.
	 * @param fusion - a {@code boolean} value which describes whether a fusion should be done
	 * 	or not.
	 */
	private void createInitialLoggingOutput(MetadataSourceType sourceType, List<String> uris, boolean fusion) {
		logger.info("Started new MetadataConnector parsing the following URIs from '" 
				+ sourceType.toString() + "':");
		for (String uri : uris) {
			logger.info("\t " + uri);
		}
		logger.info("The fusion flag was set to '" + fusion + "'");
		logger.info("--------------------------------------------------------------------------");
	}

	/**
	 * This method is responsible for loading the initial configuration before 
	 * the processing begins.
	 */
	private void initProperties() {
		logger.debug("Starting the initialization of the properties");
		RDFInput.initPredicates();
		TypeProperties.init();
		logger.debug("Property initialization done");
	}
	
	/**
	 * This method is the main control loop of the program. At the beginning it decides if a fusion
	 * should be done or not. Afterwards for every URI the metadata information is parsed from the
	 * source regarding the {@code sourceType}. Finally the metadata model is created and the RDF/XML
	 * representation is returned a s {@code String} value. 
	 * 
	 * @return a {@code String} value which represents the RDF/XML output of the metada model.
	 * @throws MetadataNotFoundException - in case an URI has no metadata which could be parsed from 
	 * 	the source.
	 * @throws ProcessingUriException - in case the metadata couldn't be parsed from the source.
	 * @throws IOException - in case something went wrong with the output stream. 
	 */
 	public String getMetadata() throws MetadataNotFoundException, ProcessingUriException, IOException {
		MetadataResult metadata;
		if (fusion && uris.size() > 4) {
			metadata = new FusionResult();
		} else {
			metadata = new ListResult();
		}
		
		for (String metadataUri : uris) {
			try {
				switch (sourceType) {
				case VIRTUOSO:
						metadata.parseVirtuosoMetadata(metadataUri);
						logger.info("--> Successfully parsed metadata for '" 
								+ metadataUri + "' from virtuoso triple store!" );
						break;
				default: //XML
						metadata.parseXMLMetadata(metadataUri);
						logger.info("--> Successfully parsed metadata for '" 
								+ metadataUri + "' from xml file!" );
						break;
				}
			} catch (MetadataNotFoundException e) {
				logger.error("No metadata information found for URI: " + e.getMessage());
				throw e;
			} catch (ProcessingUriException e){
				logger.error("Error while processing URI: " + e.getMessage());
				throw e;
			}
		}
		metadata.createRdfModel();
		return metadata.toRdfXml();
	}

}
