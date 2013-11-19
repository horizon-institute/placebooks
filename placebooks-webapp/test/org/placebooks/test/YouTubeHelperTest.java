/**
 * 
 */
package org.placebooks.test;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.placebooks.controller.YouTubeHelper;

import com.google.gdata.data.youtube.VideoFeed;
import com.google.gdata.util.AuthenticationException;

/**
 * @author pszmp
 *
 */
public class YouTubeHelperTest
{
	protected static String test_username = "placebooksTest";
	protected static String test_password = "testPass1!";		

	protected static final Logger log = 
		Logger.getLogger(EverytrailServiceTest.class.getName());
	

	/**
	 * Test method for {@link org.placebooks.controller.YouTubeHelper#HelloWorld(java.lang.String)}.
	 */
	@Test
	public final void testUserVideos()
	{
		VideoFeed feed = YouTubeHelper.UserVideos(test_username);
		assertEquals(1, feed.getEntries().size());
		try
		{
			feed = YouTubeHelper.UserVideos(test_username, test_password);
		}
		catch (AuthenticationException e)
		{
			log.error(e.getMessage());
			Assert.fail(e.getMessage());
		}
		assertEquals(2, feed.getEntries().size());		
	}

	/**
	 * Test method for {@link org.placebooks.controller.YouTubeHelper#UserFavorites(java.lang.String)}.
	 */
	@Test
	public final void testUserFavorites()
	{
		VideoFeed feed = YouTubeHelper.UserFavorites(test_username);
		assertEquals(3, feed.getEntries().size());
		try
		{
			feed = YouTubeHelper.UserFavorites(test_username, test_password);
		} catch (AuthenticationException e)
		{
			log.error(e.getMessage());
			Assert.fail(e.getMessage());
		}
		assertEquals(3, feed.getEntries().size());
	}
	
}
