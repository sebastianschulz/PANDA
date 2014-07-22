package de.fuberlin.panda.metadata.result.fusion.temporal;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	TemporalClusterDetectorTest.class,
	TemporalClusteringObjectTest.class,
	TimestampComparatorTest.class
})
public class TemporalFusionTestSuite {

}
