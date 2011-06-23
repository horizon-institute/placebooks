package placebooks.controller;

import java.util.Collections;

import javax.persistence.EntityManager;

import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import placebooks.model.User;

public class PlaceBooksUserDetailsService implements UserDetailsService
{
	@Override
	public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException, DataAccessException
	{
		final EntityManager manager = EMFSingleton.getEntityManager();
		try
		{
			final User user = UserManager.getUser(manager, email);
			if (user != null)
			{
				@SuppressWarnings("unchecked")
				final UserDetails userDetails = new org.springframework.security.core.userdetails.User(user.getEmail(),
						user.getPasswordHash(), true, true, true, true, Collections.EMPTY_SET);

				return userDetails;
			}
		}
		catch(Exception e)
		{
			System.out.println("Username not found " + email);
			throw new UsernameNotFoundException("Username not found: " + email);			
		}		
		finally
		{
			manager.close();
		}
		throw new UsernameNotFoundException("Username not found: " + email);
	}
}
