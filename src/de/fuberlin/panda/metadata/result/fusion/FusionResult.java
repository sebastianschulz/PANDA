package de.fuberlin.panda.metadata.result.fusion;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

import de.fuberlin.panda.metadata.MetadataConnector;
import de.fuberlin.panda.metadata.parsedMetadata.ParsedMetadata;
import de.fuberlin.panda.metadata.result.MetadataResult;
import de.fuberlin.panda.metadata.result.fusion.administrative.AdministrativeFusion;
import de.fuberlin.panda.metadata.result.fusion.areal.ArealFusion;
import de.fuberlin.panda.metadata.result.fusion.temporal.TemporalFusion;
import de.fuberlin.panda.metadata.result.fusion.type.TypeFusion;


/**
 * This class extends the abstract {@link MetadataResult} class. It overrides the 
 * {@link #createMetadataProperties()} method to start the metadata fusion algorithms.
 * At the beginning the in memory fusion objects regarding the several scopes are
 * created. A loop iterates over all {@code metdataEntries} and adds them to the fusion
 * objects. Finally the fuse methods are called to create the result.
 * 
 * @see #createMetadataProperties()
 * @see #fuseTemporalMetadata(TemporalFusion)
 * @see #fuseArealMetadata(ArealFusion)
 * @see #fuseAdminMetadata(AdministrativeFusion)
 * @see #fuseTypeMetadata(TypeFusion)
 * 
 * @author Sebastian Schulz
 * @since 17.12.2013
 */
public class FusionResult extends MetadataResult {
	private static Logger logger = Logger.getLogger(MetadataConnector.class.getName());
	
	private final String FUSION_ID = "fusion" + System.currentTimeMillis();
	private HashMap<Integer, ParsedMetadata> metadataMap = new HashMap<>(); 
	
	private final Property TEMPORALSCOPE_PROPERTY = ResourceFactory.createProperty(METADATA_NS, "TemporalScope"); 
	private final Property AREALSCOPE_PROPERTY = ResourceFactory.createProperty(METADATA_NS, "ArealScope");
	private final Property ADMINSCOPE_PROPERTY = ResourceFactory.createProperty(METADATA_NS, "AdministrativeScope");
	private final Property TYPE_PROPERTY = ResourceFactory.createProperty(METADATA_NS, "TypeScope");
	
	public final static Property NO_METADATA_PROPERTY = ResourceFactory.createProperty(MetadataResult.METADATA_NS, "NoMetadata");
	
	/**
	 * This method iterates over the entries of the {@code metadataEntries} list.
	 * Each entry is added to the several fusion objects as well as to the 
	 * {@code metadataMap}. Finally the fusion methods are called to perform the 
	 * the fusion regarding the different scopes.
	 */
	@Override
	protected void createMetadataProperties() {		
		logger.debug("Started new fusion");
		metadataResource = metadataModel.createResource(METADATA_NS + "md_" 
				+ FUSION_ID, METADATA_NODETYPE);
		
		TemporalFusion temporalFusion = new TemporalFusion(metadataModel, FUSION_ID);
		ArealFusion arealFusion = new ArealFusion(metadataModel, FUSION_ID);
		AdministrativeFusion adminFusion = new AdministrativeFusion(metadataModel, FUSION_ID);
		TypeFusion typeFusion = new TypeFusion(metadataModel, FUSION_ID);
		
		int metadataIndex = 0;
		for (ParsedMetadata singleMetadata : metadataEntries) {
			addDataProperty(singleMetadata);
			
			temporalFusion.add(metadataIndex, singleMetadata);
			arealFusion.add(metadataIndex, singleMetadata);
			adminFusion.add(singleMetadata);
			typeFusion.add(singleMetadata);
			
			metadataMap.put(metadataIndex, singleMetadata);
			metadataIndex++;
			logger.debug("URI: "+ singleMetadata.getUri() + " added to fusion objects");
		}
		
		fuseTemporalMetadata(temporalFusion);
		fuseArealMetadata(arealFusion);
		fuseAdminMetadata(adminFusion);
		fuseTypeMetadata(typeFusion);
		logger.info("--> Successfully finished metadata fusion!");
	}

	/**
	 * This method is responsible for adding the temporal metadata to the 
	 * {@code metadataResource}. It calls {@link TemporalFusion#fuse()} to collect 
	 * the useful temporal metadata, gets the created resource and adds it to the
	 * {@code metadataResource}.
	 * 
	 * @param temporalFusion - the {@code TemporalFusion} object which already contains 
	 * 	the necessary metadata information for the fusion. 
	 */
	private void fuseTemporalMetadata(TemporalFusion temporalFusion) {
		temporalFusion.fuse(metadataMap);
		Resource temporalScope = temporalFusion.getResource();
		metadataResource.addProperty(TEMPORALSCOPE_PROPERTY, temporalScope);
	}
	
	/**
	 * This method is responsible for adding the areal metadata to the 
	 * {@code metadataResource}. It calls {@link ArealFusion#fuse()} to collect 
	 * the useful areal metadata, gets the created resource and adds it to the
	 * {@code metadataResource}.
	 * 
	 * @param arealFusion - the {@code ArealFusion} object which already contains 
	 * 	the necessary metadata information for the fusion. 
	 */
	private void fuseArealMetadata(ArealFusion arealFusion) {
		arealFusion.fuse(metadataMap);
		Resource arealScope = arealFusion.getResource();
		metadataResource.addProperty(AREALSCOPE_PROPERTY, arealScope);
	}
	
	/**
	 * This method is responsible for adding the administrative metadata to the 
	 * {@code metadataResource}. It calls {@link AdministrativeFusion#fuse()} to collect 
	 * the useful administrative metadata, gets the created resource and adds it to the
	 * {@code metadataResource}.
	 * 
	 * @param adminFusion - the {@code AdministrativeFusion} object which already contains 
	 * 	the necessary metadata information for the fusion. 
	 */
	private void fuseAdminMetadata(AdministrativeFusion adminFusion) {
		adminFusion.fuse();
		Resource adminScope = adminFusion.getResource();
		metadataResource.addProperty(ADMINSCOPE_PROPERTY, adminScope);
	}

	/**
	 * This method is responsible for adding the type metadata to the 
	 * {@code metadataResource}. It calls {@link TypeFusion#fuse()} to collect 
	 * the useful temporal metadata, gets the created resource and adds it to the
	 * {@code metadataResource}.
	 */
	private void fuseTypeMetadata(TypeFusion typeFusion) {
		typeFusion.fuse();
		Resource typeScope = typeFusion.getResource();
		metadataResource.addProperty(TYPE_PROPERTY, typeScope);
	}

}
