package placebooks.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import javax.persistence.Entity;
import javax.persistence.Transient;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import placebooks.controller.PropertiesSingleton;

import com.vividsolutions.jts.geom.Geometry;

@Entity
public class AudioItem extends PlaceBookItem
{
	@Transient
	private File audio;

	public AudioItem(final User owner, final Geometry geom, final URL sourceURL, final File audio)
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

		// TODO: identical to VideoItem.appendConfiguration... might be some
		// abstraction here to do.
		try
		{
			// Check package dir exists already
			final String path = PropertiesSingleton.get(this.getClass().getClassLoader())
					.getProperty(PropertiesSingleton.IDEN_PKG, "") + getPlaceBook().getKey();
			getAudio(); // TODO: work out why I need this here

			if (new File(path).exists() || new File(path).mkdirs())
			{

				final FileInputStream fis = new FileInputStream(audio);
				final File to = new File(path + "/" + audio.getName());

				log.info("Copying audio file, from=" + audio.toString() + ", to=" + to.toString());

				final FileOutputStream fos = new FileOutputStream(to);
				IOUtils.copy(fis, fos);
				fis.close();
				fos.close();
				final Element filename = config.createElement("filename");
				filename.appendChild(config.createTextNode(audio.getName()));
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
