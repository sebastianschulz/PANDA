package de.fuberlin.panda.metadata.result.fusion.type;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URL;
import java.util.ArrayList;

import org.junit.Test;

import de.fuberlin.panda.metadata.result.fusion.type.tree.TypeTree;

public class TypePropertiesTest {

	@Test
	public void testParsePropertyList() {
		String listString = "test1,Test2 ,   TEST3 ,test1 ";
		ArrayList<String> resultList = TypeProperties.parsePropertyList(listString);
		assertNotNull(resultList);
		assertTrue("Wrong size of property list.", resultList.size()==3);
		assertTrue("Wrong String parsed for result list entry [0].", resultList.get(0).equals("test1"));
		assertTrue("Wrong String parsed for result list entry [1].", resultList.get(1).equals("test2"));
		assertTrue("Wrong String parsed for result list entry [2].", resultList.get(2).equals("test3"));
	}
	
	@Test
	public void testParsePropertyTree() {
		String newickString = "((child3)child1,child2)root;";
		try {
			TypeTree testTree = TypeProperties.parsePropertyTree(newickString);
			assertNotNull(testTree);
			assertFalse("Didn't found node 'child3'.", testTree.get("child3").equals(null));
			assertFalse("Didn't found node 'child3'.", testTree.get("child2").equals(null));
			assertFalse("Didn't found node 'child3'.", testTree.get("child1").equals(null));
			assertFalse("Didn't found node 'child3'.", testTree.get("root").equals(null));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testReadEmptyTypeProperties() {
		URL fileURI = this.getClass().getResource("empty.properties");
		String filePath = fileURI.toString().replace("file:/", "").replace("%20", " ");

		TypeProperties.init(filePath);
		
		assertNotNull(TypeProperties.getFormats());
		assertNotNull(TypeProperties.getLanguages());
		assertTrue("Size of formats list was wrong.", TypeProperties.getFormats().size()==0);
		assertTrue("Size of formats list was wrong.", TypeProperties.getLanguages().size()==0);
		
		assertNull(TypeProperties.getFormatsTree());
		assertNull(TypeProperties.getLanguagesTree());
		
		TypeProperties.reset();
		assertTrue("Reset didn't clear formats list.", TypeProperties.getFormats().size()==0);
		assertTrue("Reset didn't clear languages list.", TypeProperties.getLanguages().size()==0);
		assertNull(TypeProperties.getFormatsTree());
		assertNull(TypeProperties.getLanguagesTree());
	}
	
	@Test
	public void testReadNormalTypeProperties() {
		URL fileURI = this.getClass().getResource("test.properties");
		String filePath = fileURI.toString().replace("file:/", "").replace("%20", " ");
		TypeProperties.init(filePath);
		
		assertNotNull(TypeProperties.getFormats());
		assertTrue("Size of formats list was wrong.", TypeProperties.getFormats().size()==2);
		assertTrue("Wrong String at [0] of formats list", TypeProperties.getFormats().get(0).equals("test1"));
		assertTrue("Wrong String at [1] of formats list", TypeProperties.getFormats().get(1).equals("test2"));
		
		assertNotNull(TypeProperties.getLanguages());
		assertTrue("Size of formats list was wrong.", TypeProperties.getLanguages().size()==1);
		assertTrue("Wrong String at [0] of languages list", TypeProperties.getLanguages().get(0).equals("test3"));
		
		TypeTree testTree = TypeProperties.getFormatsTree();
		assertTrue("Wrong size of formats tree.", testTree.getSize()==1);
		assertFalse("Wrong root node in formats tree.", testTree.get("testFormat").equals(null));
		
		testTree = TypeProperties.getLanguagesTree();
		assertTrue("Wrong size of formats tree.", testTree.getSize()==1);
		assertFalse("Wrong root node in formats tree.", testTree.get("testLanguage").equals(null));
		
		TypeProperties.reset();
		assertTrue("Reset didn't clear formats list.", TypeProperties.getFormats().size()==0);
		assertTrue("Reset didn't clear languages list.", TypeProperties.getLanguages().size()==0);
		assertNull(TypeProperties.getFormatsTree());
		assertNull(TypeProperties.getLanguagesTree());
	}
}
