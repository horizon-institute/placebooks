package placebooks.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import placebooks.controller.CommunicationHelper;
import placebooks.controller.EMFSingleton;
import placebooks.controller.FileHelper;
import placebooks.controller.ItemFactory;
import placebooks.controller.PropertiesSingleton;

import com.vividsolutions.jts.geom.Geometry;

@Entity
public abstract class MediaItem extends PlaceBookItem
{
	@JsonIgnore
	private String path; // Always points to a file, never a dir

	private String hash; // File hash (MD5) - so clients can tell if file has changed

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
		if (path != null && new File(path).exists()) { return path; }
		if (getHash() == null) { return null; }
		log.info("Attempting to fix media path " + path + "for item " + getKey() + " hash " + getHash());
		try
		{
			final File path = getSavePath();
			final File[] files = path.listFiles(new FilenameFilter()
			{
				@Override
				public boolean accept(final File dir, final String name)
				{
					final int index = name.indexOf('.');
					if (name.startsWith(getHash()) && index == getHash().length()) { return true; }
					return false;
				}
			});

			if (files.length == 1)
			{
				setPath(files[0].getPath());
				log.info("Fixed media to " + this.path);
				return this.path;
			}
			log.debug("Can't fix path for: " + this.path);
		}
		catch (final Exception e)
		{
			log.warn(e.getMessage(), e);
		}
		return null;
	}
	
	
	

	protected void copyDataToPackage() throws IOException
	{
		// Check package dir exists already
		final String path = PropertiesSingleton.get(this.getClass().getClassLoader())
				.getProperty(PropertiesSingleton.IDEN_PKG, "") + "/" + getPlaceBook().getKey();

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
		// TODO: check no other items are using this file!
		final EntityManager em = EMFSingleton.getEntityManager();
		TypedQuery<MediaItem> q = em.createQuery("SELECT item FROM MediaItem as item WHERE (item.hash = ?1) AND (item.id != :id)", MediaItem.class);
		q.setParameter(1, this.getHash());
		q.setParameter("id", this.getKey());
		if(q.getResultList().size()==0)
		{
			log.debug("Deleting: " + getPath());
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
		else
		{
			log.debug("File in use by other items, not deleting: " + this.hash);
		}
	}

	public String getPath()
	{
		// First check that the path is valid and try and fix if needed...
		attemptPathFix();
		if(path==null)
		{
			RedownloadItem();
		}
		return path;
	}

	private File getSavePath() throws IOException
	{
		final String path = PropertiesSingleton.get(this.getClass().getClassLoader())
				.getProperty(PropertiesSingleton.IDEN_MEDIA, "");
		final File savePath = new File(path).getAbsoluteFile();
		if (!savePath.exists() && !savePath.mkdirs()) { throw new IOException("Failed to create folder: " + path); }
		if (!savePath.isDirectory()) { throw new IOException("Save Path not a directory"); }
		return savePath;

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
				pm.flush();
				this.deleteItemData();
				returnItem = item;
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
		log.debug("Setting path for item " + this.getKey() + " as " + filepath);
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
			final File mediaFile = new File(mediaItem.getPath()).getAbsoluteFile();
			if (getPath() != null && mediaFile.equals(new File(getPath()).getAbsoluteFile())) { return; }
			if (mediaFile.exists())
			{
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

	public String getHash()
	{
		return hash;
	}

	public void writeDataToDisk(final String name, final InputStream is) throws IOException
	{
		log.info("Writing media item data file '" + name +"' from: " + is);
		String saveName = null;

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

		String dir = PropertiesSingleton.get(this.getClass().getClassLoader()).getProperty(PropertiesSingleton.IDEN_MEDIA, "");
		File tempFile = File.createTempFile("mediaitem-download-", "", getSavePath());
		String tempSaveName = tempFile.getName();
		try
		{
			String tempSavedAs = FileHelper.SaveFile(input, dir, tempSaveName);
			log.debug("Created temporary file: " + tempSavedAs);

			if (md != null)
			{
				hash = String.format("%032x", new BigInteger(1, md.digest()));
				log.debug("File hash is: " + hash);
			}
			else
			{
				hash = Long.toString(System.currentTimeMillis());
				log.error("Couldn't create a hash for file '" + name + "' using time instead: " + hash);
			}

			saveName = hash;
			if (name != null)
			{
				final int extIdx = name.lastIndexOf(".");
				final String ext = name.substring(extIdx + 1, name.length());
				saveName = saveName + "." + ext;
			}
			saveName = dir + File.separator + saveName; 
			log.info("Renaming temp file to: " + saveName);
			boolean renamed = tempFile.renameTo(new File(saveName));
			if(!renamed)
			{
				log.warn("couldn't rename " + tempFile.getAbsolutePath() + " to " + saveName);
			}
		}
		finally
		{
			File tempCheck = new File(tempSaveName);
			if(tempCheck.exists())
			{
				log.debug("Deleting: " + tempCheck.getAbsolutePath());
				tempCheck.delete();
			}
		}
		log.info("Wrote file " + saveName);
		setPath(saveName);
		setTimestamp(new Date());
	}

	/**
	 * Attepmt to redownload an image from sourceURL, return true of ok false if not
	 * @return
	 */
	public boolean RedownloadItem()
	{
		boolean downloaded = true;
		log.warn("Attempting to redownload MediaItem content for " + getKey());
		if(getSourceURL()!=null)
		{
			log.warn("Attempting to redownload from " + getSourceURL());
			try
			{
				final URLConnection conn = CommunicationHelper.getConnection(getSourceURL());
				writeDataToDisk(getSourceURL().getPath(), conn.getInputStream());
			}
			catch(Exception e)
			{
				log.error("Couldn't get media from: " + getSourceURL());
				log.debug(e.getMessage());
			}
			downloaded=true;
		}
		else
		{
			log.error("No source URL for MediaItem " + getKey() + " so can't redownload.");
		}
		return downloaded;
	}

}