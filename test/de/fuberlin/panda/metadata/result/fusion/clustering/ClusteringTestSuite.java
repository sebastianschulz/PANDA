package de.fuberlin.panda.metadata.result.fusion.clustering;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
	ClusterDetectorTest.class,
	ClusteringObjectTest.class,
	ClusteringResultTest.class,
	NeighborsComparatorTest.class,
	ReachabilityObjectComparatorTest.class
})
public class ClusteringTestSuite {

}