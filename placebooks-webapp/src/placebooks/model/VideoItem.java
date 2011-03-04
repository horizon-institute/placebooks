package placebooks.model;

import java.io.File;
import java.net.URL;
import javax.jdo.annotations.*;
import com.vividsolutions.jts.geom.Geometry;


@PersistenceCapable
public class VideoItem extends PlaceBookItem
{
	// Videos are stored on disk, not database
	private File video; 

	public VideoItem() { }

	public VideoItem(int owner, Geometry geom, URL sourceURL, File video)
	{
		super(owner, geom, sourceURL);
		this.video = video;
	}

	@Persistent
	public String getVideo()
	{
		return video.toString();
	}
	@Persistent
	public void setVideo(String filepath)
	{
		if (filepath != null)
			video = new File(filepath);
		else
			video = null;
	}

}
