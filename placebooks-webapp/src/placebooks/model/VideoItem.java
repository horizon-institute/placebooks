package placebooks.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import javax.persistence.Entity;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import placebooks.controller.PropertiesSingleton;

import com.vividsolutions.jts.geom.Geometry;

@Entity
public class VideoItem extends PlaceBookItem
{
	// Videos are stored on disk, not database
	@JsonIgnore
	private File video;

	public VideoItem(final User owner, final Geometry geom, final URL sourceURL, final File video)
	{
		super(owner, geom, sourceURL);
		this.video = video;
	}

	VideoItem()
	{

	}

	@Override
	public void appendConfiguration(final Document config, final Element root)
	{
		final Element item = getConfigurationHeader(config);
		try
		{
			// Check package dir exists already
			final String path = PropertiesSingleton.get(this.getClass().getClassLoader())
					.getProperty(PropertiesSingleton.IDEN_PKG, "") + getPlaceBook().getKey();
			getVideo(); // TODO: work out why I need this here

			if (new File(path).exists() || new File(path).mkdirs())
			{

				final FileInputStream fis = new FileInputStream(video);
				final File to = new File(path + "/" + video.getName());

				log.info("Copying video file, from=" + video.toString() + ", to=" + to.toString());

				final FileOutputStream fos = new FileOutputStream(to);
				IOUtils.copy(fis, fos);
				fis.close();
				fos.close();
				final Element filename = config.createElement("filename");
				filename.appendChild(config.createTextNode(video.getName()));
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
		if (!video.delete())
		{
			log.error("Problem deleting video file " + video.toString());
		}
	}

	@Override
	public String getEntityName()
	{
		return VideoItem.class.getName();
	}

	public String getVideo()
	{
		return video.toString();
	}

	public void setVideo(final String filepath)
	{
		if (filepath != null)
		{
			video = new File(filepath);
		}
	}

}
