package placebooks.model;

import java.net.URL;

import javax.persistence.Entity;

import com.vividsolutions.jts.geom.Geometry;

@Entity
public class ImageItem extends MediaItem
{
	public ImageItem()
	{
	}

	public ImageItem(final ImageItem i)
	{
		super(i);
	}

	public ImageItem(final User owner, final Geometry geom, final URL sourceURL,
					 final String image)
	{
		super(owner, geom, sourceURL, image);
	}

	@Override
	public ImageItem deepCopy()
	{
		return new ImageItem(this);
	}

	@Override
	public String getEntityName()
	{
		return ImageItem.class.getName();
	}
}