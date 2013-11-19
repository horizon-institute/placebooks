/**
 * 
 */
package org.placebooks.test;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.placebooks.controller.CommunicationHelper;
import org.placebooks.controller.ItemFactory;
import org.placebooks.controller.PropertiesSingleton;
import org.placebooks.controller.UserManager;
import org.placebooks.model.GPSTraceItem;
import org.placebooks.model.LoginDetails;
import org.placebooks.model.PlaceBook;
import org.placebooks.model.TextItem;
import org.placebooks.model.User;
import org.placebooks.services.EverytrailService;
import org.placebooks.services.model.EverytrailLoginResponse;
import org.placebooks.services.model.EverytrailTripsResponse;
import org.w3c.dom.Node;

/**
 * @author pszmp Testing Everytrail Sync
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

	@Test
	public void testGetEverytrailDataForAUser() throws Exception
	{
		final User user = UserManager.getUser(em, "everytrail_test@live.co.uk");
		final EverytrailService service = new EverytrailService();
		service.sync(	em, user, true, Double.parseDouble(PropertiesSingleton.get(	CommunicationHelper.class
																							.getClassLoader())
								.getProperty(PropertiesSingleton.IDEN_SEARCH_LON, "0")),
						Double.parseDouble(PropertiesSingleton.get(CommunicationHelper.class.getClassLoader())
								.getProperty(PropertiesSingleton.IDEN_SEARCH_LAT, "0")), Double
								.parseDouble(PropertiesSingleton.get(CommunicationHelper.class.getClassLoader())
										.getProperty(PropertiesSingleton.IDEN_SEARCH_RADIUS, "0")));

		// Now try replacing the data with some other items
		final LoginDetails oldLoginDetails = user.getLoginDetails(EverytrailService.SERVICE_INFO.getName());
		user.remove(oldLoginDetails);
		final User otherUser = UserManager.getUser(em, "placebooks.test@gmail.com");
		final LoginDetails newLoginDetails = otherUser.getLoginDetails(EverytrailService.SERVICE_INFO.getName());
		user.add(newLoginDetails);
		log.debug(user.getLoginDetails(EverytrailService.SERVICE_INFO.getName()).getUsername() + " "
				+ user.getLoginDetails(EverytrailService.SERVICE_INFO.getName()).getPassword());
		service.sync(em, user, true, -4.051070, 52.482382, 0.1);
	}

}
