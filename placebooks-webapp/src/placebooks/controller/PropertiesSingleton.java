package placebooks.controller;

import org.apache.log4j.*;

import java.io.InputStream;
import java.io.IOException;

import java.util.Properties;

public final class PropertiesSingleton 
{
	public static final String PROPERTIES_FILENAME = "placebooks.properties";

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
			log.error("Attempted to load properties from " 
				      + PROPERTIES_FILENAME);
			log.error(e.toString());
		}

        return properties;
    }
}
