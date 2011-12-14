/**
 * 
 */
package placebooks.test;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Node;

import placebooks.controller.CommunicationHelper;
import placebooks.controller.ItemFactory;
import placebooks.controller.PropertiesSingleton;
import placebooks.controller.UserManager;
import placebooks.controller.YouTubeHelper;
import placebooks.model.GPSTraceItem;
import placebooks.model.IUpdateableExternal;
import placebooks.model.ImageItem;
import placebooks.model.LoginDetails;
import placebooks.model.PlaceBook;
import placebooks.model.TextItem;
import placebooks.model.User;
import placebooks.model.VideoItem;
import placebooks.services.EverytrailService;
import placebooks.services.PeoplesCollectionService;
import placebooks.services.model.EverytrailLoginResponse;
import placebooks.services.model.EverytrailPicturesResponse;
import placebooks.services.model.EverytrailTripsResponse;

import com.google.gdata.data.youtube.VideoFeed;

/**
 * @author pszmp
 * Testing Everytrail Sync
 *
 */
public class EverytrailServiceIntegrationTest extends PlacebooksTestSuper
{

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		// Populate the database with test data
		//InitializeDatabase.main(null);	
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception
	{
	}



	@Test
	public void testGetEverytrailDataForAUser() throws Exception
	{
		User user = UserManager.getUser(em,  "everytrail_test@live.co.uk");
		EverytrailService service = new EverytrailService();
		service.sync(em, user, true, Double.parseDouble(PropertiesSingleton.get(CommunicationHelper.class.getClassLoader()).getProperty(PropertiesSingleton.IDEN_SEARCH_LON, "0")),
				Double.parseDouble(PropertiesSingleton.get(CommunicationHelper.class.getClassLoader()).getProperty(PropertiesSingleton.IDEN_SEARCH_LAT, "0")),
				Double.parseDouble(PropertiesSingleton.get(CommunicationHelper.class.getClassLoader()).getProperty(PropertiesSingleton.IDEN_SEARCH_RADIUS, "0"))
		);
		
		//Now try replacing the data with some other items
		final LoginDetails oldLoginDetails = user.getLoginDetails(EverytrailService.SERVICE_NAME);
		user.remove(oldLoginDetails);
		User otherUser = UserManager.getUser(em,  "placebooks.test@gmail.com");
		final LoginDetails newLoginDetails = otherUser.getLoginDetails(EverytrailService.SERVICE_NAME);
		user.add(newLoginDetails);
		log.debug(user.getLoginDetails(EverytrailService.SERVICE_NAME).getUsername() + " " + user.getLoginDetails(EverytrailService.SERVICE_NAME).getPassword());
		service.sync(em, user, true, -4.051070, 52.482382, 0.1);
	}

	
	
	//@Test
	public void testEveryTrailTripPackage() throws Exception
	{
		//Log user in after getting details from db
		User testUser = getTestUser();
		LoginDetails details = testUser.getLoginDetails("Everytrail");		

		// Check login is ok then use the userid 
		EverytrailLoginResponse loginResponse =  everytrailService.userLogin(details.getUsername(), details.getPassword());
		assertEquals("success", loginResponse.getStatus());
		assertEquals(details.getUserID(), loginResponse.getValue());



		EverytrailTripsResponse tracksResponse = everytrailService.trips("1017230");
		assertEquals("success", tracksResponse.getStatus());
		assertEquals(2, tracksResponse.getTrips().size());

		GPSTraceItem gpsTrace = new GPSTraceItem(testUser, null, null);

		Node trackToUse = tracksResponse.getTrips().lastElement();
		ItemFactory.toGPSTraceItem(testUser, trackToUse, gpsTrace, "1", "Test");
		gpsTrace.saveUpdatedItem();
		
		User u = getTestUser();
		PlaceBook p = new PlaceBook();
		p.setOwner(u);
		TextItem t = new TextItem();
		t.setOwner(u);
		t.setText("Test item text");
		p.addItem(t);
		em.persist(p);
		p.addItem(gpsTrace);
		em.persist(p);
	}


	
}
