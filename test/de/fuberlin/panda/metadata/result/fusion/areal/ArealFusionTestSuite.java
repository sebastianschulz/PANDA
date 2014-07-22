package de.fuberlin.panda.metadata.result.fusion.areal;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.fuberlin.panda.metadata.result.fusion.areal.geocoding.GeocodingTestSuite;

@RunWith(Suite.class)
@SuiteClasses({
	ArealClusterDetectorTest.class,
	ArealClusteringObjectTest.class,
	ConvexHullTest.class,
	GeoAreaTest.class,
	GeoCoordinatesComparatorTest.class,
	GeocodingTestSuite.class
})
public class ArealFusionTestSuite {

}
