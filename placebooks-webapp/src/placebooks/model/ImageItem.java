package placebooks.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.persistence.Entity;

import placebooks.controller.FileHelper;
import placebooks.controller.PropertiesSingleton;

import com.vividsolutions.jts.geom.Geometry;

@Entity
public class ImageItem extends MediaItem
{
	public ImageItem()
	{
	}

	public ImageItem(final ImageItem i)
	{
		super(i);
	}

	public ImageItem(final User owner, final Geometry geom, final URL sourceURL,
					 final String image)
	{
		super(owner, geom, sourceURL, image);
	}

	@Override
	public void writeDataToDisk(final String name, final InputStream is) throws IOException
	{
		String thumbPath = FileHelper.GetSavePath(PropertiesSingleton.get(this.getClass().getClassLoader()).getProperty(PropertiesSingleton.IDEN_THUMBS, ""));
		String path = FileHelper.GetSavePath(PropertiesSingleton.get(this.getClass().getClassLoader()).getProperty(PropertiesSingleton.IDEN_MEDIA, ""));
		FileHelper.SaveFile(is, thumbPath);
		is.reset();
		super.writeDataToDisk(name, is);
	}
	
	@Override
	public ImageItem deepCopy()
	{
		return new ImageItem(this);
	}

	@Override
	public String getEntityName()
	{
		return ImageItem.class.getName();
	}
}