package org.placebooks.client.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaceBook
{
	private String id;
	private User owner;
	private String geom;
	private long timestamp;
	private String previewImage;
	private List<Page> pages = new ArrayList<Page>();
	private List<Group> groups = new ArrayList<Group>();
	private Map<String, String> metadata = new HashMap<String, String>();
	private Map<String, String> permissions = new HashMap<String, String>();
	private Map<String, Integer> parameters = new HashMap<String, Integer>();
	private String state;
	private String directory;

	public String getDirectory()
	{
		return directory;
	}

	public List<Group> getGroups()
	{
		return groups;
	}

	public String getId()
	{
		return id;
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

	public User getOwner()
	{
		return owner;
	}

	public String getGeom()
	{
		return geom;
	}

	public void setGeom(String geom)
	{
		this.geom = geom;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public void setState(String state)
	{
		this.state = state;
	}

	public void setDirectory(String directory)
	{
		this.directory = directory;
	}

	public List<Page> getPages()
	{
		return pages;
	}

	public int getParameter(final String key, final int defaultValue)
	{
		if (parameters.containsKey(key)) { return parameters.get(key); }
		return defaultValue;
	}

	public Map<String, Integer> getParameters()
	{
		return parameters;
	}

	public Map<String, String> getPermissions()
	{
		return permissions;
	}

	public String getPreviewImage()
	{
		return previewImage;
	}

	public String getState()
	{
		return state;
	}

	public long getTimestamp()
	{
		return timestamp;
	}

	public void setID(final String id)
	{
		this.id = id;
	}

	public void setOwner(final User theOwner)
	{
		owner = theOwner;
	}

	public void setPreviewImage(final String pi)
	{
		previewImage = pi;
	}

	public void setTimestamp(final long t)
	{
		timestamp = t;
	}
}