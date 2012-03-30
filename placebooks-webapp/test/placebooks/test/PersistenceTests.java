package placebooks.test;

import java.net.URL;
import java.util.Map.Entry;

import javax.persistence.EntityManager;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;

import placebooks.controller.EMFSingleton;
import placebooks.controller.UserManager;
import placebooks.model.PlaceBook;
import placebooks.model.PlaceBookItem;
import placebooks.model.TextItem;
import placebooks.model.User;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;

public class PersistenceTests
{
	private static String testName = "Testy McTesterson";
	private static String testEmail = "testy@mctesterson.co.uk";
	
	@Before
	public void clearTestUser()
	{
		final EntityManager manager = EMFSingleton.getEntityManager();
		try
		{
			User user = UserManager.getUser(manager, testEmail);
			if(user != null)
			{
				manager.getTransaction().begin();
				manager.remove(user);
				manager.getTransaction().commit();				
			}
		}
		catch(Exception e)
		{
			
		}		
		finally
		{
			if(manager.getTransaction().isActive())
			{
				manager.getTransaction().rollback();
			}
			manager.close();
		}
	}
	
	@Test
	public void newUserTest() throws Exception
	{
		final EntityManager manager = EMFSingleton.getEntityManager();
		final Md5PasswordEncoder encoder = new Md5PasswordEncoder();					
		User user = new User(testName, testEmail, encoder.encodePassword("test", null));		
		try
		{
			manager.getTransaction().begin();
			manager.merge(user);
			manager.getTransaction().commit();
			
			assert user.getKey() != null;
			assert !user.getKey().equals("");
			
			user = UserManager.getUser(manager, testEmail);
			
			assert user != null;
			assert user.getName().equals(testName);
			assert user.getEmail().equals(testEmail.toLowerCase());
			
			manager.getTransaction().begin();
			manager.remove(user);
			manager.getTransaction().commit();
		}
		finally
		{
			if(manager.getTransaction().isActive())
			{
				manager.getTransaction().rollback();
			}
			manager.close();
		}
	}
	
	@Test
	public void newPlacebook() throws Exception
	{
		final EntityManager manager = EMFSingleton.getEntityManager();

		try
		{
			final Md5PasswordEncoder encoder = new Md5PasswordEncoder();						
			final User owner = new User(testName, testEmail, encoder.encodePassword("test", null));
			final Geometry geometry = new WKTReader().read("POINT(52.5189367988799 -4.04983520507812)");

			PlaceBook placebook = new PlaceBook(owner, geometry);

			placebook.addItem(new TextItem(owner, geometry, new URL("http://www.google.com"), "Test text string"));
			placebook.addItem(new TextItem(owner, geometry, new URL("http://www.google.com"), "Test text string"));
			// placebook.addItem(new ImageItem(owner, geometry, new URL("http://www.blah.com"), new
			// BufferedImage(100, 100,
			// BufferedImage.TYPE_INT_BGR)));
			//
			// Document gpxDoc = null;
			//
			// // Some example XML
			// final String trace =
			// "<gpx version=\"1.0\" creator=\"PlaceBooks 1.0\" 				 xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" 				 xmlns=\"http://www.topografix.com/GPX/1/1\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\">			<time>			2011-02-14T13:31:10.084Z			</time>			<bounds minlat=\"52.950665120\" minlon=\"-1.183738050\" 					maxlat=\"52.950665120\" maxlon=\"-1.183738050\"/>			<trkseg>				<trkpt lat=\"52.950665120\" lon=\"-1.183738050\">				<ele>0.000000</ele>				<time>				2011-02-14T13:31:10.084Z				</time>				</trkpt>			</trkseg>			</gpx>";
			//
			// final StringReader reader = new StringReader(trace);
			// final InputSource source = new InputSource(reader);
			// final DocumentBuilder builder =
			// DocumentBuilderFactory.newInstance().newDocumentBuilder();
			// gpxDoc = builder.parse(source);
			// reader.close();
			//
			// placebook.addItem(new GPSTraceItem(owner, geometry, new URL("http://www.blah.com"),
			// gpxDoc));

			
			manager.getTransaction().begin();
			manager.persist(owner);
			manager.persist(placebook);
			manager.getTransaction().commit();

			assert owner.getPlacebookBinders().iterator().hasNext();
	
			manager.getTransaction().begin();
			manager.remove(placebook);		
			manager.remove(owner);
			manager.getTransaction().commit();	
		}
		finally
		{
			if(manager.getTransaction().isActive())
			{
				manager.getTransaction().rollback();
			}
			
		}
		manager.close();
	}

	@Test
	public void savePlaceBookJSON() throws Exception
	{
		EntityManager manager = EMFSingleton.getEntityManager();
		
		try
		{
			final Md5PasswordEncoder encoder = new Md5PasswordEncoder();						
			final User owner = new User(testName, testEmail, encoder.encodePassword("test", null));
			final Geometry geometry = new WKTReader().read("POINT(52.5189367988799 -4.04983520507812)");
			
			PlaceBook dbPlacebook = new PlaceBook(owner, geometry);
			manager.getTransaction().begin();	
			manager.persist(owner);
			manager.persist(dbPlacebook);

			ObjectMapper mapper = new ObjectMapper();			
			final PlaceBook jsonPlacebook = mapper.readValue("{\"id\":\"" + dbPlacebook.getKey() + "\", \"owner\":{\"id\":\"808180862fdee365012fdee365ac0000\", \"email\":\"ktg@cs.nott.ac.uk\", \"name\":\"Kevin Glover\", \"friends\":[]}, \"timestamp\":null, \"geom\":null, \"items\":[{\"@class\":\"placebooks.model.ImageItem\", \"sourceURL\":\"http://farm6.static.flickr.com/5104/5637692627_a6bdf5fccb_z.jpg\", \"metadata\":{}, \"parameters\":{\"order\":0, \"column\":1}},{\"@class\":\"placebooks.model.TextItem\", \"sourceURL\":\"http://www.google.com\", \"metadata\":{}, \"parameters\":{\"order\":0, \"column\":2}, \"text\":\"New Text Block\"},{\"@class\":\"placebooks.model.TextItem\", \"sourceURL\":\"http://www.google.com\", \"metadata\":{}, \"parameters\":{\"order\":0, \"column\":2}, \"text\":\"New Text Block\"}], \"metadata\":{}, \"index\":null}", PlaceBook.class);
						
			for(Entry<String, String> entry: jsonPlacebook.getMetadata().entrySet())
			{
				dbPlacebook.addMetadataEntry(entry.getKey(), entry.getValue());
			}

			for(PlaceBookItem item: jsonPlacebook.getItems())
			{
				item.setOwner(dbPlacebook.getOwner());
				item.setPlaceBook(dbPlacebook);				
				dbPlacebook.addItem(item);
			}			
	
			dbPlacebook.setGeometry(jsonPlacebook.getGeometry());

			manager.merge(dbPlacebook);
			
			manager.getTransaction().commit();
			
			
			manager.getTransaction().begin();
			manager.remove(dbPlacebook);
			manager.remove(owner);
			manager.getTransaction().commit();
		}
		finally
		{
			if(manager.getTransaction().isActive())
			{
				manager.getTransaction().rollback();
			}
			manager.close();
		}
	}	
}