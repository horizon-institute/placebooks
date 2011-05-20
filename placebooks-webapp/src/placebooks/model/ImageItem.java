package placebooks.model;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.persistence.Entity;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vividsolutions.jts.geom.Geometry;

@Entity
public class ImageItem extends PlaceBookItem
{
	@JsonIgnore
	private File image;

	public ImageItem(final User owner, final Geometry geom, final URL sourceURL,
					 final File image)
	{
		super(owner, geom, sourceURL);
		this.image = image;
	}

	ImageItem()
	{
	}

	@Override
	public void appendConfiguration(final Document config, final Element root)
	{
		final Element item = getConfigurationHeader(config);

		try
		{
			writeDataToDisk(image);
			final Element filename = config.createElement("filename");
			filename.appendChild(config.createTextNode(image.getName()));
			item.appendChild(filename);
		}
		catch (final IOException e)
		{
			log.error(e.toString());
		}

		root.appendChild(item);

	}

	@Override
	public void deleteItemData()
	{
		if (!image.delete())
		{
			log.error("Problem deleting image file " + image.toString());
		}
	}

	@Override
	public String getEntityName()
	{
		return ImageItem.class.getName();
	}

	public File getFile()
	{
		return image;
	}
	
	public String getImage()
	{
		return image.toString();
	}

	public void setImage(final String filepath)
	{
		this.image = new File(filepath);
	}

}
