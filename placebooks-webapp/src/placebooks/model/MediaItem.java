package placebooks.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.persistence.Entity;
import javax.persistence.EntityManager;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import placebooks.controller.EMFSingleton;
import placebooks.controller.EverytrailHelper;
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

			log.info("Copying file, from=" + dataFile.toString() + ", to=" + to.toString());

			final FileOutputStream fos = new FileOutputStream(to);
			IOUtils.copy(fis, fos);
			fis.close();
			fos.close();
		}
	}
	
	public void writeDataToDisk(String name, InputStream input) 
		throws IOException
	{
		final String path = 
			PropertiesSingleton
				.get(this.getClass().getClassLoader())
				.getProperty(PropertiesSingleton.IDEN_MEDIA, "");

		if(getKey() == null) { throw new IOException("Key is null"); }
		
		if (!new File(path).exists() && !new File(path).mkdirs()) 
		{
			throw new IOException("Failed to write file '" + path + "'"); 
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
	
	/* (non-Javadoc)
	 * @see placebooks.model.PlaceBookItem#udpate(PlaceBookItem)
	 */
	@Override
	public void update(PlaceBookItem item) 
	{
		super.update(item);
		if(item instanceof MediaItem)
		{
			MediaItem updatedItem = (MediaItem) item;
			//Overwrite existing file by saving new file in the existing folder with the existing name
			// Check it exists first though
			if (new File(updatedItem.getPath()).exists())
			{
				if (new File(this.getPath()).exists() || new File(this.getPath()).mkdirs())
				{
					final File dataFile = new File(updatedItem.getPath());
					FileInputStream fis;
					try
					{
						fis = new FileInputStream(dataFile);
						this.writeDataToDisk(new File(this.getPath()).getName(), fis);
						fis.close();
					}
					catch (Exception e)
					{
						log.error(e.getMessage());
					}
				}
			}
		}
	}

	
	/* (non-Javadoc)
	 * @see placebooks.model.PlaceBookItem#SaveUpdatedItem(placebooks.model.PlaceBookItem)
	 */
	@Override
	public PlaceBookItem saveUpdatedItem()
	{
		PlaceBookItem returnItem = this;
		final EntityManager pm = EMFSingleton.getEntityManager();
		MediaItem item;
		try
		{
			pm.getTransaction().begin();
			item = (MediaItem) EverytrailHelper.GetExistingItem(this);
			if(item != null)
			{
				
				log.debug("Existing item found so updating");
				item.update(this);
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
		}
		return returnItem;
	}
	
}
