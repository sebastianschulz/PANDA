package de.fuberlin.panda.metadata.operational;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Vector;

import javax.management.modelmbean.XMLParseException;

import org.junit.Test;

import de.fuberlin.panda.metadata.config.predicates.OperationalPredicates;

public class OperationsTest {

	@Test
	public void testCreateOperations() {
		Operations operations = new Operations();
		assertNotNull(operations);
		assertNotNull("Couldn't create operations vector object.", operations.getAttributes());
		assertTrue("Newly created operations vector was not empty", operations.getAttributes().size() == 0);
	}
	
	@Test
	public void testAddOperation() {
		try {
			Operations operations = new Operations();
			operations.addOperation(OperationalPredicates.CREATION, "15.09.2013 16:43:28.360");
			assertTrue("Operation wasn't add to the operations vector.", operations.getAttributes().size() == 1);
		} catch (XMLParseException e) {
			fail("Couldn't add operation to operations.");
		}
	}
	
	@Test
	public void testGetOperations() {
		String testTimestampString = "15.09.2013 16:43:28.360";
		try {
			Operations operations = new Operations();
			operations.addOperation(OperationalPredicates.CREATION, testTimestampString);
			assertTrue("Operation wasn't add to the operations vector.", operations.getAttributes().size() == 1);
			Vector<Operation> operationVector = operations.getAttributes();
			assertNotNull(operationVector);
			assertTrue("Wrong timestamp was set in operations vector.", 
					operationVector.get(0).getTimestampString().equals(testTimestampString));
			assertTrue("Wrong operation type was set in operations vector.", 
					operationVector.get(0).getType() == OperationalPredicates.CREATION);
		} catch (XMLParseException e) {
			fail("Couldn't add operation to operations.");
		}
	}
}
