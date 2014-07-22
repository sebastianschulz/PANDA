package de.fuberlin.panda.metadata.result.fusion.areal;

import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * This class extends the {@code Area} class. It overrides the 
 * {@link #contains(double, double)} method and adds a 
 * {@link #containsPartOf(GeoArea)} method.
 * 
 * @see #GeoArea(ArrayList)
 * @see #setBoundaryPath()
 * @see #contains(double, double)
 * @see #containsPartOf(GeoArea)
 * 
 * @author Sebastian Schulz
 * @since 25.02.2014
 */
public class GeoArea {
	private String name = "";
	private ArrayList<Point2D.Double> boundaryPoints = new ArrayList<Point2D.Double>();
	private Area areaObject = null;
	
	public GeoArea() {
		areaObject = new Area();
	}
	
	public GeoArea(GeoArea geoArea) {
		boundaryPoints.addAll(geoArea.getBoundaryPoints());
		name = geoArea.getName();
		areaObject = geoArea.getAreaObject();
	}
	
	/**
	 * The constructor calls the super constructor to create an empty area. 
	 * Afterwards it stores the given boundary points in a private field called
	 * {@code boundaryPoints}. Subsequently they are transformed into a 
	 * {@code Path2D.Double} object by calling {@link #setBoundaryPath()}. Finally
	 * a new created area is added to the {@code GeoArea}. This is the actual creation
	 * of the area.
	 * 
	 * @param boundary - a {@code ArrayList} of {@code Point2D.Double} which represents
	 * 	the boundary of the Area. 
	 */
	public GeoArea(ArrayList<Point2D.Double> boundary) {
		boundaryPoints.addAll(boundary);
		Path2D.Double boundaryPath = setBoundaryPath();
		areaObject = new Area(boundaryPath);
	}
	
	/**
	 * This method is called by the constructor. It converts the {@code boundaryPoints}
	 * {@code ArrayList} set before into a {@code Path2D.Double} object.
	 * 
	 * @return a {@code Path2D.Double} object.
	 */
	private Path2D.Double setBoundaryPath() {
		Path2D.Double boundaryPath = new Path2D.Double();
		boundaryPath.moveTo(boundaryPoints.get(0).x, boundaryPoints.get(0).y);
		
		int lastBoundaryIndex = boundaryPoints.size() - 1;
		for (int i = 1; i < boundaryPoints.size(); i++) {
			boundaryPath.lineTo(boundaryPoints.get(i).x, boundaryPoints.get(i).y);
		}
		//close the circle
		boundaryPath.lineTo(boundaryPoints.get(lastBoundaryIndex).x, boundaryPoints.get(lastBoundaryIndex).y);
		return boundaryPath;
	}
	
	/**
	 * This method overrides the {@code contains(double, double)} method because of the
	 * insideness. In this case every point on the boundary is inside the polygon.
	 */
	public boolean contains(double x, double y) {
		boolean isBoundaryElement = false;
		
		if(boundaryPoints.contains(new Point2D.Double(x,y))) {
			isBoundaryElement = true;
		}
		
		boolean isInsideArea = areaObject.contains(x, y);
		return (isBoundaryElement || isInsideArea);
	}

	/**
	 * This method checks if a boundary point of a second area is
	 * contained in the current {@code GeoArea}.
	 * 
	 * @param secondArea - a {@code GeoArea} object.
	 * @return a boolean value.
	 */
	public boolean containsPartOf(GeoArea secondArea) {
		for (Point2D.Double boundaryPoint : secondArea.getBoundaryPoints()) {
			if (this.contains(boundaryPoint.x, boundaryPoint.y)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This method checks if the boundary points of the given {@code GeoArea} are not 
	 * contained in the local {@code GeoArea} and adds them to the local {@code boundaryPoints}.
	 * Finally the given area is added to the local area.
	 * 
	 * @param geoArea - a {@code GeoArea} object.
	 */
	public void add(GeoArea geoArea) {
		if (!geoArea.getBoundaryPoints().isEmpty()) {
			for (Point2D.Double point : geoArea.getBoundaryPoints()) {
				if (!contains(point.x, point.y)) {
					boundaryPoints.add(point);
				}
			}
			areaObject.add(geoArea.getAreaObject());
			updateName(geoArea.getName());
		}
	}
	
	/**
	 * This method check if the {@code name} of this {@code GeoArea} can be
	 * updated.
	 * 
	 * @param otherName - a {@code String}.
	 */
	private void updateName(String otherName) {
		if (!otherName.isEmpty()) {
			if (name.isEmpty()) {
				name = otherName;
			} else if (!name.toLowerCase().contains(otherName.toLowerCase())
					&& (!otherName.toLowerCase().startsWith("cluster_"))) {
				if (name.endsWith("_") || name.isEmpty()) {
					name += otherName;
				} else {
					name += "_" + otherName;
				}
			}
		}
	}

	public void subtract(GeoArea geoArea) {
		//FIXME what if outer boundary point isn't contained in new polygon
		//this problem can't occure in the present example since subtracted areas
		//are at whole contained in the outer area... How to update name in this case?
		if(!geoArea.getBoundaryPoints().isEmpty()) {
			boundaryPoints.addAll(geoArea.getBoundaryPoints());
			areaObject.subtract(geoArea.getAreaObject());
		}
	}
	
	public ArrayList<Point2D.Double> getBoundaryPoints() {
		return boundaryPoints;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public Area getAreaObject() {
		return areaObject;
	}
}