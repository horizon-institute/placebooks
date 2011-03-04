package placebooks.model;

import java.awt.image.BufferedImage;
import java.net.URL;
import javax.jdo.annotations.*;
import com.vividsolutions.jts.geom.Geometry;

@PersistenceCapable
public class ImageItem extends PlaceBookItem
{
	@Persistent
	private BufferedImage image; 

	public ImageItem() { }

	public ImageItem(int owner, Geometry geom, URL sourceURL, 
					 BufferedImage image)
	{
		super(owner, geom, sourceURL);
		this.image = image;
	}

}
