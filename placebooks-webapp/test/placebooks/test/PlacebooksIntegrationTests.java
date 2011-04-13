/**
 * 
 */
package placebooks.test;

import static org.junit.Assert.*;

import javax.jdo.PersistenceManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import placebooks.client.model.User;
import placebooks.controller.PMFSingleton;
import placebooks.controller.UserManager;
import placebooks.utils.*;

/**
 * @author pszmp
 *
 */
public class PlacebooksIntegrationTests
{
	final PersistenceManager pm = PMFSingleton.getPersistenceManager();
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		// Populate the database with test data
		InitializeDatabase.main(null);		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void testUserLogin()
	{
		placebooks.model.User testUser = UserManager.getUser(pm, "everytrail_test@live.co.uk");
		assertEquals(testUser.getEmail(), "everytrail_test@live.co.uk");
		assertEquals(testUser.getName(), "Test User");
	}
	
	
	@Test
	public void testImageItemFromEverytrail()
	{
		
		
	}
}
