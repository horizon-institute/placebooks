package placebooks.controller;

import org.apache.log4j.*;

import java.io.InputStream;
import java.io.IOException;

import java.util.Properties;

public final class PropertiesSingleton 
{
	// TODO: enum
	public static final String PROPERTIES_FILENAME = "placebooks.properties";
	public static final String IDEN_VIDEO = "video.dir";
	public static final String IDEN_AUDIO = "audio.dir";
	public static final String IDEN_PKG = "packages.dir";
	public static final String IDEN_PKG_Z = "packages-zipped.dir";
	public static final String IDEN_CONFIG = "config.name";	
	public static final String PROXY_ACTIVE = "proxy.active";	
	public static final String PROXY_HOST = "proxy.host";	
	public static final String PROXY_PORT = "proxy.port";	
	public static final String EVERYTRAIL_API_USER = "everytrail.api_user";	
	public static final String EVERYTRAIL_API_PASSWORD = "everytrail.api_password";	

   	private static final Logger log = 
		Logger.getLogger(PropertiesSingleton.class.getName());

    private static final Properties properties = new Properties();

    private PropertiesSingleton() {}

    public static Properties get(ClassLoader cl)
	{
		InputStream in = cl.getResourceAsStream(PROPERTIES_FILENAME);
 		try
		{
			properties.load(in);
			in.close();
			log.info("Loaded properties");
		}
		catch (IOException e)
		{
			log.error("Error in loading properties from " 
				      + PROPERTIES_FILENAME);
			log.error(e.toString());
		}

        return properties;
    }
}
