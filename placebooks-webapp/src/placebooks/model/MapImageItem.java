package placebooks.model;

import java.awt.image.BufferedImage;

import java.net.URL;

import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;

import com.vividsolutions.jts.geom.Geometry;

@PersistenceCapable
@Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
public class MapImageItem extends ImageItem
{
	public MapImageItem(User owner, Geometry geom, URL sourceURL, 
					    BufferedImage image)
	{
		super(owner, geom, sourceURL, image);
	}

	
	public String getEntityName()
	{
		return MapImageItem.class.getName();
	}

}

