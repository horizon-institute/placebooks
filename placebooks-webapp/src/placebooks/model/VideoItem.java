package placebooks.model;

import java.io.File;
import java.net.URL;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;

import org.w3c.dom.Element;
import org.w3c.dom.Document;

import com.vividsolutions.jts.geom.Geometry;


@PersistenceCapable
@Inheritance(strategy=InheritanceStrategy.SUPERCLASS_TABLE)
public class VideoItem extends PlaceBookItem
{
	// Videos are stored on disk, not database
	private File video; 

	public VideoItem(int owner, Geometry geom, URL sourceURL, File video)
	{
		super(owner, geom, sourceURL);
		this.video = video;
	}

	public String getEntityName()
	{
		return VideoItem.class.getName();
	}

	public void appendConfiguration(Document config, Element root)
	{
		Element item = getConfigurationHeader(config);
		Element filename = config.createElement("filename");
		filename.appendChild(config.createTextNode(video.getPath()));
		item.appendChild(filename);
		root.appendChild(item);
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
