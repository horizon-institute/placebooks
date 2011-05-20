package placebooks.model;

import java.net.URL;

import javax.persistence.Entity;

import com.vividsolutions.jts.geom.Geometry;

@Entity
public class MapImageItem extends ImageItem
{
	public MapImageItem(final User owner, final Geometry geom, 
						final URL sourceURL, final String image)
	{
		super(owner, geom, sourceURL, image);
	}

	MapImageItem()
	{
	}

	@Override
	public String getEntityName()
	{
		return MapImageItem.class.getName();
	}

}
