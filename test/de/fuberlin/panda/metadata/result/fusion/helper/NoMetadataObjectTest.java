package de.fuberlin.panda.metadata.result.fusion.helper;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class NoMetadataObjectTest {

	@Test
	public void testNewNoMetadataObject() {
		NoMetadataObject testObject = new NoMetadataObject();
		assertNotNull(testObject);
		assertTrue("Single URIs list was not empty.", testObject.getSingleUris().size()==0);
		assertTrue("URI Groups list was not empty.", testObject.getUriGroups().size()==0);
	}
	
	@Test
	public void testAddSingleURI() {
		NoMetadataObject testObject = new NoMetadataObject();
		assertNotNull(testObject);
		String testUri = "/Test/URI/1";
		testObject.addSingleUri(testUri);
		assertTrue("Single URIs list size was not [1].", testObject.getSingleUris().size()==1);
		assertTrue("Single URIs list entry 1 was not '/Test/URI/1'.", 
				testObject.getSingleUris().get(0).equals(testUri));
	}
	
	@Test
	public void testAddURIGroup() {
		NoMetadataObject testObject = new NoMetadataObject();
		assertNotNull(testObject);
		String startUri = "Test/URI/start";
		String endUri = "Test/URI/end";
		testObject.addUriGroup(startUri, endUri);
		assertTrue("URI Groups list size was not [1].", testObject.getUriGroups().size()==1);
		assertTrue("URI Groups list entry 1 start URI was not 'Test/URI/start'.", 
				testObject.getUriGroups().get(0)[0].equals(startUri));
		assertTrue("URI Groups list entry 1 end URI was not 'Test/URI/end'.", 
				testObject.getUriGroups().get(0)[1].equals(endUri));
	}
	
}
