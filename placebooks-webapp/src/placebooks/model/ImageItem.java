package placebooks.model;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.persistence.Entity;

import org.imgscalr.Scalr;

import placebooks.controller.CommunicationHelper;
import placebooks.controller.FileHelper;
import placebooks.controller.PropertiesSingleton;

import com.vividsolutions.jts.geom.Geometry;

@Entity
public class ImageItem extends MediaItem
{
	// private static int THUMB_HEIGHT = 128;
	private static int THUMB_WIDTH = 128;

	public ImageItem()
	{
	}

	public ImageItem(final ImageItem i)
	{
		super(i);
	}

	public ImageItem(final User owner, final Geometry geom, final URL sourceURL, final String image)
	{
		super(owner, geom, sourceURL, image);
	}

	@Override
	public ImageItem deepCopy()
	{
		return new ImageItem(this);
	}

	@Override
	public boolean deleteItemData()
	{
		if (super.deleteItemData())
		{
			final File tf = new File(PropertiesSingleton.get(this.getClass().getClassLoader())
					.getProperty(PropertiesSingleton.IDEN_THUMBS, "") + File.separator + getHash());

			if (tf.exists())
			{
				tf.delete();
				return true;
			}

		}
		return false;
	}

	@Override
	public String getEntityName()
	{
		return ImageItem.class.getName();
	}

	public String getThumbPath() throws IOException
	{
		final String thumbPath = PropertiesSingleton.get(this.getClass().getClassLoader())
				.getProperty(PropertiesSingleton.IDEN_THUMBS, "") + File.separator + getHash();
		final File thumbFile = new File(thumbPath);
		if (!thumbFile.exists() || (thumbFile.length() == 0))
		{
			log.warn("Thumbnail file " + thumbPath + " doesn't exist - attempting to fix");
			generateThumbnail(thumbPath);
		}
		return thumbPath;
	}

	@Override
	public void writeDataToDisk(final String name, final InputStream is) throws IOException
	{
		// User this for now before replacing later
		super.writeDataToDisk(name, is);
		is.close();
		createThumbnail();
	}

	protected void createThumbnail() throws IOException
	{
		log.debug("Generating thumbnail from markerImage: " + getPath());
		final File imageFile = new File(getPath());
		final BufferedImage originalImage = ImageIO.read(imageFile);

		final String thumbPath = FileHelper.GetSavePath(PropertiesSingleton.get(this.getClass().getClassLoader())
				.getProperty(PropertiesSingleton.IDEN_THUMBS, ""));
		final File thumbFile = new File(thumbPath + File.separator + getHash());

		if (!thumbFile.exists() || (thumbFile.length() == 0))
		{
			final BufferedImage resizedImage = Scalr.resize(originalImage, THUMB_WIDTH);
			final Iterator<ImageReader> readers = ImageIO.getImageReaders(resizedImage);
			String fmt = "png";
			while (readers.hasNext())
			{
				final ImageReader read = readers.next();
				fmt = read.getFormatName();
				read.dispose();
			}

			final ByteArrayOutputStream os = new ByteArrayOutputStream();
			ImageIO.write(resizedImage, fmt, os);
			final InputStream thumbImageStream = new ByteArrayInputStream(os.toByteArray());
			final String savedAs = FileHelper.WriteFile(thumbPath, getHash(), thumbImageStream);
			thumbImageStream.close();

			log.debug("Saved thumbnail as: " + savedAs);
		}
		else
		{
			log.debug("Thumbnail already existed for " + thumbFile.getAbsoluteFile());
		}
	}

	private void generateThumbnail(final String thumbPath) throws IOException
	{
		log.debug("Generating thumbnail for: " + getKey());
		final File thumbFile = new File(thumbPath);
		if (!thumbFile.exists() || (thumbFile.length() == 0))
		{
			// Generate thumbnail and save with hash of original file
			final String originalPath = getPath();
			if (originalPath != null)
			{
				log.debug("Creating thumbnail from existing file: " + getPath());
				createThumbnail();
			}
			else
			{
				log.warn("Original markerImage '" + getPath() + "' does not exist for ImageItem " + getKey());
				if (getSourceURL() != null)
				{
					log.warn("Attempting to redownload from " + getSourceURL());
					final URLConnection conn = CommunicationHelper.getConnection(getSourceURL());
					writeDataToDisk(getSourceURL().getPath(), conn.getInputStream(), getSourceURL().toString());
				}
				else
				{
					log.error("Can't locate source markerImage for ImageItem " + getKey());
				}
			}
		}
		else
		{
			log.debug("Thumbnail file exists already for " + getHash());
		}
	}
}
