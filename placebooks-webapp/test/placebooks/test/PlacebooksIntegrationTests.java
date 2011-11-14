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

import placebooks.controller.ItemFactory;
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
 *
 */
public class PlacebooksIntegrationTests extends PlacebooksTestSuper
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

	//@Test
	public void testToImageItemFromEverytrail()
	{
		User testUser = getTestUser();
		LoginDetails details = testUser.getLoginDetails("Everytrail");		

		EverytrailLoginResponse loginResponse =  everytrailService.userLogin(details.getUsername(), details.getPassword());
		assertEquals("success", loginResponse.getStatus());
		assertEquals(details.getUserID(), loginResponse.getValue());

		EverytrailPicturesResponse picturesResponse = everytrailService.pictures(loginResponse.getValue());

		HashMap<String, Node> pictures = picturesResponse.getPicturesMap();
		assertEquals(42, pictures.size());
		HashMap<String, String> pictureTrip = picturesResponse.getPictureTrips();
		HashMap<String, String> tripNames = picturesResponse.getTripNames();

		ImageItem imageItem = new ImageItem(testUser, null, null, null);
		Set<String> keys = pictures.keySet();
		String id = keys.iterator().next();
		Node n = pictures.get(id);
		String tripId = pictureTrip.get(id);
		String tripName =  tripNames.get(pictureTrip.get(id));
		ItemFactory.toImageItem(testUser, n, imageItem, tripId, tripName);
		//assertEquals(800, imageItem.getImage().getWidth());
		//assertEquals(479, imageItem.getImage().getHeight());

		//ItemFactory.toImageItem(testUser, n, imageItem, pictureTrip.get(id), tripNames.get(pictureTrip.get(id)));
		((IUpdateableExternal) imageItem).saveUpdatedItem();
	}

	//@Test
	public void testToGPSTraceItem()
	{
		//Log user in after getting details from db
		User testUser = UserManager.getUser(em, "everytrail_test@live.co.uk");
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
		try {
			ItemFactory.toGPSTraceItem(testUser, trackToUse, gpsTrace, "1", "Test");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		gpsTrace.saveUpdatedItem();
	}

	//@Test
	public void testToVideoItemFromYouTube()
	{
		User testUser = UserManager.getUser(em, "placebooks.test@gmail.com");
		LoginDetails details = testUser.getLoginDetails("YouTube");		

		VideoFeed feed = YouTubeHelper.UserVideos(details.getUsername());

		VideoItem videoItem = ItemFactory.toVideoItem(testUser, feed.getEntries().get(0));
		log.debug(videoItem.getSourceURL());
		//assertEquals(800, videoItem.getSourceURL());	
	}


	//@Test
	public void testGetEverytrailDataForAUser() throws Exception
	{
		User user = UserManager.getUser(em,  "everytrail_test@live.co.uk");
		EverytrailService service = new EverytrailService();
		service.sync(em, user, true);
	}

	//@Test
	public void testCreatePlaceBookWithItems() throws Exception
	{
		User u = getTestUser();
		PlaceBook p = new PlaceBook();
		p.setOwner(u);
		TextItem t = new TextItem();
		t.setOwner(u);
		t.setText("Test item text");
		p.addItem(t);
		em.persist(p);
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

	//@Test
	public void testGetPeoplesCollectionDataForAUser() throws Exception
	{
		User user = UserManager.getUser(em,  "markdavies_@hotmail.com");
		final PeoplesCollectionService service = new PeoplesCollectionService();
		service.sync(em, user, true);
	}

	@Test
	public void testSyncPeoplesCollectionData() throws Exception
	{
		User user = UserManager.getUser(em,  "everytrail_test@live.co.uk");
		final PeoplesCollectionService service = new PeoplesCollectionService();
		service.sync(em, user, true);
		
		//Now try replacing the data with some other items
		final LoginDetails oldLoginDetails = user.getLoginDetails(PeoplesCollectionService.SERVICE_NAME);
		user.remove(oldLoginDetails);
		User otherUser = UserManager.getUser(em,  "everytrail_test@live.co.uk");
		final LoginDetails newLoginDetails = otherUser.getLoginDetails(PeoplesCollectionService.SERVICE_NAME);
		user.add(newLoginDetails);
		service.sync(em, user, true);
	}
	
}
