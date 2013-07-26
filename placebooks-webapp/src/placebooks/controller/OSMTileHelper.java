package placebooks.controller;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import placebooks.model.PlaceBook;
import placebooks.model.PlaceBookItem;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

public final class OSMTileHelper
{
	private static class TileArea
	{
		Point tileMin;
		Point tileMax;
		int zoom;
	}

	private static final Logger log = Logger.getLogger(OSMTileHelper.class.getName());

	// private static final double border = 0.0000001;

	private static final int MAX_ZOOM = 17;

	private static final int MIN_ZOOM = 0;

	// Geometry *must* be a boundary, i.e., four points
	// Geometry g is updated with the boundaries of the map
	public static final MapMetadata getMap(final Geometry g) throws IOException, IllegalArgumentException, Exception
	{
		log.debug("getMap() geometry = " + g);
		int tileWidth = 256;
		int tileHeight = 256;
		String fmt = "png";
		String mediaPath = "";

		try
		{
			tileWidth = Integer.parseInt(PropertiesSingleton.get(OSMTileHelper.class.getClassLoader())
					.getProperty(PropertiesSingleton.IDEN_TILER_PIXEL_X, "256"));
			tileHeight = Integer.parseInt(PropertiesSingleton.get(OSMTileHelper.class.getClassLoader())
					.getProperty(PropertiesSingleton.IDEN_TILER_PIXEL_Y, "256"));
			fmt = PropertiesSingleton.get(OSMTileHelper.class.getClassLoader())
					.getProperty(PropertiesSingleton.IDEN_TILER_FMT, "png").toLowerCase().trim();
			mediaPath = PropertiesSingleton.get(OSMTileHelper.class.getClassLoader())
					.getProperty(PropertiesSingleton.IDEN_MEDIA, "");

		}
		catch (final Throwable e)
		{
			log.error(e.toString());
		}

		final TileArea area = getTileArea(g, 25);
		final int imageWidth = (1 + Math.abs(area.tileMax.x - area.tileMin.x)) * tileWidth;
		final int imageHeight = (1+ Math.abs(area.tileMax.y - area.tileMin.y)) * tileHeight;

		final BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);;
		final Graphics2D graphics = image.createGraphics();

		for (int x = area.tileMin.x; x <= area.tileMax.x; x++)
		{
			for (int y = area.tileMax.y; y <= area.tileMax.y; y++)
			{
				final URLConnection conn = CommunicationHelper.getConnection(buildURL(area.zoom, x, y));
				final InputStream imageInput = conn.getInputStream();

				final Image tile = ImageIO.read(imageInput);

				graphics.drawImage(tile, (x - area.tileMin.x) * tileWidth, (y - area.tileMin.y) * tileHeight, null);
			}
		}

		graphics.dispose();

		final String name = area.zoom + "." + area.tileMin.x + "-" + area.tileMax.x + "." + area.tileMin.y + "-" + area.tileMax.y;
		final File mapFile = new File(mediaPath + "/" + name + "." + fmt);
		try
		{
			if (!new File(mediaPath).exists() && !new File(mediaPath).mkdirs()) { throw new IOException(
					"Failed to write file"); }
			ImageIO.write(image, fmt, mapFile);
			log.debug("Wrote map file " + mapFile.getAbsolutePath());

		}
		catch (final Throwable e)
		{
			log.error(e.toString());
			throw new IOException("Error creating map");
		}

		final Geometry g_ = getBoundingBox(area);

		return new MapMetadata(mapFile, g_, area.zoom);

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

	private static double tile2lat(final int y, final int z)
	{
		final double n = Math.PI - (2.0 * Math.PI * y) / Math.pow(2.0, z);
		return Math.toDegrees(Math.atan(Math.sinh(n)));
	}

	private static double tile2lon(final int x, final int z)
	{
		return x / Math.pow(2.0, z) * 360.0 - 180;
	}

	private static final URL buildURL(final int zoom, final int tileX, final int tileY) throws MalformedURLException
	{
		final String baseURL = PropertiesSingleton.get(OSMTileHelper.class.getClassLoader())
				.getProperty(PropertiesSingleton.IDEN_OPENSTREETMAP_BASEURL, "");

		return new URL(baseURL + zoom + "/" + tileX + "/" + tileY + ".png");
	}

	private static TileArea getTileArea(final Geometry g, final int maxTiles)
	{
		final Envelope envelope = g.getEnvelopeInternal();

		for (int zoom = MAX_ZOOM; zoom >= MIN_ZOOM; zoom--)
		{
			final Point pointMin = getTileNumber(envelope.getMinX(), envelope.getMinY(), zoom);
			final Point pointMax = getTileNumber(envelope.getMaxX(), envelope.getMaxY(), zoom);

			final int area = Math.abs((pointMax.x - pointMin.x) * (pointMax.y - pointMin.y));

			if (area <= maxTiles)
			{
				final TileArea tileArea = new TileArea();
				tileArea.tileMin = pointMin;
				tileArea.tileMax = pointMax;
				tileArea.zoom = zoom;
				return tileArea;
			}
		}
		return null;
	}

	private static Point getTileNumber(final double lat, final double lon, final int zoom)
	{
		final int xtile = (int) Math.floor((lon + 180) / 360 * (1 << zoom));
		final int ytile = (int) Math.floor((1 - Math.log(Math.tan(Math.toRadians(lat)) + 1
				/ Math.cos(Math.toRadians(lat)))
				/ Math.PI)
				/ 2 * (1 << zoom));
		return new Point(xtile, ytile);
	}

	private static Geometry getBoundingBox(final TileArea area)
	{
		double north = tile2lat(area.tileMin.y, area.zoom);
		double south = tile2lat(area.tileMax.y + 1, area.zoom);
		double west = tile2lon(area.tileMin.x, area.zoom);
		double east = tile2lon(area.tileMax.x + 1, area.zoom);

		Envelope envelope = new Envelope(west, east, north, south);
		GeometryFactory factory = new GeometryFactory();
		return factory.toGeometry(envelope);
	}
}