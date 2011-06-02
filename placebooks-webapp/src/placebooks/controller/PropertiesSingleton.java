package placebooks.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public final class PropertiesSingleton
{
	public static final String EVERYTRAIL_API_PASSWORD 
		= "everytrail.api_password";

	public static final String EVERYTRAIL_API_USER = "everytrail.api_user";
	public static final String IDEN_CONFIG = "config.name";

	public static final String IDEN_MEDIA = "media.dir";
	public static final String IDEN_PKG = "packages.dir";
	public static final String IDEN_PKG_Z = "packages-zipped.dir";

	public static final String IDEN_TILER_LAYER = "tiler.layer";
	public static final String IDEN_TILER_NORTHING = "tiler.northing";
	public static final String IDEN_TILER_EASTING = "tiler.easting";
	public static final String IDEN_TILER_PIXEL_X = "tiler.x_pixels";
	public static final String IDEN_TILER_PIXEL_Y = "tiler.y_pixels";
	public static final String IDEN_TILER_FMT = "tiler.format";

	public static final String IDEN_USER_AGENT = "webbundleitem.user-agent";
	public static final String IDEN_WEBBUNDLE = "webbundleitem.dir";

	public static final String IDEN_OPENSPACE_BASEURL = "openspace.baseurl";
	public static final String IDEN_OPENSPACE_HOST = "openspace.host";
	public static final String IDEN_OPENSPACE_APIKEY = "openspace.apikey";

	public static final String IDEN_WGET = "webbundleitem.wget";
	public static final String PROPERTIES_FILENAME = "placebooks.properties";
	public static final String PROXY_ACTIVE = "proxy.active";
	public static final String PROXY_HOST = "proxy.host";
	public static final String PROXY_PORT = "proxy.port";

	private static final Logger log = 
		Logger.getLogger(PropertiesSingleton.class.getName());

	private static final Properties properties = new Properties();

	public static Properties get(final ClassLoader cl)
	{
		final InputStream in = cl.getResourceAsStream(PROPERTIES_FILENAME);
		try
		{
			properties.load(in);
			in.close();
			log.info("Loaded properties");
		}
		catch (final IOException e)
		{
			log.error("Error in loading properties from " 
					  + PROPERTIES_FILENAME);
			log.error(e.toString());
		}

		return properties;
	}

	private PropertiesSingleton()
	{
	}
}
