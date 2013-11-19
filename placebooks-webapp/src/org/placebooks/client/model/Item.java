package org.placebooks.client.model;

import java.util.HashMap;
import java.util.Map;

public class Item
{
	public enum Type
	{
		ImageItem, VideoItem, AudioItem, MapImageItem, GPSTraceItem, TextItem, WebBundleItem
	}

	// Attributes
	private String id;
	private Map<String, String> metadata = new HashMap<String, String>();
	private Map<String, Integer> parameters = new HashMap<String, Integer>();
	private long timestamp;
	private String geom;
	private Type type;
	private String text;
	private String hash;

	public Item createCopy()
	{
		final Item item = new Item();
		item.id = id;
		item.geom = geom;
		item.hash = hash;
		item.metadata.putAll(metadata);
		item.parameters.putAll(parameters);
		item.text = text;
		item.timestamp = timestamp;
		item.type = type;
		return item;
	}

	public String getGeom()
	{
		return geom;
	}

	public String getHash()
	{
		return hash;
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

	public int getParameter(final String key, final int defaultValue)
	{
		if (parameters.containsKey(key)) { return parameters.get(key); }
		return defaultValue;
	}

	public Map<String, Integer> getParameters()
	{
		return parameters;
	}

	public String getText()
	{
		return text;
	}

	public long getTimestamp()
	{
		return timestamp;
	}

	public Type getType()
	{
		return type;
	}
	
	public void setTimestamp(long timestamp)
	{
		this.timestamp = timestamp;
	}

	public boolean is(final Type... types)
	{
		for (final Type type : types)
		{
			if (type == this.type) { return true; }
		}
		return false;
	}

	public boolean isMedia()
	{
		return type == Type.ImageItem || type == Type.GPSTraceItem || type == Type.AudioItem || type == Type.VideoItem;
	}

	public void setGeom(final String geom)
	{
		this.geom = geom;
	}

	public void setHash(final String hash)
	{
		this.hash = hash;
	}

	public void setId(final String id)
	{
		this.id = id;
	}

	public void setText(final String text)
	{
		this.text = text;
	}

	public void setType(final Type type)
	{
		this.type = type;
	}

	public final boolean showMarker()
	{
		return parameters.containsKey("mapPage") && getParameter("markerShow", 0) == 1;
	}
}