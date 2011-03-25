package placebooks.controller;

import org.apache.log4j.Logger;

import java.io.InputStream;
import java.io.IOException;

import java.util.Properties;

public final class PropertiesSingleton 
{
	// TODO: enum
	public static final String PROPERTIES_FILENAME = "placebooks.properties";

	public static final String IDEN_VIDEO = "videoitem.dir";
	public static final String IDEN_AUDIO = "audioitem.dir";
	public static final String IDEN_WEBBUNDLE = "webbundleitem.dir";
	
	public static final String IDEN_PKG = "packages.dir";
	public static final String IDEN_PKG_Z = "packages-zipped.dir";
	public static final String IDEN_CONFIG = "config.name";
	public static final String IDEN_WGET = "webbundleitem.wget";
	public static final String IDEN_USER_AGENT = "webbundleitem.user-agent";

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
