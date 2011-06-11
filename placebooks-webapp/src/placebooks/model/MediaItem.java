package placebooks.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.persistence.Entity;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import placebooks.controller.PropertiesSingleton;

import com.vividsolutions.jts.geom.Geometry;

@Entity
public abstract class MediaItem extends PlaceBookItem
{
	@JsonIgnore
	protected String path; // Always points to a file, never a dir

	MediaItem()
	{
	}

	public MediaItem(final User owner, final Geometry geom, final URL sourceURL,
					 final String file)
	{
		super(owner, geom, sourceURL);
		this.path = file;
	}

	public MediaItem(final MediaItem m)
	{
		super(m);
		this.path = new String(m.getPath());
	}

	@Override
	public void appendConfiguration(final Document config, final Element root)
	{
		final Element item = getConfigurationHeader(config);

		try
		{
			copyDataToPackage();
			final Element filename = config.createElement("filename");
			filename.appendChild(
				config.createTextNode(new File(path).getName())
			);
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
		final File f = new File(getPath());
		if (f.exists())
			f.delete();
		else
			log.error("Problem deleting file " + f.getAbsolutePath());
	}

	public String getPath()
	{
		return path;
	}

	public void setPath(final String filepath)
	{
		this.path = filepath;
	}

	protected void copyDataToPackage() throws IOException
	{
		// Check package dir exists already
		final String path = 
			PropertiesSingleton
				.get(this.getClass().getClassLoader())
				.getProperty(PropertiesSingleton.IDEN_PKG, "") 
					+ getPlaceBook().getKey();

		if (new File(path).exists() || new File(path).mkdirs())
		{
			final File dataFile = new File(getPath());
			final FileInputStream fis = new FileInputStream(dataFile);
			final File to = new File(path + "/" + dataFile.getName());

			log.info("Copying file, from=" + dataFile.toString() 
					 + ", to=" + to.toString());

			final FileOutputStream fos = new FileOutputStream(to);
			IOUtils.copy(fis, fos);
			fis.close();
			fos.close();
		}
	}
	
	public void writeDataToDisk(String name, InputStream input) 
		throws IOException
	{
		final String path = PropertiesSingleton
							.get(this.getClass().getClassLoader())
				.getProperty(PropertiesSingleton.IDEN_MEDIA, "");

		if(getKey() == null) { throw new IOException("Key is null"); }
		
		if (!new File(path).exists() && !new File(path).mkdirs()) 
		{
			throw new IOException("Failed to write file"); 
		}

		final int extIdx = name.lastIndexOf(".");
		final String ext = name.substring(extIdx + 1, name.length());

		String filePath = path + "/" + getKey() + "." + ext;
		
		final OutputStream output = new FileOutputStream(new File(filePath));
		int byte_;
		while ((byte_ = input.read()) != -1)
		{
			output.write(byte_);
		}
		output.close();
		input.close();

		setPath(filePath);
		
		log.info("Wrote " + name + " file " + filePath);
	}

}
