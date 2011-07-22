package placebooks.model;

import java.net.URL;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Lob;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import placebooks.controller.EMFSingleton;
import placebooks.controller.ItemFactory;
import placebooks.controller.SearchHelper;

import com.vividsolutions.jts.geom.Geometry;

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

	public TextItem(final User owner, final Geometry geom, final URL sourceURL,
					final String text)
	{
		super(owner, geom, sourceURL);
		setText(text);
	}

	@Override
	public void appendConfiguration(final Document config, final Element root)
	{
		log.info("TextItem.appendConfiguration(), text=" + this.getText());
		final Element item = getConfigurationHeader(config);
		final Element text = config.createElement("text");
		text.appendChild(config.createTextNode(this.getText()));
		item.appendChild(text);
		root.appendChild(item);
	}

	@Override
	public TextItem deepCopy()
	{
		return new TextItem(this);
	}

	@Override
	public void deleteItemData()
	{
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

	public void setText(final String text)
	{
		this.text = text;
		index.addAll(SearchHelper.getIndex(text));		
	}

	@Override
	public void updateItem(IUpdateableExternal item)
	{
		super.updateItem(item);
		if(item instanceof TextItem)
		{
			setText(((TextItem) item).getText());
		}
	}
}
