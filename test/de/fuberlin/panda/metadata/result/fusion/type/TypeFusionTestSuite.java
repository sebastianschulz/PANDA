package de.fuberlin.panda.metadata.result.fusion.type;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.fuberlin.panda.metadata.result.fusion.type.tree.TypeNodeTest;
import de.fuberlin.panda.metadata.result.fusion.type.tree.TypeTreeTest;

@RunWith(Suite.class)
@SuiteClasses({ 
	TypeNodeTest.class,
	TypeTreeTest.class,
	TypePropertiesTest.class
})
public class TypeFusionTestSuite {

}
