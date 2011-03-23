package placebooks.model;

import placebooks.controller.PropertiesSingleton;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.io.*;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;

import javax.imageio.ImageIO;

import org.apache.log4j.*;

import org.w3c.dom.Element;
import org.w3c.dom.Document;

import com.vividsolutions.jts.geom.Geometry;

@PersistenceCapable
@Inheritance(strategy=InheritanceStrategy.SUPERCLASS_TABLE)
public class ImageItem extends PlaceBookItem
{
	@Persistent
	private BufferedImage image; 

	@NotPersistent
	private File imageFile;
	
	public ImageItem(User owner, Geometry geom, URL sourceURL, 
					 BufferedImage image)
	{
		super(owner, geom, sourceURL);
		this.image = image;
		imageFile = null;
	}

	public String getEntityName()
	{
		return ImageItem.class.getName();
	}

	public File getImagePath()
	{
		return imageFile;
	}

	public void appendConfiguration(Document config, Element root)
	{
		Element item = getConfigurationHeader(config);
		
		// Dump image to disk
		try 
		{
			String path = PropertiesSingleton
							.get(this.getClass().getClassLoader())
							.getProperty(PropertiesSingleton.IDEN_PKG, "") 
							+ getPBKey();
			imageFile = new File(path + "/" + getKey() + ".png");
			log.info("Writing ImageItem data to " 
					 + imageFile.getAbsolutePath());
			if (new File(path).exists() || new File(path).mkdirs())
			{
			    ImageIO.write(image, "PNG", imageFile);
				Element filename = config.createElement("filename");
				filename.appendChild(
					config.createTextNode(imageFile.getName())
				);
				item.appendChild(filename);
			}
		}
		catch (IOException e)
		{
			log.error(e.toString());
		}
		
		root.appendChild(item);
	}

	/* (non-Javadoc)
	 * @see placebooks.model.PlaceBookItem#GetHTML()
	 */
	@Override
	public String GetHTML()
	{
		StringBuilder output = new StringBuilder();
		output.append("<img src='");
		output.append(imageFile.getPath());
		output.append("' class='placebook-item-image' id='");
		output.append(this.getPBKey());
		output.append("' />");
		return output.toString();
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

