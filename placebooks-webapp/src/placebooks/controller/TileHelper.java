package placebooks.controller;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

	private static final class MapParam
	{
		public final OSRef[] bbox;
		public final int[] blocks;

		public MapParam(final OSRef[] bbox, final int[] blocks)
		{
			this.bbox = bbox;
			this.blocks = blocks;
		}
	}

	private static final Logger log = Logger.getLogger(TileHelper.class.getName());
	// Note, these are WGS84 converted coords from OSRefs corresponding to
	// min/max of (0.0, 0.0) -> (800000.0, 1400000.0)
	public static final LatLng TILE_MIN = new OSRef(0, 0).toLatLng();
	public static final LatLng TILE_MAX = new OSRef(799999.9, 1399999.9).toLatLng();

	static
	{
		TILE_MIN.toWGS84();
		TILE_MAX.toWGS84();
	}

	// Geometry *must* be a boundary, i.e., four points
	// Geometry g is updated with the boundaries of the map
	public static final MapMetadata getMap(final Geometry g) throws IOException, IllegalArgumentException, Exception
	{
		log.debug("getMap() geometry = " + g);
		String[] layer = { "5" };
		String[] product = null;
		int[] incX = { 1000 };
		int[] incY = { 1000 };
		int pixelX = 200;
		int pixelY = 200;
		String fmt = "png";
		int[] maxTiles = { 100 };
		boolean square = false;
		int maxAttempts = 10;
		boolean singleMap = true;
		String mediaPath = "";

		String[] incX_ = null;
		String[] incY_ = null;
		String[] maxTiles_ = null;

		try
		{
			layer = PropertiesSingleton.get(TileHelper.class.getClassLoader())
					.getProperty(PropertiesSingleton.IDEN_TILER_LAYER, "5").split(" ");
			product = PropertiesSingleton.get(TileHelper.class.getClassLoader())
					.getProperty(PropertiesSingleton.IDEN_TILER_PRODUCT, null).split(" ");
			incX_ = PropertiesSingleton.get(TileHelper.class.getClassLoader())
					.getProperty(PropertiesSingleton.IDEN_TILER_EASTING, "1000").split(" ");
			incY_ = PropertiesSingleton.get(TileHelper.class.getClassLoader())
					.getProperty(PropertiesSingleton.IDEN_TILER_NORTHING, "1000").split(" ");
			maxTiles_ = PropertiesSingleton.get(TileHelper.class.getClassLoader())
					.getProperty(PropertiesSingleton.IDEN_TILER_MAX_TILES, "100").split(" ");
			pixelX = Integer.parseInt(PropertiesSingleton.get(TileHelper.class.getClassLoader())
					.getProperty(PropertiesSingleton.IDEN_TILER_PIXEL_X, "200"));
			pixelY = Integer.parseInt(PropertiesSingleton.get(TileHelper.class.getClassLoader())
					.getProperty(PropertiesSingleton.IDEN_TILER_PIXEL_Y, "200"));
			fmt = PropertiesSingleton.get(TileHelper.class.getClassLoader())
					.getProperty(PropertiesSingleton.IDEN_TILER_FMT, "png").toLowerCase().trim();
			maxAttempts = Integer.parseInt(PropertiesSingleton.get(TileHelper.class.getClassLoader())
					.getProperty(PropertiesSingleton.IDEN_TILER_MAX_ATTEMPTS, "10"));
			square = Boolean.parseBoolean(PropertiesSingleton.get(TileHelper.class.getClassLoader())
					.getProperty(PropertiesSingleton.IDEN_TILER_SQUARE, "true"));
			singleMap = Boolean.parseBoolean(PropertiesSingleton.get(TileHelper.class.getClassLoader())
					.getProperty(PropertiesSingleton.IDEN_TILER_SINGLE_MAP, "true"));
			mediaPath = PropertiesSingleton.get(TileHelper.class.getClassLoader())
					.getProperty(PropertiesSingleton.IDEN_MEDIA, "");

		}
		catch (final Throwable e)
		{
			log.error(e.toString());
		}

		incX = new int[incX_.length];
		incY = new int[incY_.length];
		maxTiles = new int[maxTiles_.length];
		for (int i = 0; i < incX.length; ++i)
		{
			incX[i] = Integer.parseInt(incX_[i]);
		}
		for (int i = 0; i < incY.length; ++i)
		{
			incY[i] = Integer.parseInt(incY_[i]);
		}
		for (int i = 0; i < maxTiles.length; ++i)
		{
			maxTiles[i] = Integer.parseInt(maxTiles_[i]);
		}

		MapParam mp = null;
		int pn = -1;
		while (mp == null && pn < layer.length)
		{
			++pn;
			try
			{
				mp = generateParameters(g, incX[pn], incY[pn], square, maxTiles[pn]);
			}
			catch (final Throwable e)
			{
				System.out.println(e.toString());
			}
		}

		if (mp == null || pn < 0 || pn > layer.length) { throw new Exception(
				"Failed to find suitable parameters for map"); }
		final OSRef[] bbox = mp.bbox;
		final int eBlocks = mp.blocks[0];
		final int nBlocks = mp.blocks[1];

		BufferedImage buf = null;
		Graphics graphics = null;
		if (singleMap)
		{
			buf = new BufferedImage(pixelX * eBlocks, pixelY * nBlocks, BufferedImage.TYPE_INT_RGB);
			graphics = buf.createGraphics();
		}

		int n = 0;
		int b = 0;
		for (int i = (int) bbox[0].getEasting(); i < (int) bbox[1].getEasting(); i += incX[pn])
		{
			if (b++ > maxTiles[pn])
			{
				log.error("Tiler stopped fetching: maxTiles (" + maxTiles[pn] + ") being exceeded");
				break;
			}

			int m = 0;
			if (buf != null)
			{
				m = buf.getHeight() - pixelY;
			}

			for (int j = (int) bbox[0].getNorthing() /* + incY[pn] */; j < (int) bbox[1].getNorthing() /*
																									 * +
																									 * incY
																									 * [
																									 * pn
																									 * ]
																									 */; j += incY[pn])
			{
				// %5C = \
				final String url = buildOpenSpaceQuery(	layer[pn], incX[pn], incY[pn], pixelX, pixelY, new OSRef(i, j),
														"markerImage%5C" + fmt, product[pn]);

				int attempts = 0;
				boolean success = true;
				do
				{
					InputStream is = null;
					try
					{
						final URLConnection conn = CommunicationHelper.getConnection(new URL(url));
						is = conn.getInputStream();
					}
					catch (final Throwable e)
					{
						// log.error(e.toString());
						success = false;
						++attempts;
						log.error("Retrying... attempt " + attempts + " of " + maxAttempts);
					}

					if (success)
					{
						Image tile = null;
						tile = ImageIO.read(new BufferedInputStream(is));
						if (singleMap)
						{
							graphics.drawImage(tile, n, m, null);
							// graphics.drawRect(n, m, pixelX, pixelY);
							log.debug("Drew tile on single map, at " + n + ", " + m);
						}
						else
						{
							buf = new BufferedImage(pixelX, pixelY, BufferedImage.TYPE_INT_RGB);
							graphics = buf.createGraphics();
							graphics.drawImage(tile, 0, 0, null);
							log.debug("Drew individual tile");
							File mapFile = null;
							try
							{
								if (!new File(mediaPath).exists() && !new File(mediaPath).mkdirs()) { throw new IOException(
										"Failed to write file"); }

								final String name = Integer.toString(i) + Integer.toString(j)
										+ Integer.toString(i + incX[pn]) + Integer.toString(j + incY[pn]);

								mapFile = new File(mediaPath + "/" + name + "." + fmt);
								ImageIO.write(buf, fmt, mapFile);
								log.debug("Wrote map tile file " + mapFile.getAbsolutePath());

							}
							catch (final Throwable e)
							{
								throw new IOException("Error creating map");
							}
						}
					}

				}
				while (!success && attempts < maxAttempts);

				m -= pixelY;
			}

			n += pixelX;
		}

		graphics.dispose();

		File mapFile = null;
		if (singleMap)
		{
			try
			{

				if (!new File(mediaPath).exists() && !new File(mediaPath).mkdirs()) { throw new IOException(
						"Failed to write file"); }

				final String name = Integer.toString((int) bbox[0].getEasting())
						+ Integer.toString((int) bbox[0].getNorthing()) + Integer.toString((int) bbox[1].getEasting())
						+ Integer.toString((int) bbox[1].getNorthing());

				mapFile = new File(mediaPath + "/" + name + "." + fmt);
				ImageIO.write(buf, fmt, mapFile);
				log.debug("Wrote map file " + mapFile.getAbsolutePath());

			}
			catch (final Throwable e)
			{
				log.error(e.toString());
				throw new IOException("Error creating map");
			}
		}

		final LatLng[] bboxLL = new LatLng[2];
		bboxLL[0] = bbox[0].toLatLng();
		bboxLL[1] = bbox[1].toLatLng();
		bboxLL[0].toWGS84();
		bboxLL[1].toWGS84();

		Geometry g_ = null;

		try
		{
			// Construct new geometry, lat/lon not OS
			g_ = new WKTReader().read("LINEARRING (" + bboxLL[0].getLatitude() + " " + bboxLL[1].getLongitude() + ", "
					+ bboxLL[0].getLatitude() + " " + bboxLL[0].getLongitude() + ", " + bboxLL[1].getLatitude() + " "
					+ bboxLL[0].getLongitude() + ", " + bboxLL[1].getLatitude() + " " + bboxLL[1].getLongitude() + ", "
					+ bboxLL[0].getLatitude() + " " + bboxLL[1].getLongitude() + ")");
		}
		catch (final Throwable e)
		{
			log.error(e.toString());
		}
		return new MapMetadata(mapFile, g_, pn);

	}

	public static final MapMetadata getMap(final PlaceBook p) throws IOException, IllegalArgumentException, Exception
	{
		if (p.getGeometry() == null)
		{
			p.calcBoundary();
			if (p.getGeometry() != null)
			{
				return getMap(p.getGeometry());
			}
			else
			{
				return null;
			}
		}
		else
		{
			return getMap(p.getGeometry());
		}
	}

	public static final MapMetadata getMap(final PlaceBookItem pi) throws IOException, IllegalArgumentException,
			Exception
	{
		return getMap(pi.getGeometry());
	}

	private static final String buildOpenSpaceQuery(final String layer, final int blockSizeX, final int blockSizeY,
			final int width, final int height, final OSRef ref, final String format, final String product)
	{
		final String baseURL = PropertiesSingleton.get(TileHelper.class.getClassLoader())
				.getProperty(PropertiesSingleton.IDEN_OPENSPACE_BASEURL, "");

		final String host = PropertiesSingleton.get(TileHelper.class.getClassLoader())
				.getProperty(PropertiesSingleton.IDEN_OPENSPACE_HOST, "");

		final String apiKey = PropertiesSingleton.get(TileHelper.class.getClassLoader())
				.getProperty(PropertiesSingleton.IDEN_OPENSPACE_APIKEY, "");

		final int x1 = (int) ref.getEasting();
		final int y1 = (int) ref.getNorthing();
		final int x2 = x1 + blockSizeX;
		final int y2 = y1 + blockSizeY;

		String productStr = "";
		if (product != null)
		{
			productStr = "&PRODUCT=" + product;
		}

		final String url = baseURL + "?FORMAT=" + format + "&KEY=" + apiKey + "&URL=" + host + "&SERVICE=WMS"
				+ "&VERSION=1.1.1" + "&REQUEST=GetMap" + "&EXCEPTIONS=application%5Cvnd.ogc.se_inimage" + "&LAYERS="
				+ layer + productStr + "&SRS=EPSG%3A27700" + "&BBOX=" + Integer.toString(x1) + ","
				+ Integer.toString(y1) + "," + Integer.toString(x2) + "," + Integer.toString(y2) + "&WIDTH="
				+ Integer.toString(width) + "&HEIGHT=" + Integer.toString(height);

		return url;
	}

	private static final MapParam generateParameters(final Geometry g, final int incX, final int incY,
			final boolean square, final int maxTiles)
	{

		// 0 = TL, 1 = BR
		final Coordinate[] coords_ = new Coordinate[2];
		coords_[0] = new Coordinate(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
		coords_[1] = new Coordinate(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);

		final Coordinate[] coords = g.getCoordinates();
		for (final Coordinate coord : coords)
		{
			coords_[0].x = Math.min(coord.x, coords_[0].x);
			coords_[0].y = Math.min(coord.y, coords_[0].y);
			coords_[1].x = Math.max(coord.x, coords_[1].x);
			coords_[1].y = Math.max(coord.y, coords_[1].y);
		}

		final OSRef[] bbox_ = new OSRef[2];
		for (int i = 0; i < coords_.length; ++i)
		{
			final LatLng latlng = new LatLng(coords_[i].x, coords_[i].y);
			if (latlng.getLatitude() < TILE_MIN.getLatitude() || latlng.getLatitude() > TILE_MAX.getLatitude()
					|| latlng.getLongitude() < TILE_MIN.getLongitude()
					|| latlng.getLongitude() > TILE_MAX.getLongitude()) { throw new IllegalArgumentException(); }
			bbox_[i] = new OSRef(latlng);
			log.debug("Bounding box unnormalised, bbox[" + i + "]=" + bbox_[i].getEasting() + ","
					+ bbox_[i].getNorthing());
		}

		final OSRef[] bbox = new OSRef[2];
		int x = (int) Math.floor((bbox_[0].getEasting() / incX)) * incX, y = (int) Math
				.floor((bbox_[0].getNorthing() / incY)) * incY;
		bbox[0] = new OSRef(x, y);
		x = (int) Math.ceil((bbox_[1].getEasting() / incX)) * incX;
		y = (int) Math.ceil((bbox_[1].getNorthing() / incY)) * incY;
		bbox[1] = new OSRef(x, y);

		for (int i = 0; i < bbox.length; ++i)
		{
			log.debug("Bounding box NORMALISED, bbox[" + i + "]=" + bbox[i].getEasting() + "," + bbox[i].getNorthing());
		}

		int eBlocks = (int) Math.ceil((Math.abs(bbox[1].getEasting() - bbox[0].getEasting())) / incX);
		int nBlocks = (int) Math.ceil((Math.abs(bbox[1].getNorthing() - bbox[0].getNorthing())) / incY);

		log.debug("eBlocks = " + eBlocks + " nBlocks = " + nBlocks);

		if (square)
		{
			int diff = Math.abs(eBlocks - nBlocks);
			if (diff > 0)
			{
				if (eBlocks > nBlocks)
				{
					nBlocks = eBlocks;
					for (; diff > 0; --diff)
					{
						if (diff % 2 == 0)
						{
							bbox[0].setNorthing(bbox[0].getNorthing() - incY);
						}
						else
						{
							bbox[1].setNorthing(bbox[1].getNorthing() + incY);
						}
					}
				}
				else
				{
					eBlocks = nBlocks;
					for (; diff > 0; --diff)
					{
						if (diff % 2 == 0)
						{
							bbox[0].setEasting(bbox[0].getEasting() - incX);
						}
						else
						{
							bbox[1].setEasting(bbox[1].getEasting() + incX);
						}

					}
				}

				log.debug("Squared tiles out");
			}
		}

		if (eBlocks * nBlocks > maxTiles)
		{
			log.error("Tiler limiting fetches to " + maxTiles + " tiles");
			return null;
		}
		else if (eBlocks * nBlocks == 1)
		{
			if (bbox_[0].getEasting() - bbox[0].getEasting() > (incX / 2))
			{
				bbox[1].setEasting(bbox[1].getEasting() + incX);
			}
			else
			{
				bbox[0].setEasting(bbox[0].getEasting() - incX);
			}
			if (bbox_[0].getNorthing() - bbox[0].getNorthing() < (incY / 2))
			{
				bbox[1].setNorthing(bbox[1].getNorthing() + incY);
			}
			else
			{
				bbox[0].setNorthing(bbox[0].getNorthing() - incY);
			}

			eBlocks = 2;
			nBlocks = 2;
		}

		return new MapParam(bbox, new int[] { eBlocks, nBlocks });
	}

}
