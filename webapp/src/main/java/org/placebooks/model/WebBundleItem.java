package org.placebooks.model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Transient;

import org.apache.commons.io.FileUtils;
import org.placebooks.controller.EMFSingleton;
import org.placebooks.controller.ItemFactory;
import org.placebooks.controller.PropertiesSingleton;

import com.vividsolutions.jts.geom.Geometry;
import org.wornchaos.logger.Log;

@Entity
public class WebBundleItem extends PlaceBookItem
{
	@Transient
	private BufferedImage thumbnail;

	private String webBundleName;

	private String webBundlePath;

	public WebBundleItem(final User owner, final Geometry geom, final URL sourceURL, final String webBundleName,
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
		webBundleName = new String(w.getWebBundleName());
		webBundlePath = new String(w.getWebBundlePath());
		thumbnail = null;
	}

	WebBundleItem()
	{
	}

	@Override
	public void copyDataToPackage()
	{
		try
		{
			final File from = new File(getWebBundlePath());
			final File to = new File(getPlaceBook().getPlaceBookBinder().getPackagePath() + "/" + getKey());

			FileUtils.copyDirectory(from, to);
		}
		catch (final IOException e)
		{
			Log.error(e.toString());
		}
	}

	@Override
	public WebBundleItem deepCopy()
	{
		return new WebBundleItem(this);
	}

	@Override
	public boolean deleteItemData()
	{
		try
		{
			FileUtils.deleteDirectory(new File(webBundlePath));
			return true;
		}
		catch (final Exception e)
		{
			Log.error(e.toString());
		}

		return false;
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

	// A thumbnail preview markerImage of the webpage - rendered somehow and stored
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.model.PlaceBookItem#SaveUpdatedItem(org.model.PlaceBookItem)
	 */
	@Override
	public PlaceBookItem saveUpdatedItem()
	{
		PlaceBookItem returnItem = this;
		final EntityManager pm = EMFSingleton.getEntityManager();
		PlaceBookItem item;
		try
		{
			pm.getTransaction().begin();
			item = ItemFactory.getExistingItem(this, pm);
			if (item != null)
			{

				item.update(this);
				pm.persist(item);
				returnItem = item;
				Log.debug("Existing item found so updating");
			}
			else
			{
				Log.debug("No existing item found so creating new");
				pm.persist(this);
			}
			pm.getTransaction().commit();
		}
		finally
		{
			if (pm.getTransaction().isActive())
			{
				pm.getTransaction().rollback();
				Log.error("Rolling current delete all transaction back");
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
