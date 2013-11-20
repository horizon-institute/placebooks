package org.placebooks.controller;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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

	public static User getUser(final EntityManager manager, final String email)
	{
		final TypedQuery<User> query = manager.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
		query.setParameter("email", email);
		return query.getSingleResult();
	}

	public static void setUser(final HttpServletRequest request, final User user)
	{
		final UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
				user.getEmail(), user.getPasswordHash());

		// Authenticate the user
		SecurityContextHolder.getContext().setAuthentication(authRequest);

		// Create a new session and add the security context.
		final HttpSession session = request.getSession(true);
		session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
	}
}
