/**
 * 
 */
package placebooks.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.w3c.dom.Node;

import placebooks.controller.EverytrailHelper;
import placebooks.model.EverytrailLoginResponse;
import placebooks.model.EverytrailPicturesResponse;
import placebooks.model.EverytrailTracksResponse;
import placebooks.model.EverytrailTripsResponse;
import placebooks.model.EverytrailVideosResponse;

/**
 * @author pszmp
 *
 */
public class EverytrailHelperTest
{
	protected static String test_username = "placebooks_everytrail_test";
	protected static String test_password = "testPass1!";		
	protected static String test_user_id = "275539";
	protected static String test_trip_id = "1017230";

	protected static final Logger log = 
		Logger.getLogger(EverytrailHelperTest.class.getName());
	
	/**
	 * Test method for {@link placebooks.controller.EverytrailHelper#UserLogin(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testUserLogin()
	{	
		EverytrailLoginResponse loginResponse =  EverytrailHelper.UserLogin(test_username, test_password);
		assertEquals("success", loginResponse.getStatus());
		assertEquals(test_user_id, loginResponse.getValue());

		loginResponse =  EverytrailHelper.UserLogin(test_username, "fail-wrong-password");
		assertEquals("error", loginResponse.getStatus());
		if(loginResponse.getValue().equals("10"))
		{
			fail("Evertrail login error 10 - unknown error at their end.");
		}
		assertEquals("11", loginResponse.getValue());

		loginResponse =  EverytrailHelper.UserLogin("", "fail-no-user");
		assertEquals("error", loginResponse.getStatus());
		if(loginResponse.getValue().equals("10"))
		{
			fail("Evertrail login error 10 - unknown error at their end.");
		}
		assertEquals("11", loginResponse.getValue());
	}

	/**
	 * Log in using the hard coded test user details and return the user ID on success.
	 * @return String test_user_id
	 */
	protected String logInTestUser()
	{
		EverytrailLoginResponse loginResponse =  EverytrailHelper.UserLogin(test_username, test_password);
		assertEquals("success", loginResponse.getStatus());
		assertEquals(test_user_id, loginResponse.getValue());
		return loginResponse.getValue();
	}
	
	/**
	 * Test method for {@link placebooks.controller.EverytrailHelper#Pictures(java.lang.String)}.
	 */
	@Test
	public void testPicturesString()
	{
		String user_id = logInTestUser();
		EverytrailPicturesResponse picturesResponse = EverytrailHelper.Pictures(user_id);
		assertEquals("success", picturesResponse.getStatus());
		// As default should return 10 pictures starting at 0 and test user has about 4 pictures
		assertEquals(35, picturesResponse.getPictures().size());
	}

	/**
	 * Test method for {@link placebooks.controller.EverytrailHelper#Videos(java.lang.String)}.
	 */
	@Test
	public void testVideosString()
	{
		String user_id = logInTestUser();
		EverytrailVideosResponse videosResponse = EverytrailHelper.Videos(user_id, test_password, test_password);
		assertEquals("success", videosResponse.getStatus());
		// As default should return 0 videos starting at 0 and test user has about 4 pictures
		assertEquals(0, videosResponse.getVideos().size());
	}

	
	
	/**
	 * Test method for {@link placebooks.controller.EverytrailHelper#Pictures(java.lang.String, java.lang.String, java.lang.String)}.
	 */
/*	@Test
	public void testPicturesStringStringString()
	{
		String user_id = logInTestUser();
		EverytrailPicturesResponse picturesResponse = EverytrailHelper.Pictures(user_id, 2, 0);
		assertEquals("success", picturesResponse.getStatus());
		// As default should return 10 pictures starting at 0 and test user has about 40 pictures
		assertEquals(35, picturesResponse.getPictures().size());
	}
*/
	/**
	 * Test method for {@link placebooks.controller.EverytrailHelper#Tracks(java.lang.String)}.
	 */
	@Test
	public void testTracksString()
	{
		EverytrailTracksResponse tracksResponse = EverytrailHelper.Tracks("bad_trip_id");
		assertEquals("error", tracksResponse.getStatus());
		
		assertEquals("105", tracksResponse.getTracks().firstElement().getTextContent());
		assertEquals("Invalid Trip Id", tracksResponse.getTracks().lastElement().getTextContent());
		
		tracksResponse = EverytrailHelper.Tracks(test_trip_id);
		assertEquals("success", tracksResponse.getStatus());
		assertEquals(2, tracksResponse.getTracks().size());
	}

	/**
	 * Test method for {@link placebooks.controller.EverytrailHelper#Tracks(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testTracksStringStringString()
	{
		EverytrailTracksResponse tracksResponse = EverytrailHelper.Tracks(test_trip_id, test_username, test_password);
		assertEquals("success",tracksResponse.getStatus());
		assertEquals(2, tracksResponse.getTracks().size());
	}

	/**
	 * Test method for {@link placebooks.controller.EverytrailHelper#Trips(java.lang.String)}.
	 */
	@Test
	public void testTripsString()
	{
		EverytrailTripsResponse response = EverytrailHelper.Trips(test_user_id);
		//Just for debugging
		log.debug(response.getTrips());
		for(Node t : response.getTrips())
		{
			log.debug(t.getTextContent());
		}

		assertEquals("success",response.getStatus());
		assertEquals(1, response.getTrips().size());
	}

	/**
	 * Test method for {@link placebooks.controller.EverytrailHelper#Trips(java.lang.String, java.lang.String, java.lang.String, java.lang.Double, java.lang.Double, java.util.Date, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.Boolean)}.
	 */
	@Test
	public void testTripsStringStringStringDoubleDoubleDateStringStringStringStringBoolean()
	{
		DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
		Date date = new Date();
		try
		{
			date = dfm.parse("2010-01-01 00:00:00 +0000");
		}
		catch (java.text.ParseException e)
		{
			log.error(e.getMessage());
			fail(e.getMessage());
		}
		EverytrailTripsResponse response = EverytrailHelper.Trips(test_username, test_password, test_user_id, 52.531021, -4.055394, date, null, null, null, null, false);
		log.debug(response.getTrips());
		assertEquals("success",response.getStatus());
		assertEquals(1, response.getTrips().size());
	}
}
