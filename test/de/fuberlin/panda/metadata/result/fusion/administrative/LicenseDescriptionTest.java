package de.fuberlin.panda.metadata.result.fusion.administrative;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.ArrayList;

import org.junit.Test;

public class LicenseDescriptionTest {
	
	@Test
	public void testCreateLicenseWithoutPropertiesFile() {
		URL fileURI = this.getClass().getResource("test.properties");
		String filePath = fileURI.toString()
				.replace("file:/", "")
				.replace("%20", " ")
				.replace("test.properties", "bla.properties");
		LicenseDescription testDesciption = new LicenseDescription(filePath, "overall", false);
		assertNotNull("Created LicenseDescription object was null.", testDesciption);
		assertNull("Reuse permission not null.", testDesciption.getReusePermission());
		assertNull("Distribute permission not null.", testDesciption.getDistributePermission());
		assertNull("Edit permission not null.", testDesciption.getEditPermission());
		assertNull("Attribution restrictio not null.", testDesciption.getAttributionRestriction());
		assertTrue("Addicted licenses should be empty.", testDesciption.getSubjectedLicenses().size()==0);
	}
	
	@Test
	public void testCreateLicenseDescriptionFromProperties() {
		URL fileURI = this.getClass().getResource("test.properties");
		String filePath = fileURI.toString().replace("file:/", "").replace("%20", " ");
		LicenseDescription testDesciption = new LicenseDescription(filePath, "overall", true);
		assertNotNull("Created LicenseDescription object was null.", testDesciption);
		assertTrue("Reuse permission load error.", testDesciption.getReusePermission().equals("commercial"));
		assertTrue("Distribute permission load error.", testDesciption.getDistributePermission().equals("yes"));
		assertTrue("Edit permission load error.", testDesciption.getEditPermission().equals("yes"));
		assertTrue("Attribution restriction load error.", testDesciption.getAttributionRestriction().equals("no"));
		ArrayList<String> subjectedLicenses = testDesciption.getSubjectedLicenses();
		assertTrue("Wrong size of addicted license list.", subjectedLicenses.size() == 2);
		assertTrue("First entry of addicted license list has to be 'GNU'.", 
				subjectedLicenses.get(0).equalsIgnoreCase("gnu"));
		assertTrue("Second entry of addicted license list has to be 'ODbl'.", 
				subjectedLicenses.get(1).equalsIgnoreCase("ODbl"));
	}
	
	@Test
	public void testCreateLicenseDescriptionWithoutProperties() {
		URL fileURI = this.getClass().getResource("emptyTest.properties");
		String filePath = fileURI.toString().replace("file:/", "").replace("%20", " ");
		LicenseDescription testDesciption = new LicenseDescription(filePath, "testLicense");
		assertNotNull("Created LicenseDescription object was null.", testDesciption);
		assertTrue("Reuse permission load error.", testDesciption.getReusePermission().equals("commercial"));
		assertTrue("Distribute permission load error.", testDesciption.getDistributePermission().equals("yes"));
		assertTrue("Edit permission load error.", testDesciption.getEditPermission().equals("yes"));
		assertTrue("Attribution restriction load error.", testDesciption.getAttributionRestriction().equals("no"));
		assertTrue("Addicted license list was not empty.", testDesciption.getSubjectedLicenses().size() == 0);
	}
	
	@Test 
	public void testUpdateLicense() {
		URL fileURI = this.getClass().getResource("test.properties");
		String filePath = fileURI.toString().replace("file:/", "").replace("%20", " ");
		LicenseDescription testDesciption = new LicenseDescription(filePath, "Default"); 
		fileURI = this.getClass().getResource("updateTest.properties");
		filePath = fileURI.toString().replace("file:/", "").replace("%20", " ");
		LicenseDescription updateDesciption = new LicenseDescription(filePath, "updateLicense");
		
		testDesciption.updateAttributes(updateDesciption);
		
		assertNotNull("Created LicenseDescription object was null.", testDesciption);
		assertTrue("Wrong name for overall license.", testDesciption.getName().equals("Default"));
		assertTrue("Reuse permission load error.", testDesciption.getReusePermission().equals("non-commercial"));
		assertTrue("Distribute permission load error.", testDesciption.getDistributePermission().equals("yes"));
		assertTrue("Edit permission load error.", testDesciption.getEditPermission().equals("no"));
		assertTrue("Attribution restriction load error.", testDesciption.getAttributionRestriction().equals("yes"));
		assertTrue("Wrong size of addicted license list.", testDesciption.getSubjectedLicenses().size() == 2);
		ArrayList<String> involvedLicenses = testDesciption.getInvolvedLicenses();
		assertTrue("Wrong size of involved licneses list.", involvedLicenses.size() == 1);
		assertTrue("First entry of involved licenses list has to be 'updateLicense'.", 
				involvedLicenses.get(0).equalsIgnoreCase("updateLicense"));
	}
}
