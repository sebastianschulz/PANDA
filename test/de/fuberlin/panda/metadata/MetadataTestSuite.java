package de.fuberlin.panda.metadata;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.fuberlin.panda.metadata.descriptive.DescriptiveMetadataTestSuite;
import de.fuberlin.panda.metadata.operational.OperationalMetadataTestSuite;
import de.fuberlin.panda.metadata.parsedMetadata.XMLMetadataTest;
import de.fuberlin.panda.metadata.result.MetadataResultTest;
import de.fuberlin.panda.metadata.result.fusion.administrative.AdministrativeFusionTestSuite;
import de.fuberlin.panda.metadata.result.fusion.areal.ArealFusionTestSuite;
import de.fuberlin.panda.metadata.result.fusion.clustering.ClusteringTestSuite;
import de.fuberlin.panda.metadata.result.fusion.helper.FusionHelperTestSuite;
import de.fuberlin.panda.metadata.result.fusion.temporal.TemporalFusionTestSuite;
import de.fuberlin.panda.metadata.result.fusion.type.TypeFusionTestSuite;
import de.fuberlin.panda.properties.PropertiesTestSuite;

@RunWith(Suite.class)
@SuiteClasses({ 
	PropertiesTestSuite.class,
	DescriptiveMetadataTestSuite.class,
	OperationalMetadataTestSuite.class,
	MetadataResultTest.class,
	FusionHelperTestSuite.class,
	XMLMetadataTest.class,
	ArealFusionTestSuite.class,
	ClusteringTestSuite.class,
	TemporalFusionTestSuite.class,
	ArealFusionTestSuite.class,
	AdministrativeFusionTestSuite.class,
	TypeFusionTestSuite.class
})
public class MetadataTestSuite {

}
