package de.fuberlin.panda.metadata.result.fusion.administrative;

import java.net.URI;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

import de.fuberlin.panda.api.APIHelper;
import de.fuberlin.panda.metadata.MetadataConnector;
import de.fuberlin.panda.metadata.parsedMetadata.ParsedMetadata;
import de.fuberlin.panda.metadata.result.MetadataResult;
import de.fuberlin.panda.metadata.result.fusion.FusionResult;
import de.fuberlin.panda.metadata.result.fusion.helper.MatchingHelper;

/**
 * This class is responsible for fusing administrative metadata. Therefore the metadata 
 * information corresponding to every URI are added by calling {@link #add(ParsedMetadata)}.
 * This method is also responsible for the actual fusion in this case. After all metadata 
 * information is added the {@link #fuse()} is called. It creates the necessary properties
 * and adds them the the {@code scopeResource}.
 * 
 * @see #AdministrativeFusion(Model, String)
 * @see #add(ParsedMetadata)
 * @see #getLicenseName(URI)
 * @see #fuse()
 * @see #createInvolvedLicensesProperties()
 * @see #createReusePermissionProperty()
 * @see #createDistributePermissionProperty()
 * @see #createEditPermissionProperty()
 * @see #createAttributionRestrictionProperty()
 * @see #createSubjectedLicensesProperties()
 * 
 * @author Sebastian Schulz
 * @since 25.03.2014
 */
public class AdministrativeFusion {
	private static Logger logger = Logger.getLogger(MetadataConnector.class.getName());
	
	//type fusion
	private static final String LICENSES_FOLDER = APIHelper.getWebContentDirPath() + "prefs\\licenses\\";
	private LicenseDescription overallLicenseDescription;
	
	//resource building
	private final Resource ADMIN_NODETYPE = ResourceFactory.createResource(MetadataResult.METADATA_NS + "Administrative");
	private Model metadataModel;
	private Resource scopeResource;
	
	/**
	 * Public constructor which creates a new {@code Resource} for the {@code scopeResource}.
	 * It also initializes the global {@code overallLicenseDescription}.
	 * 
	 * @param metadataModel - the current {@code Model} passed by the {@link FusionResult}.
	 * @param fusionId - a {@code String} which represents the current fusion identifier.
	 */
	public AdministrativeFusion(Model metadataModel, String fusionId) {
		this.metadataModel = metadataModel;
		overallLicenseDescription = new LicenseDescription();
		
		scopeResource = this.metadataModel.createResource(MetadataResult.METADATA_NS + "ads_" +
				fusionId, ADMIN_NODETYPE);
	}
	
	/**
	 * This method does the actual fusion work. It tries to obtain the license name of the 
	 * given {@code ParsedMetadata} object by calling {@link #getLicenseName(URI)} and tries
	 * to load the properties file named by the licenseName from the licenses folder. Finally
	 * the field {@code overallLicenseDescription} is updated with the obtained 
	 * {@code LicenseDescription} object.
	 * 
	 * @param singleMetadata - a {@code ParsedMetadata} object which contains the license URI.
	 */
	public void add(ParsedMetadata singleMetadata) {
		try {
			URI uri = singleMetadata.getAdministrativeScope().getLicenceUri();
			String licenseName = MatchingHelper.getUriEnding(uri);
			logger.debug("URI: " + singleMetadata.getUri() + "\t Obtained license '" + licenseName + "'");
			if (licenseName == null) {
				throw new Exception("URI: " + singleMetadata.getUri() + "\t - unable to obtain license name");
			}
			String filePath = LICENSES_FOLDER + licenseName.toLowerCase() + ".properties";
			
			LicenseDescription metadataLicenseDescription = new LicenseDescription(filePath, licenseName);
			if (!metadataLicenseDescription.wereAttributesSet()) {
				throw new Exception("URI: " + singleMetadata.getUri() + "\t - unable to set attributes of '" 
						+ licenseName + "' parsed from " + filePath);
			} else {
				overallLicenseDescription.
						updateAttributes(metadataLicenseDescription);
			}
		} catch (Exception e) {
			logger.warn(e.getMessage());
		}
	}
	
	/**
	 * This method creates the different {@code Property} objects by calling the specific
	 * method. These properties are added to the {@code scopeResource}. This method doesn't
	 * actual fuses the objects since the fusion takes place while adding them to the object.
	 */
	public void fuse() {
		createInvolvedLicensesProperties();
		createReusePermissionProperty();
		createDistributePermissionProperty();
		createEditPermissionProperty();
		createAttributionRestrictionProperty();
		createSubjectedLicensesProperties();
		logger.info("--> Successfully created Administrative Fusion Resource!");
	}

	/**
	 * This method creates an involved license property for each entry in the
	 * {@code licenseDescription} object and adds it to the {@code scopeResource}.
	 */
	private void createInvolvedLicensesProperties() {
		Property involvedLicensesProperty = ResourceFactory.
				createProperty(MetadataResult.METADATA_NS, "InvolvedLicense");
		for (String licenseName : overallLicenseDescription.getInvolvedLicenses()) {
			scopeResource.addProperty(involvedLicensesProperty, licenseName);
		}
	}

	/**
	 * This method creates a reuse permission property and adds it to the 
	 * {@code scopeResource}.
	 */
	private void createReusePermissionProperty() {
		Property reuseProperty =  ResourceFactory.createProperty(MetadataResult.METADATA_NS, "ReusePermission");
		String reuseString = overallLicenseDescription.getReusePermission();
		scopeResource.addProperty(reuseProperty, reuseString);
	}

	/**
	 * This method creates a distribute permission property and adds it to the 
	 * {@code scopeResource}.
	 */
	private void createDistributePermissionProperty() {
		Property distributeProperty =  ResourceFactory.
				createProperty(MetadataResult.METADATA_NS, "DistributePermission");
		String distributeString = overallLicenseDescription.getDistributePermission();
		scopeResource.addProperty(distributeProperty, distributeString);
	}

	/**
	 * This method creates a edit permission property and adds it to the 
	 * {@code scopeResource}.
	 */
	private void createEditPermissionProperty() {
		Property editProperty =  ResourceFactory.
				createProperty(MetadataResult.METADATA_NS, "EditPermission");
		String editString = overallLicenseDescription.getEditPermission();
		scopeResource.addProperty(editProperty, editString);
	}

	/**
	 * This method creates a attribution restriction property and adds it to the 
	 * {@code scopeResource}.
	 */
	private void createAttributionRestrictionProperty() {
		Property attributionProperty =  ResourceFactory.
				createProperty(MetadataResult.METADATA_NS, "AttributionRestriction");
		String attributionString = overallLicenseDescription.getAttributionRestriction();
		scopeResource.addProperty(attributionProperty, attributionString);
	}

	/**
	 * This method creates an reuse under license property for each entry in the
	 * {@code licenseDescription} object and adds it to the {@code scopeResource}.
	 */
	private void createSubjectedLicensesProperties() {
		Property addictedLicensesProperty =  ResourceFactory.
				createProperty(MetadataResult.METADATA_NS, "ReuseUnderLicense");
		for (String licenseName : overallLicenseDescription.getSubjectedLicenses()) {
			scopeResource.addProperty(addictedLicensesProperty, licenseName);
		}
	}
	
	public Resource getResource() {
		return scopeResource;
	}
}
