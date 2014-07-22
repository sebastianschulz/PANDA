package de.fuberlin.panda.metadata.parsedMetadata;

import org.junit.Test;

public class VirtuosoMetadataTest {

	@Test
	public void testParseMetadata() throws Exception {
		VirtuosoMetadata meta = new VirtuosoMetadata();
		meta.setUri("/Data/TestData/1");
		meta.parseMetadata();
	}
}
