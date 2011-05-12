package placebooks.controller;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import placebooks.model.User;

public class UserManager
{
	public static User getCurrentUser(final EntityManager manager)
	{
		final Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails)
		{
			final UserDetails details = ((UserDetails) principal);
			return getUser(manager, details.getUsername());
		}
		return null;
	}

	public static User getUser(final EntityManager manager, final String email)
	{
		final TypedQuery<User> query = manager.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
		query.setParameter("email", email);
		return query.getSingleResult();
	}
}
