package placebooks.controller;

import javax.jdo.PersistenceManager;

import org.junit.Test;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;

import placebooks.model.User;

public class UserTest
{
	private static String testName = "Testy McTesterson";
	private static String testEmail = "testy@mctesterson.co.uk";
	
	@Test
	public void newUserTest() throws Exception
	{
		final PersistenceManager manager = PMFSingleton.getPersistenceManager();
		try
		{
			final Md5PasswordEncoder encoder = new Md5PasswordEncoder();			
			User user = new User(testName, testEmail, encoder.encodePassword("test", null));
	
			manager.currentTransaction().begin();
			user = manager.makePersistent(user);
			manager.currentTransaction().commit();
			
			assert user.getKey() != null;
			assert !user.getKey().equals("");
			
			user = UserManager.getUser(manager, testEmail);
			
			assert user != null;
			assert user.getName().equals(testName);
			assert user.getEmail().equals(testEmail.toLowerCase());
			
			manager.currentTransaction().begin();
			manager.deletePersistent(user);
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
