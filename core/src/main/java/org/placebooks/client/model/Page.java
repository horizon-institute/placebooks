package org.placebooks.client.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Page
{
	private String geom;
	private String id;
	private List<Item> items = new ArrayList<Item>();
	private Map<String, String> metadata = new HashMap<String, String>();

	public String getGeom()
	{
		return geom;
	}

	public String getId()
	{
		return id;
	}

	public List<Item> getItems()
	{
		return items;
	}

	public Map<String, String> getMetadata()
	{
		return metadata;
	}

	public String getMetadata(final String key, final String defaultValue)
	{
		if (metadata.containsKey(key)) { return metadata.get(key); }

		return defaultValue;
	}

	public void setGeom(final String geom)
	{
		this.geom = geom;
	}

	public void setId(final String id)
	{
		this.id = id;
	}
}