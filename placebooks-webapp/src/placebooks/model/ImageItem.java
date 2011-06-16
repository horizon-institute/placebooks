package placebooks.model;

import java.net.URL;

import javax.persistence.Entity;
import javax.persistence.EntityManager;

import placebooks.controller.EMFSingleton;
import placebooks.controller.EverytrailHelper;

import com.vividsolutions.jts.geom.Geometry;

@Entity
public class ImageItem extends MediaItem
{
	public ImageItem(final User owner, final Geometry geom, final URL sourceURL,
					 final String image)
	{
		super(owner, geom, sourceURL, image);
	}

	ImageItem()
	{
	}

	public ImageItem(final ImageItem i)
	{
		super(i);
	}

	@Override
	public ImageItem deepCopy()
	{
		return new ImageItem(this);
	}

	@Override
	public String getEntityName()
	{
		return ImageItem.class.getName();
	}
	
	@Override 
	public void update(PlaceBookItem item)
	{
		super.update((MediaItem) item);
	}
	
	/* (non-Javadoc)
	 * @see placebooks.model.PlaceBookItem#SaveUpdatedItem(placebooks.model.PlaceBookItem)
	 */
	@Override
	public PlaceBookItem saveUpdatedItem()
	{
		PlaceBookItem returnItem = this;
		final EntityManager pm = EMFSingleton.getEntityManager();
		ImageItem item;
		try
		{
			pm.getTransaction().begin();
			item = (ImageItem) EverytrailHelper.GetExistingItem(this);
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
