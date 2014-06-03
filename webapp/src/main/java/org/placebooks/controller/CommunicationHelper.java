/**
 * 
 */
package org.placebooks.controller;

import org.wornchaos.logger.Log;

import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author pszmp
 */
public final class CommunicationHelper
{
	public static class HttpAuthenticator extends Authenticator
	{
		private String username, password;

		public HttpAuthenticator(final String user, final String pass)
		{
			username = user;
			password = pass;
		}

		@Override
		protected PasswordAuthentication getPasswordAuthentication()
		{
			Log.info("Requesting Host  : " + getRequestingHost());
			Log.info("Requesting Port  : " + getRequestingPort());
			Log.info("Requesting Prompt : " + getRequestingPrompt());
			Log.info("Requesting Protocol: " + getRequestingProtocol());
			Log.info("Requesting Scheme : " + getRequestingScheme());
			Log.info("Requesting Site  : " + getRequestingSite());
			return new PasswordAuthentication(username, password.toCharArray());
		}
	}

	/**
	 * Gets an http connection paying attention to the Placebooks proxy configuration values
	 * requires a try/catch block as per url.openconnection()
	 * @return URLConnection A url connection as per url.openConnection - with or without proxy
	 */
	public static URLConnection getConnection(final URL url) throws IOException
	{
		URLConnection conn;
		if (PropertiesSingleton.get(CommunicationHelper.class.getClassLoader())
				.getProperty(PropertiesSingleton.PROXY_ACTIVE, "false").equalsIgnoreCase("true"))
		{
			Log.info("Using proxy: "
					+ PropertiesSingleton.get(CommunicationHelper.class.getClassLoader())
					.getProperty(PropertiesSingleton.PROXY_HOST, "")
					+ ":"
					+ PropertiesSingleton.get(CommunicationHelper.class.getClassLoader())
					.getProperty(PropertiesSingleton.PROXY_PORT, ""));
			final Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(PropertiesSingleton
					.get(CommunicationHelper.class.getClassLoader()).getProperty(PropertiesSingleton.PROXY_HOST, ""),
					Integer.parseInt(PropertiesSingleton.get(CommunicationHelper.class.getClassLoader())
							.getProperty(PropertiesSingleton.PROXY_PORT, ""))));
			conn = url.openConnection(proxy);
		}
		else
		{
			conn = url.openConnection();
		}
		return conn;
	}
}