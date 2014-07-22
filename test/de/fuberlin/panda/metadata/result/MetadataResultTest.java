package de.fuberlin.panda.metadata.result;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import de.fuberlin.panda.metadata.result.ListResult;
import de.fuberlin.panda.metadata.result.MetadataResult;

public class MetadataResultTest {

	@Test
	public void testCreateMetadataResult() {
		MetadataResult meta = new ListResult();
		assertNotNull("Metadaobject is null", meta);
	}
	
	@Test
	public void testEmptyMetadataToRdf() {
		MetadataResult meta = new ListResult();
		String model;
		try {
			meta.createRdfModel();
			model = meta.toRdfXml();
			String testString = "<?xml version=\"1.0\"  encoding=\"UTF-8\"?>\r\n"
					+ "<rdf:RDF\r\n    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\r\n    "
					+ "xmlns:panda=\"http://www.mi.fu-berlin.de/panda#\">\r\n</rdf:RDF>\r\n";
			assertTrue("Set wrong framework information", model.equals(testString));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
}
