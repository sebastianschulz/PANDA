package de.fuberlin.panda.metadata.result.fusion.administrative;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import de.fuberlin.panda.api.APIHelper;
import de.fuberlin.panda.metadata.MetadataConnector;
import de.fuberlin.panda.metadata.result.fusion.helper.MatchingHelper;

/**
 * This class represents the in memory presentation of the relevant information
 * regarding the metadata fusing of licenses. Therefore it holds some {@code String}
 * attributes which represent the most restrictive permission properties at the 
 * current time.
 * 
 * @see #LicenseDescription()
 * @see #loadProperties(String)
 * @see #setReusePermissions(String)
 * @see #setDistributePermissions(String)
 * @see #setEditPermissions(String)
 * @see #setAttributionRestrictions(String)
 * @see #setSubjectedLicenses(String)
 * @see #setAttributes()
 * @see #updateAttributes(LicenseDescription)
 * @see #updateReusePermission(String)
 * @see #updateDistributePermission(String)
 * @see #updateEditPermission(String)
 * @see #updateAttributionRestriction(String)
 * @see #updateSubjectedLicenses(ArrayList)
 * 
 * @author Sebastian Schulz
 * @since 25.03.2014
 */
public class LicenseDescription {
	private static Logger logger = Logger.getLogger(MetadataConnector.class.getName());
	
	private String licenseName = null;
	private ArrayList<String> involvedLicenses = new ArrayList<String>();
	
	private ArrayList<String> reusePermissions = new ArrayList<String>();
	private ArrayList<String> distributePermissions = new ArrayList<String>();
	private ArrayList<String> editPermissions = new ArrayList<String>();
	private ArrayList<String> attributionRestrictions = new ArrayList<String>();
	private ArrayList<String> subjectedLicenses = new ArrayList<String>();
	
	private String reusePermission = null;
	private String distributePermission = null;
	private String editPermission = null;
	private String attributionRestriction = null;
	
	private boolean isReusePermissionMostRestrictive = false;
	private boolean isDistributePermissionMostRestrictive = false;
	private boolean isEditPermissionMostRestrictive = false;
	private boolean isAttributionRestrictionMostRestrictive = false;

	private boolean wereAttributesSet = false;
	
	public LicenseDescription() {
		this(APIHelper.getWebContentDirPath() + "\\prefs\\license.properties", "Default", true);
	}
	
	public LicenseDescription(String filePath, String licenseName) {
		this(filePath, licenseName, false);
	}
	
	/**
	 * Contructor which calls {@link #loadProperties()} and 
	 * {@link #setAttributes()}.
	 */
	public LicenseDescription(String filePath, String licenseName, boolean shouldSetDefaultAtrributes) {
		this.licenseName = licenseName;
		involvedLicenses.add(licenseName);
		loadProperties(filePath, shouldSetDefaultAtrributes);
		if (wereAttributesSet) {
			setAttributes();
		}
	}
	
	/**
	 * This method creates an {@code InputStream} to the given {@code fileName}
	 * and tries to load the properties with calling the set methods.
	 * 
	 * @param filePath - a {@code String} object which represents the filePath.
	 * @param shouldSetDefaultAttributes - a {@code boolean} value which represents if
	 * 	hard coded default attributes should be set.
	 */
	private void loadProperties(String filePath, boolean shouldSetDefaultAttributes) {
		Properties properties = new Properties();
		InputStream input = null;
		
		try {
			input = new FileInputStream(filePath);
			properties.load(input);
			logger.debug("Retrieved properties from file '" + filePath + "'");
			
			setReusePermissions(properties.getProperty("reuse_permissions"));
			setDistributePermissions(properties.getProperty("distribute_permissions"));
			setEditPermissions(properties.getProperty("edit_permissions"));
			setAttributionRestrictions(properties.getProperty("attribution_restrictions"));
			setSubjectedLicenses(properties.getProperty("subjected_licenses"));
			wereAttributesSet = true;
		} catch (Exception ex) {
			logger.warn("Couldn't retrieve properties from file '" + filePath + "';" 
					+ ex.getMessage());
			if (shouldSetDefaultAttributes) {
				setReusePermissions(null);
				setDistributePermissions(null);
				setEditPermissions(null);
				setAttributionRestrictions(null);
				setSubjectedLicenses(null);
				wereAttributesSet = true;
				logger.info("Set default attributes for '" + licenseName + "'");
			}
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			}
		}
	}

	/**
	 * This method adds the possible reuse permissions given by the {@code property} to the 
	 * {@code reusePermissions}{@code List}. If this {@code property} could not be read hard
	 * coded values are used to fill the list. 
	 * 
	 * @param property - a comma separated {@code String} representation all possible permissions.
	 */
	private void setReusePermissions(String property) {
		if (!(property==null) && !property.isEmpty()) {
			logger.debug("Reuse permission property found for '" + licenseName + "'");
			StringTokenizer st = new StringTokenizer(property, ",");
			while (st.hasMoreElements()) {
				reusePermissions.add(st.nextToken().trim().toLowerCase());
				logger.debug("Reuse permission entry set for '" + licenseName + "'");
			}
		} else {
			logger.warn("Set default reuse permissions for '" + licenseName + "'");
			reusePermissions.add("commercial");
			reusePermissions.add("non-commercial");
			reusePermissions.add("none");
		}
	}

	/**
	 * This method adds the possible distribute permissions given by the {@code property} to the 
	 * {@code distributePermissions}{@code List}. If this {@code property} could not be read hard
	 * coded values are used to fill the list. 
	 * 
	 * @param property - a comma separated {@code String} representation all possible permissions.
	 */
	private void setDistributePermissions(String property) {
		if (!(property==null) && !property.isEmpty()) {
			logger.debug("Distribute permission property found for '" + licenseName + "'");
			StringTokenizer st = new StringTokenizer(property, ",");
			while (st.hasMoreElements()) {
				distributePermissions.add(st.nextToken().trim().toLowerCase());
				logger.debug("Distribute permission entry set for '" + licenseName + "'");
			}
		} else {
			logger.warn("Set default distribute permissions for '" + licenseName + "'");
			distributePermissions.add("yes");
			distributePermissions.add("no");
		}
	}

	/**
	 * This method adds the possible edit permissions given by the {@code property} to the 
	 * {@code editPermissions}{@code List}. If this {@code property} could not be read hard
	 * coded values are used to fill the list. 
	 * 
	 * @param property - a comma separated {@code String} representation all possible permissions.
	 */
	private void setEditPermissions(String property) {
		if (!(property==null) && !property.isEmpty()) {
			logger.debug("Edit permission property found for '" + licenseName + "'");
			StringTokenizer st = new StringTokenizer(property, ",");
			while (st.hasMoreElements()) {
				editPermissions.add(st.nextToken().trim().toLowerCase());
				logger.debug("Edit permission entry set for '" + licenseName + "'");
			}
		} else {
			logger.warn("Set default edit permissions for '" + licenseName + "'");
			editPermissions.add("yes");
			editPermissions.add("no");
		}
	}

	/**
	 * This method adds the possible attribution restriction given by the {@code property} to the 
	 * {@code attributionRestrictions}{@code List}. If this {@code property} could not be read hard
	 * coded values are used to fill the list. 
	 * 
	 * @param property - a comma separated {@code String} representation all possible permissions.
	 */
	private void setAttributionRestrictions(String property) {
		if (!(property==null) && !property.isEmpty()) {
			logger.debug("Attribution restrictions property found for '" + licenseName + "'");
			StringTokenizer st = new StringTokenizer(property, ",");
			while (st.hasMoreElements()) {
				attributionRestrictions.add(st.nextToken().trim().toLowerCase());
				logger.debug("Attribution restrictions entry set for '" + licenseName + "'");
			}
		} else {
			logger.warn("Set default attribution restrictions for '" + licenseName + "'");
			attributionRestrictions.add("no");
			attributionRestrictions.add("yes");
		}
	}
	
	/**
	 * This method adds the addicted licenses given by the {@code property} to the 
	 * {@code addictedLicenses} {@code List}. If this {@code property} could not be read the
	 * list stays empty.
	 * 
	 * @param property - a comma separated {@code String} representation all possible permissions.
	 */
	private void setSubjectedLicenses(String property) {
		if (!(property==null) && !property.isEmpty()) {
			logger.debug("Subjected licenses property found for '" + licenseName + "'");
			StringTokenizer st = new StringTokenizer(property, ",");
			while (st.hasMoreElements()) {
				subjectedLicenses.add(st.nextToken().trim().toLowerCase());
				logger.debug("Subjected licenses entry set for '" + licenseName + "'");
			}
		}
	}
	
	/**
	 * This method sets all attributes to the least restrictive values which
	 * are hold at the beginning of each {@code List}. In case this value is the
	 * only value in the list the specific flag is set to true.
	 */
	private void setAttributes() {
		if (reusePermissions.size() == 1) {
			isReusePermissionMostRestrictive = true;
		}
		reusePermission = reusePermissions.get(0);
		
		if (distributePermissions.size() == 1) {
			isDistributePermissionMostRestrictive = true;
		}
		distributePermission = distributePermissions.get(0);
		
		if (editPermissions.size() == 1) {
			isEditPermissionMostRestrictive = true;
		}
		editPermission = editPermissions.get(0);
		
		if (attributionRestrictions.size() == 1) {
			isAttributionRestrictionMostRestrictive = true;
		}
		attributionRestriction = attributionRestrictions.get(0);
	}
	
	public String getName() {
		return licenseName;
	}
	
	public ArrayList<String> getInvolvedLicenses() {
		return involvedLicenses;
	}

	public String getReusePermission() {
		return reusePermission;
	}
	
	public String getDistributePermission() {
		return distributePermission;
	}
	
	public String getEditPermission() {
		return editPermission;
	}
	
	public String getAttributionRestriction() {
		return attributionRestriction;
	}
	
	public ArrayList<String> getSubjectedLicenses() {
		return subjectedLicenses;
	}
	
	public boolean wereAttributesSet() {
		return wereAttributesSet;
	}
	
	/**
	 * This method calls the several update methods to update the attributes
	 * of the current {@code LicenseDescription} object.
	 * 
	 * @param newLicense - the {@code LicenseDescription} object which should 
	 * 	update the current license description.
	 */
	public void updateAttributes(LicenseDescription newLicense) {
		if(!MatchingHelper.contains(involvedLicenses, newLicense.getName())) {
			updateReusePermission(newLicense.getReusePermission());
			updateDistributePermission(newLicense.getDistributePermission());
			updateEditPermission(newLicense.getEditPermission());
			updateAttributionRestriction(newLicense.getAttributionRestriction());
			updateSubjectedLicenses(newLicense.getSubjectedLicenses());
			
			if(MatchingHelper.contains(involvedLicenses, "Default")) {
				involvedLicenses.remove("Default");
			}
			
			if (!MatchingHelper.contains(involvedLicenses, newLicense.getName().toLowerCase())) {
				involvedLicenses.add(newLicense.getName().toLowerCase());
			}
			
			logger.debug("Updated Attributes of '" + licenseName 
					+ "' with '" + newLicense.getName() + "'");
		}
		
	}
	
	/**
	 * This method updates the {@code reusePermission} if the given {@code permission}
	 * is more restrictive. In case the new permission is the most restrictive permission
	 * the {@code isReusePermissionMostRestrictive} flag is set to {@code true}.
	 * 
	 * @param permission - a {@code String} representation of the new permission value.
	 */
	private void updateReusePermission(String permission) {
		if (!isReusePermissionMostRestrictive) {
			if (!permission.equalsIgnoreCase(reusePermission) 
					&& !permission.isEmpty()) {
				int newIndex = reusePermissions.indexOf(permission);
				int existingIndex = reusePermissions.indexOf(reusePermission);
				if (newIndex > existingIndex) {
					if (reusePermissions.size()-1 == newIndex) {
						isReusePermissionMostRestrictive = true;
					}
					reusePermission = permission;
					logger.debug("Updated reuse permission of '" + licenseName 
							+ "' with '" + permission + "'");
				}
			}
		}
	}
	
	/**
	 * This method updates the {@code distributePermission} if the given {@code permission}
	 * is more restrictive. In case the new permission is the most restrictive permission
	 * the {@code isDistributePermissionMostRestrictive} flag is set to {@code true}.
	 * 
	 * @param permission - a {@code String} representation of the new permission value.
	 */
	private void updateDistributePermission(String permission) {
		if (!isDistributePermissionMostRestrictive) {
			if (!permission.equalsIgnoreCase(distributePermission) 
					&& !permission.isEmpty()) {
				int newIndex = distributePermissions.indexOf(permission);
				int existingIndex = distributePermissions.indexOf(distributePermission);
				if (newIndex > existingIndex) {
					if (distributePermissions.size()-1 == newIndex) {
						isDistributePermissionMostRestrictive = true;
					}
					distributePermission = permission;
					logger.debug("Updated distribute permission of '" + licenseName 
							+ "' with '" + permission + "'");
				}
			}
		}
	}
	
	/**
	 * This method updates the {@code EditPermission} if the given {@code permission}
	 * is more restrictive. In case the new permission is the most restrictive permission
	 * the {@code isEditPermissionMostRestrictive} flag is set to {@code true}.
	 * 
	 * @param permission - a {@code String} representation of the new permission value.
	 */
	private void updateEditPermission(String permission) {
		if (!isEditPermissionMostRestrictive) {
			if (!permission.equalsIgnoreCase(editPermission) 
					&& !permission.isEmpty()) {
				int newIndex = editPermissions.indexOf(permission);
				int existingIndex = editPermissions.indexOf(editPermission);
				if (newIndex > existingIndex) {
					if (editPermissions.size()-1 == newIndex) {
						isEditPermissionMostRestrictive = true;
					}
					editPermission = permission;
					logger.debug("Updated edit permission of '" + licenseName 
							+ "' with '" + permission + "'");
				}
			}
		}
	}
	
	/**
	 * This method updates the {@code AttributionRestriction} if the given {@code permission}
	 * is more restrictive. In case the new restriction is the most restrictive restriction
	 * the {@code isAttributionRestrictionMostRestrictive} flag is set to {@code true}.
	 * 
	 * @param restriction - a {@code String} representation of the new restriction value.
	 */
	private void updateAttributionRestriction(String restriction) {
		if (!isAttributionRestrictionMostRestrictive) {
			if (!restriction.equalsIgnoreCase(attributionRestriction) 
					&& !restriction.isEmpty()) {
				int newIndex = attributionRestrictions.indexOf(restriction);
				int existingIndex = attributionRestrictions.indexOf(attributionRestriction);
				if (newIndex > existingIndex) {
					if (attributionRestrictions.size()-1 == newIndex) {
						isAttributionRestrictionMostRestrictive = true;
					}
					attributionRestriction = restriction;
					logger.debug("Updated attribution restriction of '" + licenseName 
							+ "' with '" + restriction + "'");
				}
			}
		}
	}

	/**
	 * This method iterates every object of the {@code newAddictedLicenses} list
	 * and append it to the {@code addictedLicenses} if it was not already in this list.
	 * 
	 * @param newSubjectededLicenses - a {@code List} of {@code String} objects.
	 */
	private void updateSubjectedLicenses(ArrayList<String> newSubjectededLicenses) {
		for (String licenseName : newSubjectededLicenses) {
			if (!(subjectedLicenses.indexOf(licenseName) >= 0)) {
				subjectedLicenses.add(licenseName);
				logger.debug("Updated subjected licenses list of '" + licenseName 
						+ "' with entry '" + licenseName + "'");
			}
		}
		
	}
}