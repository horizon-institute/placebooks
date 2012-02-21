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
import java.security.NoSuchAlgorithmException;
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

	/**
	 * Attempt to find the file for this item and set the path accordingly
	 * @return the path for the attached file, or null
	 */
	public String attemptPathFix()
	{
		String pathToReturn = null;
		if (path != null && new File(path).exists()) { return path; }
		if ((getHash() != null) || (getKey()!=null))
		{
			log.info("Attempting to fix media path '" + path + "' for item '" + getKey() + "' hash '" + getHash() + "'");
			try
			{
				pathToReturn = FileHelper.FindClosestFile(getSavePath().getAbsolutePath(), hash);
			}
			catch (final Exception e)
			{
				log.warn(e.getMessage(), e);
			}
		}
		if(pathToReturn==null)
		{
			log.debug("Can't fix path for:" + this.path + " for item " + getKey() + " hash " + getHash());
		}
		setPath(pathToReturn);
		log.info("Set media path to " + this.path);
		return this.path;
	}


	protected void copyDataToPackage() throws IOException
	{
		// Check package dir exists already
		final String path = PropertiesSingleton.get(this.getClass().getClassLoader())
				.getProperty(PropertiesSingleton.IDEN_PKG, "") + "/" + getPlaceBook().getPlaceBookBinder().getKey();

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
	public boolean deleteItemData()
	{
		// TODO: check no other items are using this file!
		final EntityManager em = EMFSingleton.getEntityManager();
		TypedQuery<MediaItem> q = em.createQuery("SELECT item FROM MediaItem as item WHERE (item.hash = ?1) AND (item.id != :id)", MediaItem.class);
		q.setParameter(1, this.getHash());
		q.setParameter("id", this.getKey());
		if(q.getResultList().size()==0)
		{
			log.debug("Deleting: " + this.path);
			if (getPath() == null) { return false; }
			final File f = new File(this.path);
			if (f.exists())
			{
				f.delete();
				return true;
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
		return true;
	}

	public String getPath()
	{
		// First check that the path is valid and try and fix if needed...
		attemptPathFix();
		if(path==null)
		{
			//RedownloadItem();
		}
		return path;
	}

	/**
	 * Gets the path for and creates the saved items folder
	 * @return File Folder to save files in 
	 * @throws IOException
	 */
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
			
			// The new version has no file... so delete the existing one
			if (mediaItem.getPath() == null)
			{
				this.deleteItemData();
				this.hash=null;
				return;
			}
			
			// Otherwise check that the new file exists, and that there's an existing file and it's not the same as the new file
			final File mediaFile = new File(mediaItem.getPath()).getAbsoluteFile();
			if (getPath() != null && mediaFile.equals(new File(getPath()).getAbsoluteFile())) { return; }
			
			// there is a new file to replace the existing one, so delete the old one (if no others are using it...)
			if (mediaFile.exists())
			{
				this.deleteItemData();
				setPath(mediaFile.getAbsolutePath());
				hash = mediaItem.hash;
			}
		}
	}

	public String getHash()
	{
		return hash;
	}


	/**
	 * Writes an item to disk for the mediaitem and save it using the files MD5 hash+original file extension to ensure only one copy of each file.
	 * @param name The original name of the file
	 * @param is InputStream for the file
	 * @param originalURL
	 * @throws IOException
	 * @throws NoSuchAlgorithmException 
	 */
	public void writeDataToDisk(final String name, final InputStream is, String originalURL) throws IOException
	{
		String urlHash = name;
		try
		{
			urlHash = MessageDigest.getInstance("MD5").digest(originalURL.getBytes()).toString();
		}
		catch (Exception ex) 
		{
			log.debug(ex.getMessage());
		}
		File downloadFile = new File(urlHash);
		if(!downloadFile.exists())
		{
			writeDataToDisk(urlHash, is);
		}
		else
		{
			// If it exists 
			// Check if it's an old download by seeing if it's been modified in the last hour...
			long timeout = System.currentTimeMillis() - (1000 * 3600);
			if(downloadFile.lastModified() < timeout)
			{
				// Delete the old file and download again...
				downloadFile.delete();
				writeDataToDisk(urlHash, is);
			}	
			else
			{
				log.warn("Download already in progress... do nothing");
			}
		}
	}
	
	
	/**
	 * Writes an item to disk for the mediaitem and save it using the files MD5 hash+original file extension to ensure only one copy of each file.
	 * @param name The original name of the file
	 * @param is InputStream for the file
	 * @throws IOException
	 */
	public void writeDataToDisk(final String name, final InputStream is) throws IOException
	{
		if (this.path != null && (new File(this.path)).exists())
		{
			log.info("Cleaning existing file");
			deleteItemData();
		}
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
		log.info("name=" + name + "; " + getSavePath().toString());
		final File tempFile = File.createTempFile(name, "", getSavePath());
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
			final File tempCheck = new File(tempSaveName);
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
	 * Attempt to redownload an image from sourceURL, return true of ok false if not
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
				writeDataToDisk(getSourceURL().getPath(), conn.getInputStream(), getSourceURL().toString());
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
