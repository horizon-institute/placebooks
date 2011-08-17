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
import com.vividsolutions.jts.io.WKTReader;

public final class TileHelper
{

	private static final Logger log = 
		Logger.getLogger(TileHelper.class.getName());


	// Note, these are WGS84 converted coords from OSRefs corresponding to
	// min/max of (0.0, 0.0) -> (800000.0, 1400000.0)
	public static final LatLng TILE_MIN = new OSRef(0, 0).toLatLng();
	public static final LatLng TILE_MAX = 
		new OSRef(799999.9, 1399999.9).toLatLng();
	static
	{
		TILE_MIN.toWGS84();
		TILE_MAX.toWGS84();
	}


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

	public static final MapMetadata getMap(final PlaceBook p)
		throws IOException, IllegalArgumentException, Exception
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

	public static final MapMetadata getMap(final PlaceBookItem pi)
		throws IOException, IllegalArgumentException, Exception
	{
		return getMap(pi.getGeometry());
	}


	// Geometry *must* be a boundary, i.e., four points
	// Geometry g is updated with the boundaries of the map
	public static final MapMetadata getMap(final Geometry g) 
		throws IOException, IllegalArgumentException, Exception
	{
		log.info("getMap() geometry = " + g);
		int layer = 5;
		int incX = 1000;
		int incY = 1000;
		int pixelX = 200;
		int pixelY = 200;
		String fmt = "png";
		int maxTiles = 100;
		boolean square = false;

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
			maxTiles = Integer.parseInt(
				PropertiesSingleton
					.get(TileHelper.class.getClassLoader())
					.getProperty(
						PropertiesSingleton.IDEN_TILER_MAX_TILES, "100"
					)
			);
			square = Boolean.parseBoolean(
				PropertiesSingleton
					.get(TileHelper.class.getClassLoader())
					.getProperty(
						PropertiesSingleton.IDEN_TILER_SQUARE, "true"
					)
			);
		}
		catch (final Throwable e)
		{
			log.error(e.toString());
		}

		// 0 = TL, 1 = BR
		Coordinate[] coords_ = new Coordinate[2];
		coords_[0] = new Coordinate(Double.POSITIVE_INFINITY, 
								    Double.POSITIVE_INFINITY);
		coords_[1] = new Coordinate(Double.NEGATIVE_INFINITY, 
				 				    Double.NEGATIVE_INFINITY);

		final Coordinate[] coords = g.getCoordinates();
		for (int i = 0; i < coords.length; ++i)
		{
			coords_[0].x = Math.min(coords[i].x, coords_[0].x);
			coords_[0].y = Math.min(coords[i].y, coords_[0].y);
			coords_[1].x = Math.max(coords[i].x, coords_[1].x);
			coords_[1].y = Math.max(coords[i].y, coords_[1].y);
		}
		
		OSRef[] bbox_ = new OSRef[2];
		for (int i = 0; i < coords_.length; ++i)
		{
			final LatLng latlng = new LatLng(coords_[i].x, coords_[i].y);
			if (latlng.getLatitude() < TILE_MIN.getLatitude() ||
				latlng.getLatitude() > TILE_MAX.getLatitude() ||
				latlng.getLongitude() < TILE_MIN.getLongitude() ||
				latlng.getLongitude() > TILE_MAX.getLongitude())
			{
				throw new IllegalArgumentException();
			}
			bbox_[i] = new OSRef(latlng);
			log.info("Bounding box unnormalised, bbox["+i+"]="
					 +bbox_[i].getEasting()+","+bbox_[i].getNorthing());
		}

		OSRef[] bbox = new OSRef[2];
		int x = (int)Math.floor((bbox_[0].getEasting() / incX)) * incX,
			y = (int)Math.floor((bbox_[0].getNorthing() / incY)) * incY;
		bbox[0] = new OSRef(x, y);
		x = (int)Math.ceil((bbox_[1].getEasting() / incX)) * incX;
		y = (int)Math.ceil((bbox_[1].getNorthing() / incY)) * incY;
		bbox[1] = new OSRef(x, y);

		for (int i = 0; i < bbox.length; ++i)
		{
			log.info("Bounding box NORMALISED, bbox["+i+"]="
					 +bbox[i].getEasting()+","+bbox[i].getNorthing());
		}


		int eBlocks = (int)Math.ceil((Math.abs(bbox[1].getEasting() 
									 	- bbox[0].getEasting())) / incX);
		int nBlocks = (int)Math.ceil((Math.abs(bbox[1].getNorthing() 
									   - bbox[0].getNorthing())) / incY);

		log.info("eBlocks = " + eBlocks + " nBlocks = " + nBlocks);

		if (square)
		{
			int diff = Math.abs(eBlocks - nBlocks);
			if (diff > 0)
			{
				if (eBlocks > nBlocks)
				{
					nBlocks = eBlocks;
					for ( ; diff > 0; --diff)
					{
						if (diff % 2 == 0)
							bbox[0].setNorthing(bbox[0].getNorthing() - incY);
						else
							bbox[1].setNorthing(bbox[1].getNorthing() + incY);
					}
				}
				else
				{
					eBlocks = nBlocks;
					for ( ; diff > 0; --diff)
					{
						if (diff % 2 == 0)
							bbox[0].setEasting(bbox[0].getEasting() - incX);
						else
							bbox[1].setEasting(bbox[1].getEasting() + incX);

					}
				}

				log.info("Squared tiles out");
			}
		}


		if (eBlocks * nBlocks > maxTiles)
			log.error("Tiler limiting fetches to " + maxTiles + " tiles");


		final BufferedImage buf = 
			new BufferedImage(pixelX * eBlocks, pixelY * nBlocks, 
							  BufferedImage.TYPE_INT_RGB);
		final Graphics graphics = buf.createGraphics();

		int n = 0;
		int b = 0;
		for (int i = (int)bbox[0].getEasting(); i <= (int)bbox[1].getEasting(); 
			 i += incX)
		{
			if (b++ > maxTiles)
			{
				log.error("Tiler stopped fetching: maxTiles being exceeded");

				break;
			}

			int m = buf.getHeight();
			for (int j = (int)bbox[0].getNorthing() - incY; 
				 j <= (int)bbox[1].getNorthing() - incY; j += incY)
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

				m -= pixelY;
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
				+ Integer.toString((int)bbox[0].getNorthing())
				+ Integer.toString((int)bbox[1].getEasting()) 
				+ Integer.toString((int)bbox[1].getNorthing());


			mapFile = new File(path + "/" + name + "." + fmt);
			ImageIO.write(buf, fmt, mapFile);
			log.info("Wrote map file " + mapFile.getAbsolutePath());
		
		}
		catch (final Throwable e)
		{
			log.error(e.toString());
			throw new IOException("Error creating map");
		}

		LatLng[] bboxLL = new LatLng[2];
		bboxLL[0] = bbox[0].toLatLng();
		bboxLL[1] = bbox[1].toLatLng();
		bboxLL[0].toWGS84();
		bboxLL[1].toWGS84();

		Geometry g_ = null;

		try
		{
			// Construct new geometry, lat/lon not OS
			g_ = 
				new WKTReader().read(
						"LINEARRING (" + bboxLL[0].getLatitude() + " " 
									   + bboxLL[1].getLongitude() + ", "
									   + bboxLL[0].getLatitude() + " " 
									   + bboxLL[0].getLongitude() + ", "
									   + bboxLL[1].getLatitude() + " " 
									   + bboxLL[0].getLongitude() + ", "
									   + bboxLL[1].getLatitude() + " " 
									   + bboxLL[1].getLongitude() + ", "
									   + bboxLL[0].getLatitude() + " " 
									   + bboxLL[1].getLongitude() + ")"
				);
		}
		catch (final Throwable e)
		{
			log.error(e.toString());
		}
		return new MapMetadata(mapFile, g_);
		
	}

	public static class MapMetadata
	{
		private File file;
		private Geometry boundingBox;

		public MapMetadata(final File file, final Geometry boundingBox)
		{
			this.file = file;
			this.boundingBox = boundingBox;
		}

		public final Geometry getBoundingBox()
		{
			return boundingBox;
		}

		public final File getFile()
		{
			return file;
		}
	}
}
