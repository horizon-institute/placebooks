package placebooks.model;

import placebooks.controller.PropertiesSingleton;

import java.io.*;
import java.net.URL;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;

import org.apache.commons.io.IOUtils;

import org.w3c.dom.Element;
import org.w3c.dom.Document;

import com.vividsolutions.jts.geom.Geometry;


@PersistenceCapable
@Inheritance(strategy=InheritanceStrategy.SUPERCLASS_TABLE)
public class VideoItem extends PlaceBookItem
{
	// Videos are stored on disk, not database
	private File video; 

	public VideoItem(User owner, Geometry geom, URL sourceURL, File video)
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

		try
		{
			// Check package dir exists already
			String path = PropertiesSingleton
							.get(this.getClass().getClassLoader())
							.getProperty(PropertiesSingleton.IDEN_PKG, "") 
							+ getPlaceBook().getKey();

			if (new File(path).exists() || new File(path).mkdirs())
			{

				FileInputStream fis = new FileInputStream(video);
				File to = new File(path + "/" + video.getName());
			
				log.info("Copying video file, from=" + video.toString() 
						 + ", to=" + to.toString());
			
				FileOutputStream fos = new FileOutputStream(to);
				IOUtils.copy(fis, fos);
				fis.close();
				fos.close();
				Element filename = config.createElement("filename");
				filename.appendChild(config.createTextNode(video.getName()));
				item.appendChild(filename);
			}
		}
		catch (IOException e)
		{
			log.error(e.toString());
		}

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

}
