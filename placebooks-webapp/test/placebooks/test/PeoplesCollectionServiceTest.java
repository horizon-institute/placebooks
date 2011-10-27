/**
 * 
 */
package placebooks.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import placebooks.services.PeoplesCollectionService;
import placebooks.services.model.PeoplesCollectionItemFeature;
import placebooks.services.model.PeoplesCollectionItemResponse;
import placebooks.services.model.PeoplesCollectionLoginResponse;
import placebooks.services.model.PeoplesCollectionTrailListItem;
import placebooks.services.model.PeoplesCollectionTrailResponse;
import placebooks.services.model.PeoplesCollectionTrailsResponse;

/**
 * @author pszmp
 *
 */
public class PeoplesCollectionServiceTest extends PlacebooksTestSuper {

	/**
	 * Test method for {@link placebooks.services.PeoplesCollectionService#Login(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testLogin() {
		PeoplesCollectionLoginResponse loginResponse =  PeoplesCollectionService.Login("fail-bad-user", "fail-bad-user");
		assertFalse("Peoples Collection login indicated valid with bad username/password", loginResponse.GetIsValid());
		assertNotNull("No failure reason given in login with bad user/password", loginResponse.GetReason());
		
		
		loginResponse =  PeoplesCollectionService.Login("", "fail-no-user");
		assertFalse("Peoples Collection login indicated valid with no username/password", loginResponse.GetIsValid());
		assertNotNull("No failure reason given in login with no user/password", loginResponse.GetReason());

		log.info("logging in as " + test_peoplescollection_username);
		loginResponse =  PeoplesCollectionService.Login(test_peoplescollection_username, test_peoplescollection_password);
		assertTrue("Login with correct username and password failed", loginResponse.GetIsValid());
		assertNull("Response data should have been null in successful login", loginResponse.GetReason());

	}

	/**
	 * Test method for {@link placebooks.controller.PeoplesCollectionHelper#TrailsByUser(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testUserTrailsResponse()
	{
		PeoplesCollectionTrailsResponse trailsResponse = PeoplesCollectionService.TrailsByUser(test_peoplescollection_username, test_peoplescollection_password);
		assertTrue("Login with correct username and password failed", trailsResponse.GetAuthenticationResponse().GetIsValid());
		log.debug("Number of trails:" + trailsResponse.GetMyTrails().size());
		for(PeoplesCollectionTrailListItem trail : trailsResponse.GetMyTrails())
		{
			log.debug("My trail #:" + trail.GetId() + " " + trail.GetProperties());
		}
		log.debug("Number of favourite trails:" + trailsResponse.GetMyFavouriteTrails().size());
		for(PeoplesCollectionTrailListItem trail : trailsResponse.GetMyFavouriteTrails())
		{
			log.debug("Favourite trail #:" + trail.GetId() + " " + trail.GetProperties().GetTitle());
		}
	}
	
	
	/**
	 * Test method for {@link placebooks.controller.PeoplesCollectionHelper#Trail(int)}.
	 */
	@Test
	public void testTrailResponse()
	{
		PeoplesCollectionTrailsResponse trailsResponse = PeoplesCollectionService.TrailsByUser(test_peoplescollection_username, test_peoplescollection_password);
		assertTrue("Login with correct username and password failed", trailsResponse.GetAuthenticationResponse().GetIsValid());
		log.debug("Number of trails:" + trailsResponse.GetMyTrails().size());
		PeoplesCollectionTrailListItem trail = trailsResponse.GetMyTrails().iterator().next();

		log.debug("Getting coordinates for trail #" + trail.GetProperties().GetId() + " " + trail.GetProperties().GetTitle());
		PeoplesCollectionTrailResponse response = PeoplesCollectionService.Trail(trail.GetProperties().GetId());
		log.debug("Got title: "+ response.GetProperties().GetTitle());
		assertEquals("Title of trail details doesn't match listed details.", response.GetProperties().GetTitle(), trail.GetProperties().GetTitle());
	}
	
	/**
	 * Test method for {@link placebooks.controller.PeoplesCollectionHelper#TrailsItems(int)}.
	 */
	@Test
	public void testTrailItemsResponse()
	{
		PeoplesCollectionTrailsResponse trailsResponse = PeoplesCollectionService.TrailsByUser(test_peoplescollection_username, test_peoplescollection_password);
		assertTrue("Login with correct username and password failed", trailsResponse.GetAuthenticationResponse().GetIsValid());
		log.debug("Number of trails:" + trailsResponse.GetMyTrails().size());
		PeoplesCollectionTrailListItem trail = trailsResponse.GetMyTrails().iterator().next();
		log.debug("Getting items for trail #" + trail.GetId());
		
		PeoplesCollectionTrailResponse trailDetails = PeoplesCollectionService.Trail(trail.GetProperties().GetId());
		log.debug("Items for trail #" + trail.GetProperties().GetId() + " = " + trailDetails.GetProperties().GetItems().length);
		assertFalse("No trail items returned", (trailDetails.GetProperties().GetItems().length==0));
		
		for(int itemId : trailDetails.GetProperties().GetItems())
		{
			PeoplesCollectionItemResponse response = PeoplesCollectionService.Item(itemId);
			log.debug("Number of objects:" + response.GetTotalObjects());
			assertFalse("No features returned for get trail items", (response.GetTotalObjects()==0));
			for(PeoplesCollectionItemFeature feature : response.getFeatures()) 
			{
				log.debug("Item :" + feature.GetProperties().GetTitle() + " id: " +  + feature.GetProperties().GetId());
			}
		}
	}

}
