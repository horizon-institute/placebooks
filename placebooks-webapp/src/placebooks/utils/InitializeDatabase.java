package placebooks.utils;

import javax.persistence.EntityManager;

import org.springframework.security.authentication.encoding.Md5PasswordEncoder;

import placebooks.controller.EMFSingleton;
import placebooks.model.LoginDetails;
import placebooks.model.User;
import placebooks.services.EverytrailService;
import placebooks.services.PeoplesCollectionService;

public class InitializeDatabase
{
	/**
	 * @param args
	 */
	public static void main(final String[] args)
	{
		final EntityManager manager = EMFSingleton.getTestEntityManager();
		try
		{
		
			manager.getTransaction().begin();
	
			final Md5PasswordEncoder encoder = new Md5PasswordEncoder();
	
			final User userk = new User("Kevin Glover", "ktg@cs.nott.ac.uk", encoder.encodePassword("test", null));
			final User users = new User("Stuart Reeves", "stuart@tropic.org.uk", encoder.encodePassword("test", null));
			final User userm = new User("Mark Paxton", "mcp@cs.nott.ac.uk", encoder.encodePassword("test", null));
			final User userd = new User("Mark Davies", "markdavies_@hotmail.com", encoder.encodePassword("test", null));
			
			final User userTest = new User("Everytrail Test User", "everytrail_test@live.co.uk",
					encoder.encodePassword("testPass!", null));
			final LoginDetails etLoginDetails = new LoginDetails(userTest, EverytrailService.SERVICE_NAME, "275539",
					"placebooks_everytrail_test", "testPass1!");
			userTest.add(etLoginDetails);
			final LoginDetails pcwLoginDetails = new LoginDetails(userTest, PeoplesCollectionService.SERVICE_NAME, "placebooksTest",
					"placebooksTest", "testPass1!");
			userTest.add(pcwLoginDetails);
	
			final User userYTTest = new User("Youtube Test User", "placebooks.test@gmail.com",
					encoder.encodePassword("testPass!", null));
			final LoginDetails ytLoginDetails = new LoginDetails(userYTTest, "YouTube", "", "placebooksTest", "testPass1!");
			userYTTest.add(ytLoginDetails);
	
			manager.persist(userk);
			manager.persist(users);
			manager.persist(userm);
			manager.persist(userTest);
			manager.persist(userYTTest);
	
			manager.getTransaction().commit();
		}
		finally 
		{
			if(manager.getTransaction().isActive())
			{
				manager.getTransaction().rollback();
			}
			manager.close();
		}
	}
}
