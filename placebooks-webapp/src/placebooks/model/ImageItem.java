package placebooks.model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import placebooks.controller.PropertiesSingleton;

import com.vividsolutions.jts.geom.Geometry;

@Entity
public class ImageItem extends PlaceBookItem
{
	@JsonIgnore
	@Transient
	private BufferedImage image;

	@JsonIgnore
	@Transient
	private File imageFile;

	public ImageItem(final User owner, final Geometry geom, final URL sourceURL, final BufferedImage image)
	{
		super(owner, geom, sourceURL);
		this.image = image;
		imageFile = null;
	}

	ImageItem()
	{
	}

	@Override
	public void appendConfiguration(final Document config, final Element root)
	{
		final Element item = getConfigurationHeader(config);

		// Dump image to disk
		try
		{
			final String path = PropertiesSingleton.get(this.getClass().getClassLoader())
					.getProperty(PropertiesSingleton.IDEN_PKG, "") + getPlaceBook().getKey();
			imageFile = new File(path + "/" + getKey() + ".png");
			log.info("Writing ImageItem data to " + imageFile.getAbsolutePath());
			if (new File(path).exists() || new File(path).mkdirs())
			{
				ImageIO.write(image, "PNG", imageFile);
				final Element filename = config.createElement("filename");
				filename.appendChild(config.createTextNode(imageFile.getName()));
				item.appendChild(filename);
			}
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
		if (!imageFile.delete())
		{
			log.error("Problem deleting image file " + imageFile.toString());
		}
	}

	@Override
	public String getEntityName()
	{
		return ImageItem.class.getName();
	}

	public BufferedImage getImage()
	{
		return image;
	}

	public File getImagePath()
	{
		return imageFile;
	}

	public void setImage(final BufferedImage image)
	{
		this.image = image;
	}

}
