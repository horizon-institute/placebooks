/**
 * 
 */
package placebooks.test;

import static org.junit.Assert.assertEquals;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import placebooks.controller.EMFSingleton;
import placebooks.controller.UserManager;
import placebooks.model.User;
import placebooks.services.EverytrailService;
import placebooks.services.model.EverytrailLoginResponse;

/**
 * @author pszmp
 *
 */
public class PlacebooksTestSuper
{
	protected static String test_user_email = "everytrail_test@live.co.uk";
	protected static String test_everytrail_username = "placebooks_everytrail_test";
	protected static String test_everytrail_password = "testPass1!";		
	protected static String test_user_id = "275539";
	protected static String test_trip_id = "1017230";

	protected static String test_peoplescollection_username = "placebooksTest";
	protected static String test_peoplescollection_password = "testPass1!";		
	
	
	protected static final Logger log = 
		Logger.getLogger(EverytrailServiceTest.class.getName());
	
	final EntityManager em = EMFSingleton.getEntityManager();

	/**
	 * Login the Placebooks test user
	 * @return User the logged in user details
	 */
	protected User logInPlacebooksTestUser()
	{
		//PlaceBookService.login(test_username, test_password, null);
		return UserManager.getCurrentUser(em);
	}
	
	/**
	 * Log in using the hard coded test user details and return the user ID on success.
	 * @return String test_user_id
	 */
	protected String logInEverytrailTestUser(EverytrailService service)
	{
		EverytrailLoginResponse loginResponse =  service.userLogin(test_everytrail_username, test_everytrail_password);
		assertEquals("success", loginResponse.getStatus());
		assertEquals(test_user_id, loginResponse.getValue());
		return loginResponse.getValue();
	}
	
	
	protected User getTestUser()
	{
		User testUser = UserManager.getUser(em, test_user_email);
		return testUser;
	}
	
	
}
