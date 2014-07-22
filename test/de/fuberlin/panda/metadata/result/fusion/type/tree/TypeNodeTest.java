package de.fuberlin.panda.metadata.result.fusion.type.tree;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class TypeNodeTest {
	TypeNode testNode;
	TypeNode secondNode;
	
	@Before
	public void setUp() {
		testNode = new TypeNode("test", null);
		secondNode = new TypeNode("second", testNode);
	}
	
	@Test
	public void testCreateNewTypeNode() {
		assertNotNull(testNode);
		assertTrue("TestNode should have no parent.", testNode.getParent()==null);
		assertTrue("TestNode has wrong name.", testNode.getName().equals("test"));
		assertTrue("TestNode shouldn't have a level.",testNode.getLevel()==-1);
		assertTrue("TestNode should have no children.", testNode.getChildren().size()==0);
		
		assertNotNull(secondNode);
		assertTrue("SecondNode should have parent 'testNode'.",secondNode.getParent().equals(testNode));
	}
	
	@Test
	public void testSetGetLevel() {
		assertNotNull(testNode);
		assertTrue("TestNode shouldn't have a level.",testNode.getLevel()==-1);
		testNode.setLevel(1);
		assertTrue("TestNode should have level 1.",testNode.getLevel()==1);
	}
	
	@Test
	public void testAddGetChildren() {
		assertTrue("TestNode should have no children.", testNode.getChildren().size()==0);
		testNode.addChild(new TypeNode("boing", testNode));
		assertTrue("TestNode should have 1 child.", testNode.getChildren().size()==1);
		assertTrue("TestNode's child should be 'boing'.", testNode.getChildren().get(0).getName().equals("boing"));
	}
	
	@Test
	public void testHasInSubtree() {
		TypeNode boingNode = new TypeNode("boing", testNode);
		TypeNode huhuNode = new TypeNode("huhu", boingNode);
		
		testNode.setLevel(0);
		secondNode.setLevel(1);
		boingNode.setLevel(1);
		huhuNode.setLevel(2);
		
		boingNode.addChild(huhuNode);
		testNode.addChild(boingNode);
		testNode.addChild(secondNode);
		
		assertFalse("HuhuNode should not be in Subtree of secondNode.", secondNode.hasInSubtree(huhuNode));
		assertTrue("HuhuNode should be in Subtree of boingNode.", boingNode.hasInSubtree(huhuNode));
		assertTrue("HuhuNode should be in Subtree of testNode.", testNode.hasInSubtree(huhuNode));
	}
}
