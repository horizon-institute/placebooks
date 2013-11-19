package org.placebooks.test;

import java.net.URL;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.placebooks.controller.EMFSingleton;
import org.placebooks.controller.UserManager;
import org.placebooks.model.PlaceBook;
import org.placebooks.model.TextItem;
import org.placebooks.model.User;
import org.wornchaos.logger.Log;

import com.google.gson.Gson;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;

public class JSONTests
{
	@Test
	public void getPlacebookJsonTest() throws Exception
	{
		final EntityManager manager = EMFSingleton.getEntityManager();

		try
		{

			final User owner = UserManager.getUser(manager, "ktg@cs.nott.ac.uk");
			final Geometry geometry = new WKTReader().read("POINT(52.5189367988799 -4.04983520507812)");

			final Gson mapper = new Gson();

			final PlaceBook placebook = new PlaceBook(owner, geometry);

			placebook.addItem(new TextItem(owner, geometry, new URL("http://www.google.com"), "Test text string"));
			placebook.addItem(new TextItem(owner, geometry, new URL("http://www.google.com"), "Test 2"));

			System.out.println(mapper.toJson(placebook));
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

	@Test
	public void jsonAmpersandTest() throws Exception
	{
		final Gson mapper = new Gson();

		System.out.println(mapper.toJson("&amp;"));

		System.out
				.println(mapper
						.fromJson(	"{\"@class\":\"placebooks.model.TextItem\",\"metadata\":{\"title\":\"Text Block\"},\"parameters\":{},\"text\":\"&amp;\"}",
									TextItem.class));
	}

	@Test
	public void parseGeometryTest() throws Exception
	{
		final String geometry = "POINT (52.5189367988799 -4.04983520507812)";
		final int startBracket = geometry.lastIndexOf('(') + 1;
		final int endBracket = geometry.indexOf(')');
		final String point = geometry.substring(startBracket, endBracket);
		final String[] coords = point.trim().split(" ");
		double latitude = Double.parseDouble(coords[0]);
		double longitude = Double.parseDouble(coords[1]);
		Log.info(latitude + ", " + longitude);
	}

	@Test
	public void parseGeometryTest2() throws Exception
	{
		final String geometry = "POINT (52.5189367988799 -4.04983520507812)";
		double minLat = Double.MAX_VALUE;
		double maxLat = Double.MIN_VALUE;
		double minLong = Double.MAX_VALUE;
		double maxLong = Double.MIN_VALUE;
		final int startBracket = geometry.lastIndexOf('(') + 1;
		final int endBracket = geometry.indexOf(')');
		final String pointlist = geometry.substring(startBracket, endBracket);
		Log.info(pointlist);
		final String[] points = pointlist.split(",");
		for (final String point : points)
		{
			try
			{
				final String[] coords = point.split(" ");
				final double lat = Double.parseDouble(coords[0]);
				final double lng = Double.parseDouble(coords[1]);
				minLat = Math.min(minLat, lat);
				maxLat = Math.max(maxLat, lat);
				minLong = Math.min(minLong, lng);
				maxLong = Math.max(maxLong, lng);
			}
			catch (final Exception e)
			{
				Log.error(e);
			}
		}
	}

	@Test
	public void parseGeometryTest3() throws Exception
	{
		final String geometry = "LINEARRING (52.95158767700183 -1.1874053478241098, 52.95158767700183 -1.1863750219344686, 52.95002365112323 -1.1863750219344686, 52.95002365112323 -1.1874053478241098, 52.95158767700183 -1.1874053478241098)";
		double minLat = Double.MAX_VALUE;
		double maxLat = Double.MIN_VALUE;
		double minLong = Double.MAX_VALUE;
		double maxLong = Double.MIN_VALUE;
		final int startBracket = geometry.lastIndexOf('(') + 1;
		final int endBracket = geometry.indexOf(')');
		final String pointlist = geometry.substring(startBracket, endBracket);
		Log.info(pointlist);
		final String[] points = pointlist.split(",");
		for (final String point : points)
		{
			Log.info(point);
			try
			{
				final String[] coords = point.trim().split(" ");
				final double lat = Double.parseDouble(coords[0]);
				final double lng = Double.parseDouble(coords[1]);
				minLat = Math.min(minLat, lat);
				maxLat = Math.max(maxLat, lat);
				minLong = Math.min(minLong, lng);
				maxLong = Math.max(maxLong, lng);
			}
			catch (final Exception e)
			{
				Log.error(e);
			}
		}
	}
}