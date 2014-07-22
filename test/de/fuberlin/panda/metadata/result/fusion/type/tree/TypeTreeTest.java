package de.fuberlin.panda.metadata.result.fusion.type.tree;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import newick.NewickParser;
import newick.NewickParser.TreeNode;

import org.junit.Test;

public class TypeTreeTest {

	@Test
	public void testNewTypeTree() {
		try {
			String newickString = "((child3)child1,child2)root;";
			InputStream propertyIn = new ByteArrayInputStream(newickString.getBytes("UTF-8"));
			TreeNode parsedTree = new NewickParser(propertyIn).tree();
			TypeTree testTree = new TypeTree(parsedTree);
			assertNotNull(testTree);
			assertFalse("Didn't found node 'child3'.", testTree.get("child3").equals(null));
			assertFalse("Didn't found node 'child3'.", testTree.get("child2").equals(null));
			assertFalse("Didn't found node 'child3'.", testTree.get("child1").equals(null));
			assertFalse("Didn't found node 'child3'.", testTree.get("root").equals(null));
		} catch (Throwable e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testComplexTypeTree() {
		TypeTree testTree = null;
		try {
			String newickString = "((((((byte)short)int)long)integer,((float)double)floating)numeric"
					+ ",(char)string)valueformat;";
			InputStream propertyIn = new ByteArrayInputStream(newickString.getBytes("UTF-8"));
			TreeNode parsedTree = new NewickParser(propertyIn).tree();
			testTree = new TypeTree(parsedTree);
		} catch (Throwable e) {
			fail(e.getMessage());
		}	
		
		assertNotNull(testTree);
		
		TypeNode currentNode = testTree.get("valueformat");
		assertFalse("Didn't found node 'valueformat'.", currentNode.equals(null));
		assertTrue("Wrong level for node 'valueformat'.", currentNode.getLevel()==0);
		
		currentNode = testTree.get("string");
		assertFalse("Didn't found node 'string'.", currentNode.equals(null));
		assertTrue("Wrong level for node 'string'.", currentNode.getLevel()==1);
		
		currentNode = testTree.get("char");
		assertFalse("Didn't found node 'char'.", currentNode.equals(null));
		assertTrue("Wrong level for node 'char'.", currentNode.getLevel()==2);
		
		currentNode = testTree.get("numeric");
		assertFalse("Didn't found node 'numeric'.", currentNode.equals(null));
		assertTrue("Wrong level for node 'numeric'.", currentNode.getLevel()==1);
		
		currentNode = testTree.get("floating");
		assertFalse("Didn't found node 'floating'.", currentNode.equals(null));
		assertTrue("Wrong level for node 'floating'.", currentNode.getLevel()==2);
		
		currentNode = testTree.get("double");
		assertFalse("Didn't found node 'double'.", currentNode.equals(null));
		assertTrue("Wrong level for node 'double'.", currentNode.getLevel()==3);
		
		currentNode = testTree.get("float");
		assertFalse("Didn't found node 'float'.", currentNode.equals(null));
		assertTrue("Wrong level for node 'float'.", currentNode.getLevel()==4);
		
		currentNode = testTree.get("integer");
		assertFalse("Didn't found node 'integer'.", currentNode.equals(null));
		assertTrue("Wrong level for node 'integer'.", currentNode.getLevel()==2);
		
		currentNode = testTree.get("long");
		assertFalse("Didn't found node 'long'.", currentNode.equals(null));
		assertTrue("Wrong level for node 'long'.", currentNode.getLevel()==3);
		
		currentNode = testTree.get("int");
		assertFalse("Didn't found node 'int'.", currentNode.equals(null));
		assertTrue("Wrong level for node 'int'.", currentNode.getLevel()==4);
		
		currentNode = testTree.get("short");
		assertFalse("Didn't found node 'short'.", currentNode.equals(null));
		assertTrue("Wrong level for node 'short'.", currentNode.getLevel()==5);
		
		currentNode = testTree.get("byte");
		assertFalse("Didn't found node 'byte'.", currentNode.equals(null));
		assertTrue("Wrong level for node 'byte'.", currentNode.getLevel()==6);
	}
	
	@Test
	public void testGetNextParent() {
		try {
			String newickString = "((child3)child1,child2)root;";
			InputStream propertyIn = new ByteArrayInputStream(newickString.getBytes("UTF-8"));
			TreeNode parsedTree = new NewickParser(propertyIn).tree();
			TypeTree testTree = new TypeTree(parsedTree);
			assertTrue("Parent should be 'child3'.", 
					testTree.getNextParent("child3", "child3").equals("child3"));
			assertTrue("Parent should be 'child1'.", 
					testTree.getNextParent("child1", "child3").equals("child1"));
			assertTrue("Parent should be 'root'.", 
					testTree.getNextParent("child3", "child2").equals("root"));
		} catch (Throwable e) {}
	}
}
