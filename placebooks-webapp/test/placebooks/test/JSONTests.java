package placebooks.test;

import java.net.URL;

import javax.persistence.EntityManager;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.junit.Test;

import placebooks.controller.EMFSingleton;
import placebooks.controller.UserManager;
import placebooks.model.PlaceBook;
import placebooks.model.TextItem;
import placebooks.model.User;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;

public class JSONTests
{
	@Test
	public void parseGeometryTest() throws Exception
	{
		String geometry = "POINT (52.5189367988799 -4.04983520507812)";
		if(geometry.startsWith("POINT ("))
		{
			String latLong = geometry.substring(6, geometry.length() - 1);
			System.out.println(latLong);
			int comma = latLong.indexOf(" ");
			String latStr = latLong.substring(0,comma);
			System.out.println(latStr);
			Float.parseFloat(latStr);
			String lonStr = latLong.substring(comma + 1);
			System.out.println(lonStr);			
			Float.parseFloat(lonStr);
		}		
	}
	
	@Test
	public void jsonAmpersandTest() throws Exception
	{
		final ObjectMapper mapper = new ObjectMapper();
		mapper.getSerializationConfig().setSerializationInclusion(JsonSerialize.Inclusion.NON_DEFAULT);

		System.out.println(mapper.writeValueAsString("&amp;"));
		
		System.out.println(mapper.readValue("{\"@class\":\"placebooks.model.TextItem\",\"metadata\":{\"title\":\"Text Block\"},\"parameters\":{},\"text\":\"&amp;\"}", TextItem.class));
	}
	
	@Test
	public void getPlacebookJsonTest() throws Exception
	{
		final EntityManager manager = EMFSingleton.getEntityManager();

		try
		{

			final User owner = UserManager.getUser(manager, "ktg@cs.nott.ac.uk");
			final Geometry geometry = new WKTReader().read("POINT(52.5189367988799 -4.04983520507812)");

			final ObjectMapper mapper = new ObjectMapper();

			final PlaceBook placebook = new PlaceBook(owner, geometry);

			placebook.addItem(new TextItem(owner, geometry, new URL("http://www.google.com"), "Test text string"));
			placebook.addItem(new TextItem(owner, geometry, new URL("http://www.google.com"), "Test 2"));

			System.out.println(mapper.writeValueAsString(placebook));
		}
		finally
		{
			if (manager.getTransaction().isActive())
			{
				manager.getTransaction().rollback();
			}

			manager.close();
		}
	}
}