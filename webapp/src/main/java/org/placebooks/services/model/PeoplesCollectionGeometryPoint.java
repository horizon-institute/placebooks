package org.placebooks.services.model;

import java.io.IOException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import org.wornchaos.logger.Log;

public class PeoplesCollectionGeometryPoint
{
	private String type;
	private float[] coordinates;

	public PeoplesCollectionGeometryPoint()
	{

	}

	public PeoplesCollectionGeometryPoint(final String type, final float[] coordinates)
	{
		this.type = type;
		this.coordinates = coordinates;
	}

	public float[] GetCoordinates()
	{
		return coordinates;
	}

	public Geometry GetGeometry() throws IOException
	{
		try
		{
			final String wkt = type.toUpperCase() + " ( " + coordinates[1] + " " + coordinates[0] + " )";
			Log.debug("Generated WKT: " + wkt);
			return new WKTReader().read(wkt);
		}
		catch (final ParseException e)
		{
			throw new IOException("Parse Error", e);
		}
	}

	public String GetType()
	{
		return type;
	}

	public void SetCoordinates(final float[] coordinates)
	{
		this.coordinates = coordinates;
	}

	public void SetType(final String type)
	{
		this.type = type;
	}
}
