package de.fuberlin.panda.metadata.descriptive;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
	AdministrativeScopeTest.class,
	ArealScopeTest.class,
	TemporalScopeTest.class,
	TypeTest.class,
})
public class DescriptiveMetadataTestSuite {

}
