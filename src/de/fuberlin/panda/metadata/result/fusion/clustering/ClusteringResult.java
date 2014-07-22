package de.fuberlin.panda.metadata.result.fusion.clustering;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is a simple data class which holds the clustering result. There
 * are three different lists which represent the result. The {@code noTimestamps}
 * respectively {@code outliers} list consist of the metadata indices of the objects
 * which don't have a timestamp respectively are outliers. The {@code clusters} list
 * holds lists of metadata indices for every detected cluster.
 * 
 * @author Sebastian Schulz
 * @since 25.02.2014
 */
public class ClusteringResult {
	private List<Integer> outliers = new ArrayList<>();
	private List<Integer> noMetadata = new ArrayList<>();
	private List<ArrayList<Integer>> clusters = new ArrayList<ArrayList<Integer>>();
	
	private int clusterId = -1;
	
	public void addOutlier(int metadataIndex) {
		outliers.add(metadataIndex);
	}
	
	public List<Integer> getOutliers() {
		return outliers;
	}
	
	public void addNoMetadata(int metadataIndex) {
		noMetadata.add(metadataIndex);
	}
	
	public List<Integer> getNoMetadata() {
		return noMetadata;
	}
	
	public void addToExistingCluster(int metadataIndex) {
		clusters.get(clusterId).add(metadataIndex);
	}
	
	public void addToNewCluster(int metadataIndex) {
		clusterId++;
		ArrayList<Integer> clusterList = new ArrayList<>();
		clusterList.add(metadataIndex);
		clusters.add(clusterList);
	}
	
	public List<ArrayList<Integer>> getClusters() {
		return clusters;
	}
}
