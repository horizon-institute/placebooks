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
		// InitializeDatabase.main(null);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception
	{
	}

	// @Test
	public void testCreatePlaceBookWithItems() throws Exception
	{
		final User u = getTestUser();
		final PlaceBook p = new PlaceBook();
		p.setOwner(u);
		final TextItem t = new TextItem();
		t.setOwner(u);
		t.setText("Test item text");
		p.addItem(t);
		em.persist(p);
	}

	// @Test
	public void testEveryTrailTripPackage() throws Exception
	{
		// Log user in after getting details from db
		final User testUser = getTestUser();
		final LoginDetails details = testUser.getLoginDetails("Everytrail");

		// Check login is ok then use the userid
		final EverytrailLoginResponse loginResponse = everytrailService.userLogin(	details.getUsername(),
																					details.getPassword());
		assertEquals("success", loginResponse.getStatus());
		assertEquals(details.getUserID(), loginResponse.getValue());

		final EverytrailTripsResponse tracksResponse = everytrailService.trips("1017230");
		assertEquals("success", tracksResponse.getStatus());
		assertEquals(2, tracksResponse.getTrips().size());

		final GPSTraceItem gpsTrace = new GPSTraceItem(testUser, null, null);

		final Node trackToUse = tracksResponse.getTrips().lastElement();
		ItemFactory.toGPSTraceItem(testUser, trackToUse, gpsTrace, "1", "Test");
		gpsTrace.saveUpdatedItem();

		final User u = getTestUser();
		final PlaceBook p = new PlaceBook();
		p.setOwner(u);
		final TextItem t = new TextItem();
		t.setOwner(u);
		t.setText("Test item text");
		p.addItem(t);
		em.persist(p);
		p.addItem(gpsTrace);
		em.persist(p);
	}

	// @Test
	public void testGetEverytrailDataForAUser() throws Exception
	{
		final User user = UserManager.getUser(em, "everytrail_test@live.co.uk");
		final EverytrailService service = new EverytrailService();
		service.sync(em, user, true, -4.051070, 52.482382, 0.1);
	}

	// @Test
	public void testGetPeoplesCollectionDataForAUser() throws Exception
	{
		final User user = UserManager.getUser(em, "markdavies_@hotmail.com");
		final PeoplesCollectionService service = new PeoplesCollectionService();
		service.sync(	em, user, true, Double.parseDouble(PropertiesSingleton.get(	CommunicationHelper.class
																							.getClassLoader())
								.getProperty(PropertiesSingleton.IDEN_SEARCH_LON, "0")),
						Double.parseDouble(PropertiesSingleton.get(CommunicationHelper.class.getClassLoader())
								.getProperty(PropertiesSingleton.IDEN_SEARCH_LAT, "0")), Double
								.parseDouble(PropertiesSingleton.get(CommunicationHelper.class.getClassLoader())
										.getProperty(PropertiesSingleton.IDEN_SEARCH_RADIUS, "0")));
	}

	@Test
	public void testSyncPeoplesCollectionData() throws Exception
	{
		final User user = UserManager.getUser(em, "everytrail_test@live.co.uk");
		final PeoplesCollectionService service = new PeoplesCollectionService();
		service.sync(	em, user, true, Double.parseDouble(PropertiesSingleton.get(	CommunicationHelper.class
																							.getClassLoader())
								.getProperty(PropertiesSingleton.IDEN_SEARCH_LON, "0")),
						Double.parseDouble(PropertiesSingleton.get(CommunicationHelper.class.getClassLoader())
								.getProperty(PropertiesSingleton.IDEN_SEARCH_LAT, "0")), Double
								.parseDouble(PropertiesSingleton.get(CommunicationHelper.class.getClassLoader())
										.getProperty(PropertiesSingleton.IDEN_SEARCH_RADIUS, "0")));

		// Now try replacing the data with some other items
		final LoginDetails oldLoginDetails = user.getLoginDetails(PeoplesCollectionService.SERVICE_INFO.getName());
		user.remove(oldLoginDetails);
		final User otherUser = UserManager.getUser(em, "azc@Cs.Nott.AC.UK");
		final LoginDetails newLoginDetails = otherUser.getLoginDetails(PeoplesCollectionService.SERVICE_INFO.getName());
		user.add(newLoginDetails);
		service.sync(	em, user, true, Double.parseDouble(PropertiesSingleton.get(	CommunicationHelper.class
																							.getClassLoader())
								.getProperty(PropertiesSingleton.IDEN_SEARCH_LON, "0")),
						Double.parseDouble(PropertiesSingleton.get(CommunicationHelper.class.getClassLoader())
								.getProperty(PropertiesSingleton.IDEN_SEARCH_LAT, "0")), Double
								.parseDouble(PropertiesSingleton.get(CommunicationHelper.class.getClassLoader())
										.getProperty(PropertiesSingleton.IDEN_SEARCH_RADIUS, "0")));
	}

	// @Test
	public void testToGPSTraceItem()
	{
		// Log user in after getting details from db
		final User testUser = UserManager.getUser(em, "everytrail_test@live.co.uk");
		final LoginDetails details = testUser.getLoginDetails("Everytrail");

		// Check login is ok then use the userid
		final EverytrailLoginResponse loginResponse = everytrailService.userLogin(	details.getUsername(),
																					details.getPassword());
		assertEquals("success", loginResponse.getStatus());
		assertEquals(details.getUserID(), loginResponse.getValue());

		final EverytrailTripsResponse tracksResponse = everytrailService.trips("1017230");
		assertEquals("success", tracksResponse.getStatus());
		assertEquals(2, tracksResponse.getTrips().size());

		final GPSTraceItem gpsTrace = new GPSTraceItem(testUser, null, null);

		final Node trackToUse = tracksResponse.getTrips().lastElement();
		try
		{
			ItemFactory.toGPSTraceItem(testUser, trackToUse, gpsTrace, "1", "Test");
		}
		catch (final Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		gpsTrace.saveUpdatedItem();
	}

	// @Test
	public void testToImageItemFromEverytrail()
	{
		final User testUser = getTestUser();
		final LoginDetails details = testUser.getLoginDetails("Everytrail");

		final EverytrailLoginResponse loginResponse = everytrailService.userLogin(	details.getUsername(),
																					details.getPassword());
		assertEquals("success", loginResponse.getStatus());
		assertEquals(details.getUserID(), loginResponse.getValue());

		final EverytrailPicturesResponse picturesResponse = everytrailService.pictures(loginResponse.getValue());

		final HashMap<String, Node> pictures = picturesResponse.getPicturesMap();
		assertEquals(42, pictures.size());
		final HashMap<String, String> pictureTrip = picturesResponse.getPictureTrips();
		final HashMap<String, String> tripNames = picturesResponse.getTripNames();

		final ImageItem imageItem = new ImageItem(testUser, null, null, null);
		final Set<String> keys = pictures.keySet();
		final String id = keys.iterator().next();
		final Node n = pictures.get(id);
		final String tripId = pictureTrip.get(id);
		final String tripName = tripNames.get(pictureTrip.get(id));
		ItemFactory.toImageItem(testUser, n, imageItem, tripId, tripName);
		// assertEquals(800, imageItem.getImage().getWidth());
		// assertEquals(479, imageItem.getImage().getHeight());

		// ItemFactory.toImageItem(testUser, n, imageItem, pictureTrip.get(id),
		// tripNames.get(pictureTrip.get(id)));
		imageItem.saveUpdatedItem();
	}

	// @Test
	public void testToVideoItemFromYouTube()
	{
		final User testUser = UserManager.getUser(em, "placebooks.test@gmail.com");
		final LoginDetails details = testUser.getLoginDetails("YouTube");

		final VideoFeed feed = YouTubeHelper.UserVideos(details.getUsername());

		final VideoItem videoItem = ItemFactory.toVideoItem(testUser, feed.getEntries().get(0));
		log.debug(videoItem.getSourceURL());
		// assertEquals(800, videoItem.getSourceURL());
	}

}
