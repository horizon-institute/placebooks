package placebooks.model;

import java.io.File;
import java.net.URL;

import javax.persistence.Entity;

import com.vividsolutions.jts.geom.Geometry;

@Entity
public class AudioItem extends MediaItem
{
	public AudioItem(final User owner, final Geometry geom, final URL sourceURL,
					 final File audio)
	{
		super(owner, geom, sourceURL, audio);
	}

	AudioItem()
	{
	}

	@Override
	public String getEntityName()
	{
		return AudioItem.class.getName();
	}
}
