/**
 *
 */
package org.placebooks.controller;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;
import org.placebooks.model.*;
import org.placebooks.services.EverytrailService;
import org.placebooks.services.PeoplesCollectionService;
import org.placebooks.services.model.PeoplesCollectionItemFeature;
import org.placebooks.services.model.PeoplesCollectionTrailResponse;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wornchaos.logger.Log;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Collection;

/**
 * @author pszmp This class provides converters to Everytrail Items from other data, such as
 *         Everytrail images, gps Logs, etc
 */
public class ItemFactory
{
	public static final String ALTERNATE_EVERYTRAIL_GPX_URL = "http://www.everytrail.com/downloadGPX.php?trip_id=";

	public static PlaceBookItem createItem(final EntityManager manager, final String externalID, final String mimeType)
	{
		// TODO Check supported mimeTypes
		PlaceBookItem item = ItemFactory.GetExistingItem(externalID, manager);
		if (item == null)
		{
			if (mimeType.startsWith("image/"))
			{
				item = new ImageItem();
			}
			else if (mimeType.startsWith("video/"))
			{
				item = new VideoItem();
			}
			else if (mimeType.startsWith("audio/"))
			{
				item = new AudioItem();
			}
			else if (mimeType.startsWith("text/"))
			{
				item = new TextItem();
			}

			if (item != null)
			{
				item.addMetadataEntry("mimeType", mimeType);
				item.setExternalID(externalID);
				manager.persist(item);
			}
		}

		return item;
	}

	/**
	 * Gets the item with the external id or null if there is none. n.b. assumes ther's only one of
	 * these in the db.
	 */
	public static PlaceBookItem getExistingItem(final PlaceBookItem itemToSave, final EntityManager em)
	{
		PlaceBookItem item = null;
		Log.debug("Querying externalID " + itemToSave.getExternalID());
		final TypedQuery<PlaceBookItem> q = em
				.createQuery("SELECT placebookitem FROM PlaceBookItem as placebookitem where (placebookitem.externalID = ?1) AND (placebookitem.placebook is null)",
						PlaceBookItem.class);
		q.setParameter(1, itemToSave.getExternalID());
		try
		{
			final Collection<PlaceBookItem> l = q.getResultList();
			if (l.size() == 1)
			{
				item = l.iterator().next();
			}
			else if (l.size() > 1)
			{
				Log.warn("Removing duplicate items for " + itemToSave.getExternalID());
				for (final PlaceBookItem o : l)
				{
					Log.debug("Removing: " + o.getKey());
					em.remove(o);
				}
				em.flush();
			}

		}
		catch (final NoResultException ex)
		{
			Log.error(ex.toString());
			item = null;
		}

		return item;
	}

	public static PlaceBookItem GetExistingItem(final String externalID, final EntityManager em)
	{
		PlaceBookItem item = null;
		Log.debug("Querying externalID " + externalID);
		final TypedQuery<PlaceBookItem> q = em
				.createQuery("SELECT placebookitem FROM PlaceBookItem as placebookitem where (placebookitem.externalID = ?1) AND (placebookitem.placebook is null)",
						PlaceBookItem.class);
		q.setParameter(1, externalID);
		try
		{
			final Collection<PlaceBookItem> l = q.getResultList();
			if (l.size() == 1)
			{
				item = l.iterator().next();
			}
			else if (l.size() > 1)
			{
				Log.warn("Removing duplicate items for " + externalID);
				for (final PlaceBookItem o : l)
				{
					Log.debug("Removing: " + o.getKey());
					em.remove(o);
				}
				em.flush();
			}

		}
		catch (final NoResultException ex)
		{
			Log.error(ex.toString());
			item = null;
		}

		return item;
	}

	/**
	 * Convert an Peoples Collection Audio to an Audio item for the given user
	 */
	public static void toAudioItem(final User testUser, final PeoplesCollectionItemFeature item, final int trailId,
			final String trailName, final AudioItem audioItem)
	{
		URL sourceUrl = null;
		final int audio_id = item.GetPropertiesId();
		String itemTitle = "";
		String audioItemTitle = "";
		String itemDescription = "";
		Geometry geom = null;
		audioItem.addMetadataEntry("trip", Integer.toString(trailId));

		if (trailName != null)
		{
			audioItem.addMetadataEntryIndexed("trip_name", trailName);
		}

		Log.debug("audio id is: " + audio_id);

		itemTitle = item.GetProperties().GetTitle();
		itemDescription = item.GetProperties().GetMarkup();
		try
		{
			sourceUrl = new URL(item.GetProperties().GetMediaURL().replace(" ", "%20"));
		}
		catch (final MalformedURLException ex)
		{
			Log.error("Couldn't get URL for peoples collection audio.");
			Log.debug(ex.getMessage());
		}

		try
		{
			final Geometry newGeom = item.GetGeometry();
			Log.debug("Detected coordinates geometry: " + newGeom.toText());
			geom = newGeom;
		}
		catch (final Exception ex)
		{
			Log.error("Couldn't get lat/lon data from peoples collection audio.", ex);
			Log.debug(ex.getMessage());
		}

		if (sourceUrl != null)
		{
			if (!itemTitle.equals(""))
			{
				audioItemTitle = itemTitle;
			}
			else
			{
				audioItemTitle = Integer.toString(audio_id);
			}
			try
			{
				final URLConnection conn = CommunicationHelper.getConnection(sourceUrl);
				audioItem.writeDataToDisk(sourceUrl.getFile().replace("?" + sourceUrl.getQuery(), ""),
						conn.getInputStream(), sourceUrl.toString());
			}
			catch (final IOException ex)
			{
				Log.error("Can't download Peoples Collection audio: " + sourceUrl.toExternalForm());
				Log.debug(ex.getMessage());
			}
			catch (final Throwable e)
			{
				Log.error(e.getMessage(), e);
			}
		}
		audioItem.setOwner(testUser);
		audioItem.setGeometry(geom);
		audioItem.setSourceURL(sourceUrl);
		// = new ImageItem(testUser, geom, sourceUrl, markerImage);
		audioItem.setExternalID("peoplescollection-" + audio_id);
		audioItem.addMetadataEntryIndexed("title", audioItemTitle);
		audioItem.addMetadataEntryIndexed("description", itemDescription);
		audioItem.addMetadataEntry("source", PeoplesCollectionService.SERVICE_INFO.getName());
	}

	/**
	 * Convert an Everytrail track to a GPSTraceItem
	 *
	 * @param owner    User creating this item
	 * @param tripId
	 * @param tripName
	 * @throws Exception
	 */
	public static void toGPSTraceItem(final User owner, final Node tripItem, final GPSTraceItem gpsItem,
			final String tripId, final String tripName) throws Exception
	{
		// tripItem.toString();
		// Log.debug(tripItem.getTextContent());

		gpsItem.setGeometry(null);
		gpsItem.addMetadataEntry("source", EverytrailService.SERVICE_INFO.getName());

		if (tripId != null)
		{
			Log.debug("Trip id is: " + tripId);
			gpsItem.setExternalID("everytrail-" + tripId);
			gpsItem.addMetadataEntry("trip", tripId);
		}

		if (tripName != null)
		{
			Log.debug("Trip name is: " + tripName);
			gpsItem.addMetadataEntryIndexed("trip_name", tripName);
			gpsItem.addMetadataEntryIndexed("title", tripName);
		}

		String tripGpxUrlString = "";
		String tripGpxUrlStringAlternate = "";
		URL tripGpxUrl = null;

		// Then look at the properties in the child nodes to get url, title, description, etc.
		final NodeList tripProperties = tripItem.getChildNodes();
		for (int propertyIndex = 0; propertyIndex < tripProperties.getLength(); propertyIndex++)
		{
			final Node item = tripProperties.item(propertyIndex);
			final String itemName = item.getNodeName();
			// Log.debug("Inspecting property: " + itemName + " which is " + item.getTextContent());
			if (itemName.equals("gpx"))
			{
				Log.debug("Trip GPX URL found, length: " + item.getTextContent().length());
				tripGpxUrlString = item.getTextContent();
				tripGpxUrlStringAlternate = ALTERNATE_EVERYTRAIL_GPX_URL + tripId.toString();
			}
			/*
			 * //Don't really need this if (itemName.equals("kml")) {
			 * Log.debug("Trip KML found, length " + + item.getTextContent().length());
			 * tripKmlUrlString = item.getTextContent(); }
			 */
		}

		try
		{
			tripGpxUrl = new URL(tripGpxUrlString);
			gpsItem.setSourceURL(tripGpxUrl);
		}
		catch (final MalformedURLException e)
		{
			Log.error("Can't create GPX URL: " + tripGpxUrlString, e);
		}

		int tryCount = 0;
		boolean keepTrying = true;
		String gpxString = "";
		while (keepTrying)
		{
			try
			{
				final URLConnection con = CommunicationHelper.getConnection(tripGpxUrl);
				final InputStream is = con.getInputStream();
				gpsItem.readText(is);
				gpxString = gpsItem.getText();
				Log.debug("InputStream for tripGPX is (first ~50 chars): "
						+ gpxString.substring(0, Math.min(50, gpxString.length())));
				keepTrying = false;
			}
			catch (final UnknownHostException e)
			{
				Log.info("Unknown host for GPX for " + tripGpxUrlString + ": " + e.getMessage(), e);

			}
			catch (final Exception e)
			{
				Log.info("Other exception for " + tripGpxUrlString + ": " + e.getMessage(), e);
			}
			tryCount++;
			if (tryCount == 3)
			{
				tripGpxUrl = new URL(tripGpxUrlStringAlternate);
				gpsItem.setSourceURL(tripGpxUrl);
				Log.error("Attempting alternative Gpx, " + tripGpxUrlStringAlternate);
			}
			else
			{
				keepTrying = false;
				Log.error("Giving up getting " + tripGpxUrlString + " and alternative " + tripGpxUrlStringAlternate);
			}
		}
	}

	/**
	 * Convert an Everytrail track to a GPSTraceItem
	 *
	 * @param owner User creating this item
	 * @throws Exception
	 */
	public static void toGPSTraceItem(final User owner, final PeoplesCollectionTrailResponse trail,
			final GPSTraceItem gpsItem) throws Exception
	{
		// tripItem.toString();
		Log.debug(trail.GetProperties().GetTitle());
		final String trailTitle = trail.GetProperties().GetTitle();
		final String trailId = Integer.toString(trail.GetPropertiesId());

		gpsItem.setGeometry(trail.GetGeometry());
		gpsItem.addMetadataEntry("source", PeoplesCollectionService.SERVICE_INFO.getName());

		if (trail != null)
		{
			Log.debug("Trip id is: " + trailId);
			gpsItem.setExternalID("peoplescollection-" + trailId);
			gpsItem.addMetadataEntry("trip", trailId);
		}

		if (trailTitle != null)
		{
			Log.debug("Trail name is: " + trailTitle);
			gpsItem.addMetadataEntryIndexed("trip_name", trailTitle);
			gpsItem.addMetadataEntryIndexed("title", trailTitle);
		}

		final StringBuilder trackGPXBuilder = new StringBuilder();
		trackGPXBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		trackGPXBuilder
				.append("<gpx xmlns=\"http://www.topografix.com/GPX/1/1\" creator=\"Placebooks - http://www.placebooks.org\" version=\"1.1\">");

		final Envelope envelope = new Envelope();
		envelope.expandToInclude(trail.GetGeometry().getEnvelopeInternal());
		final double xMin = envelope.getMinX();
		final double yMin = envelope.getMinY();

		final double xMax = envelope.getMaxX();
		final double yMax = envelope.getMaxY();

		trackGPXBuilder.append("<metadata>");
		trackGPXBuilder.append("<bounds minlat=\"" + yMin + "\" minlon=\"" + xMin + "\" maxlat=\"" + yMax
				+ "\" maxlon=\"" + xMax + "\"/>");
		trackGPXBuilder.append("<name>" + trailTitle + "</name>");
		trackGPXBuilder.append("</metadata>");
		trackGPXBuilder.append("<trk>");
		trackGPXBuilder.append("<name>" + trailTitle + "</name>");
		trackGPXBuilder.append("<trkseg>");
		for (final Coordinate c : trail.GetGeometry().getCoordinates())
		{
			trackGPXBuilder.append("<trkpt lat=\"" + c.y + "\" lon=\"" + c.x + "\">");
			trackGPXBuilder.append("<ele>0</ele>");
			trackGPXBuilder.append("</trkpt>");
		}

		trackGPXBuilder.append("</trkseg>");
		trackGPXBuilder.append("</trk>");
		trackGPXBuilder.append("</gpx>");

		Log.debug(trackGPXBuilder.toString());
		final byte[] bytes = trackGPXBuilder.toString().getBytes("UTF-8");
		gpsItem.readText(new ByteArrayInputStream(bytes));
	}

	/**
	 * Convert an Everytrail Picture to an Image item for the given user
	 *
	 * @param testUser
	 * @param everytrailPicture
	 * @param imageItem
	 * @param tripName
	 */
	public static void toImageItem(final User testUser, final Node everytrailPicture, final ImageItem imageItem,
			final String trip_id, final String tripName)
	{
		URL sourceUrl = null;
		final NamedNodeMap pictureAttributes = everytrailPicture.getAttributes();
		String picture_id = "";
		String itemTitle = "";
		String imageItemTitle = "";
		String itemDescription = "";

		Geometry geom = null;

		if (trip_id != null)
		{
			imageItem.addMetadataEntry("trip", trip_id);
		}
		if (tripName != null)
		{
			imageItem.addMetadataEntryIndexed("trip_name", tripName);
		}

		// First look at node attributes to get the unique id
		for (int attributeIndex = 0; attributeIndex < pictureAttributes.getLength(); attributeIndex++)
		{
			if (pictureAttributes.item(attributeIndex).getNodeName().equals("id"))
			{
				picture_id = pictureAttributes.item(attributeIndex).getNodeValue();
			}
		}
		if (picture_id.equals(""))
		{
			Log.error("Can't get picture id");
		}
		else
		{
			Log.debug("Picture id is: " + picture_id);
		}

		// Then look at the properties in the child nodes to get url, title, description, etc.
		final NodeList pictureProperties = everytrailPicture.getChildNodes();
		for (int propertyIndex = 0; propertyIndex < pictureProperties.getLength(); propertyIndex++)
		{
			final Node item = pictureProperties.item(propertyIndex);
			final String itemName = item.getNodeName();
			// Log.debug("Inspecting property: " + itemName + " which is " + item.getTextContent());
			if (itemName.equals("caption"))
			{
				itemTitle = item.getTextContent();
			}
			if (itemName.equals("fullsize"))
			{
				try
				{
					sourceUrl = new URL(item.getTextContent());
				}
				catch (final MalformedURLException ex)
				{
					Log.error("Can't convert Everytrail Picture URL to a valid URL.");
					Log.debug(ex.getMessage());
				}
			}
			if (itemName.equals("description"))
			{
				itemDescription = item.getTextContent();
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
					final Geometry newGeom = new WKTReader().read("POINT ( " + lat + " " + lon + " )");
					Log.debug("Detected coordinates " + lat.toString() + ", " + lon.toString());
					geom = newGeom;
				}
				catch (final Exception ex)
				{
					Log.error("Couldn't get lat/lon data from Everytrail picture.");
					Log.debug(ex.getMessage());
				}
			}
		}
		if (sourceUrl != null)
		{
			if (!itemTitle.equals(""))
			{
				imageItemTitle = itemTitle;
			}
			else
			{
				imageItemTitle = picture_id;
			}
			try
			{
				final URLConnection conn = CommunicationHelper.getConnection(sourceUrl);
				imageItem.writeDataToDisk(conn.getInputStream());
			}
			catch (final IOException ex)
			{
				Log.error("Can't download Everytrail Picture and convert to BufferedImage URL: "
						+ sourceUrl.toExternalForm());
				Log.debug(ex.getMessage());
			}
			catch (final Throwable e)
			{
				Log.error(e.getMessage(), e);
			}
		}
		imageItem.setOwner(testUser);
		imageItem.setGeometry(geom);
		imageItem.setSourceURL(sourceUrl);
		// = new ImageItem(testUser, geom, sourceUrl, markerImage);
		imageItem.setExternalID("everytrail-" + picture_id);
		imageItem.addMetadataEntryIndexed("title", imageItemTitle);
		imageItem.addMetadataEntryIndexed("description", itemDescription);
		imageItem.addMetadataEntry("source", EverytrailService.SERVICE_INFO.getName());
	}

	/**
	 * Convert an Peoples Collection Picture to an Image item for the given user
	 */
	public static void toImageItem(final User testUser, final PeoplesCollectionItemFeature item, final int trailId,
			final String trailName, final ImageItem imageItem)
	{
		URL sourceUrl = null;
		final int picture_id = item.GetPropertiesId();
		String itemTitle = "";
		String imageItemTitle = "";
		String itemDescription = "";
		Geometry geom = null;
		imageItem.addMetadataEntry("trip", Integer.toString(trailId));

		if (trailName != null)
		{
			imageItem.addMetadataEntryIndexed("trip_name", trailName);
		}

		Log.debug("Picture id is: " + picture_id);

		itemTitle = item.GetProperties().GetTitle();
		itemDescription = item.GetProperties().GetMarkup();
		try
		{
			// sourceUrl = new URL(item.GetProperties().GetMediaURL());
			// Hack to get smaller images - MCP
			sourceUrl = new URL(item.GetProperties().GetExSmallThumbPath().replace("67x37.jpg", "635x353.jpg"));
		}
		catch (final MalformedURLException ex)
		{
			Log.error("Couldn't get URL for peoples collection picture.");
			Log.debug(ex.getMessage());
		}

		try
		{
			final Geometry newGeom = item.GetGeometry();
			Log.debug("Detected coordinates geometry: " + newGeom.toText());
			geom = newGeom;
		}
		catch (final Exception ex)
		{
			Log.error("Couldn't get lat/lon data from peoples collection picture.", ex);
			Log.debug(ex.getMessage());
		}

		if (sourceUrl != null)
		{
			if (!itemTitle.equals(""))
			{
				imageItemTitle = itemTitle;
			}
			else
			{
				imageItemTitle = Integer.toString(picture_id);
			}
			try
			{
				final URLConnection conn = CommunicationHelper.getConnection(sourceUrl);
				imageItem.writeDataToDisk(conn.getInputStream());
			}
			catch (final IOException ex)
			{
				Log.error("Can't download Peoples Collection Picture and convert to BufferedImage URL: "
						+ sourceUrl.toExternalForm());
				Log.debug(ex.getMessage());
			}
			catch (final Throwable e)
			{
				Log.error(e.getMessage(), e);
			}
		}
		imageItem.setOwner(testUser);
		imageItem.setGeometry(geom);
		imageItem.setSourceURL(sourceUrl);
		// = new ImageItem(testUser, geom, sourceUrl, markerImage);
		imageItem.setExternalID("peoplescollection-" + picture_id);
		imageItem.addMetadataEntryIndexed("title", imageItemTitle);
		imageItem.addMetadataEntryIndexed("description", itemDescription);
		imageItem.addMetadataEntry("source", PeoplesCollectionService.SERVICE_INFO.getName());
	}

	public static void toTextItem(final User user, final PeoplesCollectionItemFeature feature, final int trailId,
			final String trailName, final TextItem textItem)
	{
		textItem.setOwner(user);
		textItem.addMetadataEntry("trip", Integer.toString(trailId));

		if (trailName != null)
		{
			textItem.addMetadataEntryIndexed("trip_name", trailName);
		}

		Log.debug("Picture id is: " + feature.GetPropertiesId());

		if (feature.GetProperties().GetTitle() == "")
		{
			textItem.addMetadataEntryIndexed("trip_name", trailName);
		}
		else
		{
			textItem.addMetadataEntryIndexed("trip_name", feature.GetProperties().GetTitle());
		}

		try
		{
			textItem.setGeometry(feature.GetGeometry());
		}
		catch (final IOException e)
		{
			Log.error("Can't get geometry for Peoples Collection item: " + feature.GetPropertiesId(), e);
		}

		textItem.setExternalID("peoplescollection-" + feature.GetPropertiesId());
		textItem.addMetadataEntryIndexed("title", feature.GetProperties().GetTitle());
		textItem.addMetadataEntryIndexed("description", feature.GetProperties().GetMarkup());
		textItem.addMetadataEntry("source", PeoplesCollectionService.SERVICE_INFO.getName());
	}

	/**
	 * Convert an Peoples Collection Video to an Video item for the given user
	 */
	public static void toVideoItem(final User testUser, final PeoplesCollectionItemFeature item, final int trailId,
			final String trailName, final VideoItem videoItem)
	{
		URL sourceUrl = null;
		final int video_id = item.GetPropertiesId();
		String itemTitle = "";
		String videoItemTitle = "";
		String itemDescription = "";
		Geometry geom = null;
		videoItem.addMetadataEntry("trip", Integer.toString(trailId));

		if (trailName != null)
		{
			videoItem.addMetadataEntryIndexed("trip_name", trailName);
		}

		Log.debug("Video id is: " + video_id);

		itemTitle = item.GetProperties().GetTitle();
		itemDescription = item.GetProperties().GetMarkup();
		try
		{
			sourceUrl = new URL(item.GetProperties().GetMediaURL());
		}
		catch (final MalformedURLException ex)
		{
			Log.error("Couldn't get URL for peoples collection video.");
			Log.debug(ex.getMessage());
		}

		try
		{
			final Geometry newGeom = item.GetGeometry();
			Log.debug("Detected coordinates geometry: " + newGeom.toText());
			geom = newGeom;
		}
		catch (final Exception ex)
		{
			Log.error("Couldn't get lat/lon data from peoples collection video.", ex);
			Log.debug(ex.getMessage());
		}

		if (sourceUrl != null)
		{
			if (!itemTitle.equals(""))
			{
				videoItemTitle = itemTitle;
			}
			else
			{
				videoItemTitle = Integer.toString(video_id);
			}
			try
			{
				final URLConnection conn = CommunicationHelper.getConnection(sourceUrl);
				videoItem.writeDataToDisk(sourceUrl.getFile().replace("?" + sourceUrl.getQuery(), ""),
						conn.getInputStream(), sourceUrl.toString());
				if (PropertiesSingleton.get(CommunicationHelper.class.getClassLoader())
						.getProperty(PropertiesSingleton.VIDEOITEM_FFMPEG_TRANSCODE, "false").equals("true"))
				{
					transcodeVideo(videoItem.getPath());
				}

			}
			catch (final IOException ex)
			{
				Log.error("Can't download Peoples Collection video: " + sourceUrl.toExternalForm());
				Log.debug(ex.getMessage());
			}
			catch (final Throwable e)
			{
				Log.error(e.getMessage(), e);
			}
		}
		videoItem.setOwner(testUser);
		videoItem.setGeometry(geom);
		videoItem.setSourceURL(sourceUrl);
		// = new ImageItem(testUser, geom, sourceUrl, markerImage);
		videoItem.setExternalID("peoplescollection-" + video_id);
		videoItem.addMetadataEntryIndexed("title", videoItemTitle);
		videoItem.addMetadataEntryIndexed("description", itemDescription);
		videoItem.addMetadataEntry("source", PeoplesCollectionService.SERVICE_INFO.getName());
	}

	/**
	 * Use xuggler to transcode the video to a Chrome and Android compatible format and save as the
	 * same name with x on the end.
	 */
	protected static void transcodeVideo(final String inputfilename)
	{
		TranscodeHelper.transcodeVideoForChrome(inputfilename);
		TranscodeHelper.transcodeVideoForMobile(inputfilename);
	}
}