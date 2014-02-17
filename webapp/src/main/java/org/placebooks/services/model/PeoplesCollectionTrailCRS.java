package org.placebooks.services.model;

import java.util.HashMap;

public class PeoplesCollectionTrailCRS
{
	protected String type;
	protected HashMap<String, String> properties;

	public PeoplesCollectionTrailCRS(final String type, final HashMap<String, String> properies)
	{
		this.type = type;
		properties = properies;
	}

	PeoplesCollectionTrailCRS()
	{
	}

	public HashMap<String, String> GetProperties()
	{
		return properties;
	}

	public String GetType()
	{
		return type;
	}
}