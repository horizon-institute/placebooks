package placebooks.model;

import java.io.File;
import java.net.URL;

import javax.persistence.Entity;

import com.vividsolutions.jts.geom.Geometry;

@Entity
public class ImageItem extends MediaItem
{
	public ImageItem(final User owner, final Geometry geom, final URL sourceURL,
					 final File image)
	{
		super(owner, geom, sourceURL, image);
	}

	ImageItem()
	{
	}

	@Override
	public String getEntityName()
	{
		return ImageItem.class.getName();
	}
}
