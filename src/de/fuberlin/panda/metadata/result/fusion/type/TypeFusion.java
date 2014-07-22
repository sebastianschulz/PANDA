package de.fuberlin.panda.metadata.result.fusion.type;

import java.net.URI;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

import de.fuberlin.panda.metadata.MetadataConnector;
import de.fuberlin.panda.metadata.parsedMetadata.ParsedMetadata;
import de.fuberlin.panda.metadata.result.MetadataResult;
import de.fuberlin.panda.metadata.result.fusion.FusionResult;
import de.fuberlin.panda.metadata.result.fusion.helper.MatchingHelper;
import de.fuberlin.panda.metadata.result.fusion.helper.NoMetadataObject;

/**
 * This class is responsible for fusing type metadata information. Therefore the metadata 
 * information corresponding to every URI are added to a subgroup which covers objects
 * with a similar URI. For every subgroup a common value for {@code format} and 
 * {@code language} are determined and finally appended to the {@code scopeResource}.
 * 
 * @see #TypeFusion(Model, String)
 * @see #add(ParsedMetadata)
 * @see #checkForSubgroup(String)
 * @see #fuse()
 * @see #createSubgroupProperty(TypeSubgroup)
 * @see #addNoTypeProperty(String)
 * 
 * @author Sebastian Schulz
 * @since 06.03.2014
 */
public class TypeFusion {
	private static Logger logger = Logger.getLogger(MetadataConnector.class.getName());
	
	//type fusion
	private ArrayList<TypeSubgroup> subgroups = new ArrayList<TypeSubgroup>(); 
	private ArrayList<URI> noTypeMetadata = new ArrayList<URI>();
	
	//resource building
	private final Resource TYPE_NODETYPE = ResourceFactory.createResource(MetadataResult.METADATA_NS + "Type");
	private final Property TYPE_PROPERTY = ResourceFactory.createProperty(MetadataResult.METADATA_NS, "Subgroup");
	
	private Model metadataModel;
	private Resource scopeResource;
	
	/**
	 * Public constructor which creates a new {@code Resource} for the {@code scopeResource}.
	 * 
	 * @param metadataModel - the current {@code Model} passed by the {@link FusionResult}.
	 * @param fusionId - a {@code String} which represents the current fusion identifier.
	 */
	public TypeFusion(Model metadataModel, String fusionId) {
		this.metadataModel = metadataModel;
		
		scopeResource = this.metadataModel.createResource(MetadataResult.METADATA_NS + "ty_" +
				fusionId, TYPE_NODETYPE);
	}
	
	/**
	 * This method adds the type metadata information to an existing {@code TypeSubgroup} or adds
	 * a new one to the {@code subgroups} {@code ArrayList}. Hence the {@link MatchingHelper#getSubUri(URI)}
	 * method is called to retrieve the first part of the URI of the given {@code ParsedMetadata}
	 * object. Notice that this method performs the actual fusion process in this case. 
	 * 
	 * @param singleMetadata - a {@code ParsedMetadata} object which contains the type information.
	 */
	public void add(ParsedMetadata singleMetadata) {
		String format = singleMetadata.getType().getFormat();
		String language = singleMetadata.getType().getLanguage();
		
		if ((format == null) && (language == null)) {
			noTypeMetadata.add(URI.create(singleMetadata.getUri()));
			logger.debug("No type metadata found for URI:" + singleMetadata.getUri());
		} else {
			URI metadataUri = URI.create(singleMetadata.getUri());
			String subUri = MatchingHelper.getSubUri(metadataUri);
			if(subUri != null) {
				int matchingSubgroupIndex = checkForSubgroup(subUri);
				if (matchingSubgroupIndex > -1) {
					subgroups.get(matchingSubgroupIndex).add(format, language);
					logger.debug("Added '" + singleMetadata.getUri() + "' to subgroup: " + subUri);
				} else {
					TypeSubgroup subgroup = new TypeSubgroup(subUri);
					subgroup.add(format, language);
					subgroups.add(subgroup);
					logger.debug("Created a new subgroup for: '" + subUri 
							+ "' with member URI: " + singleMetadata.getUri());
				}
			}
		}
	}
	
	/**
	 * This method iterates over the {@code ArrayList} of {@code subgroups}
	 * and checks if the given {@code subUri} is the same as the one stored
	 * in the subgroup. 
	 * 
	 * @param subUri - a {@code String} representation of the URI of the current
	 *  {@code ParsedMetadata} object.
	 * @return matchingIndex - an {@code int} value. The index of the first subgroup
	 *  which {@code sharedUri} equals to the given {@code subUri}, if no subgroup
	 *  matches '-1' is returned.
	 */
	private int checkForSubgroup(String subUri) {
		int matchingIndex = -1;
		for (TypeSubgroup subgroup : subgroups) {
			if (subgroup.getUri().equals(subUri)) {
				matchingIndex = subgroups.indexOf(subgroup);
				break;
			}
		}
		return matchingIndex;
	}
	
	/**
	 * This method calls the {@link #createSubgroupProperty(TypeSubgroup)} for every
	 * subgroup to create the type fusion resouce.
	 */
	public void fuse() {
		for (TypeSubgroup subgroup : subgroups) {
			createSubgroupProperty(subgroup);
		}
		
		NoMetadataObject noMetadata = MatchingHelper.groupNoMetadataUris(noTypeMetadata);
		addSingleNoTypeProperties(noMetadata);
		addGroupNoTypeProperties(noMetadata);
		logger.info("--> Successfully created Type Fusion Resource!");
	}
	

	/**
	 * This method adds the subgroup property to the {@code scopeResource}. If the subgroup
	 * has a {@code format} a formats property is added and if it has a defined {@code language} 
	 * a language a language property is appended.
	 * 
	 * @param subgroup - a {@code TypeSubgroup} object.
	 */
	private void createSubgroupProperty(TypeSubgroup subgroup) {	
		Resource subgroupResource = metadataModel.createResource(MetadataResult.METADATA_NS + 
				subgroup.getUri());
		Property typeProperty;
		
		if (subgroup.getFormat() != null) {
			typeProperty = ResourceFactory.createProperty(MetadataResult.METADATA_NS, "Format");
			subgroupResource.addProperty(typeProperty, subgroup.getFormat());
		}
		
		if (subgroup.getLanguage() != null) {
			typeProperty = ResourceFactory.createProperty(MetadataResult.METADATA_NS, "Language");
			subgroupResource.addProperty(typeProperty, subgroup.getLanguage());
		}
		
		scopeResource.addProperty(TYPE_PROPERTY, subgroupResource);
	}
	
	/**
	 * This method creates a {@code panda:NoMetadata} property to the 
	 * {@code scopeResource} and puts the "link" to the objects URI to it.
	 * 
	 * @param noMetadata - a {@code NoMetadataObject} all URI withou metadata.
	 */
	private void addSingleNoTypeProperties(NoMetadataObject noMetadata) {
		for (String uri : noMetadata.getSingleUris()) {
			Property typeProperty = ResourceFactory.createProperty(MetadataResult.METADATA_NS, "NoMetadata");
			Resource typeResource = metadataModel.createResource(MetadataResult.METADATA_NS + uri);
			scopeResource.addProperty(typeProperty, typeResource);
			logger.debug("Added noType Property for URI: " + uri);
		}
	}
	
	/**
	 * This method creates a subgroup for every group which has no metadata information.
	 * 
	 * @param noMetadata - a {@code NoMetadataObject} all URI withou metadata.
	 */
	private void addGroupNoTypeProperties(NoMetadataObject noMetadata) {
		int i = 1;
		for (String[] uris : noMetadata.getUriGroups()) {
			Resource noMetadataResource = metadataModel.createResource(MetadataResult.METADATA_NS + 
					"ty_noMetadata_" + i);
			
			Property noMetadataProperty = ResourceFactory.createProperty(MetadataResult.METADATA_NS,
					"NoMetadataFrom");
			noMetadataResource.addProperty(noMetadataProperty, uris[0]);
			
			noMetadataProperty = ResourceFactory.createProperty(MetadataResult.METADATA_NS,
					"NoMetadataTo");
			noMetadataResource.addProperty(noMetadataProperty, uris[1]);
			
			scopeResource.addProperty(FusionResult.NO_METADATA_PROPERTY, noMetadataResource);
			i++;
		}
	}
	
	
	public Resource getResource() {
		return scopeResource;
	}
}
