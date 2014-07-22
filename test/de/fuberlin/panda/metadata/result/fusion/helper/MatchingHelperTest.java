package de.fuberlin.panda.metadata.result.fusion.helper;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.ArrayList;

import org.junit.Test;

import de.fuberlin.panda.metadata.result.fusion.helper.MatchingHelper;

public class MatchingHelperTest {

	@Test
	public void testGetUriEnding() {
		URI testURI = URI.create("/i/am/a/uriTestEnding");
		String ending = MatchingHelper.getUriEnding(testURI);
		assertTrue("URIs ending was not 'uriTestEnding' but '" + ending + "'.", ending.equals("uriTestEnding"));
		
		testURI = URI.create("iamauriTestEnding");
		ending = MatchingHelper.getUriEnding(testURI);
		assertNull(ending);
	}
	
	@Test
	public void testGetSubUri() {
		URI testURI = URI.create("/i/am/a/uriTestEnding");
		String subUri = MatchingHelper.getSubUri(testURI);
		assertTrue("The subUri was not '/i/am/a/' but '" + subUri + "'.", subUri.equals("/i/am/a"));
		
		testURI = URI.create("iamauriTestEnding");
		subUri = MatchingHelper.getUriEnding(testURI);
		assertNull(subUri);
	}

	@Test
	public void testContains() {
		ArrayList<String> testList = new ArrayList<>();
		testList.add("one");
		testList.add("two");
		
		assertTrue("Unable to find 'one' in testList.", MatchingHelper.contains(testList, "one"));
		assertFalse("Found 'three' in testList.", MatchingHelper.contains(testList, "three"));
	}

	@Test
	public void testContainsWithSubstring() {
		ArrayList<String> testList = new ArrayList<>();
		testList.add("one");
		testList.add("two");
		
		assertFalse("Found 'on' in testList.", MatchingHelper.contains(testList, "on"));
		assertFalse("Found 'three' in testList.", MatchingHelper.contains(testList, "three"));
	}

	@Test
	public void testGroupNoMetadataUrisWithGroupEnd() {
		ArrayList<URI> testList = new ArrayList<>();
		testList.add(URI.create("this/is/a/cow"));
		testList.add(URI.create("this/is/a/elephant"));
		testList.add(URI.create("this/is/a/bird"));
		testList.add(URI.create("this/is/a/bear"));
		testList.add(URI.create("i/need/a/rest"));
		testList.add(URI.create("i/need/a/drink"));
		testList.add(URI.create("i/need/a/bitofsleep"));
		testList.add(URI.create("lets/finish/the/test"));
		
		NoMetadataObject noMetadata = MatchingHelper.groupNoMetadataUris(testList);
		
		assertNotNull(noMetadata);
		
		assertTrue("Amount of single URIs should be 1.", noMetadata.getSingleUris().size()==1);
		assertTrue("Single URI should be 'lets/finish/the/test'.", 
				noMetadata.getSingleUris().get(0).equals("lets/finish/the/test"));
		
		assertTrue("Amount of URI groups should be 2.", noMetadata.getUriGroups().size()==2);
		assertTrue("GroupStart of URI group 1 should be 'i/need/a/bitofsleep'.", 
				noMetadata.getUriGroups().get(0)[0].equals("i/need/a/bitofsleep"));
		assertTrue("GroupEnd of URI group 1 should be 'i/need/a/rest'.", 
				noMetadata.getUriGroups().get(0)[1].equals("i/need/a/rest"));
		assertTrue("GroupStart of URI group 2 should be 'this/is/a/bear'.", 
				noMetadata.getUriGroups().get(1)[0].equals("this/is/a/bear"));
		assertTrue("GroupStart of URI group 2 should be 'this/is/a/elephant'.", 
				noMetadata.getUriGroups().get(1)[1].equals("this/is/a/elephant"));
	}
	
	@Test
	public void testGroupNoMetadataUrisWithSingleEnd() {
		ArrayList<URI> testList = new ArrayList<>();
		testList.add(URI.create("i/need/a/rest"));
		testList.add(URI.create("i/need/a/drink"));
		testList.add(URI.create("i/need/a/bitofsleep"));
		testList.add(URI.create("lets/finish/the/test"));
		
		NoMetadataObject noMetadata = MatchingHelper.groupNoMetadataUris(testList);
		
		assertNotNull(noMetadata);
		
		assertTrue("Amount of single URIs should be 1.", noMetadata.getSingleUris().size()==1);
		assertTrue("Single URI should be 'lets/finish/the/test'.", 
				noMetadata.getSingleUris().get(0).equals("lets/finish/the/test"));
		
		assertTrue("Amount of URI groups should be 1.", noMetadata.getUriGroups().size()==1);
		assertTrue("GroupStart of URI group 1 should be 'i/need/a/bitofsleep'.", 
				noMetadata.getUriGroups().get(0)[0].equals("i/need/a/bitofsleep"));
		assertTrue("GroupEnd of URI group 1 should be 'i/need/a/rest'.", 
				noMetadata.getUriGroups().get(0)[1].equals("i/need/a/rest"));
	}
}
