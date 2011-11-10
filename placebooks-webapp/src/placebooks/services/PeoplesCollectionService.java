/**
 * 
 */
package placebooks.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import placebooks.controller.CommunicationHelper;
import placebooks.controller.ItemFactory;
import placebooks.model.GPSTraceItem;
import placebooks.model.ImageItem;
import placebooks.model.LoginDetails;
import placebooks.model.PlaceBookItem;
import placebooks.model.TextItem;
import placebooks.model.User;
import placebooks.services.model.PeoplesCollectionItemFeature;
import placebooks.services.model.PeoplesCollectionLoginResponse;
import placebooks.services.model.PeoplesCollectionItemResponse;
import placebooks.services.model.PeoplesCollectionTrailListItem;
import placebooks.services.model.PeoplesCollectionTrailResponse;
import placebooks.services.model.PeoplesCollectionTrailsResponse;

/**
 * @author pszmp
 *
 */
public class PeoplesCollectionService extends Service
{
	public final static String SERVICE_NAME = "PeoplesCollection";
	private static final String apiBaseUrl = "http://www.peoplescollectionwales.com/mobile";


	protected static final ObjectMapper mapper = new ObjectMapper();
	private static final Logger log = Logger.getLogger(PeoplesCollectionService.class);
	

	@Override
	public String getName()
	{
		return SERVICE_NAME;
	}
	
	/**
	 * Perform a post to the given People's Collection api destination with the parameters specified
	 * 
	 * @param postDestination
	 *            API destination after http://mubaloo.alpha.peoplescollection.or - e.g. user/trips
	 * @param Hashtable
	 *            <String, String> params a hastable of the parameters to post to the api with name
	 *            / values as strings
	 * @return String A string containing the post response from Everytrail
	 */
	private static String getPostResponseWithParams(final String postDestination, final Hashtable<String, String> params)
	{
		final StringBuilder postResponse = new StringBuilder();

		// Construct data to post by converting data to json
		final StringBuilder data = new StringBuilder();
		try
		{
			data.append(mapper.writeValueAsString(params));
			log.debug("JSON post: " + data.toString());
			

			final URL url = new URL(apiBaseUrl + postDestination);
			log.debug("URL: " + apiBaseUrl +  postDestination);
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


	
	@Override
	public boolean checkLogin(String username, String password)
	{
		PeoplesCollectionLoginResponse loginResponse = Login(username, password);
		return loginResponse.GetIsValid();
	}
	
	/**
	 * Log in to the People's Collection api
	 * @param username
	 * @param password
	 * @return 
	 */
	public static PeoplesCollectionLoginResponse Login(String username, String password)
	{
		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("username", username);
		params.put("password", password);
		String response = getPostResponseWithParams("/authenticate", params);
		
		PeoplesCollectionLoginResponse returnResult = null;
		mapper.getSerializationConfig().setSerializationInclusion(JsonSerialize.Inclusion.NON_DEFAULT);
		try
		{
			returnResult = mapper.readValue(response.toString(), PeoplesCollectionLoginResponse.class);
			log.debug("PeoplecCollection response decoded: " + (returnResult.GetIsValid() ? "valid " : "not valid ")  + returnResult.GetReason());
		}
		catch(Exception ex)
		{
			log.error("Couldn't decode response from json : " + response.toString(), ex);
			returnResult = new PeoplesCollectionLoginResponse(false, "Unable to decode response:\n" +  ex.getMessage());			
		}
		
		return returnResult;
	}
	
	/**
	 * Get the list of trails created by or favourites of a given user
	 * @param username
	 * @param password
	 * @return
	 */
	public static PeoplesCollectionTrailsResponse TrailsByUser(String username, String password)
	{
		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("username", username);
		params.put("password", password);
		String response = getPostResponseWithParams("/GetTrailsByUser", params);
		PeoplesCollectionTrailsResponse returnResult = null;
		mapper.getSerializationConfig().setSerializationInclusion(JsonSerialize.Inclusion.NON_DEFAULT);
		mapper.enableDefaultTyping();
		
		try
		{
			returnResult = mapper.readValue(response.toString(), PeoplesCollectionTrailsResponse.class);
			log.debug("PeoplesCollection response decoded: " + (returnResult.GetAuthenticationResponse().GetIsValid() ? "valid " : "not valid ")  + returnResult.GetAuthenticationResponse().GetReason());
		}
		catch(Exception ex)
		{
			log.error("Couldn't decode response from json : " + response.toString(), ex);
			returnResult = new PeoplesCollectionTrailsResponse();
		}
		
		return returnResult;
	}


	/**
	 * Get the details of a particular trail
	 * @param trailId
	 * @return
	 */
	public static PeoplesCollectionTrailResponse Trail(int trailId)
	{
		PeoplesCollectionTrailResponse result = null;
		
		Hashtable<String, String> params = new Hashtable<String, String>();
		String response = getPostResponseWithParams("/Get/full/trail/" + trailId, params);

		mapper.getSerializationConfig().setSerializationInclusion(JsonSerialize.Inclusion.NON_DEFAULT);
		try
		{
			result = mapper.readValue(response.toString(), PeoplesCollectionTrailResponse.class);
			log.debug("PeoplesCollection response decoded: " + result.GetProperties().GetTitle());
		}
		catch(Exception ex)
		{
			log.error("Couldn't decode response from json : " + response.toString(), ex);
			result = new PeoplesCollectionTrailResponse();
		}
		return result;
	}
	
	/**
	 * Get a given items 
	 * @param trailId
	 * @return
	 */
	public static PeoplesCollectionItemResponse Item(int trailId)
	{
		PeoplesCollectionItemResponse result = null;
		
		Hashtable<String, String> params = new Hashtable<String, String>();
		String response = getPostResponseWithParams("/Get/Thumb/Item/" + trailId, params);

		mapper.getSerializationConfig().setSerializationInclusion(JsonSerialize.Inclusion.NON_DEFAULT);
		try
		{
			//log.debug(response);
			result = mapper.readValue(response.toString(), PeoplesCollectionItemResponse.class);
			log.debug("PeoplesCollection response decoded: Total objects: " + result.GetTotalObjects());
		}
		catch(Exception ex)
		{
			log.error("Couldn't decode response from json : " + response.toString(), ex);
			result = new PeoplesCollectionItemResponse();
		}
		return result;
	}

	@Override
	protected void sync(EntityManager manager, User user, LoginDetails details) {
		PeoplesCollectionTrailsResponse trailsResponse = PeoplesCollectionService.TrailsByUser(details.getUserID(), details.getPassword());
		log.debug("Number of my trails got:" + trailsResponse.GetMyTrails().size());
		
		ArrayList<String> itemsSeen = new ArrayList<String>();
		for(PeoplesCollectionTrailListItem trailListItem : trailsResponse.GetMyTrails())
		{
			PeoplesCollectionTrailResponse trail = PeoplesCollectionService.Trail(trailListItem.GetPropertiesId());
			GPSTraceItem traceItem = new GPSTraceItem(user);
			try
			{
				ItemFactory.toGPSTraceItem(user, trail, traceItem);
				itemsSeen.add(traceItem.getExternalID());
				
				for(int trailItemId : trail.GetProperties().GetItems())
				{
					PeoplesCollectionItemResponse trailItem = Item(trailItemId);
					PeoplesCollectionItemFeature[] features = trailItem.getFeatures();
					for(PeoplesCollectionItemFeature feature : features)
					{
						String featureType = feature.GetProperties().GetIcontype();
						PlaceBookItem addedItem = null;
						if(featureType.equals("image"))
						{
							ImageItem newItem = new ImageItem();
							ItemFactory.toImageItem(user, feature, trailListItem.GetPropertiesId(), trailListItem.GetProperties().GetTitle(), newItem);
							addedItem = newItem;
						}
						if(featureType.equals("story"))
						{
							TextItem newItem = new TextItem();
							ItemFactory.toTextItem(user, feature, trailListItem.GetPropertiesId(), trailListItem.GetProperties().GetTitle(), newItem);
							addedItem = newItem;
						}

						if(addedItem==null)
						{
								log.debug("Unknown Peoples Collection feature type: " + featureType + " for item " + feature.GetPropertiesId());
						}
						else
						{
							addedItem.saveUpdatedItem();
							itemsSeen.add(addedItem.getExternalID());
						}
					}
				}
				
			}
			catch (Exception e)
			{
				log.error("Couldn't convert GPS item from Peoples Collection id#  " + trailListItem.GetPropertiesId(), e);
			}
		}
		log.debug("Number of favourite trails got:" + trailsResponse.GetMyFavouriteTrails().size());
		
		int itemsDeleted = cleanupItems(manager, itemsSeen, user);
		details.setLastSync();			
		log.info("Finished PeoplesCollection import, " + itemsSeen.size() + " items added/updated, " + itemsDeleted + " removed");			

	}
}
