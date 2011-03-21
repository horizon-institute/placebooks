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

		manager.currentTransaction().begin();
		manager.newQuery(User.class).deletePersistentAll();			
		manager.currentTransaction().commit();
		
		manager.currentTransaction().begin();
		final Query query = manager.newQuery(User.class);

		// 3. perform query
		final Collection<User> userCollection = (Collection<User>)query.execute();
		
		assert userCollection.size() == 0;

		manager.currentTransaction().commit();
		
		
		manager.currentTransaction().begin();

		Md5PasswordEncoder encoder = new Md5PasswordEncoder();
		
		User user = new User("Kevin Glover", "ktg@cs.nott.ac.uk", encoder.encodePassword("test", null));

		user = manager.makePersistent(user);

		manager.currentTransaction().commit();

		manager.currentTransaction().begin();
		final Query query2 = manager.newQuery(User.class);

		// 3. perform query
		final Collection<User> users = (Collection<User>)query2.execute();
		
		assert users.size() == 1;
		assert users.iterator().next().getName().equals("Kevin Glover");

		manager.currentTransaction().commit();
	}
}
