package de.fuberlin.panda.metadata.result.fusion.type.tree;

import java.util.ArrayList;

/**
 * This class represents the in memory format of a node inside a tree.
 * It is a simle data class which holds the name, the level, the parent
 * node and a {@code List} of children. Additional it implements a method
 * to check if a given {@code TypeNode} object is in a subtree where this
 * object is the root.
 * 
 * @see #hasInSubtree(TypeNode)
 *  
 * @author Sebastian Schulz
 * @since 30.03.2014
 */
public class TypeNode {
	private String name;
	private int level = -1;
	
	private TypeNode parent;
	private ArrayList<TypeNode> children = new ArrayList<TypeNode>();
     
	public TypeNode(String nodeName, TypeNode parentNode) {
		name = nodeName;
		parent = parentNode;
	}
	
	public void addChild(TypeNode childNode) {
		children.add(childNode);
	}
	
	public void setLevel(int level) {
		this.level = level;
	}
	
	/**
	 * This method recursively checks if the given {@code TypeNode}
	 * is in the subtree. The root node for this subtree is represented
	 * by this {@code TypeNode} object. Notice that this method returns 
	 * false if the given {@code node} is equal to this object. 
	 * 
	 * @param node - a {@code TypeNode} object.
	 * @return a {@code boolean} value.
	 */
	public boolean hasInSubtree(TypeNode node) {
		if (this.equals(node) || level >= node.getLevel()) {
			return false;
		} else {
			for (TypeNode childNode : children) {
				if(childNode.equals(node)) {
					return true;
				} else {
					if (childNode.hasInSubtree(node)) {
						return true;
					}
				}
			}
			return false;
		}
	}
	
	public String getName() {
		return name;
	}
	
	public int getLevel() {
		return level;
	}
	
	public TypeNode getParent() {
		return parent;
	}

	public ArrayList<TypeNode> getChildren() {
		return children;
	}
}
