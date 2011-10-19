/**
 * 
 */
package placebooks.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import placebooks.services.PeoplesCollectionService;
import placebooks.services.model.PeoplesCollectionLoginResponse;

/**
 * @author pszmp
 *
 */
public class PeoplesCollectionHelperTest extends PlacebooksTestSuper {

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

}
