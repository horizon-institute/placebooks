package placebooks.model;

import java.io.File;
import java.net.URL;
import javax.jdo.annotations.*;
import com.vividsolutions.jts.geom.Geometry;

@PersistenceCapable
public class AudioItem extends PlaceBookItem
{
	private File audio; 

	public AudioItem() { }

	public AudioItem(int owner, Geometry geom, URL sourceURL, File audio)
	{
		super(owner, geom, sourceURL);
		this.audio = audio;
	}

	@Persistent
	public String getAudio()
	{
		return audio.toString();
	}
	@Persistent
	public void setAudio(String filepath)
	{
		if (filepath != null)
			audio = new File(filepath);
		else
			audio = null;
	}

}
