/**
 * 
 */
package placebooks.model;

import java.io.*;
import java.net.*;
import java.net.Proxy.Type;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import placebooks.controller.PlaceBooksAdminController;

/**
 * @author pszmp
 *
 */
public class EverytrailHelper
{
	private static final Logger log = 
		Logger.getLogger(EverytrailHelper.class.getName());

	
	private static String apiUsername = "94482eab9c605cfed58b396b74ae7466";
	private static String apiPassword = "135df832868a3543";
	
	static class HttpAuthenticator extends Authenticator {
		private String username, password;

		public HttpAuthenticator(String user, String pass) {
			username = user;
			password = pass;
		}

		protected PasswordAuthentication getPasswordAuthentication() {
			log.debug("Requesting Host  : " + getRequestingHost());
			log.debug("Requesting Port  : " + getRequestingPort());
			log.debug("Requesting Prompt : " + getRequestingPrompt());
			log.debug("Requesting Protocol: "
					+ getRequestingProtocol());
			log.debug("Requesting Scheme : " + getRequestingScheme());
			log.debug("Requesting Site  : " + getRequestingSite());
			return new PasswordAuthentication(username, password.toCharArray());
		}
	}
	
	public static String UserLogin(String username, String password)
	{
		StringBuilder output = new StringBuilder();
		StringBuilder postResponse = new StringBuilder();
	    
		try
		{
		    // Construct data
		    String data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
		    data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");

		    // Send data
		    Authenticator.setDefault(new HttpAuthenticator(apiUsername, apiPassword));
		    URL url = new URL("http://www.everytrail.com/api/user/login");
		    Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("wwwcache.cs.nott.ac.uk", 3128));

		    URLConnection conn = url.openConnection(proxy);
		    conn.setDoOutput(true);
		    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		    wr.write(data);
		    wr.flush();

		    // Get the response
		    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		    String line;
		    while ((line = rd.readLine()) != null)
		    {
		   	 postResponse.append(line);
		    }
		    wr.close();
		    rd.close();
		} catch (Exception e) {
			log.debug(e.getMessage());
		}

		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;
			db = dbf.newDocumentBuilder();
			Document doc;
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(postResponse.toString()));
			doc = db.parse(is);
			doc.getDocumentElement().normalize();
			if(doc.getDocumentElement().getNodeName() == "etUserLoginResponse")
			{
				if(doc.getDocumentElement().getAttribute("status").equals("success"))
				{
					output.append(doc.getDocumentElement().getAttribute("status"));
					output.append(" User id:" + doc.getDocumentElement().getChildNodes().item(0).getTextContent());
				}
				else
				{
					log.error(doc.getDocumentElement().getAttribute("status"));
				}
			}
		}
		catch (ParserConfigurationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (SAXException e)
		{
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e)
	{
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		return output.toString();
	}
}
