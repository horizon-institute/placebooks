package placebooks.controller;

import java.util.Collection;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import placebooks.model.User;

public class UserManager
{
	public static User getCurrentUser(final PersistenceManager manager)
	{
		final Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails)
		{
			final UserDetails details = ((UserDetails) principal);
			return getUser(manager, details.getUsername());
		}
		return null;
	}

	public static User getUser(final PersistenceManager manager, final String email)
	{
		final Query query = manager.newQuery(User.class, "email == \"" + email.toLowerCase() + "\"");
		final Object result = query.execute();
		@SuppressWarnings("unchecked")
		final Collection<User> users = (Collection<User>) result;
		if (!users.isEmpty()) { return users.iterator().next(); }

		return null;
	}
}
