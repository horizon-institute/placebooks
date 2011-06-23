/**
 * 
 */
package placebooks.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import placebooks.model.GPSTraceItem;
import placebooks.model.ImageItem;
import placebooks.model.User;
import placebooks.model.VideoItem;

import com.google.gdata.data.DateTime;
import com.google.gdata.data.geo.impl.GeoRssWhere;
import com.google.gdata.data.youtube.VideoEntry;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

/**
 * @author pszmp
 * This class provides converters to Everytrail Items from other data, such as Everytrail images, gps logs, etc
 */
public class ItemFactory
{
	private static final Logger log = Logger.getLogger(ItemFactory.class.getName());

	/**
	 * Convert an Everytrail Picture to an Image item for the given user
	 * @param testUser
	 * @param everytrailPicture
	 * @param imageItem
	 */
	public static void toImageItem(final User testUser, final Node everytrailPicture, ImageItem imageItem, String trip_id)
	{
		URL sourceUrl = null;
		final NamedNodeMap pictureAttributes = everytrailPicture.getAttributes();
		String picture_id = "";
		String itemTitle =  "";
		String imageItemTitle = "";
		String itemDescription = "";

		Geometry geom = null;

		if(trip_id!=null)
		{
			imageItem.addMetadataEntry("trip", trip_id)	;	
		}
		
		
		//First look at node attributes to get the unique id
		for (int attributeIndex = 0; attributeIndex < pictureAttributes.getLength(); attributeIndex++)
		{
			if (pictureAttributes.item(attributeIndex).getNodeName().equals("id"))
			{
				picture_id = pictureAttributes.item(attributeIndex).getNodeValue();
			}
		}
		if(picture_id.equals(""))
		{
			log.error("Can't get picture id");
		}
		else
		{
			log.debug("Picture id is: " + picture_id);
		}

		//Then look at the properties in the child nodes to get url, title, description, etc.
		final NodeList pictureProperties = everytrailPicture.getChildNodes();
		for (int propertyIndex = 0; propertyIndex < pictureProperties.getLength(); propertyIndex++)
		{
			final Node item = pictureProperties.item(propertyIndex);
			final String itemName = item.getNodeName();
			//log.debug("Inspecting property: " + itemName + " which is " + item.getTextContent());
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
					final GeometryFactory gf = new GeometryFactory();
					final Geometry newGeom = gf.toGeometry(new Envelope(new Coordinate(Double.parseDouble(lon), Double
							.parseDouble(lat))));
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
		if(sourceUrl != null)
		{
			if(!itemTitle.equals(""))
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
				imageItem.writeNewFileToDisk(picture_id + ".jpg", conn.getInputStream());
			}
			catch (final IOException ex)
			{
				log.error("Can't download Everytrail Picture and convert to BufferedImage URL: " + sourceUrl.toExternalForm());
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
		//= new ImageItem(testUser, geom, sourceUrl, image);
		imageItem.setExternalID("everytrail-" + picture_id);
		imageItem.addMetadataEntry("title", imageItemTitle);
		imageItem.addMetadataEntry("description", itemDescription);
		imageItem.addMetadataEntry("source", EverytrailHelper.SERVICE_NAME);
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
			final GeometryFactory gf = new GeometryFactory();
			final GeoRssWhere where = youtubeVideo.getGeoCoordinates();
			log.debug("Video location: " + where.getLongitude() + ", " + where.getLatitude());
			geom = gf.toGeometry(new Envelope(new Coordinate(where.getLongitude(), where.getLatitude())));
		}
		catch (final NullPointerException e)
		{
			log.info("Can't get location of video...");
		}
		return new VideoItem(owner, geom, sourceUrl, videoFile.getAbsolutePath());
	}


	/**
	 * Convert an Everytrail track to a GPSTraceItem
	 * @param owner User creating this item
	 * @param trackItem the track item as a DOM node from EverytrailTracks response
	 */
	public static void toGPSTraceItem(final User owner, final Node trackItem, GPSTraceItem gpsItem, String trip_id)
	{
		trackItem.toString();
		log.debug(trackItem.getTextContent());

		String track_id = "";
		String track_name = "";
		Geometry trackGeom = null;
		
		if(trip_id!=null)
		{
			gpsItem.addMetadataEntry("trip", trip_id)	;	
		}
		
		//First look at node attributes to get the unique id
		final NamedNodeMap trackAttributes = trackItem.getAttributes();
		for (int attributeIndex = 0; attributeIndex < trackAttributes.getLength(); attributeIndex++)
		{
			if (trackAttributes.item(attributeIndex).getNodeName().equals("id"))
			{
				track_id = trackAttributes.item(attributeIndex).getNodeValue();
			}
		}
		if(track_id.equals(""))
		{
			log.error("Can't get track id");
		}
		else
		{
			log.debug("Track id is: " + track_id);
		}

		//Then look at the properties in the child nodes to get url, title, description, etc.
		final NodeList trackProperties = trackItem.getChildNodes();
		for (int propertyIndex = 0; propertyIndex < trackProperties.getLength(); propertyIndex++)
		{
			final Node item = trackProperties.item(propertyIndex);
			final String itemName = item.getNodeName();
			//log.debug("Inspecting property: " + itemName + " which is " + item.getTextContent());
			if (itemName.equals("name"))
			{
				track_name = item.getTextContent();
				log.debug("Track name is: " + track_name);
			}
			if (itemName.equals("trk"))
			{
				//Convert this into geometry
				final NodeList trkProperties = item.getChildNodes();
				for (int trkPropertyIndex = 0; trkPropertyIndex < trkProperties.getLength(); trkPropertyIndex++)
				{
					final Node trkItem = trkProperties.item(trkPropertyIndex);
					final String trkItemName = trkItem.getNodeName();
					//log.debug("Inspecting trk element property: " + trkItemName);

					if (trkItemName.equals("name"))
					{
						log.debug("Processing track name " + trkItem.getTextContent());
					}
					if (trkItemName.equals("trkseg"))
					{
						//log.debug("Processing track segment " + trkItem.getTextContent());
						ArrayList<Coordinate> points = new ArrayList<Coordinate>(); 
						final NodeList trkSegProperties = trkItem.getChildNodes();
						for (int trkSegPropertyIndex = 0; trkSegPropertyIndex < trkSegProperties.getLength(); trkSegPropertyIndex++)
						{
							final Node trkSegItem = trkSegProperties.item(trkSegPropertyIndex);
							final String trkSegItemName = trkSegItem.getNodeName();

							if (trkSegItemName.equals("trkpt"))
							{
								String lat = null;
								String lon = null;
								final NamedNodeMap locationAttributes = trkSegItem.getAttributes();
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
								Coordinate coordinateToAdd = new Coordinate(Double.parseDouble(lon), Double.parseDouble(lat));
								points.add(coordinateToAdd);
							}
						}
						try
						{
							final GeometryFactory gf = new GeometryFactory();
							Coordinate coordinates[] = new Coordinate[points.size()];
							points.toArray(coordinates);
							LineString lineString = gf.createLineString(coordinates);
							if(trackGeom==null)
							{
								trackGeom = gf.createGeometry(lineString);
							}
							else
							{
								trackGeom = trackGeom.union(lineString);
							}
						}
						catch (final Exception ex)
						{
							log.error("Couldn't get lat/lon data from Everytrail track.");
							log.debug(ex.getClass());
							log.debug(ex.getMessage());
						}
					}
				}
			}
		}

		gpsItem.setGeometry(trackGeom);
		gpsItem.setExternalID("everytrail-" + track_id);
		gpsItem.addMetadataEntry("source", EverytrailHelper.SERVICE_NAME);

	}
}
