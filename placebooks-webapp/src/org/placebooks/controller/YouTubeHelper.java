/**
 * 
 */
package org.placebooks.controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.gdata.client.youtube.YouTubeService;
import com.google.gdata.data.youtube.VideoEntry;
import com.google.gdata.data.youtube.VideoFeed;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

/**
 * @author pszmp
 * 
 */
public class YouTubeHelper
{
	private static final Logger log = Logger.getLogger(YouTubeHelper.class.getName());

	/**
	 * Gets a list of a user's favourite videos as posted on their profile
	 * 
	 * @param userName
	 * @return VideoFeed
	 */
	public static VideoFeed UserFavorites(final String userName)
	{
		return getFeedForURL("http://gdata.youtube.com/feeds/api/users/" + userName + "/favorites");
	}

	/**
	 * Get's a user's favorites after logging in - this shouldn't make a different as they are
	 * public
	 * 
	 * @param userName
	 * @param password
	 * @return
	 * @throws AuthenticationException
	 */
	public static VideoFeed UserFavorites(final String userName, final String password) throws AuthenticationException
	{
		return getFeedForURL("http://gdata.youtube.com/feeds/api/users/" + userName + "/favorites", userName, password);
	}

	/**
	 * Gets a users publicly published videos
	 * 
	 * @param userName
	 * @return
	 */
	public static VideoFeed UserVideos(final String userName)
	{
		return getFeedForURL("http://gdata.youtube.com/feeds/api/users/" + userName + "/uploads");
	}

	/**
	 * Gets the list of user's videos including private videos
	 * 
	 * @param userName
	 * @param password
	 * @return
	 * @throws AuthenticationException
	 */
	public static VideoFeed UserVideos(final String userName, final String password) throws AuthenticationException
	{
		return getFeedForURL("http://gdata.youtube.com/feeds/api/users/" + userName + "/uploads", userName, password);
	}

	private static VideoFeed getFeedForURL(final String urlString)
	{
		final YouTubeService service = YouTubeHelper.getYoutubeService();
		return getFeedForURLFinal(service, urlString);
	}

	/**
	 * Get the VideoFeed for a given YouTube gdata API call with a username and password supplied
	 * 
	 * @param string
	 *            The URL for the YouTube data gfeed
	 * @param userName
	 * @param password
	 * @return VideoFeed
	 */
	private static VideoFeed getFeedForURL(final String urlString, final String userName, final String password)
			throws AuthenticationException
	{
		final YouTubeService service = YouTubeHelper.getYoutubeService();
		service.setUserCredentials(userName, password);
		return getFeedForURLFinal(service, urlString);
	}

	private static VideoFeed getFeedForURLFinal(final YouTubeService service, final String urlString)
	{
		VideoFeed feed = null;
		try
		{
			// Get a list of all entries
			final URL metafeedUrl = new URL(urlString);
			log.info("Getting video entries from: " + urlString);
			feed = service.getFeed(metafeedUrl, VideoFeed.class);
			final List<VideoEntry> entries = feed.getEntries();
			for (int i = 0; i < entries.size(); i++)
			{
				final VideoEntry entry = entries.get(i);
				System.out.println("\t" + entry.getTitle().getPlainText());
			}
			log.debug("Total Entries: " + entries.size());
		}
		catch (final AuthenticationException e)
		{
			log.debug(e.getMessage());
			log.error(e.getStackTrace().toString());
		}
		catch (final ServiceException e)
		{
			log.debug(e.getMessage());
			log.error(e.getStackTrace().toString());
		}
		catch (final IOException e)
		{
			log.debug(e.getMessage());
			log.error(e.getStackTrace().toString());
		}
		return feed;
	}

	private static YouTubeService getYoutubeService()
	{
		// Create a new YouTube service
		final YouTubeService ytService = new YouTubeService("Placebooks");
		// Using ssl bypasses UoN proxy
		ytService.useSsl();
		return ytService;
	}

}