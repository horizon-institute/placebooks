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
	
	public VideoItem(final VideoItem v)
	{
		super(v);
	}

	@Override
	public VideoItem deepCopy()
	{
		return new VideoItem(this);
	}

	@Override
	public String getEntityName()
	{
		return VideoItem.class.getName();
	}
}