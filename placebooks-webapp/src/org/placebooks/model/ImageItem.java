package org.placebooks.model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.persistence.Entity;

import org.imgscalr.Scalr;
import org.placebooks.controller.PropertiesSingleton;
import org.wornchaos.logger.Log;

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
		final File thumbFile = getThumbFile();
		if (!thumbFile.exists() || (thumbFile.length() == 0))
		{
			Log.warn("Thumbnail file " + thumbFile.getAbsolutePath() + " doesn't exist - attempting to fix");
			createThumbnail();
		}
		return thumbFile.getAbsolutePath();
	}

	private File getThumbFile() throws IOException
	{
		final String thumbPath = PropertiesSingleton.get(this.getClass().getClassLoader())
				.getProperty(PropertiesSingleton.IDEN_THUMBS, "");
		return new File(thumbPath, getHash());
	}
	
	@Override
	public void writeDataToDisk(final InputStream is) throws IOException
	{
		// User this for now before replacing later
		super.writeDataToDisk(is);
		is.close();
		try
		{
			createThumbnail();
		}
		catch(Exception e)
		{
			Log.error(e);
		}
	}

	protected void createThumbnail() throws IOException
	{
		Log.debug("Generating thumbnail from markerImage: " + getPath());
		final File imageFile = new File(getPath());
		final BufferedImage originalImage = ImageIO.read(imageFile);

		final File thumbFile = getThumbFile();
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

			thumbFile.createNewFile();
			ImageIO.write(resizedImage, fmt, thumbFile);
		}
		else
		{
			Log.debug("Thumbnail already existed for " + thumbFile.getAbsoluteFile());
		}
	}
}