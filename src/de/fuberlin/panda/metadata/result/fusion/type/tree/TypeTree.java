package de.fuberlin.panda.metadata.result.fusion.type.tree;

import java.util.HashMap;

import newick.NewickParser.TreeNode;
import de.fuberlin.panda.metadata.result.fusion.helper.MatchingHelper;

/**
 * This class represents a tree representation needed to perform the 
 * type metadata fuison. It contains the necessary methods to parse the 
 * tree from a tree format build by the uses {@code NewickParser}. It 
 * also provides the methods to retrieve an arbitary node of the tree and
 * to find the nearest common parent of two tree objects.
 * 
 * @see #TypeTree(TreeNode)
 * @see #parseSubTree(TypeNode, TreeNode)
 * @see #getNextParent(String, String)
 * 
 * @author Sebastian Schulz
 * @since 30.03.2014
 */
public class TypeTree {
	private int size = 0;
	private int currentLevel = 0;
	private HashMap<String, TypeNode> treeMap = new HashMap<>();
	
	/**
	 * The constructor transforms the given {@code TreeNode} object
	 * to another in memory format. Therefore it calls the recursive
	 * {@link #parseSubTree(TypeNode, TreeNode)} method.
	 * 
	 * @param parsedTree - a {@code TreeNode} object which contains 
	 * 	a whole tree parsed from the newick string before.
	 */
	public TypeTree(TreeNode parsedTree) {
		TypeNode root = new TypeNode(parsedTree.getName(), null);
		root.setLevel(currentLevel);
		addNode(root);
		parseSubTree(root, parsedTree);
	}

	private void addNode(TypeNode node) {
		treeMap.put(node.getName(), node);
		size++;
	}
	
	/**
	 * This recursive method parses a subtree of the given {@code TypeNode}
	 * object. The second parameter ({@code TreeNode} object) represents the 
	 * remaining subtree parsed from the newick string.
	 * 
	 * @param node - a {@code TypeNode} object which represents the node which is
	 * 	currently transformed from the parsed newick tree.
	 * @param subTree - a {@code TreeNode} object which represents the remaining
	 * 	subtree parsed from the newick string.
	 */
	private void parseSubTree(TypeNode node, TreeNode subTree) {
		for (TreeNode childSubTree : subTree.getChildren()) {
			currentLevel++;
			
			boolean isInTree = MatchingHelper.contains(treeMap.keySet(), childSubTree.getName());
			if (!isInTree) {
				TypeNode childNode = new TypeNode(childSubTree.getName(), node);
				childNode.setLevel(currentLevel);
				addNode(childNode);
				
				node.addChild(childNode);
				parseSubTree(childNode, childSubTree);
			}
			
			currentLevel--;
		}
	}
	
	/**
	 * This method retrieves the name of the first node which subtree contains
	 * the first node as well as the second node. Both nodes are given by their
	 * {@code names}.
	 * 
	 * @param firstNodeName - a {@code String} value representing a node name.
	 * @param secondNodeName - a {@code String} value representing a node name.
	 * @return parentNodeName - a {@code String} value representing a node name of
	 *  the nearest parent which is the root of the subtree which contains both given
	 *  nodes.
	 */
	public String getNextParent(String firstNodeName, String secondNodeName) {
		if (firstNodeName.equals(secondNodeName)) {
			return firstNodeName;
		}
		
		String parentNodeName = null;
		TypeNode node = get(firstNodeName);
		TypeNode secondNode = get(secondNodeName);
		
		if (node.hasInSubtree(secondNode)) {
			parentNodeName = node.getName();
		} else if (secondNode.hasInSubtree(node)) {
			parentNodeName = secondNode.getName();
		} else {
			while (node.getParent() != null) {
				node = node.getParent();
				if (node.hasInSubtree(secondNode)) {
					parentNodeName = node.getName();
					break;
				}
			}
		}
		
		return parentNodeName;
	}
	
	public TypeNode get(String nodeName) {
		return treeMap.get(nodeName);
	}
	
	public int getSize() {
		return size;
	}
}
