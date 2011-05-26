package placebooks.controller;

import java.io.File;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import java.io.IOException;

import java.net.URLConnection;
import java.net.URL;

import org.apache.log4j.Logger;

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
		final int x2 = x1 + 900;
		final int y2 = y1 + 500;

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
					 + "&WIDTH=250&HEIGHT=250";

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

	public static final File getMap(final Geometry g) throws IOException
	{

		final Coordinate[] coords = g.getBoundary().getCoordinates();

		File mapFile = null;

		for (int i = 0; i < coords.length; ++i)
		{
			final OSRef osRef = new OSRef(new LatLng(coords[i].x, coords[i].y));
			log.info("Coord[" + i + "]: " + coords[i].x + "," + coords[i].y 
					 + "; OSRef = " + osRef.getEasting() + "," 
					 + osRef.getNorthing());
			// %5C = \
			final String url = buildOpenSpaceQuery(5, osRef, "image%5Cpng");
			log.info("URL = " + url);

			try
			{
					
				final URLConnection conn = 
					CommunicationHelper.getConnection(new URL(url));
				final BufferedInputStream bis = 
					new BufferedInputStream(conn.getInputStream());

				final String path = 
					PropertiesSingleton
						.get(TileHelper.class.getClassLoader())
						.getProperty(PropertiesSingleton.IDEN_MEDIA, "");

				if (!new File(path).exists() && !new File(path).mkdirs()) 
				{
					throw new IOException("Failed to write file"); 
				}
		
				final File tileFile = new File(path + "/" 
											   + Integer.toString(i) + ".png");
				final OutputStream output = new FileOutputStream(tileFile);
				int byte_;
				while ((byte_ = bis.read()) != -1)
				{
					output.write(byte_);
				}
				output.close();
				bis.close();

				log.info("Wrote tile file " + tileFile.getAbsolutePath());
				mapFile = tileFile;
			
			}
			catch (final Throwable e)
			{
				log.error(e.toString());
				throw new IOException("Error creating map");
			}
		}

		return mapFile;
		
	}
}
