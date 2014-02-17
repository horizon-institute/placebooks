package org.placebooks.model;

import java.net.URL;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Lob;

import org.placebooks.controller.EMFSingleton;
import org.placebooks.controller.ItemFactory;
import org.placebooks.controller.SearchHelper;

import com.vividsolutions.jts.geom.Geometry;
import org.wornchaos.logger.Log;

@Entity
public class TextItem extends PlaceBookItem
{
	@Lob
	private String text;

	public TextItem()
	{
		super();
	}

	public TextItem(final TextItem t)
	{
		super(t);
		setText(new String(t.getText()));
	}

	public TextItem(final User owner, final Geometry geom, final URL sourceURL, final String text)
	{
		super(owner, geom, sourceURL);
		setText(text);
	}

	@Override
	public void copyDataToPackage()
	{
	}

	@Override
	public TextItem deepCopy()
	{
		return new TextItem(this);
	}

	@Override
	public boolean deleteItemData()
	{
		return true;
	}

	@Override
	public String getEntityName()
	{
		return TextItem.class.getName();
	}

	public String getText()
	{
		return text;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see placebooks.model.PlaceBookItem#SaveUpdatedItem(placebooks.model.PlaceBookItem)
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

	public void setText(final String text)
	{
		this.text = text;
		index.clear();
		index.addAll(SearchHelper.getIndex(text.replaceAll("\\<.*?\\>", "")));
	}

	@Override
	public void updateItem(final PlaceBookItem item)
	{
		super.updateItem(item);
		if (item instanceof TextItem)
		{
			setText(((TextItem) item).getText());
		}
	}
}
