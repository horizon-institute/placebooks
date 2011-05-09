package placebooks.test;

import java.net.URL;

import javax.jdo.PersistenceManager;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import placebooks.controller.PMFSingleton;
import placebooks.controller.UserManager;
import placebooks.model.PlaceBook;
import placebooks.model.TextItem;
import placebooks.model.User;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;

public class JSONTest
{
	@Test
	public void placebookTextItemJsonTest() throws Exception
	{
		final PersistenceManager manager = PMFSingleton.getPersistenceManager();

		try
		{

			final User owner = UserManager.getUser(manager, "ktg@cs.nott.ac.uk");
			final Geometry geometry = new WKTReader().read("POINT(52.5189367988799 -4.04983520507812)");

			final ObjectMapper mapper = new ObjectMapper();
			
			PlaceBook placebook = new PlaceBook(owner, geometry);

			placebook.addItem(new TextItem(owner, geometry, new URL("http://www.google.com"), "Test text string"));
			placebook.addItem(new TextItem(owner, geometry, new URL("http://www.google.com"), "Test 2"));
			
			System.out.println(mapper.writeValueAsString(placebook));
		}
		finally
		{
			if (manager.currentTransaction().isActive())
			{
				manager.currentTransaction().rollback();
			}

			manager.close();
		}
	}
}
