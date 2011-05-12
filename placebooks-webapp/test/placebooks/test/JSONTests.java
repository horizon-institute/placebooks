package placebooks.test;

import java.util.Collections;
import java.util.Map.Entry;

import javax.persistence.EntityManager;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import placebooks.controller.EMFSingleton;
import placebooks.controller.UserManager;
import placebooks.model.PlaceBook;
import placebooks.model.PlaceBookItem;
import placebooks.model.User;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;

public class JSONTests
{
//	@Test
//	public void getPlacebookJsonTest() throws Exception
//	{
//		final PersistenceManager manager = PMFSingleton.getPersistenceManager();
//
//		try
//		{
//
//			final User owner = UserManager.getUser(manager, "ktg@cs.nott.ac.uk");
//			final Geometry geometry = new WKTReader().read("POINT(52.5189367988799 -4.04983520507812)");
//
//			final ObjectMapper mapper = new ObjectMapper();
//
//			final PlaceBook placebook = new PlaceBook(owner, geometry);
//
//			placebook.addItem(new TextItem(owner, geometry, new URL("http://www.google.com"), "Test text string"));
//			placebook.addItem(new TextItem(owner, geometry, new URL("http://www.google.com"), "Test 2"));
//
//			System.out.println(mapper.writeValueAsString(placebook));
//		}
//		finally
//		{
//			if (manager.currentTransaction().isActive())
//			{
//				manager.currentTransaction().rollback();
//			}
//
//			manager.close();
//		}
//	}

	
//	@Test
//	public void updatePlacebookJsonTest() throws Exception
//	{
//		PersistenceManager manager = PMFSingleton.getPersistenceManager();
//
//		String json;
//		
//		try
//		{
//
//			final User owner = UserManager.getUser(manager, "ktg@cs.nott.ac.uk");
//			final Geometry geometry = new WKTReader().read("POINT(52.5189367988799 -4.04983520507812)");
//
//			final ObjectMapper mapper = new ObjectMapper();
//
//			PlaceBook placebook = new PlaceBook(owner, geometry);
//
//			//placebook.addItem(new TextItem(owner, geometry, new URL("http://www.google.com"), "Test text string"));
//			//placebook.addItem(new TextItem(owner, geometry, new URL("http://www.google.com"), "Test 2"));
//			
//			System.out.println(mapper.writeValueAsString(placebook));
//			
//			manager.currentTransaction().begin();
//			
//			manager.makePersistent(placebook);
//			manager.makePersistent(owner);
//			
//			manager.currentTransaction().commit();
//			
//			json = mapper.writeValueAsString(placebook);
//			System.out.println(json);
//		}
//		finally
//		{
//			if (manager.currentTransaction().isActive())
//			{
//				manager.currentTransaction().rollback();
//			}
//
//			manager.close();
//		}
//		
//		manager = PMFSingleton.getPersistenceManager();
//		
//		try
//		{
//			final ObjectMapper mapper = new ObjectMapper();
//			PlaceBook placebook = mapper.readValue(json, PlaceBook.class);
//			
//			System.out.println(mapper.writeValueAsString(placebook));			
//			
//			manager.currentTransaction().begin();		
//			
//			System.out.println(JDOHelper.getObjectState(placebook.getOwner()));
//			
//			manager.refresh(placebook);
//			
//			System.out.println(mapper.writeValueAsString(placebook));			
//			
//			manager.currentTransaction().commit();
//			
//		}
//		finally
//		{
//			if (manager.currentTransaction().isActive())
//			{
//				manager.currentTransaction().rollback();
//			}
//
//			manager.close();
//		}
//	}
	
	@Test
	public void savePlaceBookJSON() throws Exception
	{
		ObjectMapper mapper = new ObjectMapper();
		final EntityManager manager = EMFSingleton.getEntityManager();
		try
		{
			final User owner = UserManager.getUser(manager, "ktg@cs.nott.ac.uk");
			final Geometry geometry = new WKTReader().read("POINT(52.5189367988799 -4.04983520507812)");
			
			PlaceBook dbPlacebook = new PlaceBook(owner, geometry);
			manager.persist(dbPlacebook);

			manager.getTransaction().begin();
			final PlaceBook jsonPlacebook = mapper.readValue("{\"id\":\"" + dbPlacebook.getKey() + "\", \"owner\":{\"id\":\"808180862fdee365012fdee365ac0000\", \"email\":\"ktg@cs.nott.ac.uk\", \"name\":\"Kevin Glover\", \"friends\":[]}, \"timestamp\":null, \"geom\":null, \"items\":[{\"@class\":\"placebooks.model.ImageItem\", \"sourceURL\":\"http://farm6.static.flickr.com/5104/5637692627_a6bdf5fccb_z.jpg\", \"metadata\":{}, \"parameters\":{\"order\":0, \"panel\":1}},{\"@class\":\"placebooks.model.TextItem\", \"sourceURL\":\"http://www.google.com\", \"metadata\":{}, \"parameters\":{\"order\":0, \"panel\":2}, \"text\":\"New Text Block\"},{\"@class\":\"placebooks.model.TextItem\", \"sourceURL\":\"http://www.google.com\", \"metadata\":{}, \"parameters\":{\"order\":0, \"panel\":2}, \"text\":\"New Text Block\"}], \"metadata\":{}, \"index\":null}", PlaceBook.class);
						
			for(Entry<String, String> entry: jsonPlacebook.getMetadata().entrySet())
			{
				dbPlacebook.addMetadataEntry(entry.getKey(), entry.getValue());
			}

			dbPlacebook.setItems(Collections.EMPTY_LIST);
			for(PlaceBookItem item: jsonPlacebook.getItems())
			{
				item.setOwner(dbPlacebook.getOwner());
				item.setPlaceBook(dbPlacebook);
				manager.persist(item);							
				dbPlacebook.addItem(item);
			}			
	
			dbPlacebook.setGeometry(jsonPlacebook.getGeometry());

			manager.persist(dbPlacebook);
			
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