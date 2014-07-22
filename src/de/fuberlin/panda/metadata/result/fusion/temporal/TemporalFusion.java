package de.fuberlin.panda.metadata.result.fusion.temporal;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

import de.fuberlin.panda.metadata.MetadataConnector;
import de.fuberlin.panda.metadata.descriptive.TemporalScope;
import de.fuberlin.panda.metadata.parsedMetadata.ParsedMetadata;
import de.fuberlin.panda.metadata.result.MetadataResult;
import de.fuberlin.panda.metadata.result.fusion.FusionResult;
import de.fuberlin.panda.metadata.result.fusion.clustering.ClusteringResult;
import de.fuberlin.panda.metadata.result.fusion.helper.MatchingHelper;
import de.fuberlin.panda.metadata.result.fusion.helper.MetadataTreeSet;
import de.fuberlin.panda.metadata.result.fusion.helper.NoMetadataObject;

/**
 * This class is responsible for fusing temporal metadata. Therefore the metadata 
 * information corresponding to every URI are added to the {@code metadata}
 * set by calling {@link #add(int, ParsedMetadata)}. After all metadata information
 * is added the {@link #fuse(HashMap)} is called. It fulfills the OPTICS OF algorithm
 * to generate a clustering. At the end the 
 * {@link #createTemporalProperty(ClusteringResult, HashMap)} method builds the 
 * {@code scopeResource} which contains the fused temporal metadata information 
 * to add them to the {@code metadataModel} later.
 * 
 * @see #TemporalFusion(Model, String)
 * @see #add(int, ParsedMetadata)
 * @see #getTemporalTimestamp(ParsedMetadata)
 * @see #setBoundaries(long)
 * @see #fuse(HashMap)
 * @see #calculateNeighborhoodRadius(Set)
 * @see #calculateMinNeighbors(Set)
 * @see #createTemporalProperty(ClusteringResult, HashMap)
 * @see #addNoTimestampProperty(ParsedMetadata)
 * @see #addOutlierProperty(ParsedMetadata)
 * @see #addClusterProperty(ArrayList, HashMap, int)
 * @see #updateClusterMinTimestamp(String, TemporalScope)
 * @see #updateClusterMaxTimestamp(String, TemporalScope)
 * @see #resetClusterTimestampBoundaries()
 * 
 * @since 24.02.2014
 * @author Sebastian Schulz
 */
public class TemporalFusion {
	private static Logger logger = Logger.getLogger(MetadataConnector.class.getName());
	
	//temporal fusion
	private MetadataTreeSet<TemporalClusteringObject> metadata;
	private int noTimestampCount = 0;
	private long minTimestamp = Long.MAX_VALUE;
	private long maxTimestamp = Long.MIN_VALUE;
	
	//resource building
	private final Resource TEMPORAL_NODETYPE = ResourceFactory.createResource(MetadataResult.METADATA_NS + "Temporal");
	private final Property CLUSTER_PROPERTY = ResourceFactory.createProperty(MetadataResult.METADATA_NS, "Cluster"); 
	private Model metadataModel;
	private Resource scopeResource;
	private long clusterMinTimestamp = Long.MAX_VALUE;
	private long clusterMaxTimestamp = Long.MIN_VALUE;
	
	/**
	 * Public constructor which creates a new {@code Resource} for the {@code scopeResource}.
	 * It also initializes the {@code metadata TreeSet}.
	 * 
	 * @param metadataModel - the current {@code Model} passed by the {@link FusionResult}.
	 * @param fusionId - a {@code String} which represents the current fusion identifier.
	 */
	public TemporalFusion(Model metadataModel, String fusionId) {
		this.metadataModel = metadataModel;
		
		scopeResource = this.metadataModel.createResource(MetadataResult.METADATA_NS + "ts_" +
				fusionId, TEMPORAL_NODETYPE);
		TreeSet<TemporalClusteringObject> metadataTreeSet = new TreeSet<>(new TimestampComparator());
		metadata = new MetadataTreeSet<>(metadataTreeSet);
	}
	
	/**
	 * This method builds a {@code TemporalClusteringObject} with metadata index and
	 * the timestamp parsed by {@link #getTemporalTimestamp(ParsedMetadata)} method. Afterwards 
	 * it adds the newly created object to the {@code temporalMetadata} set.
	 * 
	 * @param metadataIndex - an {@code int} value which represents the metadata index of the 
	 * 	also given {@link ParsedMetadata} object.
	 * @param singleMetadata - a {@code ParsedMetadata} object which contains the timestamp.
	 */
	public void add(int metadataIndex, ParsedMetadata singleMetadata) {
		TemporalClusteringObject temporalClusteringObject = 
				new TemporalClusteringObject(metadataIndex, getTemporalTimestamp(singleMetadata));
		metadata.add(temporalClusteringObject);
	}
	
	/**
	 * This method returns the timestamp of a given {@code ParsedMetadata} object as a {@code long}
	 * value or -1, in case the object has no timestamp metadata. If a timestamp is valid, the 
	 * {@link #setBoundaries(long)} method is called to set the {@code minTimestamp} and/or
	 * {@code maxTimestamp} as appropriate.
	 * 
	 * @param singleMetadata - a {@code ParsedMetadata} object.
	 * @return a {@code long} value which represents the timestamp of the given {@code ParsedMetadata}
	 * 	object.
	 */
	private long getTemporalTimestamp(ParsedMetadata singleMetadata) {
		try {
			long timestamp = singleMetadata.getTemporalScope().getExpirationDate().getTime();
			setBoundaries(timestamp);
			return timestamp;
		} catch (NullPointerException e) {
			return -1;
		}
	}
	
	/**
	 * This method checks whether the given timestamp is one of the boundaries. If it is smaller
	 * then the {@code minTimestamp} it becomes the new {@code minTimestamp}. Same for the 
	 * {@code maxTimestamp}. Notice that this method is only called if a real timestamp exists, so
	 * -1 is never a boundary.
	 * 
	 * @param timestamp - a {@code long} value which represents the timestamp in milliseconds.
	 */
	private void setBoundaries(long timestamp) {
		if(timestamp < minTimestamp) {
			minTimestamp = timestamp;
		} 
		if(timestamp > maxTimestamp) {
			maxTimestamp = timestamp;
		}
		logger.debug("Calculated boundary timestamps");
	}
	
	/**
	 * This method fuses the temporal properties of all chosen metadata information and 
	 * calls the {@link #createTemporalProperty(ClusteringResult, HashMap)} method to append
	 * a RDF representation to the {@code scopeResource}. At the beginning the 
	 * {@code neighborhoodRadius} and the {@code minNeighbors} are calculated. They are needed
	 * for the {@code TemporalClusterDetector} which performs the OPTICS OF algorithm. The calling
	 * of {@link TemporalClusterDetector#detectClusters(TreeSet)} does the clustering and the 
	 * {@code ClusteringResult} is passed to the 
	 * {@link #createTemporalProperty(ClusteringResult, HashMap)} method finally. 
	 * 
	 * @param metadataMap - a {@code HashMap} which matches a metadata index to it's corresponding
	 * 	{@link ParsedMetadata} object.
	 */
	public void fuse(HashMap<Integer, ParsedMetadata> metadataMap) {
		double neighborhoodRadius = calculateNeighborhoodRadius();
		int minNeighbors = calculateMinNeighbors();
		int maxOutlierFactor = 5;
		
		logger.debug("Starting temporal OPTICS algorihm (nR:'" + neighborhoodRadius 
				+ "'; mN:'" + minNeighbors + "'; mOF:'" + maxOutlierFactor + "')");
		TemporalClusterDetector temporalClusterDetector = new TemporalClusterDetector(neighborhoodRadius, 
				minNeighbors, 5);
		ClusteringResult clusteringResult = temporalClusterDetector.detectClusters(metadata);
		logger.debug("--> Successfully performed temporal OPTICS clustering!");
		
		createTemporalProperty(clusteringResult, metadataMap);
		logger.info("--> Successfully created Temporal Fusion Resource!");
	}
	
	/**
	 * This method is responsible for calculating the neighborhood radius. This value represents the
	 * distance in which the OPTICS-OF algorithm searches for neighbors later.
	 * 
	 * @return the neighborhood radius - a {@code double} value.
	 */
	private double calculateNeighborhoodRadius() {
		Iterator<TemporalClusteringObject> iterator = metadata.iterator();
		while (iterator.hasNext()) {
			TemporalClusteringObject temporalClusteringObject = iterator.next();
			if(temporalClusteringObject.getTimestamp()==-1) {
				noTimestampCount++;
			} else {
				break;
			}
		}
		
		double neighborhoodRadius = (maxTimestamp - minTimestamp) / (metadata.size() - noTimestampCount);
		return neighborhoodRadius;
	}
	
	/**
	 * This method returns the minimal amount of neighbors to identify a cluster according to the
	 * count of elements the OPTICS algorithm is performed with.
	 * 
	 * @return a {@code int} value.
	 */
	private int calculateMinNeighbors() {
		int opticsMetadataElements = metadata.size() - noTimestampCount;
		if (opticsMetadataElements <=  20 ) {
			return 2;
		} else if (opticsMetadataElements <= 50) {
			return 3;
		} else if (opticsMetadataElements < 100) {
			return 4;
		} else {
			return 5;
		}
	}

	/**
	 * This method is responsible for adding the temporal metadata information to the temporal
	 * scope property. Therefore it iterates over the {@code noMetadata}, {@code outliers} and
	 * {@code clusters} lists and calls the specific methods to add the metadata to the 
	 * {@code scopeResource}.
	 * 
	 * @param clusteringResult - the {@code ClusteringResult} object created by the OPTICS OF 
	 * 	algorithm containing the lists of  {@code noMetadata}, {@code outliers} and
	 * 	{@code clusters}.
	 * @param metadataMap - a {@code HashMap} which matches the metadata index to a corresponding
	 * 	{@code ParsedMetadata} object. 
	 */
	private void createTemporalProperty(ClusteringResult clusteringResult, 
			HashMap<Integer, ParsedMetadata> metadataMap) {
		NoMetadataObject noMetadata = getNoMetadataEntries(clusteringResult, metadataMap);
		addSingleNoTypeProperties(noMetadata);
		addGroupNoTypeProperties(noMetadata);
		
		Iterator<Integer> outlierIterator = clusteringResult.getOutliers().iterator();
		while (outlierIterator.hasNext()) {
			ParsedMetadata clusteringObjectsMetadata = metadataMap.get(outlierIterator.next());
			addOutlierProperty(clusteringObjectsMetadata);
		}
		
		Iterator<ArrayList<Integer>> clusterIterator = clusteringResult.getClusters().iterator();
		int clusterNumber = 1;
		while (clusterIterator.hasNext()) {
			addClusterProperty(clusterIterator.next(), metadataMap, clusterNumber);
			clusterNumber++;
		}
	}

	/**
	 * This method transforms the metadata indices given by the {@code ClusteringResult} 
	 * object to the actual URIs. The list is used to call the 
	 * {@link MatchingHelper#groupNoMetadataUris(java.util.List)} to obtain the returned
	 * {@code NoMetadataObject}.
	 *  
	 * @param clusteringResult - the {@code ClusteringResult} object created by the OPTICS OF 
	 * 	algorithm containing the lists of  {@code noMetadata}, {@code outliers} and
	 * 	{@code clusters}.
	 * @param metadataMap - a {@code HashMap} which matches the metadata index to a corresponding
	 * 	{@code ParsedMetadata} object. 
	 * @return a {@code NoMetadataObject}.
	 */
	private NoMetadataObject getNoMetadataEntries(ClusteringResult clusteringResult,
			HashMap<Integer, ParsedMetadata> metadataMap) {
		ArrayList<URI> noMetadataUris = new ArrayList<URI>();
		Iterator<Integer> noTimestampIterator = clusteringResult.getNoMetadata().iterator();
		while (noTimestampIterator.hasNext()) {
			ParsedMetadata clusteringObjectsMetadata = metadataMap.get(noTimestampIterator.next());
			noMetadataUris.add(URI.create(clusteringObjectsMetadata.getUri()));
		}
		
		return MatchingHelper.groupNoMetadataUris(noMetadataUris);
	}
	
	/**
	 * This method creates a {@code panda:NoMetadata} property to the 
	 * {@code scopeResource} and puts the "link" to the objects URI to it.
	 * 
	 * @param noMetadata - a {@code NoMetadataObject} all URI withou metadata.
	 */
	private void addSingleNoTypeProperties(NoMetadataObject noMetadata) {
		for (String uri : noMetadata.getSingleUris()) {
			Property temporalProperty = ResourceFactory.createProperty(MetadataResult.METADATA_NS, "NoMetadata");
			Resource temporalResource = metadataModel.createResource(MetadataResult.METADATA_NS + uri);
			scopeResource.addProperty(temporalProperty, temporalResource);
		}
	}
	
	/**
	 * This method creates a subgroup for every group which has no metadata information.
	 * 
	 * @param noMetadata - a {@code NoMetadataObject} all URI withou metadata.
	 */
	private void addGroupNoTypeProperties(NoMetadataObject noMetadata) {
		int i = 1;
		for (String[] uris : noMetadata.getUriGroups()) {
			Resource noMetadataResource = metadataModel.createResource(MetadataResult.METADATA_NS + 
					"ts_noMetadata_" + i);
			
			Property noMetadataProperty = ResourceFactory.createProperty(MetadataResult.METADATA_NS,
					"NoMetadataFrom");
			noMetadataResource.addProperty(noMetadataProperty, uris[0]);
			
			noMetadataProperty = ResourceFactory.createProperty(MetadataResult.METADATA_NS,
					"NoMetadataTo");
			noMetadataResource.addProperty(noMetadataProperty, uris[1]);
			
			scopeResource.addProperty(FusionResult.NO_METADATA_PROPERTY, noMetadataResource);
			i++;
		}
	}
	
	/**
	 * This method creates a {@code panda:Outlier} property to the {@code scopeResource}.
	 * Therefore a expiration date is added to this outlier property. 
	 * 
	 * @param clusteringObjectsMetadata - the {@code ParsedMetadata} object which contains all the 
	 * 	metadata information regarding a specific URI.
	 */
	private void addOutlierProperty(ParsedMetadata clusteringObjectsMetadata) {
		Property temporalProperty = ResourceFactory.createProperty(MetadataResult.METADATA_NS, "Outlier");
		Resource temporalResource = metadataModel.createResource(MetadataResult.METADATA_NS + "ts" +
				clusteringObjectsMetadata.getUri());
		Property dataProperty = ResourceFactory.createProperty(MetadataResult.METADATA_NS, "CreationDate");
		String expirationDateString = clusteringObjectsMetadata.getTemporalScope().getExpirationDateString();
		temporalResource.addProperty(dataProperty, expirationDateString);
		scopeResource.addProperty(temporalProperty, temporalResource);
	}
	
	/**
	 * This method adds the cluster properties to the {@code scopeResource}. First
	 * of all it generates the minimum and maximum date of a cluster by iteration all 
	 * metadata indices and calling {@link #updateClusterMinTimestamp(String, TemporalScope)}
	 * respectivly {@link #updateClusterMaxTimestamp(String, TemporalScope)}. Afterwards a 
	 * new {@code Resource} object with maximum and minimum dates is created and added to the 
	 * {@code scopeResource}.
	 * 
	 * @param clusterIndices - an {@code ArrayList} of {@code int} values which represent all 
	 * 	metadata indices of objects in the current cluster.
	 * @param metadataMap - a {@code HashMap} which matches a metadata index to it's corresponding
	 * 	{@link ParsedMetadata} object.
	 * @param clusterNumber - an {@code int} value which represents the number of the current cluster.
	 */
	private void addClusterProperty(ArrayList<Integer> clusterIndices, HashMap<Integer, ParsedMetadata> metadataMap, 
			int clusterNumber) {
		String clusterMinDate = "";
		String clusterMaxDate = "";
		
		for (Integer metadataIndex : clusterIndices) {
			TemporalScope temporalScope = metadataMap.get(metadataIndex).getTemporalScope();
			clusterMinDate = updateClusterMinTimestamp(clusterMinDate, temporalScope);
			clusterMaxDate = updateClusterMaxTimestamp(clusterMaxDate, temporalScope);
		}
		
		Resource clusterResource = metadataModel.createResource(MetadataResult.METADATA_NS + 
				"Cluster_" + clusterNumber);
		Property boundaryProperty = ResourceFactory.createProperty(MetadataResult.METADATA_NS, "StartCreationDate");
		clusterResource.addProperty(boundaryProperty, clusterMinDate);
		boundaryProperty = ResourceFactory.createProperty(MetadataResult.METADATA_NS, "EndCreationDate");
		clusterResource.addProperty(boundaryProperty, clusterMaxDate);
		scopeResource.addProperty(CLUSTER_PROPERTY, clusterResource);
		
		resetClusterTimestampBoundaries();
	}

	/**
	 * This method updates the {@code clusterMinTimestamp}. If the timestamp of the given
	 * {@code TemporalScope} object is smaller than the {@code clusterMinTimestamp} the new
	 * minimum is set and the timestamp is returned as a {@code String} value. Otherwise the 
	 * minimum {@code String} stays the same.
	 *  
	 * @param clusterMinDate - a {@code String} value which represents the current minimum date.
	 * @param temporalScope - the current {@code TemporalScope} object.
	 * @return a {@code String} value which represents the updated minimum date.
	 */
	private String updateClusterMinTimestamp(String clusterMinDate, TemporalScope temporalScope) {
		long timestamp = temporalScope.getExpirationDate().getTime();
		if(timestamp < clusterMinTimestamp) {
			clusterMinTimestamp = timestamp;
			clusterMinDate = temporalScope.getExpirationDateString();
		} 
		return clusterMinDate;	
	}
	
	/**
	 * This method updates the {@code clusterMaxTimestamp}. If the timestamp of the given
	 * {@code TemporalScope} object is greater than the {@code clusterMaxTimestamp} the new
	 * maximum is set and the timestamp is returned as a {@code String} value. Otherwise the 
	 * maximum {@code String} stays the same.
	 *  
	 * @param clusterMaxDate - a {@code String} value which represents the current maximum date.
	 * @param temporalScope - the current {@code TemporalScope} object.
	 * @return a {@code String} value which represents the updated maximum date.
	 */
	private String updateClusterMaxTimestamp(String clusterMaxDate, TemporalScope temporalScope) {
		long timestamp = temporalScope.getExpirationDate().getTime();
		if(timestamp > clusterMaxTimestamp) {
			clusterMinTimestamp = timestamp;
			clusterMaxDate = temporalScope.getExpirationDateString();
		} 
		return clusterMaxDate;	
	}
	
	/**
	 * This method simply resets the {@code clusterMaxTimestamp} to {@code Long.MIN_VALUE}
	 * and the {@code clusterMinTimestamp} to {@code Long.MAX_VALUE}.
	 */
	private void resetClusterTimestampBoundaries() {
		clusterMinTimestamp = Long.MAX_VALUE;
		clusterMaxTimestamp = Long.MIN_VALUE;
	}
	
	public Resource getResource() {
		return scopeResource;
	}
}
