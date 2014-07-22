package de.fuberlin.panda.metadata.result.fusion.areal;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import de.fuberlin.panda.metadata.MetadataConnector;

/**
 * Implementation of the quick hull algorithm to compute the convex hull of
 * a set of given points.
 * 
 *  @see #ConvexHull(ArrayList)
 *  @see #createHull(ArrayList)
 *  @see #getExtremalIndices(ArrayList)
 *  @see #getSquaredDistance(Point2D.Double, Point2D.Double, Point2D.Double)
 *  @see #calculateHullSet(Point2D.Double, Point2D.Double, ArrayList, ArrayList)
 *  @see #calculatePointLocation(Point2D.Double, Point2D.Double, Point2D.Double)
 * 
 * @author Sebastian Schulz
 * @since 26.02.2014
 */
public class ConvexHull {
	private static Logger logger = Logger.getLogger(MetadataConnector.class.getName());
	
	private GeoArea convexHullPolygon = null;
	
	/**
	 * The constructer transforms the given list {@code ArealClusteringObject}s to 
	 * a {@code ArrayList} of {@code Point2D.Double} values. Afterwards it calls the
	 * {@link #createHull(ArrayList)} method to determine the hull.
	 * 
	 * @param arealObjects - a {@code ArrayList} of {@code ArealClusteringObjects}.
	 */
	public ConvexHull(ArrayList<ArealClusteringObject> arealObjects) {
		ArrayList<Point2D.Double> points = new ArrayList<Point2D.Double>();
		for (ArealClusteringObject object : arealObjects) {
			Point2D.Double currentPoint = new Point2D.Double(object.getLongitude(), object.getLatitude());
			if (!points.contains(currentPoint)) {
				points.add(currentPoint);
			}
		}
		createHull(points);
		logger.debug("--> Successfully transformed areal objects into Point2D objects");
	}
	
	/**
	 * This method is the starting point of the QuickHull algorithm. In case the size of the 
	 * {@code ArrayList} is to small to calculate a hull the is not much to do. Otherwise calling
	 * the {@link #getExtremalIndices(ArrayList)} method calculates the left most and right most 
	 * point. Both are added to the hull and two sets are created. Staying at point A and looking 
	 * to point B one set represents all remaining points lying on the left and the other represents
	 * all lying on the right. With these two sets the 
	 * {@link #calculateHullSet(Point2D.Double, Point2D.Double, ArrayList, ArrayList)} method is called
	 * twice to recursively calculate the other points on the hull.     
	 * 
	 * @param points - an {@code ArrayList} of {@code Point2D.Double}.
	 */
	private void createHull(ArrayList<Point2D.Double> points) {
	    if(points.size() < 2) {
	    	convexHullPolygon = new GeoArea(points);
	    	logger.warn("Not enough points to calculate convex hull -> area with single point!");
	    } else if(points.size() < 3) {
	    	convexHullPolygon = new GeoArea(points);
	    	logger.debug("Created new convex hull polygon since there are only two points available");
	    } else {
	    	ArrayList<Point2D.Double> convexHull = new ArrayList<Point2D.Double>();
		    int[] extremalIndices = getExtremalIndices(points);
		    logger.debug("Determined extremal indices");
		    
		    //add extremal points to convexHull
		    Point2D.Double leftMostPoint = points.get(extremalIndices[0]);
		    Point2D.Double rightMostPoint = points.get(extremalIndices[1]);
		    convexHull.add(leftMostPoint);
		    convexHull.add(rightMostPoint);
		    points.remove(leftMostPoint);
		    points.remove(rightMostPoint);
		    
		    ArrayList<Point2D.Double> rightSet = new ArrayList<Point2D.Double>();
		    ArrayList<Point2D.Double> leftSet = new ArrayList<Point2D.Double>();
		    
		    for (int i = 0; i < points.size(); i++) {
		    	Point2D.Double p = points.get(i);
			    if (calculatePointLocation(leftMostPoint, rightMostPoint, p) == -1) {
			    	rightSet.add(p);  
		      	} else {
		      		leftSet.add(p);
		      	}
		    }
		    
		    logger.debug("Recusive call left set");
		    calculateHullSet(leftMostPoint, rightMostPoint, leftSet, convexHull);
		    logger.debug("Recusive call left set");
		    calculateHullSet(rightMostPoint, leftMostPoint, rightSet, convexHull);
		    
		    convexHullPolygon = new GeoArea(convexHull);
		    logger.debug("Created new convex hull polygon");
	    }
	}

	/**
	 * This method finds the index of the point of the {@code ArrayList} which
	 * is at most on the left and of the point which is at most on the right.
	 * 
	 * @param points - a {@code ArrayList} of {@code Point2D.Double}.
	 * @return a {@code Array} of {@code int} values. The first represents the index
	 * 	of the left most point and the second the index of the rights most point.
	 */
	private int[] getExtremalIndices(ArrayList<Point2D.Double> points) {
		int minPointIndex = -1, maxPointIndex = -1;
	    double minX = Double.MAX_VALUE;
	    double maxX = Double.MIN_VALUE;
	    
	    for (int i = 0; i < points.size(); i++) {
	      if (points.get(i).x < minX) {
	        minX = points.get(i).x;
	        minPointIndex = i;
	      } 
	      if (points.get(i).x > maxX) {
	        maxX = points.get(i).x;
	        maxPointIndex = i;       
	      }
	    }
		return new int[]{minPointIndex, maxPointIndex};
	}
	
	/**
	 * This method computes the square of the distance of point C to the segment defined by 
	 * points AB.
	 * 
	 * @param a - a {@code Point2D.Double}.
	 * @param b - a {@code Point2D.Double}.
	 * @param c - a {@code Point2D.Double}.
	 * @return a {@code double} value representing the distance.
	 */
	private double getSquaredDistance(Point2D.Double a, Point2D.Double b, Point2D.Double c) {
		double abx = b.x-a.x;
		double aby = b.y-a.y;
		double distance = Math.abs(abx*(a.y-c.y)-aby*(a.x-c.x));
		return distance;
	}
  
	/**
	 * This method represents the divide step. A subset is processed. The furthest
	 * distance from A or B to a point P of the subset is found. P is added to the 
	 * {@code convexHull}. Afterwards the subsets for the next step are builded an the recursive 
	 * call is made.
	 * 
	 * @param a - a {@code Point2D.Double} object.
	 * @param b - a {@code Point2D.Double} object.
	 * @param set - a {@code ArrayList} of {@code Point2D.Double} representing the processed subset.
	 * @param hull - a {@code ArrayList} of {@code Point2D.Double} representing the hull so far.
	 */
	private void calculateHullSet(Point2D.Double a, Point2D.Double b, 
			ArrayList<Point2D.Double> set, ArrayList<Point2D.Double> hull) {
	    int insertPosition = hull.indexOf(b);
	    if (set.size() == 0) {
	    	logger.debug("Set size 0 - nothing to do");
	    	return;
	    } else if (set.size() == 1) {
	    	Point2D.Double p = set.get(0);
	    	set.remove(p);
	    	hull.add(insertPosition,p);
	    	logger.debug("Set size 1 - add point to convex hull");
	    	return;
	    }
	    
	    double furthestDistance = Double.MIN_VALUE;
	    int furthestPoint = -1;
	    for (int i = 0; i < set.size(); i++) {
	    	Point2D.Double p = set.get(i);
	    	double currentDistance  = getSquaredDistance(a,b,p);
	    	if (currentDistance > furthestDistance) {
	    		furthestDistance = currentDistance;
	    		furthestPoint = i;
	    	}
	    }
	    Point2D.Double p = set.get(furthestPoint);
	    logger.debug("Add furthest point to convex hull");
	    hull.add(insertPosition,p);
	    set.remove(furthestPoint);
	    
	    //determine who's to the left of AP
	    ArrayList<Point2D.Double> leftSetAP = new ArrayList<Point2D.Double>();
	    for (int i = 0; i < set.size(); i++) {
	    	Point2D.Double m = set.get(i);
	    	if (calculatePointLocation(a,p,m) == 1) {
	    		leftSetAP.add(m);
		    }
	    }
	    
	    //determine who's to the left of PB
	    ArrayList<Point2D.Double> leftSetPB = new ArrayList<Point2D.Double>();
	    for (int i = 0; i < set.size(); i++) {
	    	Point2D.Double m = set.get(i);
	    	if (calculatePointLocation(p,b,m)==1) {
	    		leftSetPB.add(m);
	    	}
	    } 
	    logger.debug("Recursive call left set");
	    calculateHullSet(a,p,leftSetAP,hull);
	    logger.debug("Recursive call right set");
	    calculateHullSet(p,b,leftSetPB,hull);
	  }
	
	/**
	 * This method determines the cross product for the points A and B. It calculates
	 * if point P is above or below the virtual line between A and B. 
     *
     * @param a - a {@code Point2D.Double} 
     * @param b - a {@code Point2D.Double}
     * @param p - a {@code Point2D.Double}
     * @return 1 or -1 as {@code int} value (cross product).
	 */
	private int calculatePointLocation(Point2D.Double a, Point2D.Double b, Point2D.Double p) {
	    double cp1 = (b.x-a.x)*(p.y-a.y) - (b.y-a.y)*(p.x-a.x);
	    return (cp1>0)?1:-1;
	}

	public GeoArea getPolygon() {
		return convexHullPolygon;
	}
	
}
