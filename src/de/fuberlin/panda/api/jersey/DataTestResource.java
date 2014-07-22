package de.fuberlin.panda.api.jersey;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.fuberlin.panda.api.APIHelper;

@Path( "/Data/TestData" )
public class DataTestResource
{	   
	//http://localhost:8080/PANDA/rest/Data/TestData
	@GET
	@Produces( MediaType.TEXT_PLAIN )
	public String message()
	{
		return "Hello! ";
    }
	
	//http://localhost:8080/PANDA/rest/Data/TestData/{id}
	@GET
	@Path( "{subID}" )
	@Produces( MediaType.APPLICATION_XML )
	public String getValue(@PathParam("subID") String subID)
	{
		String directory = "testData";
		String fileName = "valueExample"+subID+".xml";
		return APIHelper.readFileContent(directory, fileName);
	}
}