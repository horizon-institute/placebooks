/**
 * 
 */
package placebooks.controller;

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
	private static final Logger log = 
		Logger.getLogger(YouTubeHelper.class.getName());

	private static YouTubeService getYoutubeService()
	{
		// Create a new YouTube service
		YouTubeService ytService = new YouTubeService("Placebooks");
		//Using ssl bypasses UoN proxy
		ytService.useSsl();
		return ytService;
	}
	
	private static VideoFeed getFeedForURLFinal(YouTubeService service, String urlString)
	{
		VideoFeed feed = null;
		try
		{
			// Get a list of all entries
			URL metafeedUrl = new URL(urlString);
			log.info("Getting video entries from: " + urlString);
			feed = service.getFeed(metafeedUrl, VideoFeed.class);
			List<VideoEntry> entries = feed.getEntries();
			for(int i=0; i<entries.size(); i++) {
				VideoEntry entry = entries.get(i);
				System.out.println("\t" + entry.getTitle().getPlainText());
			}
			log.debug("Total Entries: "+entries.size());
		}
		catch(AuthenticationException e) {
			log.debug(e.getMessage());
			log.error(e.getStackTrace().toString());
		}
		catch(ServiceException e) {
			log.debug(e.getMessage());
			log.error(e.getStackTrace().toString());
		}
		catch(IOException e) {
			log.debug(e.getMessage());
			log.error(e.getStackTrace().toString());
		}
		return feed; 
	}
	
	
	private static VideoFeed getFeedForURL(String urlString)
	{
		YouTubeService service = YouTubeHelper.getYoutubeService();
		return getFeedForURLFinal(service, urlString);
	}
	
	/**
	 * Get the VideoFeed for a given YouTube gdata API call with a username and password supplied
	 * @param string The URL for the YouTube data gfeed
	 * @param userName
	 * @param password
	 * @return VideoFeed
	 */
	private static VideoFeed getFeedForURL(String urlString, String userName, String password) 
	throws AuthenticationException
	{
		YouTubeService service = YouTubeHelper.getYoutubeService();
		service.setUserCredentials(userName, password);
		return getFeedForURLFinal(service, urlString);
	}
	
	/**
	 * Gets a users publicly published  videos
	 * @param userName
	 * @return
	 */
	public static VideoFeed UserVideos(String userName)
	{
		return getFeedForURL("http://gdata.youtube.com/feeds/api/users/"+ userName + "/uploads");
	}
	
	/**
	 * Gets the list of user's videos including private videos 
	 * @param userName
	 * @param password
	 * @return
	 * @throws AuthenticationException
	 */
	public static VideoFeed UserVideos(String userName, String password) throws AuthenticationException
	{
		return getFeedForURL("http://gdata.youtube.com/feeds/api/users/"+ userName + "/uploads", userName, password);
	}

	/**
	 * Get's a user's favorites after logging in - this shouldn't make a different as they are public
	 * @param userName
	 * @param password
	 * @return
	 * @throws AuthenticationException
	 */
	public static VideoFeed UserFavorites(String userName, String password) throws AuthenticationException
	{		
		return getFeedForURL("http://gdata.youtube.com/feeds/api/users/"+ userName + "/favorites", userName, password);
	}

	/**
	 * Gets a list of a user's favourite videos as posted on their profile
	 * @param userName
	 * @return VideoFeed
	 */
	public static VideoFeed UserFavorites(String userName)
	{
		return getFeedForURL("http://gdata.youtube.com/feeds/api/users/"+ userName + "/favorites");
	}

	
}