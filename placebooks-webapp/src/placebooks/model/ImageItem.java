package placebooks.model;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.io.*;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
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
	private static final Logger log = 
		Logger.getLogger(TextItem.class.getName());

	@Persistent
	private BufferedImage image; 

	public ImageItem(int owner, Geometry geom, URL sourceURL, 
					 BufferedImage image)
	{
		super(owner, geom, sourceURL);
		this.image = image;
	}

	public String getEntityName()
	{
		return ImageItem.class.getName();
	}

	public void appendConfiguration(Document config, Element root)
	{
		Element item = getConfigurationHeader(config);
		
		// Dump image to disk
		try 
		{
			File file = new File(hashCode() + ".png");
		    ImageIO.write(image, "PNG", file);
			Element filename = config.createElement("filename");
			filename.appendChild(config.createTextNode(file.getPath()));
			item.appendChild(filename);
		}
		catch (IOException e)
		{
			log.error(e.toString());
		}
		
		root.appendChild(item);
	}

}
