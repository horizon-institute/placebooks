package org.placebooks.model;

import java.net.URL;

import javax.persistence.Entity;

import com.vividsolutions.jts.geom.Geometry;

@Entity
public class AudioItem extends MediaItem
{
	public AudioItem()
	{
		super();
	}

	public AudioItem(final AudioItem a)
	{
		super(a);
	}

	public AudioItem(final User owner, final Geometry geom, final URL sourceURL, final String audio)
	{
		super(owner, geom, sourceURL, audio);
	}

	@Override
	public AudioItem deepCopy()
	{
		return new AudioItem(this);
	}

	@Override
	public String getEntityName()
	{
		return AudioItem.class.getName();
	}
}
