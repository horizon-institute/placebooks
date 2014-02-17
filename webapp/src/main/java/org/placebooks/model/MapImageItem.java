package org.placebooks.model;

import java.net.URL;

import javax.persistence.Entity;

import com.vividsolutions.jts.geom.Geometry;

@Entity
public class MapImageItem extends ImageItem
{
	public MapImageItem(final MapImageItem m)
	{
		super(m);
	}

	public MapImageItem(final User owner, final Geometry geom, final URL sourceURL, final String image)
	{
		super(owner, geom, sourceURL, image);
	}

	MapImageItem()
	{
	}

	@Override
	public MapImageItem deepCopy()
	{
		return new MapImageItem(this);
	}

	@Override
	public String getEntityName()
	{
		return MapImageItem.class.getName();
	}

}
