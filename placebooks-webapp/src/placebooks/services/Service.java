package placebooks.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;

import placebooks.model.LoginDetails;
import placebooks.model.PlaceBookItem;
import placebooks.model.User;

public abstract class Service
{
	private static final Logger log = Logger.getLogger(Service.class);
	
	protected abstract void sync(EntityManager manager, User user, LoginDetails details, double lon, double lat, double radius);

	protected abstract void search(EntityManager em, User user, double lon, double lat, double radius);
	
	public abstract boolean checkLogin(final String username, final String password);
	
	public String getAuthenticationURL(final EntityManager manager, final User user, final String callbackURL)
	{
		return null;
	}
	
	protected boolean shouldSync(LoginDetails details)
	{
		if(details.getLastSync() != null)
		{
			log.info("Last sync of " + this.getInfo().getName()  + ": " + details.getLastSync());
			final Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND,0);
			if(calendar.getTime().before(details.getLastSync()))
			{
				log.debug("Not syncing " + this.getInfo().getName()  + " automatically as last sync date was today.");
				return false;
			}		
		}		
		return true;
	}
	
	public void sync(EntityManager manager, final User user, boolean force, double lon, double lat, double radius)
	{
		final LoginDetails details = user.getLoginDetails(this.getInfo().getName());

		if (details == null)
		{
			log.error("Service for " + this.getInfo().getName() + " import failed, login details null");
			return;
		}
		
		if(!force && details.isSyncInProgress())
		{
			log.info(details.getService() +  " sync already in progress");
			return;			
		}
		
		if(!force && !shouldSync(details))
		{
			return;
		}

		log.info(details.getService() +  " sync starting");
		manager.getTransaction().begin();
		details.setSyncInProgress(true);
		details.setLastSync();
		manager.merge(details);
		manager.getTransaction().commit();			

		try
		{
			sync(manager, user, details, lon, lat, radius);
		}
		catch(Exception e)
		{
			log.error(e.getMessage(), e);
		}
		finally
		{
			manager.getTransaction().begin();			
			details.setSyncInProgress(false);
			manager.merge(details);
			manager.getTransaction().commit();
			log.info("Synced " + this.getInfo().getName() +": " + details.getLastSync());			
		}
	}
	
	public abstract ServiceInfo getInfo();
	
	public int cleanupItems(EntityManager manager, ArrayList<String> itemsToKeep, User user)
	{
		log.debug("Starting cleanup for " + this.getInfo().getName() + " " + itemsToKeep.size() + " items to keep");
		int deletedItems = 0;
		try
		{
			manager.getTransaction().begin();
			TypedQuery<PlaceBookItem> q = manager.createQuery("SELECT placebookitem FROM PlaceBookItem AS placebookitem " +
					" WHERE (placebookitem.owner = ?1) AND (placebookitem.placebook IS null)", PlaceBookItem.class);
			q.setParameter(1, user);
			Collection<PlaceBookItem> items = q.getResultList();
			for(PlaceBookItem placebookitem: items)
			{
				if(placebookitem.getMetadataValue("source").equals(this.getInfo().getName()))
				{
					if(itemsToKeep.contains(placebookitem.getExternalID()))
					{
						log.debug("Keeping item: " + placebookitem.getExternalID() + " id: " + placebookitem.getKey());
					}
					else
					{
						log.debug("Removing item: " + placebookitem.getExternalID() + " id: " + placebookitem.getKey());
						manager.remove(placebookitem);
						deletedItems++;
					}
				}
			}
			manager.getTransaction().commit();
		}
		finally
		{
			if (manager.getTransaction().isActive())
			{
				manager.getTransaction().rollback();
				log.error("Rolling " + this.getInfo().getName() +" cleanup back");
			}
		}	
		return deletedItems;
	}
}
