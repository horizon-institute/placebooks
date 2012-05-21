package placebooks.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.Properties;

import org.apache.log4j.Logger;

public final class PropertiesSingleton
{
	public static final String EVERYTRAIL_API_PASSWORD = "everytrail.api_password";

	public static final String EVERYTRAIL_API_USER = "everytrail.api_user";
	public static final String IDEN_AUDIO_MAX_SIZE = "audioitem.size_limit";

	public static final String VIDEOITEM_FFMPEG_TRANSCODE = "videoitem.ffmpeg_transcode";
	
	public static final String IDEN_CONFIG = "config.name";
	public static final String IDEN_IMAGE_MAX_SIZE = "imageitem.size_limit";
	public static final String IDEN_MEDIA = "media.dir";
	public static final String IDEN_THUMBS = "thumbs.dir";

	public static final String IDEN_SEARCH_LAT = "search.lat";
	public static final String IDEN_SEARCH_LON = "search.lon";
	public static final String IDEN_SEARCH_RADIUS = "search.radius";
	public static final String IDEN_SEARCH_TERMS = "search.terms";
	
	public static final String IDEN_OPENSPACE_APIKEY = "openspace.apikey";
	public static final String IDEN_OPENSPACE_BASEURL = "openspace.baseurl";
	public static final String IDEN_OPENSPACE_HOST = "openspace.host";

	public static final String IDEN_PKG = "packages.dir";
	public static final String IDEN_PKG_Z = "packages-zipped.dir";
	public static final String IDEN_TILER_EASTING = "tiler.easting";
	public static final String IDEN_TILER_FMT = "tiler.format";
	public static final String IDEN_TILER_LAYER = "tiler.layer";
	public static final String IDEN_TILER_NORTHING = "tiler.northing";

	public static final String IDEN_TILER_PIXEL_X = "tiler.x_pixels";
	public static final String IDEN_TILER_PIXEL_Y = "tiler.y_pixels";
	public static final String IDEN_TILER_MAX_TILES = "tiler.max_tiles";
	public static final String IDEN_TILER_SQUARE = "tiler.square";
	public static final String IDEN_TILER_SINGLE_MAP = "tiler.single_map";	
	public static final String IDEN_TILER_MAX_ATTEMPTS = "tiler.max_attempts";	
	public static final String IDEN_TILER_PRODUCT = "tiler.product";

	public static final String IDEN_USER_AGENT = "webbundleitem.user-agent";
	public static final String IDEN_VIDEO_MAX_SIZE = "videoitem.size_limit";
	public static final String IDEN_WEBBUNDLE = "webbundleitem.dir";

	public static final String IDEN_WGET = "webbundleitem.wget";
	public static final String IDEN_WGET_TIMEOUT = "webbundleitem.wget_timeout";	
	public static final String PROPERTIES_FILENAME = "placebooks.properties";
	public static final String PROXY_ACTIVE = "proxy.active";
	public static final String PROXY_HOST = "proxy.host";
	public static final String PROXY_PORT = "proxy.port";
	
	public static final String DROPBOX_APIKEY = "dropbox.apikey";
	public static final String DROPBOX_APISECRET = "dropbox.apisecret";
	public static final String DROPBOX_ACCESSTYPE = "dropbox.accesstype";

	public static final String IDEN_SERVER_NAME = "server.name";

	private static long lastModified = 0;

	private static final Logger log = Logger.getLogger(PropertiesSingleton.class.getName());
	private static final Properties properties = new Properties();

	public static Properties get(final ClassLoader cl)
	{
		try
		{
			final URLConnection url = cl.getResource(PROPERTIES_FILENAME).openConnection();
			if (lastModified == 0 || lastModified != url.getLastModified())
			{
				final InputStream in = url.getInputStream();
				properties.clear();
				properties.load(in);
				in.close();
				log.info("Loaded properties");
				lastModified = url.getLastModified();
			}
		}
		catch (final IOException e)
		{
			log.error("Error in loading properties from " + PROPERTIES_FILENAME);
			log.error(e.toString());
		}

		return properties;
	}

	private PropertiesSingleton()
	{
	}
}
