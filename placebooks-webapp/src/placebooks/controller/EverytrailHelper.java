/**
 * 
 */
package placebooks.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
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
import org.w3c.dom.Entity;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import placebooks.model.*;

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

		Document doc = parseResponseToXml(postResponse);
		// Parse the XML response and construct the response data to return
		log.debug(postResponse);
		EverytrailResponseStatusData responseStatus = EverytrailHelper.parseResponseStatus("etUserLoginResponse", doc);
		loginStatus = responseStatus.getStatus();
		loginStatusValue = responseStatus.getValue();
		log.debug(responseStatus.getStatus());
		log.debug(responseStatus.getValue());
		if(responseStatus.getStatus().equals("success"))
		{
			log.info("Log in succeeded with user id: " + responseStatus.getValue());
		}
		else
		{
			if(doc.getDocumentElement().getAttribute("status").equals("error"))
			{
				log.warn("Log in failed with error code: " + loginStatusValue);
			}
			else
			{
				log.error("Everytrail login status gave unexpected value: " + loginStatusValue);
			}
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
	   	log.debug("Using proxy: " + PropertiesSingleton.get(EverytrailHelper.class.getClassLoader()).getProperty(PropertiesSingleton.PROXY_HOST, "") + ":" +
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
	 * Parse and Everytrail SPI response into an XML document from HTTP string response.
	 * @param Strung Post response from everytrail http post
	 * @return Document XML structured document
	 */
	private static Document parseResponseToXml(String postString)
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		Document doc = null;
		try
		{
			db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(postString));
			doc = db.parse(is);
			doc.getDocumentElement().normalize();
		}
		catch(Exception ex)
		{
			log.equals("Problem parsing Everytrail XML response: " + ex.getMessage());
			log.debug(ex.getStackTrace());
		}
		return doc;
	}
	
	
	/**
	 * Parse and Everytrail SPI response into an XML document from HTTP string response.
	 * @param StrungBuilder Post response from everytrail http post
	 * @return Document XML structured document
	 */
	private static Document parseResponseToXml(StringBuilder postResponse)
	{
		return parseResponseToXml(postResponse.toString());
	}
	
	/**
	 * Return the success/error status of an Everytrail API call
	 * @param targetElementId
	 * @return
	 */
	private static EverytrailResponseStatusData parseResponseStatus(String targetElementId, Document doc)
	{
		String status = "";
		String value = "";
		try
		{
			if(doc.getDocumentElement().getNodeName() == targetElementId)
			{
				status = doc.getDocumentElement().getAttribute("status");
				if(status.equals("success"))
				{
					value = doc.getDocumentElement().getChildNodes().item(0).getTextContent();
				}
				else
				{
					log.debug("Everytrail call returned status: " + status);
					value = doc.getDocumentElement().getChildNodes().item(0).getTextContent();				
				}
			}
		}
		catch(Exception ex)
		{
			log.error("Problem checking Everytrail response: " + ex.getMessage());
			log.debug(ex.getStackTrace());
		}
		return new EverytrailResponseStatusData(status, value);
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
	 * @param userId an everytrail userid - supply their username and password if private pictures are needed 
	 * @param String A user's username
	 * @param String A user's password
	 * @return EverytrailPicturesResponse 
	 */
	public static EverytrailPicturesResponse Pictures(String userId, String username, String password)
	{
		// The pictures API doesn't work well, so get pictures via trips...
		EverytrailTripsResponse tripsData = EverytrailHelper.Trips(username, password, userId);

		Vector<Node> picturesToReturn = new Vector<Node>();
		String status_to_return = "error";

		if(tripsData.getStatus().equals("error"))
		{
			log.warn("Get pictures failed when getting trips with error code: " + tripsData.getStatus());
			picturesToReturn = tripsData.getTrips();
		}
		else
		{
			status_to_return = "success";
			Vector<Node> trips = tripsData.getTrips();
			//log.debug("Got " + trips.size() + " trips, geting pictures...");
			for(int tripListIndex=0; tripListIndex<trips.size(); tripListIndex++)
			{
				Node tripNode = trips.elementAt(tripListIndex);
				NamedNodeMap attributes = tripNode.getAttributes();
				String tripId = attributes.getNamedItem("id").getNodeValue();
				log.debug("Getting pictures for trip: " + tripId);
				EverytrailPicturesResponse tripPics = EverytrailHelper.TripPictures(tripId, username, password);

				log.debug("Pictures in trip: " + tripPics.getPictures().size());
				Vector<Node> tripPicList = tripPics.getPictures();
				for(int picturesDataIndex=0; picturesDataIndex<tripPicList.size(); picturesDataIndex++)
				{
					Node picture_node = tripPicList.get(picturesDataIndex);
					if(picture_node.getNodeName().equals("picture"))
					{
						picturesToReturn.add(picture_node);
					}
					log.debug("Picture: " + picturesDataIndex + " " + picture_node.getTextContent());
				}
			}
		}

		EverytrailPicturesResponse returnValue = new EverytrailPicturesResponse(status_to_return, picturesToReturn);
		return returnValue;
	}


	/**
	 * Get a list of pictures for a given userid
	 * @param userId an everytrail userid - supply their username and password if private pictures are needed 
	 * @param String A user's username
	 * @param String A user's password
	 * @return EverytrailVideosResponse 
	 */
	public static EverytrailVideosResponse Videos(String userId, String username, String password)
	{
		// The pictures API doesn't work well, so get pictures via trips...
		EverytrailTripsResponse tripsData = EverytrailHelper.Trips(username, password, userId);

		Vector<Node> picturesToReturn = new Vector<Node>();
		String status_to_return = "error";

		if(tripsData.getStatus().equals("error"))
		{
			log.warn("Get pictures failed when getting trips with error code: " + tripsData.getStatus());
			picturesToReturn = tripsData.getTrips();
		}
		else
		{
			status_to_return = "success";
			Vector<Node> trips = tripsData.getTrips();
			//log.debug("Got " + trips.size() + " trips, geting pictures...");
			for(int tripListIndex=0; tripListIndex<trips.size(); tripListIndex++)
			{
				Node tripNode = trips.elementAt(tripListIndex);
				NamedNodeMap attributes = tripNode.getAttributes();
				String tripId = attributes.getNamedItem("id").getNodeValue();
				log.debug("Getting pictures for trip: " + tripId);
				EverytrailVideosResponse tripPics = EverytrailHelper.TripVideos(tripId, username, password);

				log.debug("Videos in trip: " + tripPics.getVideos().size());
				Vector<Node> tripPicList = tripPics.getVideos();
				for(int picturesDataIndex=0; picturesDataIndex<tripPicList.size(); picturesDataIndex++)
				{
					Node picture_node = tripPicList.get(picturesDataIndex);
					if(picture_node.getNodeName().equals("picture"))
					{
						picturesToReturn.add(picture_node);
					}
					//log.debug("Video: " + picturesDataIndex + " " + picture_node.getTextContent());
				}
			}
		}

		EverytrailVideosResponse returnValue = new EverytrailVideosResponse(status_to_return, picturesToReturn);
		return returnValue;
	}

	
	
	/**
	 * Get a list of public pictures for a particular trip
	 * @param tripId
	 * @return Vector<Node>
	 */
	public static EverytrailPicturesResponse TripPictures(String tripId)
	{
		return TripPictures(tripId, null, null);
	}
	
	
	/**
	 * Get a list of pictures for a particular trip including private pictures
	 * @param tripId
	 * @param username
	 * @param password
	 * @return Vector<Node>
	 */
	public static EverytrailPicturesResponse TripPictures(String tripId, String username, String password)
	{
		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("trip_id", tripId);
		if( username != null)
		{
			params.put("username", username);
		}
		if( password != null)
		{
			params.put("password", password);
		}

		String postResponse = getPostResponseWithParams("trip/pictures", params);
		Vector<Node> picturesList = new Vector<Node>();

		Document doc = parseResponseToXml(postResponse);
		EverytrailResponseStatusData resultStatus = parseResponseStatus("etTripPicturesResponse", doc); 		
		if(resultStatus.getStatus().equals("success"))
		{
			NodeList pictureNodes = doc.getElementsByTagName("picture");;
			//log.debug("Child nodes: " + pictureNodes.getLength());
			for(int pictureNodesIndex=0; pictureNodesIndex<pictureNodes.getLength(); pictureNodesIndex++)
			{
				Node pictureNode = pictureNodes.item(pictureNodesIndex);
				picturesList.add(pictureNode);
			}
		}
		else
		{
			if(resultStatus.getStatus().equals("error"))
			{
				log.warn("Get pictures failed with error code: " + resultStatus.getValue());
				log.debug(postResponse);
			}
			else
			{
				log.error("Get pictures error status gave unexpected value: " + resultStatus.getStatus());				
			}
			NodeList response_list_data = doc.getElementsByTagName("error");
			{
				for(int i=0; i<response_list_data.getLength(); i++)
				{
					Node element = response_list_data.item(i);
					NodeList error_children = element.getChildNodes();
					for(int child_counter=0; child_counter<error_children.getLength(); child_counter++)
					{
						try
						{
							Node error_element = error_children.item(child_counter);
							picturesList.add(error_element);
						}
						catch(NullPointerException npx)
						{
							log.error("Can't interpret error data for item " + child_counter + " from response: " + resultStatus.getValue());							
						}
					}
				}
			}
		}
		return new EverytrailPicturesResponse(resultStatus.getStatus(), picturesList);	
	}
	
	/**
	 * Get a list of pictures for a particular trip including private pictures
	 * @param tripId
	 * @param username
	 * @param password
	 * @return Vector<Node>
	 */
	public static EverytrailVideosResponse TripVideos(String tripId, String username, String password)
	{
		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("trip_id", tripId);
		if( username != null)
		{
			params.put("username", username);
		}
		if( password != null)
		{
			params.put("password", password);
		}

		String postResponse = getPostResponseWithParams("trip/data", params);
		Vector<Node> picturesList = new Vector<Node>();

		Document doc = parseResponseToXml(postResponse);
		EverytrailResponseStatusData resultStatus = parseResponseStatus("etTripDataResponse", doc); 		
		if(resultStatus.getStatus().equals("success"))
		{
			log.info("Pictures success");
			NodeList pictureNodes = doc.getElementsByTagName("video");;
			//log.debug("Child nodes: " + pictureNodes.getLength());
			for(int pictureNodesIndex=0; pictureNodesIndex<pictureNodes.getLength(); pictureNodesIndex++)
			{
				Node pictureNode = pictureNodes.item(pictureNodesIndex);
				picturesList.add(pictureNode);
			}
		}
		else
		{
			if(resultStatus.getStatus().equals("error"))
			{
				log.warn("Get videos failed with error code: " + resultStatus.getValue());
				log.debug(postResponse);
			}
			else
			{
				log.error("Get videos error status gave unexpected value: " + resultStatus.getStatus());				
			}
			NodeList response_list_data = doc.getElementsByTagName("error");
			{
				for(int i=0; i<response_list_data.getLength(); i++)
				{
					Node element = response_list_data.item(i);
					NodeList error_children = element.getChildNodes();
					for(int child_counter=0; child_counter<error_children.getLength(); child_counter++)
					{
						try
						{
							Node error_element = error_children.item(child_counter);
							picturesList.add(error_element);
						}
						catch(NullPointerException npx)
						{
							log.error("Can't interpret error data for item " + child_counter + " from response: " + resultStatus.getValue());							
						}
					}
				}
			}
		}
		return new EverytrailVideosResponse(resultStatus.getStatus(), picturesList);	
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
	public static EverytrailTracksResponse Tracks(String tripId, String username, String password)
	{
		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("trip_id", tripId);
		if( username != null)
		{
			params.put("username", username);
		}
		if( password != null)
		{
			params.put("password", password);
		}
		String postResponse = getPostResponseWithParams("trip/tracks", params);
		Document doc = parseResponseToXml(postResponse);
		EverytrailResponseStatusData responseStatus = parseResponseStatus("etTripTracksResponse", doc);
		
		Vector<Node> tracksList = new Vector<Node>();
		if(responseStatus.getStatus().equals("success"))
		{
			NodeList response_list_data = doc.getElementsByTagName("track");
			for(int i=0; i<response_list_data.getLength(); i++)
			{
				Node element = response_list_data.item(i);
				tracksList.add(element);
			}
		}
		else
		{
			NodeList response_list_data = doc.getElementsByTagName("error");
			{
				for(int i=0; i<response_list_data.getLength(); i++)
				{
					Node element = response_list_data.item(i);
					NodeList error_children = element.getChildNodes();
					for(int child_counter=0; child_counter<error_children.getLength(); child_counter++)
					{
						try
						{
							Node error_element = error_children.item(child_counter);
							tracksList.add(error_element);
						}
						catch(NullPointerException npx)
						{
							log.error("Can't interpret error data for item " + child_counter + " from response: " + responseStatus.getValue());							
						}
					}
				}
			}
		}
		EverytrailTracksResponse returnValue = new EverytrailTracksResponse(responseStatus.getStatus(), tracksList);
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
		EverytrailTripsResponse result = Trips(null, null, userId, null, null, null, null, null, null, null, null);
		return  result;
	}

	/**
	 * Return a list of all trips for a given user_id - return private pictures and trips if username.password is given
	 * @param username
	 * @param password
	 * @param userId an everytrail userid obtained from a user - user may need to be logged in first, and this will
	 * at least get the user id
	 * @return
	 * @return EverytrailTripsResponse
	 */
	public static EverytrailTripsResponse Trips(String username, String password, String userId)
	{
		EverytrailTripsResponse result = Trips(username, password, userId, null, null, null, null, null, null, null, null);
		return  result;
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
		Document doc = parseResponseToXml(postResponse);
		EverytrailResponseStatusData responseStatus = parseResponseStatus("etUserTripsResponse", doc);
		
		Vector<Node> tripsList = new Vector<Node>();
		if(responseStatus.getStatus().equals("success"))
		{
			NodeList response_list_data = doc.getElementsByTagName("trip");
			for(int i=0; i<response_list_data.getLength(); i++)
			{
				Node element = response_list_data.item(i);
				tripsList.add(element);
			}
		}
		else
		{
			NodeList response_list_data = doc.getElementsByTagName("error");
			{
				for(int i=0; i<response_list_data.getLength(); i++)
				{
					Node element = response_list_data.item(i);
					NodeList error_children = element.getChildNodes();
					for(int child_counter=0; child_counter<error_children.getLength(); child_counter++)
					{
						try
						{
							Node error_element = error_children.item(child_counter);
							tripsList.add(error_element);
						}
						catch(NullPointerException npx)
						{
							log.error("Can't interpret error data for item " + child_counter + " from response: " + responseStatus.getValue());							
						}
					}
				}
			}
		}

		EverytrailTripsResponse returnValue = new EverytrailTripsResponse(responseStatus.getStatus(), tripsList);
		return returnValue;
	}
}
