package de.fuberlin.panda.properties;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.fuberlin.panda.metadata.config.MetadataType;
import de.fuberlin.panda.metadata.config.RDFInput;

public class RDFInputTest {

	@Test
	public void testInitPredicates() {
		RDFInput.initPredicates();
	}
	
	@Test
	public void testGetMetadataType() {
		RDFInput.initPredicates();
		
		MetadataType type = RDFInput.getMetadataType("Creation");
		assertNotNull("Couldn't find MetadataType in predicates HashMap.", type);
		assertTrue("MetadataType was send back wrong from predicates HashMap.", 
				type.equals(MetadataType.OPERATIONAL));
		
		type = null;
		type = RDFInput.getMetadataType("License");
		assertNotNull("Couldn't find MetadataType in predicates HashMap.", type);
		assertTrue("MetadataType was send back wrong from predicates HashMap.", 
				type.equals(MetadataType.ADMINISTRATIVE_SCOPE));
		
		type = null;
		type = RDFInput.getMetadataType("Longitude");
		assertNotNull("Couldn't find MetadataType in predicates HashMap.", type);
		assertTrue("MetadataType was send back wrong from predicates HashMap.", 
				type.equals(MetadataType.AREAL_SCOPE));
		
		type = null;
		type = RDFInput.getMetadataType("ExpirationDate");
		assertNotNull("Couldn't find MetadataType in predicates HashMap.", type);
		assertTrue("MetadataType was send back wrong from predicates HashMap.", 
				type.equals(MetadataType.TEMPORAL_SCOPE));
		
		type = null;
		type = RDFInput.getMetadataType("Format");
		assertNotNull("Couldn't find MetadataType in predicates HashMap.", type);
		assertTrue("MetadataType was send back wrong from predicates HashMap.", 
				type.equals(MetadataType.TYPE));
		
		type = RDFInput.getMetadataType("WrongInput");
		assertNull("MetadataType was send back wrong from predicates HashMap.", type);
	}
	
}
