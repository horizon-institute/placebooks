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

import org.apache.log4j.Logger;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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

/**
 * @author pszmp
 * 
 */
public class ItemFactory
{
	private static final Logger log = Logger.getLogger(ItemFactory.class.getName());

	public static void toImageItem(final User testUser, final Node everytrailPicture, ImageItem imageItem)
	{
		//ImageItem imageItem = null;
		URL sourceUrl = null;
		final NamedNodeMap pictureAttributes = everytrailPicture.getAttributes();
		String picture_id = "";
		Geometry geom = null;

		for (int attributeIndex = 0; attributeIndex < pictureAttributes.getLength(); attributeIndex++)
		{
			if (pictureAttributes.item(attributeIndex).getNodeName().equals("id"))
			{
				picture_id = pictureAttributes.item(attributeIndex).getNodeValue();
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
					sourceUrl = new URL(item.getTextContent());
					final URLConnection conn = CommunicationHelper.getConnection(sourceUrl);

					// Get the response
					//final BufferedImage bi = ImageIO.read(conn.getInputStream());
					//image = bi;
					//log.debug("image width: " + bi.getWidth() + "px Height: " + bi.getHeight() + "px");

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
		imageItem.setOwner(testUser);
		imageItem.setGeometry(geom);
		imageItem.setSourceURL(sourceUrl);
			//= new ImageItem(testUser, geom, sourceUrl, image);
		imageItem.addMetadataEntry("title", picture_id);
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
}
