/**
 * 
 */
package org.placebooks.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.persistence.EntityManager;

import org.placebooks.client.model.ServiceInfo;
import org.placebooks.controller.CommunicationHelper;
import org.placebooks.controller.ItemFactory;
import org.placebooks.model.AudioItem;
import org.placebooks.model.GPSTraceItem;
import org.placebooks.model.ImageItem;
import org.placebooks.model.LoginDetails;
import org.placebooks.model.PlaceBookItem;
import org.placebooks.model.TextItem;
import org.placebooks.model.User;
import org.placebooks.model.VideoItem;
import org.placebooks.services.model.PeoplesCollectionItemFeature;
import org.placebooks.services.model.PeoplesCollectionItemResponse;
import org.placebooks.services.model.PeoplesCollectionLoginResponse;
import org.placebooks.services.model.PeoplesCollectionSearchTrailsResponse;
import org.placebooks.services.model.PeoplesCollectionTrailListItem;
import org.placebooks.services.model.PeoplesCollectionTrailResponse;
import org.placebooks.services.model.PeoplesCollectionTrailsResponse;

import com.google.gson.Gson;
import org.wornchaos.logger.Log;

/**
 * @author pszmp
 * 
 */
public class PeoplesCollectionService extends Service
{
	public final static ServiceInfo SERVICE_INFO = new ServiceInfo("PeoplesCollection",
			"http://www.peoplescollectionwales.com", false);

	private static final String apiBaseUrl = "http://www.peoplescollectionwales.com/mobile";

	protected static final Gson gson = new Gson();

	/**
	 * Get a given items
	 * 
	 * @param trailId
	 * @return
	 */
	public static PeoplesCollectionItemResponse Item(final int trailId)
	{
		PeoplesCollectionItemResponse result = null;

		final Hashtable<String, String> params = new Hashtable<String, String>();
		final String response = getPostResponseWithParams("/Get/Thumb/Item/" + trailId, params);

		try
		{
			// Log.debug(response);
			result = gson.fromJson(response.toString(), PeoplesCollectionItemResponse.class);
			Log.debug("PeoplesCollection response decoded: Total objects: " + result.GetTotalObjects());
		}
		catch (final Exception ex)
		{
			Log.error("Couldn't decode response from json : " + response.toString(), ex);
			result = new PeoplesCollectionItemResponse();
		}
		return result;
	}

	/**
	 * Log in to the People's Collection api
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	public static PeoplesCollectionLoginResponse Login(final String username, final String password)
	{
		final Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("username", username);
		params.put("password", password);
		final String response = getPostResponseWithParams("/authenticate", params);

		PeoplesCollectionLoginResponse returnResult = null;
		try
		{
			returnResult = gson.fromJson(response.toString(), PeoplesCollectionLoginResponse.class);
			Log.debug("PeoplecCollection response decoded: " + (returnResult.GetIsValid() ? "valid " : "not valid ")
					+ returnResult.GetReason());
		}
		catch (final Exception ex)
		{
			Log.error("Couldn't decode response from json : " + response.toString(), ex);
			returnResult = new PeoplesCollectionLoginResponse(false, "Unable to decode response:\n" + ex.getMessage());
		}

		return returnResult;
	}

	public static PeoplesCollectionSearchTrailsResponse Search(final double lon, final double lat, final double radius)
	{
		final double left = lon - radius;
		final double right = lon + radius;
		final double bottom = lat - radius;
		final double top = lat + radius;

		final Hashtable<String, String> params = new Hashtable<String, String>();
		final String bbox = Double.toString(left) + "," + Double.toString(bottom) + "," + Double.toString(right) + ","
				+ Double.toString(top);
		params.put("types", "trails");

		final String response = getPostResponseWithParams("/search?bbox=" + bbox + "&types=trails", params);
		PeoplesCollectionItemResponse items = null;

		try
		{
			items = gson.fromJson(response.toString(), PeoplesCollectionItemResponse.class);
			Log.debug("PeoplesCollection response decoded: " + items.GetTotalObjects());
		}
		catch (final Exception ex)
		{
			Log.error("Couldn't decode response from json : " + response.toString(), ex);
			items = new PeoplesCollectionItemResponse();
		}

		final PeoplesCollectionSearchTrailsResponse returnItems = new PeoplesCollectionSearchTrailsResponse();

		returnItems.SetTotalObjects(items.GetTotalObjects());
		returnItems.SetPerPage(items.getFeatures().length);
		for (final PeoplesCollectionItemFeature item : items.getFeatures())
		{
			final String idType = item.GetId().substring(0, item.GetId().indexOf('.'));
			if (idType.equals("trail"))
			{
				Log.debug("Got a trail: " + item.GetId());
				final PeoplesCollectionTrailListItem trailItem = new PeoplesCollectionTrailListItem(item.GetId(),
						item.GetType(), item.GetPeoplesCollectionGeometry(), item.GetProperties());
				returnItems.AddItem(trailItem);
			}
		}

		return returnItems;
	}

	/**
	 * Get the details of a particular trail
	 * 
	 * @param trailId
	 * @return
	 */
	public static PeoplesCollectionTrailResponse Trail(final int trailId)
	{
		PeoplesCollectionTrailResponse result = null;

		final Hashtable<String, String> params = new Hashtable<String, String>();
		final String response = getPostResponseWithParams("/Get/full/trail/" + trailId, params);

		try
		{
			result = gson.fromJson(response.toString(), PeoplesCollectionTrailResponse.class);
			Log.debug("PeoplesCollection response decoded: " + result.GetProperties().GetTitle());
		}
		catch (final Exception ex)
		{
			Log.error("Couldn't decode response from json : " + response.toString(), ex);
			result = new PeoplesCollectionTrailResponse();
		}
		return result;
	}

	/**
	 * Get the list of trails created by or favourites of a given user
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	public static PeoplesCollectionTrailsResponse TrailsByUser(final String username, final String password)
	{
		final Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("username", username);
		params.put("password", password);
		final String response = getPostResponseWithParams("/GetTrailsByUser", params);
		PeoplesCollectionTrailsResponse returnResult = null;

		try
		{
			returnResult = gson.fromJson(response.toString(), PeoplesCollectionTrailsResponse.class);
			Log.debug("PeoplesCollection response decoded: "
					+ (returnResult.GetAuthenticationResponse().GetIsValid() ? "valid " : "not valid ")
					+ returnResult.GetAuthenticationResponse().GetReason());
		}
		catch (final Exception ex)
		{
			Log.error("Couldn't decode response from json : " + response.toString(), ex);
			returnResult = new PeoplesCollectionTrailsResponse();
		}

		return returnResult;
	}

	/**
	 * Perform a post to the given People's Collection api destination with the parameters specified
	 * 
	 * @param postDestination
	 *            API destination after http://mubaloo.alpha.peoplescollection.or - e.g. user/trips
	 * @return String A string containing the post response from Everytrail
	 */
	private static String getPostResponseWithParams(final String postDestination, final Hashtable<String, String> params)
	{
		final StringBuilder postResponse = new StringBuilder();

		// Construct data to post by converting data to json
		final StringBuilder data = new StringBuilder();
		try
		{
			data.append(gson.toJson(params));
			Log.debug("JSON post: " + data.toString());

			final URL url = new URL(apiBaseUrl + postDestination);
			Log.debug("URL: " + apiBaseUrl + postDestination);
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
			Log.debug(ex.getMessage());
		}

		return postResponse.toString();
	}

	@Override
	public boolean checkLogin(final String username, final String password)
	{
		final PeoplesCollectionLoginResponse loginResponse = Login(username, password);
		return loginResponse.GetIsValid();
	}

	@Override
	public ServiceInfo getInfo()
	{
		return SERVICE_INFO;
	}

	@Override
	public void search(final EntityManager manager, final User user, final double lon, final double lat,
			final double radius)
	{

		final PeoplesCollectionSearchTrailsResponse items = Search(lon, lat, radius);
		final ArrayList<String> itemsSeen = new ArrayList<String>();
		for (final PeoplesCollectionTrailListItem trailListItem : items.GetTrails())
		{
			final PeoplesCollectionTrailResponse trail = PeoplesCollectionService
					.Trail(trailListItem.GetPropertiesId());
			final GPSTraceItem traceItem = new GPSTraceItem(user);
			try
			{
				importTrail(manager, user, trail, traceItem, trailListItem, itemsSeen);
			}
			catch (final Exception e)
			{
				Log.error(	"Couldn't convert GPS item from Peoples Collection id#  " + trailListItem.GetPropertiesId(),
							e);
			}
		}
	}

	protected GPSTraceItem importTrail(final EntityManager manager, final User user,
			final PeoplesCollectionTrailResponse trail, final GPSTraceItem traceItem,
			final PeoplesCollectionTrailListItem trailListItem, final ArrayList<String> itemsSeen) throws Exception
	{
		Log.info("Importing Peoples Collection Trail: " + trail.GetPropertiesId() + " '"
				+ trail.GetProperties().GetTitle());
		try
		{
			ItemFactory.toGPSTraceItem(user, trail, traceItem);
			itemsSeen.add(traceItem.getExternalID());
			Log.debug("Saving GPSTraceItem: " + traceItem.getExternalID());
			traceItem.saveUpdatedItem();
		}
		catch (final Exception ex)
		{
			Log.error(ex.getMessage());
		}

		for (final int trailItemId : trail.GetProperties().GetItems())
		{
			try
			{
				final PeoplesCollectionItemResponse trailItem = Item(trailItemId);
				final PeoplesCollectionItemFeature[] features = trailItem.getFeatures();
				for (final PeoplesCollectionItemFeature feature : features)
				{
					final String featureType = feature.GetProperties().GetIcontype();
					PlaceBookItem addedItem = null;
					if (featureType.equals("markerImage"))
					{
						final ImageItem newItem = new ImageItem();
						ItemFactory.toImageItem(user, feature, trail.GetPropertiesId(), trailListItem.GetProperties()
								.GetTitle(), newItem);
						newItem.saveUpdatedItem();
						addedItem = newItem;
					}
					if (featureType.equals("video"))
					{
						final VideoItem newItem = new VideoItem();
						ItemFactory.toVideoItem(user, feature, trail.GetPropertiesId(), trailListItem.GetProperties()
								.GetTitle(), newItem);
						newItem.saveUpdatedItem();
						addedItem = newItem;
					}
					if (featureType.equals("audio"))
					{
						final AudioItem newItem = new AudioItem();
						ItemFactory.toAudioItem(user, feature, trail.GetPropertiesId(), trailListItem.GetProperties()
								.GetTitle(), newItem);
						newItem.saveUpdatedItem();
						addedItem = newItem;
					}

					if (featureType.equals("story"))
					{
						final TextItem newItem = new TextItem();
						ItemFactory.toTextItem(user, feature, trail.GetPropertiesId(), trailListItem.GetProperties()
								.GetTitle(), newItem);
						newItem.saveUpdatedItem();
						addedItem = newItem;
					}

					if (addedItem == null)
					{
						Log.debug("Unknown Peoples Collection feature type: " + featureType + " for item "
								+ feature.GetPropertiesId());
					}
					else
					{
						Log.debug("Saved item: " + addedItem.getExternalID());
						itemsSeen.add(addedItem.getExternalID());
					}
				}
			}
			catch (final Exception ex)
			{
				Log.error(ex.getMessage());
			}
		}
		return traceItem;
	}

	@Override
	protected void sync(final EntityManager manager, final User user, final LoginDetails details, final double lon,
			final double lat, final double radius)
	{
		final PeoplesCollectionTrailsResponse trailsResponse = PeoplesCollectionService.TrailsByUser(details
				.getUsername(), details.getPassword());
		Log.debug("Number of my trails got:" + trailsResponse.GetMyTrails().size());
		Log.debug("Number of favourite trails got:" + trailsResponse.GetMyFavouriteTrails().size());

		final ArrayList<String> itemsSeen = new ArrayList<String>();

		final ArrayList<PeoplesCollectionTrailListItem> allTrailListItems = new ArrayList<PeoplesCollectionTrailListItem>();

		allTrailListItems.addAll(trailsResponse.GetMyTrails());
		allTrailListItems.addAll(trailsResponse.GetMyFavouriteTrails());

		for (final PeoplesCollectionTrailListItem trailListItem : allTrailListItems)
		{
			final PeoplesCollectionTrailResponse trail = PeoplesCollectionService
					.Trail(trailListItem.GetPropertiesId());
			final GPSTraceItem traceItem = new GPSTraceItem(user);
			try
			{
				importTrail(manager, user, trail, traceItem, trailListItem, itemsSeen);
			}
			catch (final Exception e)
			{
				Log.error(	"Couldn't convert GPS item from Peoples Collection id#  " + trailListItem.GetPropertiesId(),
							e);
			}
		}

		final int itemsDeleted = cleanupItems(manager, itemsSeen, user);
		Log.info("Finished PeoplesCollection cleanup, " + itemsSeen.size() + " items added/updated, " + itemsDeleted
				+ " removed");

		final PeoplesCollectionSearchTrailsResponse searchResponse = Search(54, -4, 0.1);
		for (final PeoplesCollectionTrailListItem trailListItem : searchResponse.GetTrails())
		{
			final PeoplesCollectionTrailResponse trail = PeoplesCollectionService
					.Trail(trailListItem.GetPropertiesId());
			final GPSTraceItem traceItem = new GPSTraceItem(user);
			try
			{
				importTrail(manager, user, trail, traceItem, trailListItem, itemsSeen);
			}
			catch (final Exception e)
			{
				Log.error(	"Couldn't convert GPS item from Peoples Collection id#  " + trailListItem.GetPropertiesId(),
							e);
			}
		}
	}

}
