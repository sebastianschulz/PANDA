package de.fuberlin.panda.metadata.result;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

import de.fuberlin.panda.api.APIHelper;
import de.fuberlin.panda.metadata.exceptions.MetadataNotFoundException;
import de.fuberlin.panda.metadata.exceptions.ProcessingUriException;
import de.fuberlin.panda.metadata.parsedMetadata.ParsedMetadata;
import de.fuberlin.panda.metadata.parsedMetadata.VirtuosoMetadata;
import de.fuberlin.panda.metadata.parsedMetadata.XMLMetadata;

/**
 * This abstract class is responsible for creating a result for the metadata query. It parses
 * the metadata addicted to the given source with calling {@link #parseXMLMetadata(String)} 
 * respectively {@link #parseVirtuosoMetadata(String)} and adds the metadata to the 
 * {@code metadataEntries} list. Afterwards the RDF model is created and finally the 
 * {@code String} representation of the created model returned.<br> The actual building of the
 * model is done by the abstract {@link #createMetadataProperties()} method which has to be 
 * implemented by the subclasess.
 * 
 * @see #parseXMLMetadata(String)
 * @see #parseVirtuosoMetadata(String)
 * @see #createRdfModel()
 * @see #createMetadataProperties()
 * @see #addDataProperty(ParsedMetadata)
 * @see #toRdfXml()
 * 
 * @since 11.09.2013
 * @author Sebastian Schulz
 *
 */
public abstract class MetadataResult {
//	private final String CSS_PATH = APIHelper.getWebContentDirPath() + "WEB-INF\\style.css";
	private final String XML_START_TAG = "<?xml version=\"1.0\"  encoding=\"UTF-8\"?>\r\n";
//			+ "<?xml-stylesheet type=\"text/css\" href=\"style.css\" ?>";
	
	public static final String METADATA_NS = "http://www.mi.fu-berlin.de/panda#";
	public static final String METADATA_NS_NAME = "panda";
	
	protected final Resource METADATA_NODETYPE = ResourceFactory.createResource(METADATA_NS + "Metadata");
	
	protected Model metadataModel;
	protected Resource metadataResource;
	protected List<ParsedMetadata> metadataEntries = new ArrayList<>();
	
	/**
	 * This method is responsible for parsing the metadata from a XML file and adding it to the
	 * list of {@code metadataEntries}. Therefore it creates a new {@link XMLMetadata} object.
	 * 
	 * @param uri - the corresponding metadata is searched for.
	 * @throws MetadataNotFoundException in case there was no metadata found in the XML file for this given URI. 
	 * @throws ProcessingUriException in case there went something wrong while creating the {@link XMLMetadata}
	 * object.
	 */
	public void parseXMLMetadata(String uri) throws MetadataNotFoundException, ProcessingUriException {
		XMLMetadata singleMetadata = new XMLMetadata();
		singleMetadata.setUri(uri);
		singleMetadata.parseMetadata();
		metadataEntries.add(singleMetadata);
	}
	
	/**
	 * This method is responsible for parsing the metadata from the virtuoso triple store and adding it to the
	 * list of {@code metadataEntries}. Therefore it creates a new {@link VirtuosoMetadata} object.
	 * 
	 * @param uri - the corresponding metadata is searched for.
	 * @throws MetadataNotFoundException in case there was no metadata found in the XML file for this given URI. 
	 * @throws ProcessingUriException in case there went something wrong while creating the 
	 * {@link VirtuosoMetadata} object.
	 */
	public void parseVirtuosoMetadata(String uri) throws MetadataNotFoundException, ProcessingUriException {
		VirtuosoMetadata singleMetadata = new VirtuosoMetadata();
		singleMetadata.setUri(uri);
		singleMetadata.parseMetadata();
		metadataEntries.add(singleMetadata);
	}

	/**
	 * This method creates the {@code metadataModel} and calls the 
	 * abstract {@link #createMetadataProperties()} to fill the basic model with
	 * properties.
	 */
	public void createRdfModel() {
		metadataModel = ModelFactory.createDefaultModel();
		metadataModel.setNsPrefix(METADATA_NS_NAME, METADATA_NS);
		createMetadataProperties();
	}
	
	/**
	 * Abstract method for creating the properties. 
	 */
	protected abstract void createMetadataProperties();
	
	/**
	 * This method creates a {@code panda:Data} property and adds it to the 
	 * {@code metadataResource} of the {@code metadataModel}.
	 * 
	 * @param singleMetadata - the {@link ParsedMetadata} object from the object list
	 */
	protected void addDataProperty(ParsedMetadata singleMetadata) {
		Property dataProperty = ResourceFactory.createProperty(METADATA_NS + "Data");
		metadataResource.addProperty(dataProperty, singleMetadata.getUri());
	}
	
	public Model getMetadataModel() {
		return metadataModel;
	}
	
	/**
	 * This method creates the {@code metadataModel}s output. It writes the 
	 * {@code XML_START_TAG} and the model as RDF/XML to an output stream.
	 * 
	 * @return the RDF/XML as a String object.
	 * @throws IOException in case something went wrong while openig or writing the stream.
	 */
	public String toRdfXml() throws IOException {
		ByteArrayOutputStream rdfXmlStream = new ByteArrayOutputStream(); 
		rdfXmlStream.write(XML_START_TAG.getBytes());
		metadataModel.write(rdfXmlStream, "RDF/XML-ABBREV");
		rdfXmlStream.close();
		return rdfXmlStream.toString();
	}
	
}
