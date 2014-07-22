package de.fuberlin.panda.metadata.result.fusion.areal;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

import de.fuberlin.panda.metadata.MetadataConnector;
import de.fuberlin.panda.metadata.descriptive.ArealScope;
import de.fuberlin.panda.metadata.parsedMetadata.ParsedMetadata;
import de.fuberlin.panda.metadata.result.MetadataResult;
import de.fuberlin.panda.metadata.result.fusion.FusionResult;
import de.fuberlin.panda.metadata.result.fusion.areal.geocoding.Geocoder;
import de.fuberlin.panda.metadata.result.fusion.clustering.ClusteringResult;
import de.fuberlin.panda.metadata.result.fusion.helper.MatchingHelper;
import de.fuberlin.panda.metadata.result.fusion.helper.MetadataTreeSet;
import de.fuberlin.panda.metadata.result.fusion.helper.NoMetadataObject;

/**
 * This class is responsible for fusing areal metadata. Therefore the metadata 
 * information corresponding to every URI are added to the {@code metadata}
 * set by calling {@link #add(int, ParsedMetadata)}. After all metadata information
 * is added the {@link #fuse(HashMap)} is called. It fulfills the OPTICS OF algorithm
 * to generate a clustering. At the end the 
 * {@link #createArealProperty(ClusteringResult, HashMap)} method builds the 
 * {@code arealScopeResource} which contains the fused areal metadata information 
 * to add them to the {@code metadataModel} later.
 * 
 * @see #ArealFusion(Model, String)
 * @see #add(int, ParsedMetadata)
 * @see #mergePolygonList(GeoArea)
 * @see #addArealClusterObject(int, Double[])
 * @see #fuse(HashMap)
 * @see #updatePolygons(ClusteringResult)
 * @see #getConvexPolygon(ArrayList)
 * @see #mergePolygonList(GeoArea)
 * @see #calculateNeighborhoodRadius()
 * @see #calculateReferenceDistance(GeoArea)
 * @see #calculateMinNeighbors()
 * @see #createArealProperty(ClusteringResult, HashMap)
 * @see #addNoGeoCoordsProperty(ParsedMetadata)
 * @see #addOutlierProperty(String, ArealClusteringObject)
 * @see #addPolygonProperty(GeoArea)
 * 
 * @author Sebastian Schulz
 * @since 23.02.2014
 */
public class ArealFusion {
	private static Logger logger = Logger.getLogger(MetadataConnector.class.getName());
	
	//areal fusion
	private MetadataTreeSet<ArealClusteringObject> metadata;
	private List<GeoArea> polygonList = new ArrayList<GeoArea>();
	private int noGeoCoordsCount = 0;
	
	//resource building
	private final Resource AREAL_NODETYPE = ResourceFactory.createResource(MetadataResult.METADATA_NS + "Areal");
	private final Property AREA_PROPERTY = ResourceFactory.createProperty(MetadataResult.METADATA_NS, "Area");
	private Model metadataModel;
	private Resource scopeResource;
	
	/**
	 * Public constructor which creates a new {@code Resource} for the {@code arealScopeResource}.
	 * 
	 * @param metadataModel - the current {@code Model} passed by the {@link FusionResult}.
	 * @param fusionId - a {@code String} which represents the current fusion identifier.
	 */
	public ArealFusion(Model metadataModel, String fusionId) {
		this.metadataModel = metadataModel;
		
		scopeResource = this.metadataModel.createResource(MetadataResult.METADATA_NS + "as_" +
				fusionId, AREAL_NODETYPE);
		TreeSet<ArealClusteringObject> metadataTreeSet = new TreeSet<>(new GeoCoordinatesComparator());
		metadata = new MetadataTreeSet<>(metadataTreeSet);
	}

	/**
	 * This method is responsible for analyzing the priority of the areal scope definition
	 * of the given {@code ParsedMetadata} object. First the {@code ArealScope} is parsed. If
	 * it has an location attribute a new {@code Geocoder} is initialized. In case the geocoding
	 * result is an polygon this polygon is added to the {@code polygonList}. Otherwise if the 
	 * {@code arealScope} has no geographical coordinates but the location could be transformed to
	 * coordinates this coordinates are used for further processing. <br><br>
	 * If no polygon could be builded by geocoding or the {@code arealScope} has no location 
	 * attribute the {@link #addArealClusterObject(int, Double[])} is called to add the current
	 * object to the {@code metadata} list for further processing by the optics algorithm.
	 *
	 * @param metadataIndex - an {@code int} value which represents the metadata index of the 
	 * 	also given {@link ParsedMetadata} object.
	 * @param singleMetadata - a {@code ParsedMetadata} object which contains the location attribute
	 * 	as well as the latitude and longitude information in it's {@code ArealScope}. 
	 */
	public void add(int metadataIndex, ParsedMetadata singleMetadata) {
		ArealScope arealScope = singleMetadata.getArealScope();
		Double[] geoCoord = new Double[2];
		geoCoord[0] = arealScope.getLongitude();
		geoCoord[1] = arealScope.getLatitude();
		
		if (arealScope.hasLocation()) {
			Geocoder geocoder = new Geocoder(arealScope.getLocation());
			
			if (geocoder.isResultPolygon()) {
				logger.debug("URI: " + singleMetadata.getUri() + "; \t geocoding result is polygon");
				mergePolygonList(geocoder.getPolygon());
			} else {
				if (!arealScope.hasGeoCoords() && geocoder.isResultGeoCoord()) {
					geoCoord = geocoder.getGeoCoords();
					logger.debug("URI: " + singleMetadata.getUri() + "; \t updated geographical coordinates");
				} 
				addArealClusterObject(metadataIndex, geoCoord);
			}
		} else {
			addArealClusterObject(metadataIndex, geoCoord);
		}
	}

	/**
	 * This method merges a given {@code GeoArea} object with the {@code polygonList}. It
	 * iterates over the list and check if the given polygon contains a part of the current
	 * polygon from the list or other way around. In this case a {@code mergedPolygon} is 
	 * created and the polygons to be removed are saved in a several list. Finally all of 
	 * them are removed from the {@code polygonList} and the newly merged Polygon is added. 
	 * 
	 * @param currentPolygon - a {@code GeoArea} object.
	 */
	private void mergePolygonList(GeoArea currentPolygon) {
		boolean isMerged = false;
		GeoArea mergedPolygon = new GeoArea();
		ArrayList<GeoArea> polygonsToRemove = new ArrayList<>();
		
		Iterator<GeoArea> polygonIterator = polygonList.iterator();
		while (polygonIterator.hasNext()) {
			GeoArea storedPolygon = polygonIterator.next();
			boolean doPolygonsIntersect = storedPolygon.containsPartOf(currentPolygon)
					|| currentPolygon.containsPartOf(storedPolygon);
			if (doPolygonsIntersect) {
				mergedPolygon.add(storedPolygon);
				polygonsToRemove.add(storedPolygon);
				
				if (!isMerged) {
					mergedPolygon.add(currentPolygon);
				}
				
				isMerged = true;
			}
		}
		
		polygonList.removeAll(polygonsToRemove);
		
		if (!isMerged) {
			polygonList.add(currentPolygon);
		} else {
			polygonList.add(mergedPolygon);
		}
		logger.debug("Polygons were merged");
	}
	
	/**
	 * This method creates a new {@code ArealClusteringObject} with the given 
	 * geographical coordinates and adds it to the {@code metadata TreeSet}. In case there are
	 * no coordinates defined the object is initialized with NULL and filtered out by the Optics
	 * algorithm.
	 * 
	 * @param metadataIndex - an {@code int} value which represents the metadata index of the 
	 * 	also given {@link ParsedMetadata} object.
	 * @param geoCoord - a {@code Double Array} which represents the latitude and the longitude
	 * 	value of the created {@code ArealClusteringObject}.
	 */
	private void addArealClusterObject(int metadataIndex, Double[] geoCoord) {
		ArealClusteringObject arealClusteringObject = 
				new ArealClusteringObject(metadataIndex, geoCoord[0], geoCoord[1]);
		metadata.add(arealClusteringObject);
	}

	/**
	 * This method fuses the areal properties of all chosen metadata information and 
	 * calls the {@link #createArealProperty(ClusteringResult, HashMap)} method to append
	 * a RDF representation to the {@code arealScopeResource}. At the beginning the 
	 * {@code neighborhoodRadius} and the {@code minNeighbors} are calculated. They are needed
	 * for the {@code ArealClusterDetector} which performs the OPTICS OF algorithm. The calling
	 * of {@link ArealClusterDetector#detectClusters(TreeSet)} does the clustering and the 
	 * {@code ClusteringResult} is passed to the 
	 * {@link #createArealProperty(ClusteringResult, HashMap)} method after processing the found
	 * clusters with the methods {@link #updatePolygons(ClusteringResult)} and 
	 * {@link #mergePolygonListWithOutliers(ClusteringResult)}. 
	 * 
	 * @param metadataMap - a {@code HashMap} which matches a metadata index to it's corresponding
	 * 	{@link ParsedMetadata} object.
	 */
	public void fuse(HashMap<Integer, ParsedMetadata> metadataMap) {
		if (metadata.size() > 0) {
			double neighborhoodRadius = calculateNeighborhoodRadius();
			int minNeighbors = calculateMinNeighbors();
			int maxOutlierFactor = 5;
			
			logger.debug("Starting areal OPTICS algorihm (nR:'" + neighborhoodRadius 
					+ "'; mN:'" + minNeighbors + "'; mOF:'" + maxOutlierFactor + "')");
			ArealClusterDetector arealClusterDetector = new ArealClusterDetector(neighborhoodRadius, 
					minNeighbors, maxOutlierFactor);
			ClusteringResult clusteringResult = arealClusterDetector.detectClusters(metadata);
			logger.debug("--> Successfully performed areal OPTICS clustering!");
			
			updatePolygons(clusteringResult);
			mergePolygonListWithOutliers(clusteringResult);
			
			createArealProperty(clusteringResult, metadataMap);
		} else {
			for (GeoArea polygon : polygonList) {
				addPolygonProperty(polygon);
			}
		}
		
		logger.info("--> Successfully created Areal Fusion Resource!");
	}
	
	/**
	 * This method iterates over the cluster list of the given {@code ClusteringResult}
	 * and calls the {@link #getConvexPolygon(ArrayList)} method to retrieve the polygon.
	 * Afterwards it is check if this polygon is already stored in the {@code polygonList}
	 * by calling {@link #mergePolygonList(GeoArea)}.
	 * 
	 * @param clusteringResult - a {@code ClusteringResult} object which represents the result
	 * 	of the OPTICS OF algorithm.
	 */
	private void updatePolygons(ClusteringResult clusteringResult) {
		Iterator<ArrayList<Integer>> clusterIterator = clusteringResult.getClusters().iterator();
		while (clusterIterator.hasNext()) {
			GeoArea clusterPolygon = getConvexPolygon(clusterIterator.next());
			String coordinates = Double.toString(clusterPolygon.getBoundaryPoints().get(0).x) +"_" 
					+ Double.toString(clusterPolygon.getBoundaryPoints().get(0).y);
			clusterPolygon.setName("Cluster_" + coordinates);
			mergePolygonList(clusterPolygon);
		}
	}
	
	/**
	 * This method transforms the cluster of points given by an 
	 * {@code ArrayList} of their indices to an convex {@code GeoArea} object.
	 * After retrieving all {@code ArealClusteringObject}s addicted to the indices
	 * a new {@link ConvexHull} object is created to determine the polygon.
	 * 
	 * @param clusterIndices - an {@code ArrayList} representing the indices of 
	 * 	the current cluster.
	 * @return a {@code GeoArea} object which represents the convex polygon.
	 */
	@SuppressWarnings("unchecked")
	private GeoArea getConvexPolygon(ArrayList<Integer> clusterIndices) {
		ArrayList<Integer> decreasingClusterIndices = (ArrayList<Integer>) clusterIndices.clone();
		ArrayList<ArealClusteringObject> currentClusterPoints = new ArrayList<ArealClusteringObject>();
		
		for (ArealClusteringObject arealClusteringObject : metadata) {
			int currentIndex = arealClusteringObject.getMetadataIndex();
			if(decreasingClusterIndices.size() < 1) {
				break;
			} else if (decreasingClusterIndices.contains(currentIndex)) {
				currentClusterPoints.add(arealClusteringObject);
				decreasingClusterIndices.remove((Integer)currentIndex);
			}
		}
		
		ConvexHull convexHull = new ConvexHull(currentClusterPoints);
		logger.debug("--> Successfully created convex hull out of cluster!");
		return convexHull.getPolygon();
	}
	
	/**
	 * This methods checks if an outlier computed by the OPTICS OF algorithm is contained
	 * in one of the polygons from the {@code polygonList}. It iterates over the 
	 * {@code metadata} and checks if the current object is contained in the outlier list
	 * of the given {@code ClusteringResult}. In this case the test if it is contained in
	 * one of the polygons takes place. If necessary the outlier is deleted from the outlier
	 * list. 
	 * 
	 * @param clusteringResult - the {@code ClusteringResult} object created by the OPTICS OF 
	 * 	algorithm containing the lists of  {@code noMetadata}, {@code outliers} and
	 * 	{@code clusters}.
	 */
	private void mergePolygonListWithOutliers(ClusteringResult clusteringResult) {
		List<Integer> outlierIndices = clusteringResult.getOutliers();
		List<Integer> outlierIndicesToRemove = new ArrayList<Integer>();
		int outliersToStay = 0;
		
		for (ArealClusteringObject arealClusteringObject : metadata) {
			int currentIndex = arealClusteringObject.getMetadataIndex();
			if(outlierIndices.size() == (outliersToStay + outlierIndicesToRemove.size())) {
				break;
			} else if (outlierIndices.contains(currentIndex)) {
				Point2D.Double outlier = new Point2D.Double(arealClusteringObject.getLongitude(), 
						arealClusteringObject.getLatitude());
				for (GeoArea polygon : polygonList) {
					if (polygon.contains(outlier.x, outlier.y)) {
						outlierIndicesToRemove.add(currentIndex);
					} else {
						outliersToStay++;
					}
				}
			}
		}
		
		outlierIndices.removeAll(outlierIndicesToRemove);
		logger.debug("--> Successfully merged polygon with outlier!");
	}

	/**
	 * This method is responsible for calculating the neighborhood radius. This value represents the
	 * distance in which the OPTICS-OF algorithm searches for neighbors later.
	 * 
	 * @return the neighborhood radius - a {@code double} value.
	 */
	private double calculateNeighborhoodRadius() {
		ArrayList<ArealClusteringObject> convexHullList = new ArrayList<>();
		
		for (ArealClusteringObject arealClusteringObject : metadata) {
			if((arealClusteringObject.getLatitude() == null)
					|| (arealClusteringObject.getLongitude() == null)) {
				noGeoCoordsCount++;
			} else {
				convexHullList.add(arealClusteringObject);
			}
		}
		
		//create a hull to get the longest possible distance of its bounds 
		ConvexHull convexHull = new ConvexHull(convexHullList);
		GeoArea hullPolygon = convexHull.getPolygon();
		double referenceDistance = calculateReferenceDistance(hullPolygon);
		
		double neighborhoodRadius = referenceDistance / (metadata.size() - noGeoCoordsCount);
		return neighborhoodRadius;
	}
	
	/**
	 * This method calculates the distance from the lower left point of the bounding
	 * box of the given {@code GeoArea} to its upper right point.
	 * 
	 * @param hullPolygon - a {@code GeoArea} object.
	 * @return a {@code double} value which represents the distance.
	 */
	private double calculateReferenceDistance(GeoArea hullPolygon) {
		Rectangle2D hullBounds = hullPolygon.getAreaObject().getBounds2D();
		Point2D.Double maxPoint = new Point2D.Double(hullBounds.getMaxX(), hullBounds.getMaxY());
		Point2D.Double minPoint = new Point2D.Double(hullBounds.getMinX(), hullBounds.getMinY());
		
		double distance = Math.sqrt(
				Math.pow(minPoint.x - maxPoint.x,2) +
				Math.pow(minPoint.y - minPoint.y, 2));
		
		return distance; 
	}
	
	/**
	 * This method returns the minimal amount of neighbors to identify a cluster according to the
	 * count of elements the OPTICS algorithm is performed with.
	 * 
	 * @return a {@code int} value.
	 */
	private int calculateMinNeighbors() {
		int opticsMetadataElements = metadata.size() - noGeoCoordsCount;
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
	 * This method is responsible for adding the areal metadata information to the areal
	 * scope property. Therefore it iterates over the {@code noMetadata}, {@code outliers} and
	 * {@code polygons} lists and calls the specific methods to add the metadata to the 
	 * {@code arealScopeResource}.
	 * 
	 * @param clusteringResult - the {@code ClusteringResult} object created by the OPTICS OF 
	 * 	algorithm containing the lists of  {@code noTimestamps}, {@code outliers} and
	 * 	{@code clusters}.
	 * @param metadataMap - a {@code HashMap} which matches the metadata index to a corresponding
	 * 	{@code ParsedMetadata} object. 
	 */
	private void createArealProperty(ClusteringResult clusteringResult, 
			HashMap<Integer, ParsedMetadata> metadataMap) {
		NoMetadataObject noMetadata = getNoMetadataEntries(clusteringResult, metadataMap);
		addSingleNoMetadataProperties(noMetadata);
		addGroupNoMetadataProperties(noMetadata);
		
		int outlierCount = 0;
		List<Integer> outlierIndices = clusteringResult.getOutliers();
		for (ArealClusteringObject possibleOutlier : metadata) {
			int currentIndex = possibleOutlier.getMetadataIndex();
			if(outlierCount == outlierIndices.size()) {
				break;
			} else if(outlierIndices.contains(currentIndex)){
				String uri = metadataMap.get(currentIndex).getUri();
				addOutlierProperty(uri, possibleOutlier);
			}
		}
		
		for (GeoArea polygon : polygonList) {
			addPolygonProperty(polygon);
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
		Iterator<Integer> noGeoCoordsIterator = clusteringResult.getNoMetadata().iterator();
		while (noGeoCoordsIterator.hasNext()) {
			ParsedMetadata clusteringObjectsMetadata = metadataMap.get(noGeoCoordsIterator.next());
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
	private void addSingleNoMetadataProperties(NoMetadataObject noMetadata) {
		for (String uri : noMetadata.getSingleUris()) {
			Property arealProperty = ResourceFactory.createProperty(MetadataResult.METADATA_NS, "NoMetadata");
			Resource arealResource = metadataModel.createResource(MetadataResult.METADATA_NS + uri);
			scopeResource.addProperty(arealProperty, arealResource);
		}
	}
	
	/**
	 * This method creates a subgroup for every group which has no metadata information.
	 * 
	 * @param noMetadata - a {@code NoMetadataObject} all URI withou metadata.
	 */
	private void addGroupNoMetadataProperties(NoMetadataObject noMetadata) {
		int i = 1;
		for (String[] uris : noMetadata.getUriGroups()) {
			Resource noMetadataResource = metadataModel.createResource(MetadataResult.METADATA_NS + 
					"as_noMetadata_" + i);
			
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
	 * This method creates a {@code panda:Outlier} property to the {@code arealScopeResource}.
	 * Therefore the longitude and latitude values are parsed from the given 
	 * {@code ArealClusteringObject}.
	 * 
	 * @param clusteringObjectsMetadata - the {@code ParsedMetadata} object which contains all the 
	 * 	metadata information regarding a specific URI.
	 */
	private void addOutlierProperty(String uri, ArealClusteringObject clusteringObjectsMetadata) {
		Property arealProperty = ResourceFactory.createProperty(MetadataResult.METADATA_NS, "Outlier");
		Resource arealResource = metadataModel.createResource(MetadataResult.METADATA_NS + "as" + uri);
		Property longitudeProperty = ResourceFactory.createProperty(MetadataResult.METADATA_NS, "Longitude");
		Property latitudeProperty = ResourceFactory.createProperty(MetadataResult.METADATA_NS, "Latitude");
		Double longitude = clusteringObjectsMetadata.getLongitude();
		Double latitude = clusteringObjectsMetadata.getLatitude();
		arealResource.addProperty(longitudeProperty, longitude.toString());
		arealResource.addProperty(latitudeProperty, latitude.toString());
		scopeResource.addProperty(arealProperty, arealResource);
	}
	
	/**
	 * This method creates and adds a polygon property. Therefore the polygons name is added. 
	 * (Since the old code which is commented out represents a polygon by all its boundary points.
	 * This method is very verbose, so there is more writing overhead addicted to the complexity 
	 * of the polygon.)
	 * 
	 * @param polygon - a {@code GeoArea} object which represents the polygon.
	 */
	private void addPolygonProperty(GeoArea polygon) {
		Resource polygonResource = metadataModel.createResource(MetadataResult.METADATA_NS + 
				"Polygon_" + polygon.getName());
//		Property boundaryProperty = ResourceFactory.createProperty(MetadataResult.METADATA_NS, "boundaryPoint");
//		Property longitudeProperty = ResourceFactory.createProperty(MetadataResult.METADATA_NS, "Longitude");
//		Property latitudeProperty = ResourceFactory.createProperty(MetadataResult.METADATA_NS, "Latitude");
//		
//		ArrayList<Point2D.Double> boundaryPoints = polygon.getBoundaryPoints();
//		int i = 1;
//		for (Point2D.Double point : boundaryPoints) {
//			Resource arealResource = metadataModel.createResource(MetadataResult.METADATA_NS + 
//						"ts_" + fusionId + "_id_" + i);
//			arealResource.addProperty(longitudeProperty, String.valueOf(point.x));
//			arealResource.addProperty(latitudeProperty, String.valueOf(point.y));
//			polygonResource.addProperty(boundaryProperty, arealResource);
//			i++;
//		}

		scopeResource.addProperty(AREA_PROPERTY, polygonResource);
	}
	
	public Resource getResource() {
		return scopeResource;
	}
}