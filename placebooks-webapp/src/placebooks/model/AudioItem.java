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
public class AudioItem extends PlaceBookItem
{
	@JsonIgnore
	private File audio;

	public AudioItem(final User owner, final Geometry geom, final URL sourceURL,
					 final File audio)
	{
		super(owner, geom, sourceURL);
		this.audio = audio;
	}

	AudioItem()
	{
	}

	@Override
	public void appendConfiguration(final Document config, final Element root)
	{
		final Element item = getConfigurationHeader(config);

		try
		{
			writeDataToDisk(audio);
			final Element filename = config.createElement("filename");
			filename.appendChild(config.createTextNode(audio.getName()));
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
		if (!audio.delete())
		{
			log.error("Problem deleting audio file " + audio.toString());
		}
	}

	public String getAudio()
	{
		return audio.toString();
	}

	@Override
	public String getEntityName()
	{
		return AudioItem.class.getName();
	}

	public void setAudio(final String filepath)
	{
		if (filepath != null)
		{
			audio = new File(filepath);
		}
	}
}
