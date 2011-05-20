package placebooks.model;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.persistence.Entity;
import javax.persistence.Transient;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vividsolutions.jts.geom.Geometry;

import placebooks.controller.PropertiesSingleton;

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
			copyDataToPackage(image);
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

	public void writeDataToDisk(String name, InputStream input) 
		throws IOException
	{
		final String path = 
			PropertiesSingleton
				.get(this.getClass().getClassLoader())
				.getProperty(PropertiesSingleton.IDEN_MEDIA, "");

		if (!new File(path).exists() && !new File(path).mkdirs()) 
		{
			throw new IOException("Failed to write file"); 
		}

		final int extIdx = name.lastIndexOf(".");
		final String ext = name.substring(extIdx + 1, name.length());

		setImage(path + "/" + getKey() + "." + ext);

		final OutputStream output = new FileOutputStream(image);
		int byte_;
		while ((byte_ = input.read()) != -1)
		{
			output.write(byte_);
		}
		output.close();
		input.close();

		log.info("Wrote " + name + " file " + image.getAbsolutePath());

	}
}
