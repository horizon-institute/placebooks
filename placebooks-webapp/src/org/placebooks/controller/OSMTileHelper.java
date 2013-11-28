package org.placebooks.controller;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.placebooks.model.MapImageItem;
import org.placebooks.model.PlaceBook;

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
	public static final MapImageItem getMap(final PlaceBook placebook) throws IOException, IllegalArgumentException,
			Exception
	{
		if (placebook == null || placebook.getGeometry() == null) { return null; }

		log.fine("getMap() geometry = " + placebook.getGeometry());

		int tileWidth = 256;
		int tileHeight = 256;
		String fmt = "png";
		try
		{
			tileWidth = Integer.parseInt(PropertiesSingleton.get(OSMTileHelper.class.getClassLoader())
					.getProperty(PropertiesSingleton.IDEN_TILER_PIXEL_X, "256"));
			tileHeight = Integer.parseInt(PropertiesSingleton.get(OSMTileHelper.class.getClassLoader())
					.getProperty(PropertiesSingleton.IDEN_TILER_PIXEL_Y, "256"));
			fmt = PropertiesSingleton.get(OSMTileHelper.class.getClassLoader())
					.getProperty(PropertiesSingleton.IDEN_TILER_FMT, "png").toLowerCase().trim();
		}
		catch (final Throwable e)
		{
			log.log(Level.SEVERE, e.getMessage(), e);
		}

		final TileArea area = getTileArea(placebook.getGeometry(), 25);
		final int imageWidth = (1 + Math.abs(area.tileMax.x - area.tileMin.x)) * tileWidth;
		final int imageHeight = (1 + Math.abs(area.tileMax.y - area.tileMin.y)) * tileHeight;

		final BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);;
		final Graphics2D graphics = image.createGraphics();

		log.info("Tile X = " + area.tileMin.x + " - " + area.tileMax.x);
		log.info("Tile Y = " + area.tileMin.y + " - " + area.tileMax.y);

		for (int x = area.tileMin.x; x <= area.tileMax.x; x++)
		{
			for (int y = area.tileMin.y; y <= area.tileMax.y; y++)
			{
				try
				{
					final URLConnection conn = CommunicationHelper.getConnection(buildURL(area.zoom, x, y));
					final InputStream imageInput = conn.getInputStream();

					final Image tile = ImageIO.read(imageInput);

					final int xoffset = (x - area.tileMin.x) * tileWidth;
					final int yoffset = (y - area.tileMin.y) * tileHeight;

					log.info("Draw tile at " + xoffset + ", " + yoffset);

					graphics.drawImage(tile, xoffset, yoffset, null);
				}
				catch (final Exception e)
				{
					log.log(Level.SEVERE, e.getMessage(), e);
				}
			}
		}

		graphics.dispose();
	
		final MapImageItem result = new MapImageItem(placebook.getOwner(), placebook.getGeometry(), null, null);
		result.setPlaceBook(placebook);
		
		try
		{
			final ByteArrayOutputStream byteos = new ByteArrayOutputStream();
			ImageIO.write(image, fmt, byteos);

			final ByteArrayInputStream byteis = new ByteArrayInputStream(byteos.toByteArray());

			result.writeDataToDisk(byteis);
		}
		catch (final Throwable e)
		{
			log.log(Level.SEVERE, e.getMessage(), e);
			throw new IOException("Error creating map");
		}

		result.setGeometry(getBoundingBox(area));

		return result;
	}

	private static final URL buildURL(final int zoom, final int tileX, final int tileY) throws MalformedURLException
	{
		final String baseURL = PropertiesSingleton.get(OSMTileHelper.class.getClassLoader())
				.getProperty(PropertiesSingleton.IDEN_OPENSTREETMAP_BASEURL, "");

		log.info("Get OSM Tile: " + baseURL + zoom + "/" + tileX + "/" + tileY + ".png");
		return new URL(baseURL + zoom + "/" + tileX + "/" + tileY + ".png");
	}

	private static Geometry getBoundingBox(final TileArea area)
	{
		final double north = tile2lat(area.tileMin.y, area.zoom);
		final double south = tile2lat(area.tileMax.y + 1, area.zoom);
		final double west = tile2lon(area.tileMin.x, area.zoom);
		final double east = tile2lon(area.tileMax.x + 1, area.zoom);

		final Envelope envelope = new Envelope(north, south, west, east);
		final GeometryFactory factory = new GeometryFactory();
		return factory.toGeometry(envelope);
	}

	private static TileArea getTileArea(final Geometry g, final int maxTiles)
	{
		final Envelope envelope = g.getEnvelopeInternal();

		for (int zoom = MAX_ZOOM; zoom >= MIN_ZOOM; zoom--)
		{
			final Point pointMin = getTileNumber(envelope.getMaxX(), envelope.getMinY(), zoom);
			final Point pointMax = getTileNumber(envelope.getMinX(), envelope.getMaxY(), zoom);

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

	private static double tile2lat(final int y, final int z)
	{
		final double n = Math.PI - (2.0 * Math.PI * y) / Math.pow(2.0, z);
		return Math.toDegrees(Math.atan(Math.sinh(n)));
	}

	private static double tile2lon(final int x, final int z)
	{
		return x / Math.pow(2.0, z) * 360.0 - 180;
	}
}