/**
 * 
 */
package placebooks.test;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import placebooks.model.PlaceBook;
import placebooks.model.User;

/**
 * @author pszmp
 *
 */
public class PlaceBookTest extends PlacebooksTestSuper
{

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception
	{
	}

	@Test void testCreatePlaceBook() throws Exception
	{
		User u = logInPlacebooksTestUser();
		PlaceBook p = new PlaceBook();
		p.setOwner(u);
		em.persist(p);
	}
}
