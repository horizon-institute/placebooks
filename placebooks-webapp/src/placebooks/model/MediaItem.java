package placebooks.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;

import javax.persistence.Entity;
import javax.persistence.EntityManager;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import placebooks.controller.EMFSingleton;
import placebooks.controller.ItemFactory;
import placebooks.controller.PropertiesSingleton;

import com.vividsolutions.jts.geom.Geometry;

@Entity
public abstract class MediaItem extends PlaceBookItem
{
	@JsonIgnore
	protected String path; // Always points to a file, never a dir

	protected String hash; // File hash (MD5) - so clients can tell if file has changed

	MediaItem()
	{
	}

	public MediaItem(final MediaItem m)
	{
		super(m);
		this.path = m.getPath();
	}

	public MediaItem(final User owner, final Geometry geom, final URL sourceURL, final String file)
	{
		super(owner, geom, sourceURL);
		this.path = file;
	}

	@Override
	public void appendConfiguration(final Document config, final Element root)
	{
		final Element item = getConfigurationHeader(config);

		try
		{
			copyDataToPackage();
			final Element filename = config.createElement("filename");
			filename.appendChild(config.createTextNode(new File(path).getName()));
			item.appendChild(filename);
		}
		catch (final IOException e)
		{
			log.error(e.toString());
		}

		root.appendChild(item);
	}

	public String attemptPathFix()
	{
		if(path != null && new File(path).exists())
		{
			return path;
		}
		
		if (getKey() == null) { return null; }

		log.info("Attempting to fix media path");

		final File path = new File(PropertiesSingleton.get(this.getClass().getClassLoader())
				.getProperty(PropertiesSingleton.IDEN_MEDIA, ""));

		if (!path.exists()) { return null; }

		final File[] files = path.listFiles(new FilenameFilter()
		{
			@Override
			public boolean accept(final File dir, final String name)
			{
				final int index = name.indexOf('.');
				if (name.startsWith(getKey()) && index == getKey().length()) { return true; }
				return false;
			}
		});

		if (files.length == 1)
		{
			setPath(files[0].getPath());
			log.info("Fixed media to " + this.path);
			return this.path;
		}
		return null;
	}

	protected void copyDataToPackage() throws IOException
	{
		// Check package dir exists already
		final String path = PropertiesSingleton.get(this.getClass().getClassLoader())
				.getProperty(PropertiesSingleton.IDEN_PKG, "") + getPlaceBook().getKey();

		if (path != null && (new File(path).exists() || new File(path).mkdirs()))
		{
			final File dataFile = new File(getPath());
			final FileInputStream fis = new FileInputStream(dataFile);
			final File to = new File(path + "/" + dataFile.getName());

			log.info("Copying file, from=" + dataFile.toString() + ", to=" + to.toString());

			final FileOutputStream fos = new FileOutputStream(to);
			IOUtils.copy(fis, fos);
			fis.close();
			fos.close();
		}
	}

	@Override
	public void deleteItemData()
	{
		if (getPath() == null) { return; }
		final File f = new File(getPath());
		if (f.exists())
		{
			f.delete();
		}
		else
		{
			log.error("Problem deleting file " + f.getAbsolutePath());
		}
	}

	public String getPath()
	{
		return path;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see placebooks.model.PlaceBookItem#SaveUpdatedItem(placebooks.model.PlaceBookItem)
	 */
	@Override
	public IUpdateableExternal saveUpdatedItem()
	{
		IUpdateableExternal returnItem = this;
		final EntityManager pm = EMFSingleton.getEntityManager();
		IUpdateableExternal item;
		try
		{
			pm.getTransaction().begin();
			item = ItemFactory.GetExistingItem(this, pm);
			if (item != null)
			{

				log.debug("Existing item found so updating");
				item.update(this);
				log.debug("Deleting file: " + this.getPath());
				this.deleteItemData();
				returnItem = item;
				pm.flush();
			}
			else
			{
				log.debug("No existing item found so creating new");
				pm.persist(this);
			}
			pm.getTransaction().commit();
		}
		finally
		{
			if (pm.getTransaction().isActive())
			{
				pm.getTransaction().rollback();
				log.error("Rolling current delete all transaction back");
			}
			pm.close();
		}
		return returnItem;
	}

	public void setPath(final String filepath)
	{
		this.path = filepath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see placebooks.model.PlaceBookItem#udpateItem(PlaceBookItem)
	 */
	@Override
	public void updateItem(final IUpdateableExternal item)
	{
		super.updateItem(item);
		if (item instanceof MediaItem)
		{
			final MediaItem mediaItem = (MediaItem) item;
			if (mediaItem.getPath() == null) { return; }
			if (mediaItem.getPath().equals(getPath())) { return; }
			log.debug("Looking for " + mediaItem.getPath());
			final File mediaFile = new File(mediaItem.getPath());
			if (mediaFile.exists())
			{
				// @TODO Remove old file?

				try
				{
					writeDataToDisk(mediaFile.getName(), new FileInputStream(mediaFile));
				}
				catch (final Exception e)
				{
					log.error(e.getMessage());
				}
			}
		}
	}

	public void writeDataToDisk(final String name, final InputStream is) throws IOException
	{
		final String path = PropertiesSingleton.get(this.getClass().getClassLoader())
				.getProperty(PropertiesSingleton.IDEN_MEDIA, "");

		if (getKey() == null) { throw new IOException("Key is null"); }

		if (!new File(path).exists() && !new File(path).mkdirs()) { throw new IOException("Failed to write file '"
				+ path + "'"); }

		final int extIdx = name.lastIndexOf(".");
		final String ext = name.substring(extIdx + 1, name.length());

		final String filePath = path + "/" + getKey() + "." + ext;

		InputStream input;
		MessageDigest md = null;
		try
		{
			md = MessageDigest.getInstance("MD5");
			input = new DigestInputStream(is, md);
		}
		catch (final Exception e)
		{
			input = is;
		}

		final OutputStream output = new FileOutputStream(new File(filePath));
		int byte_;
		while ((byte_ = input.read()) != -1)
		{
			output.write(byte_);
		}
		output.close();
		is.close();

		if (md != null)
		{
			hash = String.format("%032x", new BigInteger(1, md.digest()));
		}

		log.info("Wrote " + name + " file " + filePath);
		setPath(filePath);
	}

	public void writeNewFileToDisk(final String name, final InputStream input) throws IOException
	{
		if (getKey() == null)
		{
			final String path = PropertiesSingleton.get(this.getClass().getClassLoader())
					.getProperty(PropertiesSingleton.IDEN_MEDIA, "");

			if (new File(path).exists() || new File(path).mkdirs())
			{
				final String filePath = path + "/" + System.currentTimeMillis() + name;

				log.info("Copying file to=" + filePath);
				final FileOutputStream output = new FileOutputStream(new File(filePath));
				int byte_;
				while ((byte_ = input.read()) != -1)
				{
					output.write(byte_);
				}
				output.close();
				input.close();
				setPath(filePath);
			}
			else
			{
				throw new IOException("Failed to write file '" + path + "'");
			}
		}
		else
		{
			writeDataToDisk(name, input);
		}
	}
}