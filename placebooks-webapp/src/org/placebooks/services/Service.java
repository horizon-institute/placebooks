package org.placebooks.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;
import org.placebooks.client.model.ServiceInfo;
import org.placebooks.model.LoginDetails;
import org.placebooks.model.PlaceBookItem;
import org.placebooks.model.User;

public abstract class Service
{
	private static final Logger log = Logger.getLogger(Service.class);

	public abstract boolean checkLogin(final String username, final String password);

	public int cleanupItems(final EntityManager manager, final ArrayList<String> itemsToKeep, final User user)
	{
		log.debug("Starting cleanup for " + getInfo().getName() + " " + itemsToKeep.size() + " items to keep");
		int deletedItems = 0;
		try
		{
			manager.getTransaction().begin();
			final TypedQuery<PlaceBookItem> q = manager
					.createQuery(	"SELECT placebookitem FROM PlaceBookItem AS placebookitem "
											+ " WHERE (placebookitem.owner = ?1) AND (placebookitem.placebook IS null)",
									PlaceBookItem.class);
			q.setParameter(1, user);
			final Collection<PlaceBookItem> items = q.getResultList();
			for (final PlaceBookItem placebookitem : items)
			{
				if (placebookitem.getMetadataValue("source").equals(getInfo().getName()))
				{
					if (itemsToKeep.contains(placebookitem.getExternalID()))
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
				log.error("Rolling " + getInfo().getName() + " cleanup back");
			}
		}
		return deletedItems;
	}

	public String getAuthenticationURL(final EntityManager manager, final User user, final String callbackURL)
	{
		return null;
	}

	public abstract ServiceInfo getInfo();

	public void sync(final EntityManager manager, final User user, final boolean force, final double lon,
			final double lat, final double radius)
	{
		final LoginDetails details = user.getLoginDetails(getInfo().getName());

		if (details == null)
		{
			log.error("Service for " + getInfo().getName() + " import failed, login details null");
			return;
		}

		if (!force && details.isSyncInProgress())
		{
			log.info(details.getService() + " sync already in progress");
			return;
		}

		if (!force && !shouldSync(details)) { return; }

		log.info(details.getService() + " sync starting");
		manager.getTransaction().begin();
		details.setSyncInProgress(true);
		details.setLastSync();
		manager.merge(details);
		manager.getTransaction().commit();

		try
		{
			sync(manager, user, details, lon, lat, radius);
		}
		catch (final Exception e)
		{
			log.error(e.getMessage(), e);
		}
		finally
		{
			manager.getTransaction().begin();
			log.info("Synced " + getInfo().getName() + ": " + details.getLastSync());
			if (user.contains(details))
			{
				details.setSyncInProgress(false);
				manager.merge(details);
			}
			else
			{
				manager.remove(details);
			}
			manager.getTransaction().commit();

		}
	}

	protected abstract void search(EntityManager em, User user, double lon, double lat, double radius);

	protected boolean shouldSync(final LoginDetails details)
	{
		if (details.getLastSync() != null)
		{
			log.info("Last sync of " + getInfo().getName() + ": " + details.getLastSync());
			final Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			if (calendar.getTime().before(details.getLastSync()))
			{
				log.debug("Not syncing " + getInfo().getName() + " automatically as last sync date was today.");
				return false;
			}
		}
		return true;
	}

	protected abstract void sync(EntityManager manager, User user, LoginDetails details, double lon, double lat,
			double radius);
}
