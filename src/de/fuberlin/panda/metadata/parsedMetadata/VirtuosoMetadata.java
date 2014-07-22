package de.fuberlin.panda.metadata.parsedMetadata;

import javax.management.modelmbean.XMLParseException;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

import de.fuberlin.panda.metadata.MetadataConnector;
import de.fuberlin.panda.metadata.config.MetadataType;
import de.fuberlin.panda.metadata.config.RDFInput;
import de.fuberlin.panda.metadata.config.predicates.AdministrativePredicates;
import de.fuberlin.panda.metadata.config.predicates.ArealPredicates;
import de.fuberlin.panda.metadata.config.predicates.OperationalPredicates;
import de.fuberlin.panda.metadata.config.predicates.TemporalPredicates;
import de.fuberlin.panda.metadata.config.predicates.TypePredicates;
import de.fuberlin.panda.metadata.descriptive.AdministrativeScope;
import de.fuberlin.panda.metadata.descriptive.TemporalScope;
import de.fuberlin.panda.metadata.descriptive.Type;
import de.fuberlin.panda.metadata.exceptions.MetadataNotFoundException;
import de.fuberlin.panda.metadata.exceptions.ProcessingUriException;
import de.fuberlin.panda.metadata.operational.Operation;
import de.fuberlin.panda.metadata.operational.Operations;

/**
 * This class extends the abstract class {@link ParsedMetadata}. It searches in 
 * the Virtuoso Triple Store for a given URI and parses all relevant information 
 * concerning this URI in the systems data classes.
 *
 * @see #parseMetadata()
 * @see #executeQuery()
 * @see #prepareQuery()
 * @see #processSolution(QuerySolution)
 * @see #eliminatePandaPrefix(RDFNode)
 * @see #processOperationalMetadata(String, String)
 * @see #processAdministrativeMetadata(String, String)
 * @see #processArealMetadata(String, String)
 * @see #processTemporalMetadata(String, String)
 * @see #processTypeMetadata(String, String)
 * 
 * @since 01.11.2013
 * @author Sebastian Schulz
 *
 */
public class VirtuosoMetadata extends ParsedMetadata {
	private static Logger logger = Logger.getLogger(MetadataConnector.class.getName());
	
	private String virtuosoServerAdress = "http://localhost:8890";
	private String virtuosoGraphName = virtuosoServerAdress + "/DAV/home/PANDA/Metadata";
	private String pandaPrefix = "http://www.mi.fu-berlin.de/panda#";
	
	/**
	 * This overridden method calls the {@link #executeQuery()} method and iterates over the
	 * returned ResultSet to process the single rows with calling the 
	 * {@link #processSolution(QuerySolution)} method. If the ResultSet has no entries, a 
	 * Exception is thrown.
	 * 
	 * @throws MetadataNotFoundException if there are no results for the given URI
	 * @throws ProcessingUriException if there was a problem while processing the single objects
	 */
	@Override
	public void parseMetadata() throws MetadataNotFoundException, ProcessingUriException {
		ResultSet queryResults = executeQuery();
		
		if(queryResults == null) {
			throw new MetadataNotFoundException("No data recieved from Virtuoso Triple Store.");
		}
		
		int queryResultEntries = 0;
		while (queryResults.hasNext()) {
			queryResultEntries++;
			processSolution(queryResults.next());
		}
		
		if(queryResultEntries==0) {
			throw new MetadataNotFoundException(uri); 
		} 
	
	}

	/** 
	 * This method calls the {@linkplain #prepareQuery()} method to get the query string.
	 * Then it manages the query execution and returns its results.
	 * 
	 * @return a ResultSet which holds the queries results.
	 */
	private ResultSet executeQuery() {
		ResultSet queryResults = null;
		
		String queryString = prepareQuery();
		Query query = QueryFactory.create(queryString);
		QueryExecution qExe = QueryExecutionFactory.sparqlService(virtuosoServerAdress + "/sparql", query);
		
		try {
			queryResults = qExe.execSelect();
		} catch(Exception e) {
			logger.error("Unable to retrieve Metadata from Virtuoso Server on '"
					+ virtuosoServerAdress +"/sparql' with query: '" + queryString
					+ "; " + e.getMessage());
		} finally {
			qExe.close();
		}
		
       	return queryResults;
	}
	
	/**
	 * This method builds the actual query string which is responsible to get 
	 * all the RDF predicates and objects for the given URI from the graph 
	 * with {@code virtuosoGraphName} which is saved on the Virtuoso server.
	 * 
	 * @return queryString 
	 */
	private String prepareQuery() {
		String queryString = "PREFIX panda: <" + pandaPrefix + "> " 
				+ " select ?p ?o "
				+ " from <" + virtuosoGraphName + "> "
				+ " where {?uri panda:Uri \"" + uri +"\"."
				+ 		 "?uri ?p ?o }";
		return queryString;
	}
	
	/**
	 * This method filters the {@link MetadataType} of the predicate and calls the 
	 * a further process method according to its type. If the predicate isn't listed
	 * in the {@link RDFInput}s predicate hash map, nothing is processed.
	 * 
	 * @param solution - The QuerySoultion object which represents the current "row" in the
	 * 	queries result set.
	 */
	private void processSolution(QuerySolution solution) throws ProcessingUriException {
		String predicate = eliminatePandaPrefix(solution.get("?p"));
		String object = solution.get("?o").toString();
		MetadataType metadataType = RDFInput.getMetadataType(predicate);
		
		if(metadataType==null) {
			return;
		}
		
		try {
			switch (metadataType) {
			case OPERATIONAL:
				processOperationalMetadata(predicate, object);
				break;
			case ADMINISTRATIVE_SCOPE:
				processAdministrativeMetadata(predicate, object);
				break;
			case AREAL_SCOPE:
				processArealMetadata(predicate, object);
				break;
			case TEMPORAL_SCOPE:
				processTemporalMetadata(predicate, object);
				break;
			case TYPE:
				processTypeMetadata(predicate, object);
				break;
			default:
				break;
			}
		} catch(Exception e) {
			throw new ProcessingUriException(uri + ", " + e.toString());
		}
	}

	/**
	 * This method trims the RDFNode name and returns a string with the node name without
	 * it's panda prefix.
	 * 
	 * @param solutionNode - The RDFNode object from the solution (e.g. "?p" or "?o" node)
	 * @return a String with the node name without panda prefix
	 */
	private String eliminatePandaPrefix(RDFNode solutionNode) {
		String trimmedNodeName = solutionNode.toString().substring(pandaPrefix.length());
		return trimmedNodeName;
	}
	
	/**
	 * This method is called if a operational metadata entry has been detected,
	 * it adds the object's value as an {@link Operation} to the {@link Operations}
	 * object of the current {@link ParsedMetadata} object. 
	 * 
	 * @param predicate - the predicate as a string
	 * @param object - the object value as a string
	 * @throws XMLParseException in case the operational timestamp has a wrong format
	 */
	private void processOperationalMetadata(String predicate, String object) throws XMLParseException {
		if(predicate.equals(OperationalPredicates.CREATION.toString())) {
			operations.addOperation(OperationalPredicates.CREATION, object);
		} else if(predicate.equals(OperationalPredicates.ACCESS.toString())) {
			operations.addOperation(OperationalPredicates.ACCESS, object);
		} else if(predicate.equals(OperationalPredicates.DELETING.toString())) {
			operations.addOperation(OperationalPredicates.DELETING, object);
		}
	}

	/**
	 * This method is responsible for parsing the administrative scope information
	 * into the {@link AdministrativeScope} object of the current {@link ParsedMetadata} 
	 * object.
	 * 
	 * @param predicate - the predicate as a string
	 * @param object - the object value as a string
	 * @throws XMLParseException in case the URI has a wrong format and couldn't be parsed
	 */
	private void processAdministrativeMetadata(String predicate, String object) throws XMLParseException {
		if(predicate.equals(AdministrativePredicates.LICENSE.toString())) {
			administrativeScope.setLicenseUri(object);
		} else if(predicate.equals(AdministrativePredicates.RIGHTS.toString())) {
			administrativeScope.setRightsUri(object);
		} else if(predicate.equals(AdministrativePredicates.DUTIES.toString())) {
			administrativeScope.setDutiesUri(object);
		}
	}

	/**
	 * This method parses the areal scope information into the
	 * {@link AdministrativeScope} object of the current {@link ParsedMetadata} 
	 * object. 
	 * 
	 * @param predicate - the predicate as a string
	 * @param object - the object value as a string
	 */
	private void processArealMetadata(String predicate, String object) {
		if(predicate.endsWith(ArealPredicates.LATITUDE.toString())) {
			arealScope.setLatitude(object);
		} else if(predicate.endsWith(ArealPredicates.LONGITUDE.toString())) {
			arealScope.setLongitude(object);
		} else if(predicate.endsWith(ArealPredicates.LOCATION.toString())) {
			arealScope.setLocation(object);
		}
	}
	
	/**
	 * This method parses the temporal scope information into the
	 * {@link TemporalScope} object of the current {@link ParsedMetadata} 
	 * object. 
	 * 
	 * @param predicate - the predicate as a string
	 * @param object - the object value as a string
	 * @throws XMLParseException in case the timestamp couldn't be parsed
	 */
	private void processTemporalMetadata(String predicate, String object) throws XMLParseException {
		if(predicate.endsWith(TemporalPredicates.EXPIRATION_DATE.toString())) {
			temporalScope.setExpirationDate(object);
		} 
	}

	/**
	 * This method parses the type information into the
	 * {@link Type} object of the current {@link ParsedMetadata} 
	 * object. 
	 * 
	 * @param predicate - the predicate as a string
	 * @param object - the object value as a string
	 */
	private void processTypeMetadata(String predicate, String object) throws XMLParseException {
		if(predicate.endsWith(TypePredicates.FORMAT.toString())) {
			type.setFormat(object);
		} else if(predicate.endsWith(TypePredicates.LANGUAGE.toString())) {
			type.setLanguage(object);
		}
	}
	
}