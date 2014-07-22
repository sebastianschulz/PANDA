package de.fuberlin.panda.api;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

/**
 * This class represents a simple rest client, which returns some of the
 * test data files from the tomcat server.
 * 
 * @since 27.08.2013
 * @author Christoph Schröder, Sebastian Schulz
 *
 */
public class TestClient {
	private static String baseURI = "http://localhost:8080/PANDA/rest";
	
	public static void main(String[] args) throws Exception, IOException {
		WebResource service = createService();
		getTestText(service);
		System.out.println("----------------------");
		getTestValueXML(service);
		System.out.println("----------------------");
	    getTestMetadataXML(service);
	}
	
	/**
	 * This method creates the client an binds it to the service uri
	 * 
	 * @author Christoph Schröder
	 */
	private static WebResource createService() {
	    ClientConfig config = new DefaultClientConfig();
	    Client client = Client.create(config);
	    return client.resource(getBaseURI());
	}
	
	/**
	 * This method configurates the basic URI the client should call.
	 * 
	 * @return URI - the clients baseURI
	 * @author Christoph Schröder
	 */
	private static URI getBaseURI() {
		return UriBuilder.fromUri(baseURI).build();
	}
	
	/**
	 * This method performs a testing GET request on the Data/TestData path. 
	 * The whole URL looks like this: http://localhost:8080/PANDA/rest/Data/TestData
	 * 
	 * @param service - a {@link WebResource}
	 * @author Christoph Schröder
	 */
	private static void getTestText(WebResource service) {
	    ClientResponse clRsp1 = service.path("Data/TestData").accept(MediaType.TEXT_PLAIN).get( ClientResponse.class );
		System.out.println( clRsp1.getStatus() );
		if ( clRsp1.hasEntity() ) {
			System.out.println( clRsp1.getEntity( String.class ));
		}
	}
	
	/**
	 * This method gets XML for application test from
	 * http://localhost:8080/PANDA/rest/Data/TestData/1
	 * 
	 * @param service - a {@link WebResource}
	 * @author Christoph Schröder
	 */
	private static void getTestValueXML(WebResource service) {
		ClientResponse clRsp2 = service.path("Data/TestData/1").accept(MediaType.APPLICATION_XML).get( ClientResponse.class );
		System.out.println( clRsp2.getStatus() );
		if ( clRsp2.hasEntity() ) {
			System.out.println( clRsp2.getEntity( String.class ));
		}
	}
    
	/**
	 * This method gets XML for application test from
	 * http://localhost:8080/PANDA/rest/Metadata/TestData/1
	 * 
	 * @param service - a {@link WebResource}
	 * @author Sebastian Schulz
	 */
	private static void getTestMetadataXML(WebResource service) {
		ClientResponse clRsp2 = service.path("Metadata/TestData/1").accept(MediaType.APPLICATION_XML).get( ClientResponse.class );
		System.out.println( clRsp2.getStatus() );
		if ( clRsp2.hasEntity() ) {
			System.out.println( clRsp2.getEntity( String.class ));
		}
	}
}
