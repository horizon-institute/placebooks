package placebooks.model;

import java.io.File;
import java.net.URL;

import javax.persistence.Entity;

import com.vividsolutions.jts.geom.Geometry;

@Entity
public class VideoItem extends MediaItem
{
	public VideoItem(final User owner, final Geometry geom, final URL sourceURL,
					 final File video)
	{
		super(owner, geom, sourceURL, video);
	}

	VideoItem()
	{

	}

	@Override
	public String getEntityName()
	{
		return VideoItem.class.getName();
	}
}
