/**
 * 
 */
package placebooks.controller;

import java.io.*;
import java.net.*;
import java.net.Proxy.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import placebooks.controller.PlaceBooksAdminController;
import placebooks.model.EverytrailLoginResponse;
import placebooks.model.EverytrailPicturesResponse;
import placebooks.model.EverytrailTripsResponse;

/**
 * @author pszmp
 *
 */
public class EverytrailHelper
{
	private static final Logger log = 
		Logger.getLogger(EverytrailHelper.class.getName());

	private static String apiBaseUrl = "http://www.everytrail.com/api/";
	private static String apiUsername = "94482eab9c605cfed58b396b74ae7466";
	private static String apiPassword = "135df832868a3543";
	
	private static String httpProxyName = "wwwcache.cs.nott.ac.uk";
	private static int httpProxyPort = 3128;
	
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
	
	/**
	 * Log in to the Everytrail API with a given username and password
	 * n.b. this appears to be submitted as HTTP not HTTPS so password is 
	 * potentially insecure.
	 * @param username An everytrail username
	 * @param password An everytrail password
	 * @return EverytrailLoginResponse with status success/error and value userid/error code
	 */
	public static EverytrailLoginResponse UserLogin(String username, String password)
	{
		StringBuilder postResponse = new StringBuilder();
		String loginStatus = "";
		String loginStatusValue = "";
	    
		try
		{
		    // Construct data to post - username and password
		    String data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
		    data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");

		    // Send data by setting up the api password http authentication and UoN proxy
		    // TODO Add global config for proxy support...
		    Authenticator.setDefault(new HttpAuthenticator(apiUsername, apiPassword));
		    URL url = new URL(apiBaseUrl +  "user/login");
		    Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(httpProxyName, httpProxyPort));

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

		// Parse the XML response and construct the response data to return
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
				loginStatus = doc.getDocumentElement().getAttribute("status");
				loginStatusValue = doc.getDocumentElement().getChildNodes().item(0).getTextContent();
				if(loginStatus.equals("success"))
				{
					log.info("Log in succeeded with user id: " + loginStatusValue);
						}
				else
				{
					if(doc.getDocumentElement().getAttribute("status").equals("error"))
					{
						log.warn("Log in failed with error code: " + loginStatusValue);
					}
					else
					{
						log.error("Everytrail login status gave unexpected value: " + loginStatus);
					}
				}
			}
			else
			{
				log.error("Unexpected result from Everytrail: " + doc.getDocumentElement().getNodeName());
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
		EverytrailLoginResponse output = new EverytrailLoginResponse(loginStatus, loginStatusValue);
		return output;
	}
	
	/**
	 * Perform a post to the given Everytrail api destination with the parameters specified
	 * @param postDestination API destination after http://www.everytrail.com/api/ - e.g. user/trips
	 * @param Hashtable<String, String> params a hastable of the parameters to post to the api with name / values as strings
	 * @return String A string containing the post response from Everytrail 
	 */
	private static String getPostResponseWithParams(String postDestination, Hashtable<String, String> params)
	{
		StringBuilder postResponse = new StringBuilder();

		// Construct data to post by iterating through parameter Hashtable keys Enumeration
		StringBuilder data = new StringBuilder();
		Enumeration<String> paramNames = params.keys();
		try
		{
			while(paramNames.hasMoreElements())
			{
				String paramName = (String) paramNames.nextElement();
				data.append(URLEncoder.encode(paramName, "UTF-8") + "=" + URLEncoder.encode(params.get(paramName), "UTF-8"));
				if(paramNames.hasMoreElements())
				{
					data.append("&");
				}
			} 

		    // Send data by setting up the api password http authentication and UoN proxy
		    // TODO Add global config for proxy support...
		    Authenticator.setDefault(new HttpAuthenticator(apiUsername, apiPassword));
		    URL url = new URL(apiBaseUrl + postDestination);
		    Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(httpProxyName, httpProxyPort));

		    URLConnection conn = url.openConnection(proxy);
		    
		    conn.setDoOutput(true);
		    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		    wr.write(data.toString());
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
		} catch (Exception ex) {
			log.debug(ex.getMessage());
		}
		log.debug(apiBaseUrl + postDestination + " response:" + postResponse.toString());
		
		return postResponse.toString();		
	}
	
	
	/**
	 * Return a list of all pictures for a given user_id
	 * @param userId
	 * @return EverytrailPicturesResponse
	 */
	public static EverytrailPicturesResponse Pictures(String userId)
	{
		return Pictures(userId, null, null);
	}

	/**
	 * Get a list of pictures for a given userid
	 * @param userId an everytrail userid obtained from a user - user may need to be logged in first, and this will
	 * at least get the user id
	 * @param limit  Number of photos to get, limit is 20 as default, set by everytrail API.
	 * @param start Starting point for photos, default 0 set by everytrail api
	 * @return EverytrailPicturesResponse 
	 */

	public static EverytrailPicturesResponse Pictures(String userId, String limit, String start)
	{
		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("user_id", userId);
		if( limit != null)
		{
			params.put("limit", limit);
		}
		if( limit != null)
		{
			params.put("start", start);
		}
		String postResponse = getPostResponseWithParams("user/photos", params);
		String resultStatus = postResponse;
		Vector<String> picturesList = new Vector<String>();
		
		EverytrailPicturesResponse returnValue = new EverytrailPicturesResponse(resultStatus, picturesList);
		return returnValue;
	}
	
	
	/**
	 * Return a list of all trips for a given user_id
	 * @param userId an everytrail userid obtained from a user - user may need to be logged in first, and this will
	 * at least get the user id
	 * @return EverytrailTripsResponse
	 */
	public static EverytrailTripsResponse Trips(String userId)
	{
		return Trips(null, null, userId, null, null, null, null, null, null, null, null);
	}

	
	/**
	 * Get a list of trips for a given userid
	 * @param userId an everytrail userid obtained from a user - user may need to be logged in first, and this will
	 * at least get the user id
	 * @param limit  Number of photos to get, limit is 20 as default, set by everytrail API.
	 * @param start Starting point for photos, default 0 set by everytrail api
	 * @return EverytrailTripsResponse 
	 */
	public static EverytrailTripsResponse Trips(String username, String password, String userId, Double lat, Double lon, Date modifiedAfter, String sort, String order, String limit, String start, Boolean minimal)
	{
		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("user_id", userId);
			
		if(username != null)
		{
			params.put("username", username);
		}
		if(password != null)
		{
			params.put("password", password);
		}

		if(lat!=null)
		{
			params.put("lat", lat.toString());
		}
		if(lon!=null)
		{
			params.put("lon", lon.toString());
		}

		if(modifiedAfter != null)
		{
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			params.put("modified_after",  formatter.format(modifiedAfter));
		}
		
		if(sort != null)
		{
			params.put("sort", sort);
		}
		if(order != null)
		{
			params.put("order", order);
		}
		if( limit != null)
		{
			params.put("limit", limit);
		}
		if( limit != null)
		{
			params.put("start", start);
		}
		if( minimal != null)
		{
			params.put("minimal", minimal.toString());
		}
		
		String postResponse = getPostResponseWithParams("user/trips", params);
		String resultStatus = postResponse;
		Vector<String> tripsList = new Vector<String>();
		
		EverytrailTripsResponse returnValue = new EverytrailTripsResponse(resultStatus, tripsList);
		return returnValue;
	}
}
