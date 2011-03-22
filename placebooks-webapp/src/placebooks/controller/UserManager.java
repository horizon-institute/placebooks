package placebooks.controller;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import placebooks.model.User;

public class UserManager
{
	public static User getCurrentUser()
	{
		final Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails)
		{
			UserDetails details = ((UserDetails)principal);
			return getUser(details.getUsername());
		}
		return null;
	}

	public static User getUser(final PersistenceManager manager, final String email)
	{
		manager.currentTransaction().begin();

		final Query query = manager.newQuery(User.class, "email == '" + email + "'");
		final Object result = query.execute();
		manager.currentTransaction().commit();
		if (result instanceof User) { return (User) result; }

		return null;
	}

	public static User getUser(final String email)
	{
		final PersistenceManager manager = PMFSingleton.get().getPersistenceManager();
		try
		{
			return getUser(manager, email);
		}
		finally
		{
			manager.close();
		}
	}

	public static User login()
	{
		return null;
	}

	public static void logout()
	{
	}
}
