/**
 * 
 */
package placebooks.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import placebooks.services.EverytrailService;
import placebooks.services.model.EverytrailLoginResponse;

/**
 * @author pszmp
 *
 */
public class EverytrailServiceTest extends PlacebooksTestSuper
{
	/**
	 * Test method for {@link placebooks.controller.EverytrailHelper#UserLogin(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testUserLogin()
	{	
		final EverytrailService service = new EverytrailService();
		EverytrailLoginResponse loginResponse = service.userLogin(test_everytrail_username, test_everytrail_password);
		assertEquals("success", loginResponse.getStatus());
		assertEquals(test_user_id, loginResponse.getValue());

		loginResponse =  service.userLogin(test_everytrail_username, "fail-wrong-password");
		assertEquals("error", loginResponse.getStatus());
		if(loginResponse.getValue().equals("10"))
		{
			fail("Evertrail login error 10 - unknown error at their end.");
		}
		assertEquals("11", loginResponse.getValue());

		loginResponse =  service.userLogin("", "fail-no-user");
		assertEquals("error", loginResponse.getStatus());
		if(loginResponse.getValue().equals("10"))
		{
			fail("Evertrail login error 10 - unknown error at their end.");
		}
		assertEquals("11", loginResponse.getValue());
	}


	
//	/**
//	 * Test method for {@link placebooks.controller.EverytrailHelper#Pictures(java.lang.String)}.
//	 */
//	@Test
//	public void testPicturesString()
//	{
//		final EverytrailService service = new EverytrailService();		
//		String user_id = logInEverytrailTestUser(service);
//		EverytrailPicturesResponse picturesResponse = service.pictures(user_id);
//		assertEquals("success", picturesResponse.getStatus());
//		// As default should return 10 pictures starting at 0 and test user has about 4 pictures
//		assertEquals(36, picturesResponse.getPicturesMap().size());
//	}

//	/**
//	 * Test method for {@link placebooks.controller.EverytrailHelper#Videos(java.lang.String)}.
//	 */
//	@Test
//	public void testVideosString()
//	{
//		final EverytrailService service = new EverytrailService();		
//		String user_id = logInEverytrailTestUser(service);
//		EverytrailVideosResponse videosResponse = service.videos(user_id, test_everytrail_password, test_everytrail_password);
//		assertEquals("success", videosResponse.getStatus());
//		// As default should return 0 videos starting at 0 and test user has about 4 pictures
//		assertEquals(0, videosResponse.getVideos().size());
//	}

	
	
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
//	/**
//	 * Test method for {@link placebooks.controller.EverytrailHelper#Tracks(java.lang.String)}.
//	 */
//	@Test
//	public void testTracksString()
//	{
//		EverytrailTracksResponse tracksResponse = EverytrailHelper.Tracks("bad_trip_id");
//		assertEquals("error", tracksResponse.getStatus());
//		
//		assertEquals("105", tracksResponse.getTracks().firstElement().getTextContent());
//		assertEquals("Invalid Trip Id", tracksResponse.getTracks().lastElement().getTextContent());
//		
//		tracksResponse = EverytrailHelper.Tracks(test_trip_id);
//		assertEquals("success", tracksResponse.getStatus());
//		assertEquals(2, tracksResponse.getTracks().size());
//	}
//
//	/**
//	 * Test method for {@link placebooks.controller.EverytrailHelper#Tracks(java.lang.String, java.lang.String, java.lang.String)}.
//	 */
//	@Test
//	public void testTracksStringStringString()
//	{
//		EverytrailTracksResponse tracksResponse = EverytrailHelper.Tracks(test_trip_id, test_everytrail_username, test_everytrail_password);
//		assertEquals("success",tracksResponse.getStatus());
//		assertEquals(2, tracksResponse.getTracks().size());
//	}
//
//	/**
//	 * Test method for {@link placebooks.controller.EverytrailHelper#Trips(java.lang.String)}.
//	 */
//	@Test
//	public void testTripsString()
//	{
//		EverytrailTripsResponse response = EverytrailHelper.Trips("273791");
//		//Just for debugging
//		log.debug("Trips got: " + response.getTrips().size());
//		int count = 0;
//		for(Node t : response.getTrips())
//		{
//			count++;
//			log.debug("Trip [" + count + "]: "+  t.getTextContent());
//		}
//
//		assertEquals("success",response.getStatus());
//		assertEquals(2, response.getTrips().size());
//	}
//
//	/**
//	 * Test method for {@link placebooks.controller.EverytrailHelper#Trips(java.lang.String, java.lang.String, java.lang.String, java.lang.Double, java.lang.Double, java.util.Date, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.Boolean)}.
//	 */
//	@Test
//	public void testTripsStringStringStringDoubleDoubleDateStringStringStringStringBoolean()
//	{
//		DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
//		Date date = new Date();
//		try
//		{
//			date = dfm.parse("2010-01-01 00:00:00 +0000");
//		}
//		catch (java.text.ParseException e)
//		{
//			log.error(e.getMessage());
//			fail(e.getMessage());
//		}
//		EverytrailTripsResponse response = EverytrailHelper.Trips(test_everytrail_username, test_everytrail_password, test_user_id, 52.531021, -4.055394, date, null, null, null, null, false);
//		log.debug(response.getTrips());
//		assertEquals("success",response.getStatus());
//		assertEquals(4, response.getTrips().size());
//	}
}
