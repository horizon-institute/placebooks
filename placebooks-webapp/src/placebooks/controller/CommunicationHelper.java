/**
 * 
 */
package placebooks.controller;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;

/**
 * @author pszmp
 *
 */
public class CommunicationHelper
{
	private static final Logger log = 
		Logger.getLogger(CommunicationHelper.class.getName());
	
	/**
	 * Gets an http connection paying attention to the Placebooks proxy configuration values
	 * requires a try/catch block as per url.openconnection()
	 * @param URL url The url to get the connection for
	 * @return URLConnection A url connection as per url.openConnection - with or without proxy
	 */
		public static URLConnection getConnection(URL url) throws IOException
		{
			URLConnection conn;
		   if(PropertiesSingleton.get(CommunicationHelper.class.getClassLoader()).getProperty(PropertiesSingleton.PROXY_ACTIVE, "false").equalsIgnoreCase("true"))
		   {
		   	log.debug("Using proxy: " + PropertiesSingleton.get(CommunicationHelper.class.getClassLoader()).getProperty(PropertiesSingleton.PROXY_HOST, "") + ":" +
		   			PropertiesSingleton.get(CommunicationHelper.class.getClassLoader()).getProperty(PropertiesSingleton.PROXY_PORT, ""));
			    Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
			   		 PropertiesSingleton.get(CommunicationHelper.class.getClassLoader()).getProperty(PropertiesSingleton.PROXY_HOST, ""),
			   		 Integer.parseInt(PropertiesSingleton.get(CommunicationHelper.class.getClassLoader()).getProperty(PropertiesSingleton.PROXY_PORT, ""))));
			    conn = url.openConnection(proxy);
		    }
		    else
		    {
		   	 conn = url.openConnection();
		    }
		    return conn;
		}
}
