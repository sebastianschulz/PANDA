package de.fuberlin.panda.metadata.descriptive;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Vector;

import javax.management.modelmbean.XMLParseException;

import org.junit.Test;

public class TypeTest {

	@Test
	public void testCreateType() {
		Type type = new Type();
		assertNotNull(type);
	}
	
	@Test
	public void testSetAndGetFormat() {
		Type type = new Type();
		String testFormat = "int";
		ArrayList<String> testList = new ArrayList<>();
		testList.add(testFormat);
		try {
			type.setFormat("double", testList);
		} catch (XMLParseException e) {
			assertNull(type.getFormat());
		}
		
		try {
			type.setFormat(testFormat, testList);
			assertTrue("Format has been set wrong.", testFormat.equals(type.getFormat()));
		} catch (XMLParseException e) {
			fail();
		}
	}
	
	@Test
	public void testSetAndGetLanguage() {
		Type type = new Type();
		String testLanguage = "german";
		ArrayList<String> testList = new ArrayList<>();
		testList.add(testLanguage);
		try {
			type.setLanguage("english", testList);
		} catch (XMLParseException e) {
			assertNull(type.getLanguage());
		}
		
		try {
			type.setLanguage(testLanguage, testList);
			assertTrue("Language has been set wrong.", testLanguage.equals(type.getLanguage()));
		} catch (XMLParseException e) {
			fail();
		}
	}

	@Test
	public void testGetTypeWith2Entries() {
		Type type = new Type();
		String testLanguage = "testlang";
		String testFormat = "xs:test";
		ArrayList<String> testLanguageList = new ArrayList<>();
		testLanguageList.add(testLanguage);
		ArrayList<String> testFormatList = new ArrayList<>();
		testFormatList.add(testFormat);
		
		try {
			type.setLanguage(testLanguage, testLanguageList);
			type.setFormat(testFormat, testFormatList);
			
			Vector<String[]> typeV = type.getAttributes();
			assertTrue("Type vector has wrong size", typeV.size() == 2);
			
			assertTrue("Language array has wrong length.", typeV.get(0).length == 2);
			assertTrue("Language array label was incorrect.", typeV.get(0)[0].equals("Language"));
			assertTrue("Language array URI was incorrect.", typeV.get(0)[1].equals(testLanguage));
			
			assertTrue("Format array has wrong length.", typeV.get(1).length == 2);
			assertTrue("Format array label was incorrect.", typeV.get(1)[0].equals("Format"));
			assertTrue("Format array URI was incorrect.", typeV.get(1)[1].equals(testFormat));	
		} catch (XMLParseException e) {
			fail();
		}
	}
	
	@Test
	public void testGetTypeWith1Entry() {
		Type type = new Type();
		String testLanguage = "testlang";
		ArrayList<String> testList = new ArrayList<>(); 
		testList.add(testLanguage);
		
		try {
			type.setLanguage(testLanguage, testList);
			
			Vector<String[]> typeV = type.getAttributes();
			assertTrue("Type vector has wrong size", typeV.size() == 1);
			
			assertTrue("Language array has wrong length.", typeV.get(0).length == 2);
			assertTrue("Language array label was incorrect.", typeV.get(0)[0].equals("Language"));
			assertTrue("Language array URI was incorrect.", typeV.get(0)[1].equals(testLanguage));
		} catch (XMLParseException e) {
			fail();
		}
	}
	
	@Test
	public void testGetAdministrativeScopeWith0Entries() {
		Type type = new Type();
		Vector<String[]> typeV = type.getAttributes();
		assertTrue("Type vector has wrong size", typeV.size() == 0);
	}

}
