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
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import javax.persistence.EntityManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import placebooks.model.EverytrailLoginResponse;
import placebooks.model.EverytrailPicturesResponse;
import placebooks.model.EverytrailResponseStatusData;
import placebooks.model.EverytrailTracksResponse;
import placebooks.model.EverytrailTripsResponse;
import placebooks.model.EverytrailVideosResponse;
import placebooks.model.ImageItem;
import placebooks.model.LoginDetails;
import placebooks.model.User;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * @author pszmp
 * 
 */
public class EverytrailHelper
{
	static class HttpAuthenticator extends Authenticator
	{
		private String username, password;

		public HttpAuthenticator(final String user, final String pass)
		{
			username = user;
			password = pass;
		}

		protected PasswordAuthentication getPasswordAuthentication()
		{
			log.debug("Requesting Host  : " + getRequestingHost());
			log.debug("Requesting Port  : " + getRequestingPort());
			log.debug("Requesting Prompt : " + getRequestingPrompt());
			log.debug("Requesting Protocol: " + getRequestingProtocol());
			log.debug("Requesting Scheme : " + getRequestingScheme());
			log.debug("Requesting Site  : " + getRequestingSite());
			return new PasswordAuthentication(username, password.toCharArray());
		}
	}

	public final static String SERVICE_NAME = "Everytrail";

	private static final String apiBaseUrl = "http://www.everytrail.com/api/";

	private static final Logger log = Logger.getLogger(EverytrailHelper.class.getName());

	/**
	 * Perform a post to the given Everytrail api destination with the parameters specified
	 * 
	 * @param postDestination
	 *            API destination after http://www.everytrail.com/api/ - e.g. user/trips
	 * @param Hashtable
	 *            <String, String> params a hastable of the parameters to post to the api with name
	 *            / values as strings
	 * @return String A string containing the post response from Everytrail
	 */
	private static String getPostResponseWithParams(final String postDestination, final Hashtable<String, String> params)
	{
		final StringBuilder postResponse = new StringBuilder();

		// Add version 3 param to all requests
		params.put("version", "3");
		// Construct data to post by iterating through parameter Hashtable keys Enumeration
		final StringBuilder data = new StringBuilder();
		final Enumeration<String> paramNames = params.keys();
		try
		{
			while (paramNames.hasMoreElements())
			{
				final String paramName = paramNames.nextElement();
				data.append(URLEncoder.encode(paramName, "UTF-8") + "="
						+ URLEncoder.encode(params.get(paramName), "UTF-8"));
				if (paramNames.hasMoreElements())
				{
					data.append("&");
				}
			}

			// Send data by setting up the api password http authentication and UoN proxy

			Authenticator.setDefault(new HttpAuthenticator(PropertiesSingleton.get(	EverytrailHelper.class
					.getClassLoader())
					.getProperty(PropertiesSingleton.EVERYTRAIL_API_USER, ""), PropertiesSingleton
					.get(EverytrailHelper.class.getClassLoader())
					.getProperty(PropertiesSingleton.EVERYTRAIL_API_PASSWORD, "")));

			final URL url = new URL(apiBaseUrl + postDestination);
			final URLConnection conn = CommunicationHelper.getConnection(url);

			conn.setDoOutput(true);
			final OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(data.toString());
			wr.flush();

			// Get the response
			final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null)
			{
				postResponse.append(line);
			}
			wr.close();
			rd.close();
		}
		catch (final Exception ex)
		{
			log.debug(ex.getMessage());
		}

		return postResponse.toString();
	}

	public static ImageItem imageItemFromEverytrailImage(final User owner, final Node everytrailPicture)
	{
		final ImageItem imageItem = new ImageItem(owner, null, null, null);

		final NamedNodeMap pictureAttributes = everytrailPicture.getAttributes();
		for (int attributeIndex = 0; attributeIndex < pictureAttributes.getLength(); attributeIndex++)
		{
			if (pictureAttributes.item(attributeIndex).getNodeName().equals("id"))
			{
				imageItem.addParameterEntry("picture_id",
						Integer.getInteger(pictureAttributes.item(attributeIndex).getNodeValue()));
			}
		}
		final NodeList pictureProperties = everytrailPicture.getChildNodes();
		for (int propertyIndex = 0; propertyIndex < pictureProperties.getLength(); propertyIndex++)
		{
			final Node item = pictureProperties.item(propertyIndex);
			final String itemName = item.getNodeName();
			log.debug("Inspecting property: " + itemName + " which is " + item.getTextContent());
			if (itemName.equals("fullsize"))
			{
				try
				{
					imageItem.setSourceURL(new URL(item.getTextContent()));
					final URL url = imageItem.getSourceURL();
					URLConnection conn;
					if (PropertiesSingleton.get(ImageItem.class.getClassLoader())
							.getProperty(PropertiesSingleton.PROXY_ACTIVE, "false").equalsIgnoreCase("true"))
					{
						log.debug("Using proxy: "
								+ PropertiesSingleton.get(ImageItem.class.getClassLoader())
								.getProperty(PropertiesSingleton.PROXY_HOST, "")
								+ ":"
								+ PropertiesSingleton.get(ImageItem.class.getClassLoader())
								.getProperty(PropertiesSingleton.PROXY_PORT, ""));
						final Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(PropertiesSingleton
								.get(ImageItem.class.getClassLoader()).getProperty(PropertiesSingleton.PROXY_HOST, ""),
								Integer.parseInt(PropertiesSingleton.get(ImageItem.class.getClassLoader())
										.getProperty(PropertiesSingleton.PROXY_PORT, ""))));
						conn = url.openConnection(proxy);
					}
					else
					{
						conn = url.openConnection();
					}

					// Get the response
					//					final BufferedImage bi = ImageIO.read(conn.getInputStream());
					//					imageItem.setImage(bi);
					//					log.debug("image width: " + bi.getWidth() + "px Height: " + bi.getHeight() + "px");

					try
					{
						imageItem.writeDataToDisk("blah.jpg", conn.getInputStream());
					}
					catch (final Throwable e)
					{
						log.error(e.getMessage(), e);
					}
				}
				catch (final MalformedURLException ex)
				{
					log.error("Can't convert Everytrail Picture URL to a valid URL.");
					log.debug(ex.getMessage());
				}
				catch (final IOException ex)
				{
					log.error("Can't download Everytrail Picture and convert to BufferedImage.");
					log.debug(ex.getMessage());
				}
			}
			if (itemName.equals("location"))
			{
				final NamedNodeMap locationAttributes = item.getAttributes();
				String lat = null;
				String lon = null;
				for (int locAttributeIndex = 0; locAttributeIndex < locationAttributes.getLength(); locAttributeIndex++)
				{
					if (locationAttributes.item(locAttributeIndex).getNodeName().equals("lat"))
					{
						lat = locationAttributes.item(locAttributeIndex).getNodeValue();
					}
					if (locationAttributes.item(locAttributeIndex).getNodeName().equals("lon"))
					{
						lon = locationAttributes.item(locAttributeIndex).getNodeValue();
					}
				}
				try
				{
					final GeometryFactory gf = new GeometryFactory();
					final Geometry newGeom = gf.toGeometry(new Envelope(new Coordinate(Double.parseDouble(lat), Double
							.parseDouble(lon))));
					log.debug("Detected coordinates " + lat.toString() + ", " + lon.toString());
					imageItem.setGeometry(newGeom);
				}
				catch (final Exception ex)
				{
					log.error("Couldn't get lat/lon data from Everytrail picture.");
					log.debug(ex.getMessage());
				}
			}
		}

		return imageItem;
	}

	/**
	 * Return the success/error status of an Everytrail API call
	 * 
	 * @param targetElementId
	 * @return
	 */
	private static EverytrailResponseStatusData parseResponseStatus(final String targetElementId, final Document doc)
	{
		String status = "";
		String value = "";
		try
		{
			if (doc.getDocumentElement().getNodeName() == targetElementId)
			{
				status = doc.getDocumentElement().getAttribute("status");
				if(doc.getDocumentElement().hasChildNodes())
				{
					if (status.equals("success"))
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
		}
		catch (final Exception e)
		{
			log.error("Problem checking Everytrail response: " + e.toString());
			log.debug(e.toString(), e);
		}
		return new EverytrailResponseStatusData(status, value);
	}

	/**
	 * Parse and Everytrail API response into an XML document from HTTP string response.
	 * 
	 * @param Strung
	 *            Post response from everytrail http post
	 * @return Document XML structured document
	 */
	private static Document parseResponseToXml(final String postString)
	{
		final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		Document doc = null;
		try
		{
			db = dbf.newDocumentBuilder();
			final InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(postString));
			doc = db.parse(is);
			doc.getDocumentElement().normalize();
		}
		catch (final Exception ex)
		{
			log.equals("Problem parsing Everytrail XML response: " + ex.getMessage());
			log.debug(ex.getStackTrace());
		}
		return doc;
	}

	/**
	 * Parse and Everytrail API response into an XML document from HTTP string response.
	 * 
	 * @param StrungBuilder
	 *            Post response from everytrail http post
	 * @return Document XML structured document
	 */
	private static Document parseResponseToXml(final StringBuilder postResponse)
	{
		return parseResponseToXml(postResponse.toString());
	}

	/**
	 * Return a list of all pictures for a given user_id
	 * 
	 * @param userId
	 * @return EverytrailPicturesResponse
	 */
	public static EverytrailPicturesResponse Pictures(final String userId)
	{
		return Pictures(userId, null, null);
	}

	/**
	 * Get a list of pictures for a given userid
	 * 
	 * @param userId
	 *            an everytrail userid - supply their username and password if private pictures are
	 *            needed
	 * @param String
	 *            A user's username
	 * @param String
	 *            A user's password
	 * @return EverytrailPicturesResponse
	 */
	public static EverytrailPicturesResponse Pictures(final String userId, final String username, final String password)
	{
		// The pictures API doesn't work well, so get pictures via trips...
		final EverytrailTripsResponse tripsData = EverytrailHelper.Trips(username, password, userId);

		HashMap<String, Node> picturesToReturn = new HashMap<String, Node>();
		HashMap<String, String> pictureTrips = new HashMap<String, String>();
		HashMap<String, String> tripNames = new HashMap<String, String>();
		
		String status_to_return = "error";

		if (tripsData.getStatus().equals("error"))
		{
			log.warn("Get pictures failed when getting trips with error code: " + tripsData.getStatus());
			//picturesToReturn = tripsData.getTrips();
		}
		else
		{
			status_to_return = "success";
			final Vector<Node> trips = tripsData.getTrips();
			// log.debug("Got " + trips.size() + " trips, getting pictures...");
			for (int tripListIndex = 0; tripListIndex < trips.size(); tripListIndex++)
			{
				final Node tripNode = trips.elementAt(tripListIndex);
				final NamedNodeMap attributes = tripNode.getAttributes();
				final String tripId = attributes.getNamedItem("id").getNodeValue();
				//Then look at the properties in the child nodes to get url, title, description, etc.
				final NodeList tripProperties = tripNode.getChildNodes();
				String tripName = "";
				for (int propertyIndex = 0; propertyIndex < tripProperties.getLength(); propertyIndex++)
				{
					final Node item = tripProperties.item(propertyIndex);
					final String itemName = item.getNodeName();
					//log.debug("Inspecting property: " + itemName + " which is " + item.getTextContent());
					if (itemName.equals("name"))
					{
						tripName = item.getTextContent();
					}
				}

				log.debug("Getting pictures for trip: " + tripId + " " +  tripName);
				tripNames.put(tripId, tripName);
				
				final EverytrailPicturesResponse tripPics = EverytrailHelper.TripPictures(tripId, username, password, tripName);

				log.debug("Pictures in trip: " + tripPics.getPicturesMap().values().size());
				final HashMap<String, Node> tripPicList = tripPics.getPicturesMap();
				for (String pic_id : tripPicList.keySet())
				{
					Node picture_node = tripPicList.get(pic_id);
					if (picture_node.getNodeName().equals("picture"))
					{						
						picturesToReturn.put(pic_id, picture_node);
						pictureTrips.put(pic_id, tripId);
					}
					//log.debug("Picture: " + pic_id + " " + picture_node.getTextContent());
				}
			}
		}

		final EverytrailPicturesResponse returnValue = new EverytrailPicturesResponse(status_to_return, picturesToReturn, pictureTrips, tripNames);
		return returnValue;
	}

	/**
	 * Get a list of tracks for a given trip
	 * 
	 * @param String
	 *            trackId an everytrail track id obtained from a user's trip - is it is a private
	 *            trip, accessing the track may require logging in first at least get the user id
	 * @return EverytrailPicturesResponse
	 */
	public static EverytrailTracksResponse Tracks(final String trackId)
	{
		return Tracks(trackId, null, null);
	}

	/**
	 * Get a list of tracks for a given trip
	 * 
	 * @param String
	 *            trackId an everytrail track id obtained from a user's trip - is it is a private
	 *            trip, accessing the track may require logging in first at least get the user id
	 * @param username
	 *            String Everytrail username for private tracks
	 * @param password
	 *            String Everytrail password for private tracks
	 * @return EverytrailPicturesResponse
	 */
	public static EverytrailTracksResponse Tracks(final String tripId, final String username, final String password)
	{
		final Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("trip_id", tripId);
		if (username != null)
		{
			params.put("username", username);
		}
		if (password != null)
		{
			params.put("password", password);
		}
		final String postResponse = getPostResponseWithParams("trip/tracks", params);
		final Document doc = parseResponseToXml(postResponse);
		final EverytrailResponseStatusData responseStatus = parseResponseStatus("etTripTracksResponse", doc);
		final Vector<Node> tracksList = new Vector<Node>();
		if (responseStatus.getStatus().equals("success"))
		{
			final NodeList response_list_data = doc.getElementsByTagName("track");
			for (int i = 0; i < response_list_data.getLength(); i++)
			{
				final Node element = response_list_data.item(i);
				tracksList.add(element);
			}
		}
		else
		{
			final NodeList response_list_data = doc.getElementsByTagName("error");
			{
				for (int i = 0; i < response_list_data.getLength(); i++)
				{
					final Node element = response_list_data.item(i);
					final NodeList error_children = element.getChildNodes();
					for (int child_counter = 0; child_counter < error_children.getLength(); child_counter++)
					{
						try
						{
							final Node error_element = error_children.item(child_counter);
							tracksList.add(error_element);
						}
						catch (final NullPointerException npx)
						{
							log.error("Can't interpret error data for item " + child_counter + " from response: "
									+ responseStatus.getValue());
						}
					}
				}
			}
		}
		final EverytrailTracksResponse returnValue = new EverytrailTracksResponse(responseStatus.getStatus(),
				tracksList);
		return returnValue;
	}

	/**
	 * Get a list of public pictures for a particular trip
	 * 
	 * @param tripId
	 * @return Vector<Node>
	 */
	public static EverytrailPicturesResponse TripPictures(final String tripId)
	{
		return TripPictures(tripId, null, null, null);
	}

	/**
	 * Get a list of pictures for a particular trip including private pictures
	 * 
	 * @param tripId
	 * @param username
	 * @param password
	 * @return Vector<Node>
	 */
	public static EverytrailPicturesResponse TripPictures(final String tripId, final String username,
			final String password, final String tripNameParam)
	{
		
		final Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("trip_id", tripId);
		if (username != null)
		{
			params.put("username", username);
		}
		if (password != null)
		{
			params.put("password", password);
		}

		String tripName = "";
		final HashMap<String, Node> picturesList = new HashMap<String, Node>();
		EverytrailResponseStatusData resultStatus = null;
		if(tripNameParam==null)
		{
			final String tripResponse = getPostResponseWithParams("trip/", params);
			final Document tripDoc = parseResponseToXml(tripResponse);
			final EverytrailResponseStatusData tripResultStatus = parseResponseStatus("etTripPicturesResponse", tripDoc);
			if (tripResultStatus.getStatus().equals("success"))
			{
				final NodeList nameNodes = tripDoc.getElementsByTagName("name");;
				// log.debug("Child nodes: " + pictureNodes.getLength());
				for (int nameNodesIndex = 0; nameNodesIndex < nameNodes.getLength(); nameNodesIndex++)
				{
					final Node nameNode = nameNodes.item(nameNodesIndex);
					tripName = nameNode.getTextContent();
				}
			}
			else
			{
				log.error("Can't get trip name");
			}
		}
		else
		{
			tripName = tripNameParam;
		}
		final HashMap<String, String> tripData = new HashMap<String, String>();
		tripData.put(tripId, tripName);
		final HashMap<String, String> pictureTrips = new HashMap<String, String>();
		
		
		final String postResponse = getPostResponseWithParams("trip/pictures", params);
		final Document doc = parseResponseToXml(postResponse);
		resultStatus = parseResponseStatus("etTripPicturesResponse", doc);

		if (resultStatus.getStatus().equals("success"))
		{
			final NodeList pictureNodes = doc.getElementsByTagName("picture");;
			//log.debug("Child nodes: " + pictureNodes.getLength());
			for (int pictureNodesIndex = 0; pictureNodesIndex < pictureNodes.getLength(); pictureNodesIndex++)
			{
				final Node pictureNode = pictureNodes.item(pictureNodesIndex);
			// Get pic ID
				final NamedNodeMap attr = pictureNode.getAttributes();
				final String id = attr.getNamedItem("id").getNodeValue();
				pictureTrips.put(id, tripId);
				picturesList.put(id, pictureNode);
			}
		}
		else
		{
			if (resultStatus.getStatus().equals("error"))
			{
				log.warn("Get pictures failed with error code: " + resultStatus.getValue());
				log.debug(postResponse);
			}
			else
			{
				log.error("Get pictures error status gave unexpected value: " + resultStatus.getStatus());
			}
			final NodeList response_list_data = doc.getElementsByTagName("error");
			{
				for (int i = 0; i < response_list_data.getLength(); i++)
				{
					final Node element = response_list_data.item(i);
					final NodeList error_children = element.getChildNodes();
					for (int child_counter = 0; child_counter < error_children.getLength(); child_counter++)
					{
						try
						{
							final Node error_element = error_children.item(child_counter);
							picturesList.put("0", error_element);
						}
						catch (final NullPointerException npx)
						{
							log.error("Can't interpret error data for item " + child_counter + " from response: "
									+ resultStatus.getValue());
						}
					}
				}
			}
		}
		return new EverytrailPicturesResponse(resultStatus.getStatus(), picturesList, pictureTrips, tripData);
	}

	/**
	 * Return a list of all trips for a given user_id
	 * 
	 * @param userId
	 *            an everytrail userid obtained from a user - user may need to be logged in first,
	 *            and this will at least get the user id
	 * @return EverytrailTripsResponse
	 */
	public static EverytrailTripsResponse Trips(final String userId)
	{
		final EverytrailTripsResponse result = Trips(null, null, userId, null, null, null, null, null, null, null, null);
		return result;
	}

	/**
	 * Return a list of all trips for a given user_id - return private pictures and trips if
	 * username.password is given
	 * 
	 * @param username
	 * @param password
	 * @param userId
	 *            an everytrail userid obtained from a user - user may need to be logged in first,
	 *            and this will at least get the user id
	 * @return
	 * @return EverytrailTripsResponse
	 */
	public static EverytrailTripsResponse Trips(final String username, final String password, final String userId)
	{
		final EverytrailTripsResponse result = Trips(	username, password, userId, null, null, null, null, null, null,
				null, null);
		return result;
	}

	/**
	 * Get a list of trips for a given userid
	 * 
	 * @param userId
	 *            an everytrail userid obtained from a user - user may need to be logged in first,
	 *            and this will at least get the user id
	 * @param limit
	 *            Number of photos to get, limit is 20 as default, set by everytrail API.
	 * @param start
	 *            Starting point for photos, default 0 set by everytrail api
	 * @return EverytrailTripsResponse
	 */
	public static EverytrailTripsResponse Trips(final String username, final String password, final String userId,
			final Double lat, final Double lon, final Date modifiedAfter, final String sort, final String order,
			final String limit, final String start, final Boolean minimal)
	{
		final Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("user_id", userId);

		if (username != null)
		{
			params.put("username", username);
		}
		if (password != null)
		{
			params.put("password", password);
		}

		if (lat != null)
		{
			params.put("lat", lat.toString());
		}
		if (lon != null)
		{
			params.put("lon", lon.toString());
		}

		if (modifiedAfter != null)
		{
			final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			params.put("modified_after", formatter.format(modifiedAfter));
		}

		if (sort != null)
		{
			params.put("sort", sort);
		}
		if (order != null)
		{
			params.put("order", order);
		}
		if (limit != null)
		{
			params.put("limit", limit);
		}
		if (limit != null)
		{
			params.put("start", start);
		}
		if (minimal != null)
		{
			params.put("minimal", minimal.toString());
		}

		final String postResponse = getPostResponseWithParams("user/trips", params);
		log.info(postResponse);
		final Document doc = parseResponseToXml(postResponse);
		final EverytrailResponseStatusData responseStatus = parseResponseStatus("etUserTripsResponse", doc);
		log.info("Trips called parseResponseStatus, result="
					 + responseStatus.getStatus());

		final Vector<Node> tripsList = new Vector<Node>();
		if (responseStatus.getStatus().equals("success"))
		{
			final NodeList response_list_data = doc.getElementsByTagName("trip");
			for (int i = 0; i < response_list_data.getLength(); i++)
			{
				final Node element = response_list_data.item(i);
				tripsList.add(element);
			}
		}
		else
		{
			final NodeList response_list_data = doc.getElementsByTagName("error");
			{
				for (int i = 0; i < response_list_data.getLength(); i++)
				{
					final Node element = response_list_data.item(i);
					final NodeList error_children = element.getChildNodes();
					for (int child_counter = 0; child_counter < error_children.getLength(); child_counter++)
					{
						try
						{
							final Node error_element = error_children.item(child_counter);
							tripsList.add(error_element);
						}
						catch (final NullPointerException npx)
						{
							log.error("Can't interpret error data for item " + child_counter + " from response: "
									+ responseStatus.getValue());
						}
					}
				}
			}
		}

		final EverytrailTripsResponse returnValue = new EverytrailTripsResponse(responseStatus.getStatus(), tripsList);
		return returnValue;
	}

	/**
	 * Get a list of pictures for a particular trip including private pictures
	 * 
	 * @param tripId
	 * @param username
	 * @param password
	 * @return Vector<Node>
	 */
	public static EverytrailVideosResponse TripVideos(final String tripId, final String username, final String password)
	{
		final Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("trip_id", tripId);
		if (username != null)
		{
			params.put("username", username);
		}
		if (password != null)
		{
			params.put("password", password);
		}

		final String postResponse = getPostResponseWithParams("trip/data", params);
		final Vector<Node> picturesList = new Vector<Node>();

		final Document doc = parseResponseToXml(postResponse);
		final EverytrailResponseStatusData resultStatus = parseResponseStatus("etTripDataResponse", doc);
		if (resultStatus.getStatus().equals("success"))
		{
			//log.info("Videos success");
			final NodeList pictureNodes = doc.getElementsByTagName("video");;
			// log.debug("Child nodes: " + pictureNodes.getLength());
			for (int pictureNodesIndex = 0; pictureNodesIndex < pictureNodes.getLength(); pictureNodesIndex++)
			{
				final Node pictureNode = pictureNodes.item(pictureNodesIndex);
				picturesList.add(pictureNode);
			}
		}
		else
		{
			if (resultStatus.getStatus().equals("error"))
			{
				log.warn("Get videos failed with error code: " + resultStatus.getValue());
				log.debug(postResponse);
			}
			else
			{
				log.error("Get videos error status gave unexpected value: " + resultStatus.getStatus());
			}
			final NodeList response_list_data = doc.getElementsByTagName("error");
			{
				for (int i = 0; i < response_list_data.getLength(); i++)
				{
					final Node element = response_list_data.item(i);
					final NodeList error_children = element.getChildNodes();
					for (int child_counter = 0; child_counter < error_children.getLength(); child_counter++)
					{
						try
						{
							final Node error_element = error_children.item(child_counter);
							picturesList.add(error_element);
						}
						catch (final NullPointerException npx)
						{
							log.error("Can't interpret error data for item " + child_counter + " from response: "
									+ resultStatus.getValue());
						}
					}
				}
			}
		}
		return new EverytrailVideosResponse(resultStatus.getStatus(), picturesList);
	}

	/**
	 * Copy all of a users content from Everytrail to Placebooks database, replacing items where
	 *  necessary based on everytrail ids
	 * @param pm EntityManager for database
	 * @param user 
	 */
	public static void UpdateEverytrailContent(final EntityManager pm, final User user)
	{
		LoginDetails login = user.getLoginDetails(SERVICE_NAME);
		EverytrailPicturesResponse picturesResponse = EverytrailHelper.Pictures(login.getUserID(), login.getUsername(), login.getPassword());
		Map<String, Node> pics = picturesResponse.getPicturesMap();

		for(String key : pics.keySet() )
		{
			ImageItem item = new ImageItem(user, null, null, null);
			String tripId = picturesResponse.getPictureTrips().get(key);
			ItemFactory.toImageItem(user, pics.get(key), item, tripId, picturesResponse.getTripNames().get(tripId));
		}
	}

	/**
	 * Log in to the Everytrail API with a given username and password n.b. this appears to be
	 * submitted as HTTP not HTTPS so password is potentially insecure.
	 * 
	 * @param username
	 *            An everytrail username
	 * @param password
	 *            An everytrail password
	 * @return EverytrailLoginResponse with status success/error and value userid/error code
	 */
	public static EverytrailLoginResponse UserLogin(final String username, final String password)
	{
		final StringBuilder postResponse = new StringBuilder();
		String loginStatus = "";
		String loginStatusValue = "";

		try
		{
			// Construct data to post - username and password
			String data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
			data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");

			// Send data by setting up the api password http authentication and UoN proxy
			Authenticator.setDefault(new HttpAuthenticator(PropertiesSingleton.get(	EverytrailHelper.class
					.getClassLoader())
					.getProperty(PropertiesSingleton.EVERYTRAIL_API_USER, ""), PropertiesSingleton
					.get(EverytrailHelper.class.getClassLoader())
					.getProperty(PropertiesSingleton.EVERYTRAIL_API_PASSWORD, "")));
			final URL url = new URL(apiBaseUrl + "user/login");

			// Use getConnection to get the URLConnection with or without a proxy
			final URLConnection conn = CommunicationHelper.getConnection(url);
			conn.setDoOutput(true);
			final OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(data);
			wr.flush();

			// Get the response
			final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null)
			{
				postResponse.append(line);
			}
			wr.close();
			rd.close();
		}
		catch (final Exception e)
		{
			log.debug(e.getMessage());
		}

		final Document doc = parseResponseToXml(postResponse);
		// Parse the XML response and construct the response data to return
		log.debug(postResponse);
		final EverytrailResponseStatusData responseStatus = 
			EverytrailHelper.parseResponseStatus("etUserLoginResponse", doc);
		log.info("UserLogin called parseResponseStatus, result="
				 + responseStatus.getStatus() + ","
				 + responseStatus.getValue());

		loginStatus = responseStatus.getStatus();
		loginStatusValue = responseStatus.getValue();
		log.debug(responseStatus.getStatus());
		log.debug(responseStatus.getValue());
		if (responseStatus.getStatus().equals("success"))
		{
			log.info("Log in succeeded with user id: " + responseStatus.getValue());
		}
		else
		{
			if (doc.getDocumentElement().getAttribute("status").equals("error"))
			{
				log.warn("Log in failed with error code: " + loginStatusValue);
			}
			else
			{
				log.error("Everytrail login status gave unexpected value: " + loginStatusValue);
			}
		}

		final EverytrailLoginResponse output = new EverytrailLoginResponse(loginStatus, loginStatusValue);
		return output;
	}

	/**
	 * Get a list of pictures for a given userid
	 * 
	 * @param userId
	 *            an everytrail userid - supply their username and password if private pictures are
	 *            needed
	 * @param String
	 *            A user's username
	 * @param String
	 *            A user's password
	 * @return EverytrailVideosResponse
	 */
	public static EverytrailVideosResponse Videos(final String userId, final String username, final String password)
	{
		// The pictures API doesn't work well, so get pictures via trips...
		final EverytrailTripsResponse tripsData = EverytrailHelper.Trips(username, password, userId);

		Vector<Node> picturesToReturn = new Vector<Node>();
		String status_to_return = "error";

		if (tripsData.getStatus().equals("error"))
		{
			log.warn("Get pictures failed when getting trips with error code: " + tripsData.getStatus());
			picturesToReturn = tripsData.getTrips();
		}
		else
		{
			status_to_return = "success";
			final Vector<Node> trips = tripsData.getTrips();
			// log.debug("Got " + trips.size() + " trips, geting pictures...");
			for (int tripListIndex = 0; tripListIndex < trips.size(); tripListIndex++)
			{
				final Node tripNode = trips.elementAt(tripListIndex);
				final NamedNodeMap attributes = tripNode.getAttributes();
				final String tripId = attributes.getNamedItem("id").getNodeValue();
				log.debug("Getting pictures for trip: " + tripId);
				final EverytrailVideosResponse tripPics = EverytrailHelper.TripVideos(tripId, username, password);

				log.debug("Videos in trip: " + tripPics.getVideos().size());
				final Vector<Node> tripPicList = tripPics.getVideos();
				for (int picturesDataIndex = 0; picturesDataIndex < tripPicList.size(); picturesDataIndex++)
				{
					final Node picture_node = tripPicList.get(picturesDataIndex);
					if (picture_node.getNodeName().equals("picture"))
					{
						picturesToReturn.add(picture_node);
					}
					// log.debug("Video: " + picturesDataIndex + " " +
					// picture_node.getTextContent());
				}
			}
		}

		final EverytrailVideosResponse returnValue = new EverytrailVideosResponse(status_to_return, picturesToReturn);
		return returnValue;
	}

}
