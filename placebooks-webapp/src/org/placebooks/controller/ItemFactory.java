/**
 * 
 */
package org.placebooks.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;
import org.placebooks.model.AudioItem;
import org.placebooks.model.GPSTraceItem;
import org.placebooks.model.ImageItem;
import org.placebooks.model.PlaceBookItem;
import org.placebooks.model.TextItem;
import org.placebooks.model.User;
import org.placebooks.model.VideoItem;
import org.placebooks.services.EverytrailService;
import org.placebooks.services.PeoplesCollectionService;
import org.placebooks.services.model.PeoplesCollectionItemFeature;
import org.placebooks.services.model.PeoplesCollectionTrailResponse;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.gdata.data.DateTime;
import com.google.gdata.data.geo.impl.GeoRssWhere;
import com.google.gdata.data.youtube.VideoEntry;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

/**
 * @author pszmp This class provides converters to Everytrail Items from other data, such as
 *         Everytrail images, gps logs, etc
 */
public class ItemFactory
{
	private static final Logger log = Logger.getLogger(ItemFactory.class.getName());

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
	 * 
	 * @param externalId
	 * @return IUpdateableExternal item or null
	 */
	public static PlaceBookItem getExistingItem(final PlaceBookItem itemToSave, final EntityManager em)
	{
		PlaceBookItem item = null;
		log.debug("Querying externalID " + itemToSave.getExternalID());
		final TypedQuery<PlaceBookItem> q = em
				.createQuery(	"SELECT placebookitem FROM PlaceBookItem as placebookitem where (placebookitem.externalID = ?1) AND (placebookitem.placebook is null)",
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
				log.warn("Removing duplicate items for " + itemToSave.getExternalID());
				for (final PlaceBookItem o : l)
				{
					log.debug("Removing: " + o.getKey());
					em.remove(o);
				}
				em.flush();
			}

		}
		catch (final NoResultException ex)
		{
			log.error(ex.toString());
			item = null;
		}

		return item;
	}

	public static PlaceBookItem GetExistingItem(final String externalID, final EntityManager em)
	{
		PlaceBookItem item = null;
		log.debug("Querying externalID " + externalID);
		final TypedQuery<PlaceBookItem> q = em
				.createQuery(	"SELECT placebookitem FROM PlaceBookItem as placebookitem where (placebookitem.externalID = ?1) AND (placebookitem.placebook is null)",
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
				log.warn("Removing duplicate items for " + externalID);
				for (final PlaceBookItem o : l)
				{
					log.debug("Removing: " + o.getKey());
					em.remove(o);
				}
				em.flush();
			}

		}
		catch (final NoResultException ex)
		{
			log.error(ex.toString());
			item = null;
		}

		return item;
	}

	/**
	 * Convert an Peoples Collection Audio to an Audio item for the given user
	 * 
	 * @param testUser
	 * @param everytrailPicture
	 * @param audioItem
	 * @param tripName
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

		log.debug("audio id is: " + audio_id);

		itemTitle = item.GetProperties().GetTitle();
		itemDescription = item.GetProperties().GetMarkup();
		try
		{
			sourceUrl = new URL(item.GetProperties().GetMediaURL().replace(" ", "%20"));
		}
		catch (final MalformedURLException ex)
		{
			log.error("Couldn't get URL for peoples collection audio.");
			log.debug(ex.getMessage());
		}

		try
		{
			final Geometry newGeom = item.GetGeometry();
			log.debug("Detected coordinates geometry: " + newGeom.toText());
			geom = newGeom;
		}
		catch (final Exception ex)
		{
			log.error("Couldn't get lat/lon data from peoples collection audio.", ex);
			log.debug(ex.getMessage());
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
				audioItem.writeDataToDisk(	sourceUrl.getFile().replace("?" + sourceUrl.getQuery(), ""),
											conn.getInputStream(), sourceUrl.toString());
			}
			catch (final IOException ex)
			{
				log.error("Can't download Peoples Collection audio: " + sourceUrl.toExternalForm());
				log.debug(ex.getMessage());
			}
			catch (final Throwable e)
			{
				log.error(e.getMessage(), e);
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
	 * @param owner
	 *            User creating this item
	 * @param trackItem
	 *            the track item as a DOM node from EverytrailTracks response
	 * @param tripId
	 * @param tripName
	 * @throws Exception
	 */
	public static void toGPSTraceItem(final User owner, final Node tripItem, final GPSTraceItem gpsItem,
			final String tripId, final String tripName) throws Exception
	{
		// tripItem.toString();
		// log.debug(tripItem.getTextContent());

		gpsItem.setGeometry(null);
		gpsItem.addMetadataEntry("source", EverytrailService.SERVICE_INFO.getName());

		if (tripId != null)
		{
			log.debug("Trip id is: " + tripId);
			gpsItem.setExternalID("everytrail-" + tripId);
			gpsItem.addMetadataEntry("trip", tripId);
		}

		if (tripName != null)
		{
			log.debug("Trip name is: " + tripName);
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
			// log.debug("Inspecting property: " + itemName + " which is " + item.getTextContent());
			if (itemName.equals("gpx"))
			{
				log.debug("Trip GPX URL found, length: " + item.getTextContent().length());
				tripGpxUrlString = item.getTextContent();
				tripGpxUrlStringAlternate = ALTERNATE_EVERYTRAIL_GPX_URL + tripId.toString();
			}
			/*
			 * //Don't really need this if (itemName.equals("kml")) {
			 * log.debug("Trip KML found, length " + + item.getTextContent().length());
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
			log.error("Can't create GPX URL: " + tripGpxUrlString, e);
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
				gpsItem.readTrace(is);
				gpxString = gpsItem.getTrace();
				log.debug("InputStream for tripGPX is (first ~50 chars): "
						+ gpxString.substring(0, Math.min(50, gpxString.length())));
				keepTrying = false;
			}
			catch (final UnknownHostException e)
			{
				log.info("Unknown host for GPX for " + tripGpxUrlString + ": " + e.getMessage(), e);

			}
			catch (final Exception e)
			{
				log.info("Other exception for " + tripGpxUrlString + ": " + e.getMessage(), e);
			}
			tryCount++;
			if (tryCount == 3)
			{
				tripGpxUrl = new URL(tripGpxUrlStringAlternate);
				gpsItem.setSourceURL(tripGpxUrl);
				log.error("Attempting alternative Gpx, " + tripGpxUrlStringAlternate);
			}
			else
			{
				keepTrying = false;
				log.error("Giving up getting " + tripGpxUrlString + " and alternative " + tripGpxUrlStringAlternate);
			}
		}
	}

	/**
	 * Convert an Everytrail track to a GPSTraceItem
	 * 
	 * @param owner
	 *            User creating this item
	 * @param trackItem
	 *            the track item as a DOM node from EverytrailTracks response
	 * @param tripId
	 * @param tripName
	 * @throws Exception
	 */
	public static void toGPSTraceItem(final User owner, final PeoplesCollectionTrailResponse trail,
			final GPSTraceItem gpsItem) throws Exception
	{
		// tripItem.toString();
		log.debug(trail.GetProperties().GetTitle());
		final String trailTitle = trail.GetProperties().GetTitle();
		final String trailId = Integer.toString(trail.GetPropertiesId());

		gpsItem.setGeometry(trail.GetGeometry());
		gpsItem.addMetadataEntry("source", PeoplesCollectionService.SERVICE_INFO.getName());

		if (trail != null)
		{
			log.debug("Trip id is: " + trailId);
			gpsItem.setExternalID("peoplescollection-" + trailId);
			gpsItem.addMetadataEntry("trip", trailId);
		}

		if (trailTitle != null)
		{
			log.debug("Trail name is: " + trailTitle);
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

		log.debug(trackGPXBuilder.toString());
		final byte[] bytes = trackGPXBuilder.toString().getBytes("UTF-8");
		gpsItem.readTrace(new ByteArrayInputStream(bytes));
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
			log.error("Can't get picture id");
		}
		else
		{
			log.debug("Picture id is: " + picture_id);
		}

		// Then look at the properties in the child nodes to get url, title, description, etc.
		final NodeList pictureProperties = everytrailPicture.getChildNodes();
		for (int propertyIndex = 0; propertyIndex < pictureProperties.getLength(); propertyIndex++)
		{
			final Node item = pictureProperties.item(propertyIndex);
			final String itemName = item.getNodeName();
			// log.debug("Inspecting property: " + itemName + " which is " + item.getTextContent());
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
					log.error("Can't convert Everytrail Picture URL to a valid URL.");
					log.debug(ex.getMessage());
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
					log.debug("Detected coordinates " + lat.toString() + ", " + lon.toString());
					geom = newGeom;
				}
				catch (final Exception ex)
				{
					log.error("Couldn't get lat/lon data from Everytrail picture.");
					log.debug(ex.getMessage());
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
				imageItem.writeDataToDisk(picture_id + ".jpg", conn.getInputStream());
			}
			catch (final IOException ex)
			{
				log.error("Can't download Everytrail Picture and convert to BufferedImage URL: "
						+ sourceUrl.toExternalForm());
				log.debug(ex.getMessage());
			}
			catch (final Throwable e)
			{
				log.error(e.getMessage(), e);
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
	 * 
	 * @param testUser
	 * @param everytrailPicture
	 * @param imageItem
	 * @param tripName
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

		log.debug("Picture id is: " + picture_id);

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
			log.error("Couldn't get URL for peoples collection picture.");
			log.debug(ex.getMessage());
		}

		try
		{
			final Geometry newGeom = item.GetGeometry();
			log.debug("Detected coordinates geometry: " + newGeom.toText());
			geom = newGeom;
		}
		catch (final Exception ex)
		{
			log.error("Couldn't get lat/lon data from peoples collection picture.", ex);
			log.debug(ex.getMessage());
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
				imageItem.writeDataToDisk(picture_id + ".jpg", conn.getInputStream());
			}
			catch (final IOException ex)
			{
				log.error("Can't download Peoples Collection Picture and convert to BufferedImage URL: "
						+ sourceUrl.toExternalForm());
				log.debug(ex.getMessage());
			}
			catch (final Throwable e)
			{
				log.error(e.getMessage(), e);
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

		log.debug("Picture id is: " + feature.GetPropertiesId());

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
			log.error("Can't get geometry for Peoples Collection item: " + feature.GetPropertiesId(), e);
		}

		textItem.setExternalID("peoplescollection-" + feature.GetPropertiesId());
		textItem.addMetadataEntryIndexed("title", feature.GetProperties().GetTitle());
		textItem.addMetadataEntryIndexed("description", feature.GetProperties().GetMarkup());
		textItem.addMetadataEntry("source", PeoplesCollectionService.SERVICE_INFO.getName());
	}

	/**
	 * Convert an Peoples Collection Video to an Video item for the given user
	 * 
	 * @param testUser
	 * @param everytrailPicture
	 * @param videoItem
	 * @param tripName
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

		log.debug("Video id is: " + video_id);

		itemTitle = item.GetProperties().GetTitle();
		itemDescription = item.GetProperties().GetMarkup();
		try
		{
			sourceUrl = new URL(item.GetProperties().GetMediaURL());
		}
		catch (final MalformedURLException ex)
		{
			log.error("Couldn't get URL for peoples collection video.");
			log.debug(ex.getMessage());
		}

		try
		{
			final Geometry newGeom = item.GetGeometry();
			log.debug("Detected coordinates geometry: " + newGeom.toText());
			geom = newGeom;
		}
		catch (final Exception ex)
		{
			log.error("Couldn't get lat/lon data from peoples collection video.", ex);
			log.debug(ex.getMessage());
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
				videoItem.writeDataToDisk(	sourceUrl.getFile().replace("?" + sourceUrl.getQuery(), ""),
											conn.getInputStream(), sourceUrl.toString());
				if (PropertiesSingleton.get(CommunicationHelper.class.getClassLoader())
						.getProperty(PropertiesSingleton.VIDEOITEM_FFMPEG_TRANSCODE, "false").equals("true"))
				{
					transcodeVideo(videoItem.getPath());
				}

			}
			catch (final IOException ex)
			{
				log.error("Can't download Peoples Collection video: " + sourceUrl.toExternalForm());
				log.debug(ex.getMessage());
			}
			catch (final Throwable e)
			{
				log.error(e.getMessage(), e);
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

	public static VideoItem toVideoItem(final User owner, final VideoEntry youtubeVideo)
	{
		Geometry geom = null;
		URL sourceUrl = null;
		File videoFile = null;

		try
		{
			sourceUrl = new URL(youtubeVideo.getHtmlLink().getHref());
			final URLConnection conn = CommunicationHelper.getConnection(sourceUrl);

			// Get the response
			final String filename = owner.getKey() + DateTime.now().getValue() + ".video";
			log.info("Writing video to : " + filename);
			videoFile = new File(filename);
			InputStream inputStream;

			inputStream = conn.getInputStream();
			final OutputStream out = new FileOutputStream(videoFile);
			final byte buf[] = new byte[1024];
			int len;
			while ((len = inputStream.read(buf)) > 0)
			{
				out.write(buf, 0, len);
			}
			out.close();
			inputStream.close();
			log.debug("File is being created...");
		}
		catch (final MalformedURLException e)
		{
			log.error("Can't convert Youtube URL item to a valid url.");
			log.debug(e.getMessage());
		}
		catch (final IOException e)
		{
			log.error("Can't convert video to file...");
			log.debug(e.getMessage());
		}

		try
		{
			final GeoRssWhere where = youtubeVideo.getGeoCoordinates();
			log.debug("Video location: " + where.getLongitude() + ", " + where.getLatitude());
			try
			{
				geom = new WKTReader().read("POINT ( " + where.getLatitude() + " " + where.getLongitude() + " )");
			}
			catch (final ParseException e)
			{
				log.error(e.getMessage());
				e.printStackTrace();
			}
		}
		catch (final NullPointerException e)
		{
			log.info("Can't get location of video...");
		}
		return new VideoItem(owner, geom, sourceUrl, videoFile.getAbsolutePath());
	}

	/**
	 * Use xuggler to transcode the video to a Chrome and Android compatible format and save as the
	 * same name with x on the end.
	 * 
	 * @param filename
	 *            Input file will be transcoded to inputfilex.ogg
	 */
	protected static void transcodeVideo(final String inputfilename)
	{
		TranscodeHelper.transcodeVideoForChrome(inputfilename);
		TranscodeHelper.transcodeVideoForMobile(inputfilename);
	}

}
