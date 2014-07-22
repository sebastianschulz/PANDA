package de.fuberlin.panda.api.jersey;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.fuberlin.panda.api.APIHelper;

/**
 * This class provides the methods to get fictional test metadata 
 * information for a single data value.
 * 
 * @since 29.08.2013
 * @author Sebastian Schulz
 */
@Path( "/Metadata/TestData" )
public class MetadataTestResource {

   
	//http://localhost:8080/PANDA/rest/Metadata/TestData
	@GET
	@Produces( MediaType.TEXT_PLAIN )
	public String message() {
		return "This is the metadata test path!";
    }
	
	//http://localhost:8080/PANDA/rest/Metadata/TestData/{id}
	@GET
	@Path( "{subID}" )
	@Produces( MediaType.APPLICATION_XML )
	public String getValue(@PathParam("subID") String subID) {
		String directory = "testData";
		String fileName = "metadataExample"+subID+".xml";
		return APIHelper.readFileContent(directory, fileName);
	}
}
