package de.fuberlin.panda.metadata.parsedMetadata;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.management.modelmbean.XMLParseException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.fuberlin.panda.metadata.config.MetadataType;
import de.fuberlin.panda.metadata.config.predicates.AdministrativePredicates;
import de.fuberlin.panda.metadata.config.predicates.ArealPredicates;
import de.fuberlin.panda.metadata.config.predicates.OperationalPredicates;
import de.fuberlin.panda.metadata.config.predicates.TemporalPredicates;
import de.fuberlin.panda.metadata.config.predicates.TypePredicates;
import de.fuberlin.panda.metadata.descriptive.AdministrativeScope;
import de.fuberlin.panda.metadata.descriptive.ArealScope;
import de.fuberlin.panda.metadata.descriptive.SemanticDescription;
import de.fuberlin.panda.metadata.descriptive.TemporalScope;
import de.fuberlin.panda.metadata.descriptive.Type;
import de.fuberlin.panda.metadata.exceptions.MetadataNotFoundException;
import de.fuberlin.panda.metadata.exceptions.ProcessingUriException;
import de.fuberlin.panda.metadata.operational.Operations;

/**
 * This class extends the abstract class {@link ParsedMetadata}. It searches in 
 * a XML File (which path has to be set in the xmlFilePath field) for a given URI
 * and parses all relevant information concerning this URI in the systems data 
 * classes.
 * 
 * @see #parseMetadata()
 * @see #parseMetadataNode(NodeList)
 * @see #checkForUri()
 * @see #checkNodeNameForDataNode(Node)
 * @see #getNextRelevantNode()
 * @see #checkForProcessingRelevantComment(Node)
 * @see #processNextMetadataSequence(String)
 * @see #processOperationalSequence()
 * @see #getCharacterData(Node)
 * @see #processAdministrativeScopeSequence()
 * @see #processArealScopeSequence()
 * @see #processTemporalScopeSequence()
 * @see #processTypeSequence()
 * @see #processSemanticSequence()
 * 
 * @since 06.09.2013
 * @author Sebastian Schulz
 */
public class XMLMetadata extends ParsedMetadata {
	private String xmlFilePath= "C:/Users/Sebastian Schulz/workspace/masterthesis/PANDA/WebContent/testData/MetadataExample2.xml";
	
	private NodeList metadataNodeList;
	private int metadataNodeIndex;
	
	/**
	 * This overridden method creates a document builder on the XML file which was set in the 
	 * {@code xmlFilePath} field. Afterwards it checks if a root node could be found and then
	 * it calls the {@link #parseMetadataNode(NodeList)} method for every {@code panda:Metadata}
	 * node which is included in the document. 
	 * 
	 * @throws ParserConfigurationException if there was a problem while setting up the parser
	 * @throws IOException if the file couldn't be opened
	 * @throws SAXException if the document builded couldn't be set up
	 * @throws XMLParseException if the document doesn't have a root element 
	 */
	@Override
	public void parseMetadata() throws MetadataNotFoundException, ProcessingUriException {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;
			db = dbf.newDocumentBuilder();
			Document doc = db.parse(new File(xmlFilePath));
		
			if(doc.getDocumentElement() == null) {
				throw new XMLParseException("Wrong document structure. Couldn't find root element"
						+ " while parsing the xml input file: " + xmlFilePath);
			} else {
				NodeList nodeList = doc.getDocumentElement().getChildNodes();
				for (int i = 0; i < nodeList.getLength(); i++) {
					Node tmpNode = nodeList.item(i);
					if(tmpNode.getNodeName().equals("panda:Metadata")) {
						parseMetadataNode(tmpNode.getChildNodes());
					}
				}
			}
		} catch (ParserConfigurationException | SAXException | IOException | 
				XMLParseException | URISyntaxException e) {
			throw new ProcessingUriException(uri + e.toString());
		}
	}
	
	/**
	 * This method is responsible for the processing of a single {@code panda:Metadata} node.
	 * It calls {@link #checkForUri()} to check if the requested URI is contained in this list
	 * and calls {@link #processNextMetadataSequence(String)} to parse the metadata, if the
	 * URI is contained. 
	 * 
	 * @param childNodeList - a list of all child nodes of {@code panda:Metadata}
	 * @throws MetadataNotFoundException in case there exists no metadata for the given uri
	 * @throws XMLParseException in case their was a problem while parsing the document
	 * @throws URISyntaxException in case a URI couldn't be parsed because of invalid format
	 */
	protected void parseMetadataNode(NodeList childNodeList) throws MetadataNotFoundException, XMLParseException, URISyntaxException {
		metadataNodeList = childNodeList;
		boolean containsRequestedUri = checkForUri();
		
		if(!containsRequestedUri) {
			throw new MetadataNotFoundException(uri);
		} else {
			Node commentNode = getNextRelevantNode();
			String startComment = commentNode.getNodeValue().trim();
			processNextMetadataSequence(startComment);
		}
	}

	/**
	 * This method checks if the node list contains the requested URI and
	 * return a boolean value.
	 *  
	 * @return a boolean - true if the URI is included, otherwise false
	 */
	protected boolean checkForUri() {
		metadataNodeIndex = -1;
		Node tmpNode;
		
		//skip emtpy text nodes at the beginning of the list
		do {
			metadataNodeIndex++;
			tmpNode = metadataNodeList.item(metadataNodeIndex);
		} while(checkNodeNameForDataNode(tmpNode) == false);
		
		//check if XML file contains requested URI
		while (checkNodeNameForDataNode(tmpNode) == true) {
			Node uriAttributeNode = tmpNode.getAttributes().item(0);
			if(uriAttributeNode.getNodeValue().equals(uri)) {
				return true;
			}
			metadataNodeIndex++;
			tmpNode = metadataNodeList.item(metadataNodeIndex);
		}
		
		return false;
	}
	
	/**
	 * This method checks if a node is a "panda:Data" note and returns a boolean
	 * value.
	 * 
	 * @param node - a node of the node list
	 * @return a boolean - true, if the nodes name is "panda:Data", false otherwise
	 */
	protected boolean checkNodeNameForDataNode(Node node) {
		return node.getNodeName().equalsIgnoreCase("panda:Data");
	}
	
	/**
	 * This method gets the next comment node from the node list and 
	 * returns it's value as a string.
	 * 
	 * @return nodeValue - the proper comment
	 */
	protected Node getNextRelevantNode() {
		Node tmpNode;
		boolean isProcessingRelevantComment = false;
		do {
			metadataNodeIndex++;
			tmpNode = metadataNodeList.item(metadataNodeIndex);
			if(tmpNode == null) {
				return null;
			}
			isProcessingRelevantComment = checkForProcessingRelevantComment(tmpNode);
		} while(!(isProcessingRelevantComment) && !(tmpNode.getNodeName().startsWith("panda:")));
		
		return tmpNode;
	}
	
	/**
	 * This method checks if a comment node is relevant for further processing. In this
	 * case the string value matches to one of the values in {@link MetadataType}.
	 * 
	 * @param node - the current node which is examined
	 * @return a boolean - true if the comment matches, false otherwise
	 */
	protected boolean checkForProcessingRelevantComment(Node node) {
		if(node.getNodeType() == Node.COMMENT_NODE) {
			String nodeValue = node.getNodeValue().trim();
			for (MetadataType type : MetadataType.values()) {
				if(nodeValue.equals(type.toString())) {
					return true;
				}
			}
			return false;
		} else return false;
	}

	/**
	 * This method calls a method to process a sequence of the node
	 * list depending on the {@code startComment}.
	 * 
	 * @param startComment - A comment in the document which introduces the
	 * 		following metadata sequence
	 * @throws URISyntaxException 
	 */
	protected void processNextMetadataSequence(String startComment) throws XMLParseException {
		if(startComment.equalsIgnoreCase(MetadataType.OPERATIONAL.toString())) {
			processOperationalSequence();
		} else if(startComment.equals(MetadataType.ADMINISTRATIVE_SCOPE.toString())) {
			processAdministrativeScopeSequence();
		} else if(startComment.equals(MetadataType.AREAL_SCOPE.toString())) {
			processArealScopeSequence();
		} else if(startComment.equals(MetadataType.TEMPORAL_SCOPE.toString())) {
			processTemporalScopeSequence();
		} else if(startComment.equals(MetadataType.TYPE.toString())) {
			processTypeSequence();
		} else if(startComment.equals(MetadataType.SEMANTIC.toString())) {
			processSemanticSequence();
		} else {
			Node commentNode = getNextRelevantNode();
			startComment = commentNode.getNodeValue().trim();
			processNextMetadataSequence(startComment);
		}
	}

	/**
	 * This method is responsible to parse the operational metadata. It creates a new 
	 * {@link Operations} object and parses all found values from the document in 
	 * to objects operations list.
	 * 
	 * @throws XMLParseException in case the operational timestamp has a wrong format
	 */
	protected void processOperationalSequence() throws XMLParseException {
		Node node = getNextRelevantNode();
		
		if(!(node.getNodeType() == Node.COMMENT_NODE)) {	
			operations = new Operations();
			
			do {
				String nodeName = node.getNodeName();
				String nodeValue = getCharacterData(node);
				
				if(nodeName.endsWith(OperationalPredicates.CREATION.toString())) {
					operations.addOperation(OperationalPredicates.CREATION, nodeValue);
				} else if(nodeName.endsWith(OperationalPredicates.ACCESS.toString())) {
					operations.addOperation(OperationalPredicates.ACCESS, nodeValue);
				} else if(nodeName.endsWith(OperationalPredicates.DELETING.toString())) {
					operations.addOperation(OperationalPredicates.DELETING, nodeValue);
				}
				
				node = getNextRelevantNode();
				if(node == null) return;
			} while(!(node.getNodeType() == Node.COMMENT_NODE));
		}
		
		String startComment = node.getNodeValue().trim();
		processNextMetadataSequence(startComment);
	}
	
	/**
	 * This method parses the CDATA from a given XML node and returns it's value
	 * as a string.
	 * 
	 * @param node - the current node.
	 * @return a string of the CDATA of the given node and "?" if there was no CDATA
	 */
	protected String getCharacterData(Node node) {
		//workaround to parse character data
		Element element = (Element) node;
		node = element.getFirstChild();
		if (node instanceof CharacterData) {
	       CharacterData cd = (CharacterData) node;
	       return cd.getData();
	    }
		else return "?";
	}

	/**
	 * This method is responsible to parse the administrative scope sequence. It creates a new 
	 * {@link AdministrativeScope} object and parses all found values from the document in 
	 * to object data URI's.
	 * 
	 * @throws XMLParseException in case the URI has a wrong format and couldn't be parsed 
	 */
	protected void processAdministrativeScopeSequence() throws XMLParseException {
		Node node = getNextRelevantNode();
		
		if(!(node.getNodeType() == Node.COMMENT_NODE)) {	
			administrativeScope = new AdministrativeScope();
			
			do {
				String nodeName = node.getNodeName();
				String attributeValue = node.getAttributes().getNamedItem("rdf:resource").getNodeValue();
				
				if(nodeName.endsWith(AdministrativePredicates.LICENSE.toString())) {
					administrativeScope.setLicenseUri(attributeValue);
				} else if(nodeName.endsWith(AdministrativePredicates.RIGHTS.toString())) {
					administrativeScope.setRightsUri(attributeValue);
				} else if(nodeName.endsWith(AdministrativePredicates.DUTIES.toString())) {
					administrativeScope.setDutiesUri(attributeValue);
				}
				
				node = getNextRelevantNode();
				if(node == null) return;
			} while(!(node.getNodeType() == Node.COMMENT_NODE));
		}
		
		String startComment = node.getNodeValue().trim();
		processNextMetadataSequence(startComment);
	}

	/**
	 * This method is responsible to parse the areal scope sequence. It calls the
	 * {@link #getCharacterData(Node)} to get the String value from every areal data node 
	 * and parses it to the {@link ArealScope} object. 
	 * 
	 * @throws XMLParseException in case {@link #processNextMetadataSequence(String)} causes a exception
	 */
	protected void processArealScopeSequence() throws XMLParseException {
		Node node = getNextRelevantNode();

		if(!(node.getNodeType() == Node.COMMENT_NODE)) {	
			arealScope = new ArealScope();
			
			do {
				String nodeName = node.getNodeName();
				String nodeValue = getCharacterData(node);
				
				if(nodeName.endsWith(ArealPredicates.LATITUDE.toString())) {
					arealScope.setLatitude(nodeValue);
				} else if(nodeName.endsWith(ArealPredicates.LONGITUDE.toString())) {
					arealScope.setLongitude(nodeValue);
				} else if(nodeName.endsWith(ArealPredicates.LOCATION.toString())) {
					arealScope.setLocation(nodeValue);
				}
				
				node = getNextRelevantNode();
				if(node == null) return;
			} while(!(node.getNodeType() == Node.COMMENT_NODE));
		}
		
		String startComment = node.getNodeValue().trim();
		processNextMetadataSequence(startComment);
	}
	
	/**
	 * This method is responsible to parse the temporal scope sequence. It creates a new
	 * {@link TemporalScope} object and parses the expiration date from the document in the 
	 * object.
	 * 
	 * @throws XMLParseException in case the timestamp couldn't be parsed
	 */
	protected void processTemporalScopeSequence() throws XMLParseException {
		Node node = getNextRelevantNode();

		if(!(node.getNodeType() == Node.COMMENT_NODE)) {	
			temporalScope = new TemporalScope();
			
			do {
				String nodeName = node.getNodeName();
				String nodeValue = getCharacterData(node);
				
				if(nodeName.endsWith(TemporalPredicates.EXPIRATION_DATE.toString())) {
					temporalScope.setExpirationDate(nodeValue);
				} 
				
				node = getNextRelevantNode();
				if(node == null) return;
			} while(!(node.getNodeType() == Node.COMMENT_NODE));
		}
		
		String startComment = node.getNodeValue().trim();
		processNextMetadataSequence(startComment);
	}

	/**
	 * This method is responsible to parse the type sequence. It creates a new
	 * {@link Type} object and parses all found information in it.
	 * 
	 * @throws XMLParseException in case {@link #processNextMetadataSequence(String)} causes a exception
	 */
	protected void processTypeSequence() throws XMLParseException {
		Node node = getNextRelevantNode();

		if(!(node.getNodeType() == Node.COMMENT_NODE)) {	
			type = new Type();
			
			do {
				String nodeName = node.getNodeName();
				String nodeValue = getCharacterData(node);
				
				if(nodeName.endsWith(TypePredicates.FORMAT.toString())) {
					type.setFormat(nodeValue);
				} else if(nodeName.endsWith(TypePredicates.LANGUAGE.toString())) {
					type.setLanguage(nodeValue);
				}
				
				node = getNextRelevantNode();
				if(node == null) return;
			} while(!(node.getNodeType() == Node.COMMENT_NODE));
		}
		
		String startComment = node.getNodeValue().trim();
		processNextMetadataSequence(startComment);
	}

	/**
	 * This method is responsible to parse the all the remaining data nodes which represent
	 * further semantic metadata information. It creates a {@link SemanticDescription} object
	 * and adds all nodes. 
	 * 
	 * @throws XMLParseException in case 
	 */
	protected void processSemanticSequence() throws XMLParseException {
//		metadataNodeIndex++;
//		Node node = null;
//		Element element = null;
//		Boolean isProcessingRelevantComment = false;
//		
//		for (int i = metadataNodeIndex; metadataNodeIndex < metadataNodeList.getLength(); i++) {
//			node = metadataNodeList.item(i);
//			if(node.getNodeType() != Node.TEXT_NODE) {
//				element = (Element) node;
//				String test = element.toString();
//				test
//			}
//			
//			isProcessingRelevantComment = checkForProcessingRelevantComment(node);
//			if(isProcessingRelevantComment) {
//				metadataNodeIndex = i;
//				break;
//			}
//		}
		
		Node node = getNextRelevantNode();
		if(node == null) return;
		String startComment = node.getNodeValue().trim();
		processNextMetadataSequence(startComment);
	}

	public String getXmlFilePath() {
		return xmlFilePath;
	}

}
