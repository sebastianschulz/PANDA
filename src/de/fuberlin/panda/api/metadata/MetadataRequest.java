package de.fuberlin.panda.api.metadata;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.fuberlin.panda.api.APIHelper;
import de.fuberlin.panda.metadata.MetadataConnector;
import de.fuberlin.panda.metadata.config.MetadataSourceType;
import de.fuberlin.panda.metadata.exceptions.MetadataNotFoundException;
import de.fuberlin.panda.metadata.exceptions.ProcessingUriException;

/**
 * This class extends the {@link javax.servlet.http.HttpServlet} class.
 * It processes the entered parameters for the metadata source and the 
 * requested uris and returns the metadata to the URIs in a xml text. 
 * 
 * @see #doGet(HttpServletRequest, HttpServletResponse)
 * @see #processXMLTestData(String, String)
 * 
 * @since 05.09.2013
 * @author Sebastian Schulz
 */
public class MetadataRequest extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private final String TEST_DATA_BASE_URI = "/Data/TestData/";
	private final String URI_DELIMETER = System.lineSeparator();
	private HttpServletResponse response;
	
	/**
	 * Overwritten method of {@link javax.servlet.http.HttpServlet}.
	 * This methods retrieves the requests parameters and calls the method
	 * to process the data depending on the chosen metadata source. 
	 */
	protected void doGet(HttpServletRequest request, 
			HttpServletResponse response) throws ServletException, IOException {
        this.response = response;
        
        //get the request parameters 
        String source = request.getParameter("source");
        boolean fusion = request.getParameter("fusion") != null;
        List<String> uris = getURIs(request.getParameter("uris"));
        
        try {
        	if (source.equals(MetadataSourceType.XML_TEST.toString())) {
             	processXMLTestData(uris);
     		} else if(source.equals(MetadataSourceType.XML.toString())) {
     			MetadataConnector metadataConnector = new MetadataConnector(MetadataSourceType.XML, uris, fusion);
     			response.setContentType("application/xml;charset=UTF-8");
         		PrintWriter out = response.getWriter();
     			String outputData = metadataConnector.getMetadata(); 
         		out.print(outputData);
     		} else if(source.equals(MetadataSourceType.VIRTUOSO.toString())) {
     			MetadataConnector metadataConnector = new MetadataConnector(MetadataSourceType.VIRTUOSO, uris, fusion);
     			response.setContentType("application/xml;charset=UTF-8");
         		PrintWriter out = response.getWriter();
         		out.print(metadataConnector.getMetadata());
     		} else {
     			APIHelper.createErrorMessage(response, "unkown error", "Metadata");
     		}
        } catch (MetadataNotFoundException e) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, 
					"Couldn't find metadata with URI: " + e.getMessage());
		} catch (ProcessingUriException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
					"Error while processing the URI: " + e.getMessage());
		} catch (IOException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
					"Unablbe to open OutputStream: " + e.getMessage());
		}
    }
	
	/**
	 * This method uses a {@link StringTokenizer} to separate the requested URIs 
	 * given in the input field.  
	 * 
	 * @param requestUris - a String with all URIs separated by the {@link #URI_DELIMETER}
	 * @return a List of URIs represented as a String
	 */
	private List<String> getURIs(String requestUris) {
		List<String> uris = new ArrayList<>();
		
		StringTokenizer st = new StringTokenizer(requestUris, URI_DELIMETER);
		while (st.hasMoreTokens()) {
			uris.add(st.nextToken());
		}
		
		return uris;
	}
	

	/**
	 * This method is called when the users has chosen to get the testdata which
	 * is deposited on the webserver. It checks if one of the three data pieces is 
	 * correctly chosen by its URI and prints out its file content in xml text
	 * 
	 * @param uris - the List of requested URIs 
	 * @throws IOException if something went wrong with the {@link java.io.PrintWriter}
	 */
	private void processXMLTestData(List<String> uris) throws IOException {
		String XmlTestUri = uris.get(0);
		String subID = XmlTestUri.substring(15);
    	
    	if (XmlTestUri.startsWith(TEST_DATA_BASE_URI) && 
    			(subID.equals("1") || subID.equals("2") || subID.equals("3"))) {
    		String directory = "testData";
        	String fileName = "metadataExample"+subID+".xml";
        	
    		response.setContentType("application/xml;charset=UTF-8");
    		PrintWriter out = response.getWriter();
    		out.print(APIHelper.readFileContent(directory, fileName));
    	} else {
			String errorMessage = "Wrong parameter for XML TEST FILES, please use URI: "
					+ "'/Data/TestData/1' OR '/Data/TestData/2' OR '/Data/TestData/3' "
					+ "(only one URI allowed so far)";
			APIHelper.createErrorMessage(response, errorMessage, "Metadata");
		}
	}

}
