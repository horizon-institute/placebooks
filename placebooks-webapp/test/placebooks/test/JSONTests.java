package placebooks.test;

import java.util.Collections;
import java.util.Map.Entry;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import placebooks.controller.PMFSingleton;
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
		final PersistenceManager manager = PMFSingleton.getPersistenceManager();
		try
		{

			manager.currentTransaction().begin();
			final PlaceBook placebook = mapper.readValue("{\"key\":\"808180862fdee382012fdee3823d0000\", \"owner\":{\"key\":\"808180862fdee365012fdee365ac0000\", \"email\":\"ktg@cs.nott.ac.uk\", \"name\":\"Kevin Glover\", \"friends\":[]}, \"timestamp\":null, \"geom\":null, \"items\":[{\"@class\":\"placebooks.model.ImageItem\", \"sourceURL\":\"http://farm6.static.flickr.com/5104/5637692627_a6bdf5fccb_z.jpg\", \"metadata\":{}, \"parameters\":{\"order\":0, \"panel\":1}},{\"@class\":\"placebooks.model.TextItem\", \"sourceURL\":\"http://www.google.com\", \"metadata\":{}, \"parameters\":{\"order\":0, \"panel\":2}, \"text\":\"New Text Block\"},{\"@class\":\"placebooks.model.TextItem\", \"sourceURL\":\"http://www.google.com\", \"metadata\":{}, \"parameters\":{\"order\":0, \"panel\":2}, \"text\":\"New Text Block\"}], \"metadata\":{}, \"index\":null}", PlaceBook.class);
			
			final PlaceBook dbPlacebook = manager.getObjectById(PlaceBook.class, placebook.getKey());	
			
			for(Entry<String, String> entry: placebook.getMetadata().entrySet())
			{
				dbPlacebook.addMetadataEntry(entry.getKey(), entry.getValue());
			}

			dbPlacebook.setItems(Collections.EMPTY_LIST);
			for(PlaceBookItem item: placebook.getItems())
			{
				item.setOwner(dbPlacebook.getOwner());
				item.setPlaceBook(dbPlacebook);
				manager.makePersistent(item);							
				dbPlacebook.addItem(item);
			}			
	
			dbPlacebook.setGeometry(placebook.getGeometry());

			manager.makePersistent(dbPlacebook);
			
			manager.currentTransaction().commit();
			
		}
		finally
		{
			if(manager.currentTransaction().isActive())
			{
				manager.currentTransaction().rollback();
			}
			manager.close();
		}
	}	
}