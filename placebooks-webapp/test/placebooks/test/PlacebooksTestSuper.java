/**
 * 
 */
package placebooks.test;

import static org.junit.Assert.assertEquals;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;

import placebooks.client.PlaceBookService;
import placebooks.controller.EMFSingleton;
import placebooks.controller.EverytrailHelper;
import placebooks.controller.UserManager;
import placebooks.model.EverytrailLoginResponse;
import placebooks.model.User;

/**
 * @author pszmp
 *
 */
public class PlacebooksTestSuper
{
	protected static String test_user_email = "everytrail_test@live.co.uk";
	protected static String test_username = "placebooks_everytrail_test";
	protected static String test_password = "testPass1!";		
	protected static String test_user_id = "275539";
	protected static String test_trip_id = "1017230";

	protected static final Logger log = 
		Logger.getLogger(EverytrailHelperTest.class.getName());
	
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
	protected String logInEverytrailTestUser()
	{
		EverytrailLoginResponse loginResponse =  EverytrailHelper.UserLogin(test_username, test_password);
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
