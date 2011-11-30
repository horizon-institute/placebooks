package placebooks.model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Transient;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import placebooks.controller.EMFSingleton;
import placebooks.controller.ItemFactory;
import placebooks.controller.PropertiesSingleton;

import com.vividsolutions.jts.geom.Geometry;

@Entity
public class WebBundleItem extends PlaceBookItem
{

	private static final Logger log = 	
		Logger.getLogger(WebBundleItem.class.getName());

	@Transient
	private BufferedImage thumbnail;

	private String webBundleName;

	private String webBundlePath;

	WebBundleItem()
	{
	}

	public WebBundleItem(final User owner, final Geometry geom, 
						 final URL sourceURL, final String webBundleName,
						 final String webBundlePath)
	{
		super(owner, geom, sourceURL);
		this.webBundleName = webBundleName;
		this.webBundlePath = webBundlePath;
		thumbnail = null;
	}

	public WebBundleItem(final WebBundleItem w)
	{
		super(w);
		this.webBundleName = new String(w.getWebBundleName());
		this.webBundlePath = new String(w.getWebBundlePath());		
		thumbnail = null;
	}

	@Override
	public void appendConfiguration(final Document config, final Element root)
	{
		final Element item = getConfigurationHeader(config);

		try
		{
			final File from = new File(getWebBundlePath());
			final File to = new File(getPlaceBook().getPlaceBookBinder().getPackagePath() 
									 + "/" + getKey());

			FileUtils.copyDirectory(from, to);

			final Element filename = config.createElement("filename");
			filename.appendChild(config.createTextNode(getWebBundle()));
			item.appendChild(filename);
		}
		catch (final IOException e)
		{
			log.error(e.toString());
		}

		root.appendChild(item);
	}

	@Override
	public WebBundleItem deepCopy()
	{
		return new WebBundleItem(this);
	}

	@Override
	public void deleteItemData()
	{
		try
		{
			FileUtils.deleteDirectory(new File(webBundlePath));
		}
		catch (final Exception e)
		{
			log.error(e.toString());
		}
	}

	public String generateWebBundlePath()
	{
		return PropertiesSingleton.get(this.getClass().getClassLoader())
				.getProperty(PropertiesSingleton.IDEN_WEBBUNDLE, "") + "/" + getKey();
	}

	@Override
	public String getEntityName()
	{
		return WebBundleItem.class.getName();
	}

	// A thumbnail preview image of the webpage - rendered somehow and stored
	// here
	public BufferedImage getPreview()
	{
		if (thumbnail == null)
		{
			// Thumbnail rendering functionality TODO
		}
		return thumbnail;
	}

	public String getWebBundle()
	{
		return webBundlePath + "/" + webBundleName;
	}
	public String getWebBundleName()
	{
		return webBundleName;
	}

	public String getWebBundlePath()
	{
		return webBundlePath;
	}

	/* (non-Javadoc)
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
			if(item != null)
			{
				
				item.update(this);
				pm.persist(item);
				returnItem = item;
				log.debug("Existing item found so updating");
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

	public void setWebBundleName(final String name)
	{
		webBundleName = name;
	}

	public void setWebBundlePath(final String path)
	{
		webBundlePath = path;
	}
}