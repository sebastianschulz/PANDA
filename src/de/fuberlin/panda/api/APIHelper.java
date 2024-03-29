package de.fuberlin.panda.api;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletResponse;

/**
 * The class provides basic features which are required in the 
 * whole API.
 * 
 * @see #getWebContentDirPath()
 * @see #readFileContent(String, String)
 * @see #readFileContent(String, String, String)
 * @see #createErrorMessage(HttpServletResponse, String, String)
 * 
 * @since 28.08.2013
 * @author Sebastian Schulz
 */
public class APIHelper {

	/**
	 * This method checks for the path of the PANDA WebContent folder which
	 * is deployed on the Tomcat 7 Server and returns its path as a string
	 * value.
	 * 
	 * @return String - The path of the WebContent folder
	 * @author Sebastian Schulz
	 */
	public static String getWebContentDirPath() {
		String baseDirPath="";
		String token = "";
		String thisClassPath = APIHelper.class.getResource(APIHelper.class.getSimpleName()+".class").getFile();
		thisClassPath = thisClassPath.replaceAll("%20", " ");
					
		StringTokenizer st = new StringTokenizer(thisClassPath,"/");
			
		while (st.hasMoreElements()) {
			token = st.nextElement().toString();
			if(token.toUpperCase().equals("WEB-INF"))
				break;
			baseDirPath+=token+"/";
		}	
		return baseDirPath;
	}
	
	/**
	 * This method is called if the file one wants to read is located in the webContent
	 * directory. It calls {@code @see #readFileContent(String, String, String)} which 
	 * manages the actual reading process.
	 * 
	 * @return String - fileContent
	 * @author Sebastian Schulz
	 */
	public static String readFileContent(String directory, String fileName) {
		return readFileContent(APIHelper.getWebContentDirPath(), directory, fileName);
	}
	
	/**
	 * This method reads the content of a file which is deployed on the Tomcat
	 * Server.
	 * 
	 * @param baseDirPath
	 * @param directory
	 * @param fileName
	 * @return String - fileContent
	 * @author Sebastian Schulz
	 */
	public static String readFileContent(String baseDirPath, String directory, String fileName) {
		String fileContent = "";
		File file = new File(baseDirPath + directory + "/" + fileName);
		char[] buf = new char[(int)file.length()] ;
		FileReader fr;
		try {
			fr = new FileReader(file);
			fr.read(buf) ;
			fileContent = new String(buf);
			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileContent;
	}
	
	/**
	 * This method creates a simple error (information) parameterized output on 
	 * a webpage and a link back to a parameterized page.
	 * 
	 * @param response - the current response the servlet is working with
	 * @param errorMessage - a string which should be shown on the page
	 * @param redirectedPage - the servlets url-pattern to redirect with a link
	 * @throws IOException in case the {@link java.io.PrintWriter} fails
	 * @author Sebastian Schulz
	 */
	public static void createErrorMessage(HttpServletResponse response, String errorMessage, String redirectedPage) throws IOException {
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.println(errorMessage + "<br/><br/>");
        out.println("<a href='" + redirectedPage + "'>Back to " + redirectedPage + " page </a>");
	}
}