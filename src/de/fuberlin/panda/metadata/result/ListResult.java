package de.fuberlin.panda.metadata.result;

import java.util.Collections;
import java.util.Vector;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

import de.fuberlin.panda.metadata.operational.Operation;
import de.fuberlin.panda.metadata.parsedMetadata.ParsedMetadata;

/**
 * This class extends the abstract {@link MetadataResult} class. It overrides the 
 * {@link #createMetadataProperties()} method to add all {@code metdataEntries} 
 * to the {@code metadataModel}.
 * 
 * @see #createMetadataProperties()
 * @see #addDataProperty(ParsedMetadata)
 * @see #addOperationsProperties(ParsedMetadata)
 * @see #addAdministrativeScopeProperties(ParsedMetadata)
 * @see #addArealScopeProperties(ParsedMetadata)
 * @see #addTemporalScopeProperties(ParsedMetadata)
 * @see #addTypeProperties(ParsedMetadata)
 * 
 * @author Sebastian Schulz
 * @since 17.12.2013
 */
public class ListResult extends MetadataResult {
	
	/**
	 * This method iterates over all {@code metadataEntries} and adds their 
	 * information as separate {@code panda:Metadata} subjects to the 
	 * {@code metadataModel}. For that reason it calls the methods responsible
	 * to add the properties.
	 * 
	 * @see #addDataProperty(ParsedMetadata)
	 * @see #addOperationsProperties(ParsedMetadata)
	 * @see #addAdministrativeScopeProperties(ParsedMetadata)
	 * @see #addArealScopeProperties(ParsedMetadata)
	 * @see #addTemporalScopeProperties(ParsedMetadata)
	 * @see #addTypeProperties(ParsedMetadata)
	 */
	@Override
	protected void createMetadataProperties() {
		for (ParsedMetadata singleMetadata : metadataEntries) {
			metadataResource = metadataModel.createResource(METADATA_NS + 
					singleMetadata.getUri(), METADATA_NODETYPE);
			addDataProperty(singleMetadata);
			addOperationsProperties(singleMetadata);
			addAdministrativeScopeProperties(singleMetadata);
			addArealScopeProperties(singleMetadata);
			addTemporalScopeProperties(singleMetadata);
			addTypeProperties(singleMetadata);
		}
	}

	/**
	 * This method creates a property for every operation which has been parsed and adds 
	 * it to the {@code metadataResource} of the {@code metadataModel}.
	 *  
	 * @param singleMetadata - the {@link ParsedMetadata} object from the object list
	 */
	private void addOperationsProperties(ParsedMetadata singleMetadata) {
		Vector<Operation> operationVector = singleMetadata.getOperations().getAttributes();
		Collections.reverse(operationVector);
		
		for (Operation operation : operationVector) {
			Property operationsProperty = ResourceFactory.createProperty(METADATA_NS 
					+ operation.getType().toString());
			metadataResource.addProperty(operationsProperty, operation.getTimestampString());
		}
	}
	
	/**
	 * This method creates a {@code rdf:resource} property for every administrative scope
	 * entry which has been parsed and adds it to the {@code metadataResource} of the 
	 * {@code metadataModel}.
	 * 
	 * @param singleMetadata - the {@link ParsedMetadata} object from the object list
	 */
	private void addAdministrativeScopeProperties(ParsedMetadata singleMetadata) {
		Vector<String[]> administrativeScopeVector = singleMetadata.getAdministrativeScope().getAttributes();
		Collections.reverse(administrativeScopeVector);
		
		for (String[] asEntry : administrativeScopeVector) {
			Property administrativeProperty = ResourceFactory.createProperty(METADATA_NS + asEntry[0]);
			Resource asEntryResource = metadataModel.createResource(asEntry[1]);
			metadataModel.add(metadataResource, administrativeProperty, asEntryResource);
		}
	}
	
	/**
	 * This method creates a property for every areal scope entry which has been parsed and 
	 * adds it to the {@code metadataResource} of the {@code metadataModel}.
	 * 
	 * @param singleMetadata - the {@link ParsedMetadata} object from the object list
	 */
	private void addArealScopeProperties(ParsedMetadata singleMetadata) {
		Vector<String[]> arealScopeVector = singleMetadata.getArealScope().getAttributes();
		Collections.reverse(arealScopeVector);
		
		for (String[] asEntry : arealScopeVector) {
			Property arealProperty = ResourceFactory.createProperty(METADATA_NS + asEntry[0]);
			metadataResource.addProperty(arealProperty, asEntry[1]);
		}
	}
	
	/**
	 * This method creates a {@code panda:ExpirationDate} property and adds it to the 
	 * {@code metadataResource} of the {@code metadataModel}.
	 * 
	 * @param singleMetadata - the {@link ParsedMetadata} object from the object list
	 */
	private void addTemporalScopeProperties(ParsedMetadata singleMetadata) {
		String expirationDateString = singleMetadata.getTemporalScope().getExpirationDateString();
		if (expirationDateString != null) {
			Property temporalProperty = ResourceFactory.createProperty(METADATA_NS + "CreationDate");
			metadataResource.addProperty(temporalProperty, expirationDateString);
		}
	}
	
	/**
	 * This method creates a property for every type entry which has been parsed and 
	 * adds it to the {@code metadataResource} of the {@code metadataModel}.
	 * 
	 * @param singleMetadata - the {@link ParsedMetadata} object from the object list
	 */
	private void addTypeProperties(ParsedMetadata singleMetadata) {
		Vector<String[]> typeVector = singleMetadata.getType().getAttributes();
		Collections.reverse(typeVector);
		
		for (String[] typeEntry : typeVector) {
			Property typeProperty = ResourceFactory.createProperty(METADATA_NS + typeEntry[0]);
			metadataResource.addProperty(typeProperty, typeEntry[1]);
		}
		typeVector.clear();
	}

	
}
