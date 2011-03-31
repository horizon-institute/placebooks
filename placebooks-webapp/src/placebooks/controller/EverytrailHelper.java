/**
 * 
 */
package placebooks.controller;

import placebooks.model.*;

import java.io.*;
import java.net.*;
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

import placebooks.controller.PropertiesSingleton;

/**
 * @author pszmp
 *
 */
public class EverytrailHelper
{
	private static final Logger log = 
		Logger.getLogger(EverytrailHelper.class.getName());

	private static String apiBaseUrl = "http://www.everytrail.com/api/";
	
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
	    
		log.info(PropertiesSingleton.get(EverytrailHelper.class.getClassLoader()).getProperty(PropertiesSingleton.EVERYTRAIL_API_USER));
		log.info(PropertiesSingleton.get(EverytrailHelper.class.getClassLoader()).getProperty(PropertiesSingleton.EVERYTRAIL_API_PASSWORD));
		try
		{
		    // Construct data to post - username and password
		    String data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
		    data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");

		    // Send data by setting up the api password http authentication and UoN proxy
		    Authenticator.setDefault(new HttpAuthenticator(
		   		 PropertiesSingleton.get(EverytrailHelper.class.getClassLoader()).getProperty(PropertiesSingleton.EVERYTRAIL_API_USER, ""),
		   		 PropertiesSingleton.get(EverytrailHelper.class.getClassLoader()).getProperty(PropertiesSingleton.EVERYTRAIL_API_PASSWORD, "")
		    ));
		    URL url = new URL(apiBaseUrl +  "user/login");
		    
		    // Use getConnection to get the URLConnection with or without a proxy 
		    URLConnection conn = EverytrailHelper.getConnection(url);
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

		log.info(postResponse.toString());
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
 * Gets the http connection for the API paying attention to the Placebooks proxy configuration values
 * requires a try/catch block as per url.openconnection()
 * @param URL url The url to get the connection for
 * @return URLConnection A url connection as per url.openConnection - with or without proxy
 */
	private static URLConnection getConnection(URL url) throws IOException
	{
		URLConnection conn;
	   if(PropertiesSingleton.get(EverytrailHelper.class.getClassLoader()).getProperty(PropertiesSingleton.PROXY_ACTIVE, "false").equalsIgnoreCase("true"))
	   {
	   	log.info("proxy:" + PropertiesSingleton.get(EverytrailHelper.class.getClassLoader()).getProperty(PropertiesSingleton.PROXY_HOST, "") + " " +
	   			PropertiesSingleton.get(EverytrailHelper.class.getClassLoader()).getProperty(PropertiesSingleton.PROXY_PORT, ""));
		    Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
		   		 PropertiesSingleton.get(EverytrailHelper.class.getClassLoader()).getProperty(PropertiesSingleton.PROXY_HOST, ""),
		   		 Integer.parseInt(PropertiesSingleton.get(EverytrailHelper.class.getClassLoader()).getProperty(PropertiesSingleton.PROXY_PORT, ""))));
		    conn = url.openConnection(proxy);
	    }
	    else
	    {
	   	 conn = url.openConnection();
	    }
	    return conn;
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
				String paramName = paramNames.nextElement();
				data.append(URLEncoder.encode(paramName, "UTF-8") + "=" + URLEncoder.encode(params.get(paramName), "UTF-8"));
				if(paramNames.hasMoreElements())
				{
					data.append("&");
				}
			} 

		    // Send data by setting up the api password http authentication and UoN proxy
		    // TODO Add global config for proxy support...
		    Authenticator.setDefault(new HttpAuthenticator(
		   		 PropertiesSingleton.get(EverytrailHelper.class.getClassLoader()).getProperty(PropertiesSingleton.EVERYTRAIL_API_USER, ""),
				    PropertiesSingleton.get(EverytrailHelper.class.getClassLoader()).getProperty(PropertiesSingleton.EVERYTRAIL_API_PASSWORD, "")
		    ));

		    URL url = new URL(apiBaseUrl + postDestination);
		    URLConnection conn = EverytrailHelper.getConnection(url);

		    
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
		String postResponse = getPostResponseWithParams("user/pictures", params);
		String resultStatus = postResponse;
		Vector<String> picturesList = new Vector<String>();
		
		EverytrailPicturesResponse returnValue = new EverytrailPicturesResponse(resultStatus, picturesList);
		return returnValue;
	}
	
	/**
	 * Get a list of tracks for a given trip
	 * @param String trackId an everytrail track id obtained from a user's trip - is it is a private trip, accessing the track may require logging in first
	 * at least get the user id
	 * @return EverytrailPicturesResponse 
	 */
	public static EverytrailTracksResponse Tracks(String trackId)
	{
		return Tracks(trackId, null, null);
	}
	
	
	/**
	 * Get a list of tracks for a given trip
	 * @param String trackId an everytrail track id obtained from a user's trip - is it is a private trip, accessing the track may require logging in first
	 * at least get the user id
	 * @param username String Everytrail username for private tracks
	 * @param password String Everytrail password for private tracks
	 * @return EverytrailPicturesResponse 
	 */
	public static EverytrailTracksResponse Tracks(String trackId, String username, String password)
	{
		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("track_id", trackId);
		if( username != null)
		{
			params.put("username", username);
		}
		if( password != null)
		{
			params.put("password", password);
		}
		String postResponse = getPostResponseWithParams("trip/tracks", params);
		String resultStatus = postResponse;
		Vector<String> tracksList = new Vector<String>();
		
		EverytrailTracksResponse returnValue = new EverytrailTracksResponse(resultStatus, tracksList);
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
