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
public class AudioItem extends PlaceBookItem
{
	private File audio; 

	public AudioItem(int owner, Geometry geom, URL sourceURL, File audio)
	{
		super(owner, geom, sourceURL);
		this.audio = audio;
	}

	public String getEntityName()
	{
		return AudioItem.class.getName();
	}

	public void appendConfiguration(Document config, Element root)
	{
		Element item = getConfigurationHeader(config);
		Element filename = config.createElement("filename");
		filename.appendChild(config.createTextNode(audio.getPath()));
		item.appendChild(filename);
		root.appendChild(item);
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

	/* (non-Javadoc)
	 * @see placebooks.model.PlaceBookItem#GetHTML()
	 */
	@Override
	public String GetHTML()
	{
		// TODO Auto-generated method stub
		return "";
	}

	/* (non-Javadoc)
	 * @see placebooks.model.PlaceBookItem#GetCSS()
	 */
	@Override
	public String GetCSS()
	{
		// TODO Auto-generated method stub
		return "";
	}

	/* (non-Javadoc)
	 * @see placebooks.model.PlaceBookItem#GetJavaScript()
	 */
	@Override
	public String GetJavaScript()
	{
		// TODO Auto-generated method stub
		return "";
	}

	/* (non-Javadoc)
	 * @see placebooks.model.PlaceBookItem#toMobilePackage()
	 */
	@Override
	public void toMobilePackage()
	{
		// TODO Auto-generated method stub
		
	}

}
