package placebooks.controller;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import placebooks.model.PlaceBook;
import placebooks.model.PlaceBookItem;
import uk.me.jstott.jcoord.LatLng;
import uk.me.jstott.jcoord.OSRef;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

public final class TileHelper
{

	private static final Logger log = 
		Logger.getLogger(TileHelper.class.getName());


	private static final String buildOpenSpaceQuery(final int layer, 
													final int blockSizeX,
													final int blockSizeY,
													final int width,
													final int height,
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
		final int x2 = x1 + blockSizeX;
		final int y2 = y1 + blockSizeY;

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
					 + "&WIDTH=" + Integer.toString(width) + "&HEIGHT=" 
					 + Integer.toString(height);

		return url;
	}

	// Geometry *must* be a boundary, i.e., four points
	public static final File getMap(final Geometry g) 
		throws IOException, IllegalArgumentException
	{
		log.info("getMap() geometry = " + g);
		int layer = 5;
		int incX = 1000;
		int incY = 1000;
		int pixelX = 200;
		int pixelY = 200;
		String fmt = "png";

		try
		{
			layer = Integer.parseInt(
				PropertiesSingleton
					.get(TileHelper.class.getClassLoader())
					.getProperty(PropertiesSingleton.IDEN_TILER_LAYER, "5")
			);
			incX = Integer.parseInt(
				PropertiesSingleton
					.get(TileHelper.class.getClassLoader())
					.getProperty(PropertiesSingleton.IDEN_TILER_EASTING, 
								 "1000")
			);
			incY = Integer.parseInt(
				PropertiesSingleton
					.get(TileHelper.class.getClassLoader())
					.getProperty(PropertiesSingleton.IDEN_TILER_NORTHING, 
								 "1000")
			);
			pixelX = Integer.parseInt(
				PropertiesSingleton
					.get(TileHelper.class.getClassLoader())
					.getProperty(PropertiesSingleton.IDEN_TILER_PIXEL_X, "200")
			);
			pixelY = Integer.parseInt(
				PropertiesSingleton
					.get(TileHelper.class.getClassLoader())
					.getProperty(PropertiesSingleton.IDEN_TILER_PIXEL_Y, "200")
			);
			fmt = PropertiesSingleton
					.get(TileHelper.class.getClassLoader())
					.getProperty(PropertiesSingleton.IDEN_TILER_FMT, "png")
					.toLowerCase().trim();

		}
		catch (final Throwable e)
		{
			log.error(e.toString());
		}


		// 0 = TL, 1 = BR
		Coordinate[] bbox_ = new Coordinate[2];
		bbox_[0] = new Coordinate(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
		bbox_[1] = new Coordinate(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);

		final Coordinate[] coords = g.getCoordinates();
		for (int i = 0; i < coords.length; ++i)
		{
			bbox_[0].x = Math.min(coords[i].x, bbox_[0].x);
			bbox_[0].y = Math.min(coords[i].y, bbox_[0].y);
			bbox_[1].x = Math.max(coords[i].x, bbox_[1].x);
			bbox_[1].y = Math.max(coords[i].y, bbox_[1].y);
		}
		
		OSRef[] bbox = new OSRef[2];
		for (int i = 0; i < bbox_.length; ++i)
		{
			bbox[i] = new OSRef(new LatLng(bbox_[i].x, bbox_[i].y));

			log.info("OSRef = " + bbox[i].getEasting() + "," 
					 + bbox[i].getNorthing());
		}

		int x = (int)Math.floor((bbox[0].getEasting() / incX)) * incX,
			y = (int)Math.floor((bbox[0].getNorthing() / incY)) * incY;
		bbox[0] = new OSRef(x, y);
		x = (int)Math.ceil((bbox[1].getEasting() / incX)) * incX;
		y = (int)Math.ceil((bbox[1].getNorthing() / incY)) * incY;
		bbox[1] = new OSRef(x, y);
		

		final int eBlocks = (int)Math.ceil(
									(Math.abs(bbox[1].getEasting() 
									 - bbox[0].getEasting())
								 	) / incX);
		final int nBlocks = (int)Math.ceil(
									(Math.abs(bbox[1].getNorthing() 
									   - bbox[0].getNorthing())
									) / incY);
		log.info("eBlocks = " + eBlocks + " nBlocks = " + nBlocks);

		final BufferedImage buf = 
			new BufferedImage(pixelX * eBlocks, pixelY * nBlocks, 
							  BufferedImage.TYPE_INT_RGB);
		final Graphics graphics = buf.createGraphics();

		int n = 0;
		for (int i = (int)bbox[0].getEasting(); i <= (int)bbox[1].getEasting(); 
			 i += incX)
		{
			int m = 0;
			for (int j = (int)bbox[1].getNorthing(); 
				 j >= (int)bbox[0].getNorthing(); j -= incY)
			{
				log.info("i = " + i + " j = " + j);
				// %5C = \
				final String url = 
					buildOpenSpaceQuery(layer, incX, incY, pixelX, pixelY, 
										new OSRef(i, j), "image%5C" + fmt
					);
				try
				{
					
					final URLConnection conn = 
						CommunicationHelper.getConnection(new URL(url));

					Image tile = null;
					tile = ImageIO.read(
								new BufferedInputStream(conn.getInputStream())
						   );

					graphics.drawImage(tile, n, m, null);
					log.info("Drawing tile at " + n + ", " + m);
				}
				catch (final Throwable e)
				{
					log.error(e.toString());
				}

				m += pixelY;
			}

			n += pixelX;
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
	
			final String name = 
				Integer.toString((int)bbox[0].getEasting()) 
				+ Integer.toString((int)bbox[1].getNorthing())
				+ Integer.toString((int)bbox[1].getEasting()) 
				+ Integer.toString((int)bbox[0].getNorthing());


			mapFile = new File(path + "/" + name + "." + fmt);
			ImageIO.write(buf, fmt, mapFile);
			log.info("Wrote map file " + mapFile.getAbsolutePath());
		
		}
		catch (final Throwable e)
		{
			log.error(e.toString());
			throw new IOException("Error creating map");
		}

		return mapFile;
		
	}

	public static final File getMap(final PlaceBook p)
		throws IOException, IllegalArgumentException
	{
		if (p.getGeometry() == null)
		{
			p.calcBoundary();
			if (p.getGeometry() != null)
				return getMap(p.getGeometry());
			else
				return null;
		}
		else
			return getMap(p.getGeometry());
	}

	public static final File getMap(final PlaceBookItem pi)
		throws IOException, IllegalArgumentException
	{
		return getMap(pi.getGeometry());
	}
}
