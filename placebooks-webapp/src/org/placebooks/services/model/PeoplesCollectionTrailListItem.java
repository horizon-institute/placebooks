package org.placebooks.services.model;

import java.io.IOException;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Class to encapsulate response from Peoples Collection API for a Trail
 * 
 * @author pszmp
 * 
 */
public class PeoplesCollectionTrailListItem
{
	String id;
	String type;

	PeoplesCollectionGeometryPoint geometry;

	PeoplesCollectionProperties properties;

	public PeoplesCollectionTrailListItem()
	{

	}

	public PeoplesCollectionTrailListItem(final String id, final String type,
			final PeoplesCollectionGeometryPoint geometry, final PeoplesCollectionProperties properties)
	{
		this.id = id;
		this.type = type;
		this.geometry = geometry;
		this.properties = properties;
	}

	public Geometry GetGeometry() throws IOException
	{
		return geometry.GetGeometry();
	}

	/**
	 * Get the ID from the list items, this is in the string format trail.{id} so to get the integer
	 * id use GetPropertiesID.
	 * 
	 * @return String trail.{id} of the trail
	 */
	public String GetId()
	{
		return id;
	}

	public PeoplesCollectionGeometryPoint GetPeoplesCollectionGeometry()
	{
		return geometry;
	}

	/**
	 * Get the properties for the trail, e.g. 'markup' html representation, title and icon type,
	 * etc.
	 * 
	 * @return
	 */
	public PeoplesCollectionProperties GetProperties()
	{
		return properties;
	}

	/**
	 * Get the integer ID from the trail properties rather than the trail.id type id from the list
	 * item
	 * 
	 * @return
	 */
	public int GetPropertiesId()
	{
		return properties.GetId();
	}

	public String GetType()
	{
		return type;
	}

}
