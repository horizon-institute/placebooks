package placebooks.model;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.persistence.Entity;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vividsolutions.jts.geom.Geometry;

@Entity
public abstract class MediaItem extends PlaceBookItem
{
	@JsonIgnore
	protected File file;
	
	MediaItem() { }
	
	public MediaItem(final User owner, final Geometry geom, final URL sourceURL,
			 final File file)
	{
		this.file = file;
	}
	
	@Override
	public void appendConfiguration(final Document config, final Element root)
	{
		final Element item = getConfigurationHeader(config);

		try
		{
			writeDataToDisk(file);
			final Element filename = config.createElement("filename");
			filename.appendChild(config.createTextNode(file.getName()));
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
		if (!file.delete())
		{
			log.error("Problem deleting file " + file.toString());
		}
	}	
	
	public File getFile()
	{
		return file;
	}
	
	public String getPath()
	{
		return file.toString();
	}

	public void setPath(final String filepath)
	{
		this.file = new File(filepath);
	}
}