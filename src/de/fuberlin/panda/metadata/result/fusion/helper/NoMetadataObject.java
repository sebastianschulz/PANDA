package de.fuberlin.panda.metadata.result.fusion.helper;

import java.util.ArrayList;

/**
 * Simple data object for holding a list of groups of URIs
 * represented by their first and last URI and single URIs
 * which can't be summarized in a group.
 * 
 * @author Sebastian Schulz
 * @since 02.04.2014
 */
public class NoMetadataObject {
	private ArrayList<String> singleUris = new ArrayList<String>();
	private ArrayList<String[]> groupUris = new ArrayList<String[]>();
	
	public void addSingleUri(String uri) {
		singleUris.add(uri);
	}
	
	public void addUriGroup(String start, String end) {
		groupUris.add(new String[]{start,end});
	}
	
	public ArrayList<String> getSingleUris() {
		return singleUris;
	}
	
	public ArrayList<String[]> getUriGroups() {
		return groupUris;
	}
}
