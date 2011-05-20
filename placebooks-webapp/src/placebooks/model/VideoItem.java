package placebooks.model;

import java.net.URL;

import javax.persistence.Entity;

import com.vividsolutions.jts.geom.Geometry;

@Entity
public class VideoItem extends MediaItem
{
	public VideoItem(final User owner, final Geometry geom, final URL sourceURL,
					 final String video)
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
