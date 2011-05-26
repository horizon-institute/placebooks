package placebooks.controller;

import java.io.File;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import java.io.IOException;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

import java.net.URLConnection;
import java.net.URL;

import java.awt.Image;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import org.apache.log4j.Logger;

import javax.imageio.ImageIO;

import uk.me.jstott.jcoord.OSRef;
import uk.me.jstott.jcoord.LatLng;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.geom.Coordinate;

import placebooks.model.PlaceBook;
import placebooks.model.PlaceBookItem;

public final class TileHelper
{

	private static final Logger log = 
		Logger.getLogger(TileHelper.class.getName());


	private static final String buildOpenSpaceQuery(final int layer, 
													final int blockSize,
													final OSRef ref,
													final String format)
	{
		final String baseURL = 
			PropertiesSingleton
				.get(TileHelper.class.getClassLoader())
				.getProperty(PropertiesSingleton.IDEN_OPENSPACE_BASEURL, "");

		final String host = 
			PropertiesSingleton
				.get(TileHelper.class.getClassLoader())
				.getProperty(PropertiesSingleton.IDEN_OPENSPACE_HOST, "");

		final String apiKey = 
			PropertiesSingleton
				.get(TileHelper.class.getClassLoader())
				.getProperty(PropertiesSingleton.IDEN_OPENSPACE_APIKEY, "");

		final int x1 = (int)ref.getEasting();
		final int y1 = (int)ref.getNorthing();
		final int x2 = x1 + blockSize;
		final int y2 = y1 + blockSize;

		final String url = baseURL
					 + "?FORMAT=" + format
					 + "&KEY=" + apiKey
					 + "&URL=" + host
					 + "&SERVICE=WMS"
					 + "&VERSION=1.1.1"
					 + "&REQUEST=GetMap"
					 + "&EXCEPTIONS=application%5Cvnd.ogc.se_inimage"
					 + "&LAYERS=" + Integer.toString(layer)
					 + "&SRS=EPSG%3A27700"
					 + "&BBOX=" 
					 	+ Integer.toString(x1) + "," + Integer.toString(y1)
						+ "," + Integer.toString(x2) + "," 
						+ Integer.toString(y2)
					 + "&WIDTH=200&HEIGHT=200";

		return url;
	}

	public static final File getMap(final PlaceBook p)
	{
		return null;
	}

	public static final File getMap(final PlaceBookItem pi)
	{
		return null;
	}

	public static final File getMap(final Geometry g) 
		throws IOException, IllegalArgumentException
	{
		// 0 = TL, 1 = BR
		Coordinate[] bbox_ = new Coordinate[2];
		bbox_[0] = new Coordinate(Double.MAX_VALUE, Double.MAX_VALUE);
		bbox_[1] = new Coordinate(Double.MIN_VALUE, Double.MIN_VALUE);

		final Coordinate[] coords = g.getBoundary().getCoordinates();
		for (int i = 0; i < coords.length; ++i)
		{
			bbox_[0].x = Math.min(coords[i].x, bbox_[0].x);
			bbox_[0].y = Math.min(coords[i].y, bbox_[0].y);
			bbox_[1].x = Math.max(coords[i].x, bbox_[1].x);
			bbox_[1].y = Math.max(coords[i].y, bbox_[1].y);
		}
		
		log.info("bbox_[] = " + Arrays.toString(bbox_));
		
		OSRef[] bbox = new OSRef[2];
		for (int i = 0; i < bbox_.length; ++i)
		{
			bbox[i] = new OSRef(new LatLng(bbox_[i].x, bbox_[i].y));

			log.info("OSRef = " + bbox[i].getEasting() + "," 
					 + bbox[i].getNorthing());
		}

		final double eDelta = Math.abs(bbox[1].getEasting() 
									   - bbox[0].getEasting());
		final double nDelta = Math.abs(bbox[1].getEasting() 
									   - bbox[0].getEasting());

		final int inc = 2000;
		final int eBlocks = (int)Math.ceil(eDelta / (double)inc);
		final int nBlocks = (int)Math.ceil(nDelta / (double)inc);

		final BufferedImage buf = 
			new BufferedImage(200 * eBlocks, 200 * nBlocks, 
							  BufferedImage.TYPE_INT_RGB);
		final Graphics graphics = buf.createGraphics();

		int n = 0, m = 0;
		for (int i = (int)bbox[0].getEasting(); i < (int)bbox[1].getEasting(); 
			 i += inc)
		{
			for (int j = (int)bbox[0].getNorthing(); 
				 j < (int)bbox[1].getNorthing(); j += inc)
			{
				// %5C = \
				final String url = 
					buildOpenSpaceQuery(5, inc, new OSRef(i, j), "image%5Cpng");
				log.info("URL = " + url);
				try
				{
					
					final URLConnection conn = 
						CommunicationHelper.getConnection(new URL(url));

					Image tile = null;
					tile = ImageIO.read(
								new BufferedInputStream(conn.getInputStream())
						   );

					graphics.drawImage(tile, n, m, null);
				}
				catch (final Throwable e)
				{
					log.error(e.toString());
				}

				m += 200;
			}

			n += 200;
		}

		graphics.dispose();

		File mapFile = null;
		try
		{
			final String path = 
				PropertiesSingleton
					.get(TileHelper.class.getClassLoader())
					.getProperty(PropertiesSingleton.IDEN_MEDIA, "");

			if (!new File(path).exists() && !new File(path).mkdirs()) 
			{
				throw new IOException("Failed to write file"); 
			}
	
			mapFile = new File(path + "/map.png");
			ImageIO.write(buf, "png", mapFile);
			log.info("Wrote map file " + mapFile.getAbsolutePath());
		
		}
		catch (final Throwable e)
		{
			log.error(e.toString());
			throw new IOException("Error creating map");
		}

		return mapFile;
		
	}
}
