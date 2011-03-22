package placebooks.controller;

import java.util.Collection;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.junit.Test;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;

import placebooks.model.User;

public class UserTest
{
	@Test
	public void newUserTest() throws Exception
	{
		final PersistenceManager manager = PMFSingleton.getPersistenceManager();
		try
		{
		
		manager.currentTransaction().begin();
		manager.newQuery(User.class).deletePersistentAll();			
		manager.currentTransaction().commit();
		
		manager.currentTransaction().begin();
		final Query query = manager.newQuery(User.class);

		@SuppressWarnings("unchecked")
		final Collection<User> userCollection = (Collection<User>)query.execute();
		
		assert userCollection.size() == 0;

		manager.currentTransaction().commit();
		
		
		manager.currentTransaction().begin();

		Md5PasswordEncoder encoder = new Md5PasswordEncoder();
		
		User user1 = new User("Kevin Glover", "ktg@cs.nott.ac.uk", encoder.encodePassword("test", null));
		User user2 = new User("Stuart Reeves", "stuart@tropic.org.uk",  encoder.encodePassword("test", null));
		User user3 = new User("Mark Paxton", "mcp@cs.nott.ac.uk",  encoder.encodePassword("test", null));		

		manager.makePersistent(user1);
		manager.makePersistent(user2);
		manager.makePersistent(user3);			

		user1.add(user2);
		user3.add(user2);
		
		manager.refresh(user1);
		manager.refresh(user3);		
		
		manager.currentTransaction().commit();

		manager.currentTransaction().begin();
		final Query query2 = manager.newQuery(User.class);

		@SuppressWarnings("unchecked")		
		final Collection<User> users = (Collection<User>)query2.execute();
		
		assert users.size() == 1;
		assert users.iterator().next().getName().equals("Kevin Glover");

		manager.currentTransaction().commit();
		
		}
		finally
		{
			if(manager.currentTransaction().isActive())
			{
				manager.currentTransaction().rollback();
			}
			manager.close();
		}
	}
}
