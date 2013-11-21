package org.placebooks.controller;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.placebooks.model.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

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

	public static void setUser(final User user)
	{
		final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPasswordHash());
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
	
	public static User getUser(final EntityManager manager, final String email)
	{
		final TypedQuery<User> query = manager.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
		query.setParameter("email", email);
		return query.getSingleResult();
	}
}
