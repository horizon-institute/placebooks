package placebooks.services;

import java.util.Calendar;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import placebooks.model.LoginDetails;
import placebooks.model.User;

public abstract class Service
{
	private static final Logger log = Logger.getLogger(Service.class);
	
	protected abstract void sync(EntityManager manager, User user, LoginDetails details);
	
	public abstract boolean checkLogin(final String username, final String password);
	
	public void sync(EntityManager manager, final User user, boolean force)
	{
		final LoginDetails details = user.getLoginDetails(getName());

		if (details == null)
		{
			log.error("Everytrail import failed, login details null");
			return;
		}
		if(details.isSyncInProgress())
		{
			log.info("Everytrail sync already in progress");
			return;			
		}
		
		if(!force && details.getLastSync() != null)
		{
			log.info("Last sync: " + details.getLastSync());
			final Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND,0);
			if(calendar.getTime().before(details.getLastSync()))
			{
				return;
			}		
		}

		sync(manager, user, details);
	}
	
	public abstract String getName();
}
