package org.placebooks.services.model;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class PeoplesCollectionGeometryLineString
{
	protected static final Logger log = Logger.getLogger(PeoplesCollectionGeometryLineString.class);

	private String type;
	private float[][] coordinates;

	public PeoplesCollectionGeometryLineString()
	{

	}

	public PeoplesCollectionGeometryLineString(final String type, final float[][] coordinates)
	{
		this.type = type;
		this.coordinates = coordinates;
	}

	public float[][] GetCoordinates()
	{
		return coordinates;
	}

	public Geometry GetGeometry() throws IOException
	{
		try
		{
			final StringBuilder wktStringBuilder = new StringBuilder();
			if (coordinates.length == 1)
			{
				log.warn("Converting LineString to Point in Peoplescollection item");
				wktStringBuilder.append("POINT (" + coordinates[0][0] + " " + coordinates[0][1] + ")");
			}
			else
			{
				wktStringBuilder.append(type.toUpperCase());
				if (coordinates.length == 0)
				{
					wktStringBuilder.append(" EMPTY");
				}
				else
				{
					wktStringBuilder.append(" (");
					boolean first = true;
					for (final float[] coordinate : coordinates)
					{
						if (first)
						{
							first = false;
						}
						else
						{
							wktStringBuilder.append(", ");
						}
						wktStringBuilder.append(coordinate[0] + " " + coordinate[1]);
					}
					wktStringBuilder.append(")");
				}
			}
			// log.debug("Created WKT string:" + wktStringBuilder.toString());
			return new WKTReader().read(wktStringBuilder.toString());
		}
		catch (final ParseException e)
		{
			throw new IOException("WKT Parse Error", e);
		}
	}

	public String GetType()
	{
		return type;
	}

	public void SetCoordinates(final float[][] coordinates)
	{
		this.coordinates = coordinates;
	}

	public void SetType(final String type)
	{
		this.type = type;
	}
}