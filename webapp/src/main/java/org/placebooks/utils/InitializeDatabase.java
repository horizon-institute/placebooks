package org.placebooks.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.persistence.EntityManager;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import org.placebooks.controller.EMFSingleton;
import org.placebooks.model.LoginDetails;
import org.placebooks.model.User;
import org.placebooks.services.EverytrailService;
import org.placebooks.services.PeoplesCollectionService;
import org.wornchaos.logger.Log;

public class InitializeDatabase
{
	private static String encodePassword(final String password) throws NoSuchAlgorithmException
	{
		MessageDigest passwordDigest = MessageDigest.getInstance("MD5");
		return (new HexBinaryAdapter()).marshal(passwordDigest.digest(password.getBytes())).toLowerCase();
	}

	
	/**
	 * @param args
	 */
	public static void main(final String[] args)
	{
		final EntityManager manager = EMFSingleton.getTestEntityManager();
		try
		{

			manager.getTransaction().begin();

			final User userk = new User("Kevin Glover", "ktg@cs.nott.ac.uk", encodePassword("test"));
			final User users = new User("Stuart Reeves", "stuart@tropic.org.uk", encodePassword("test"));
			final User userm = new User("Mark Paxton", "mcp@cs.nott.ac.uk", encodePassword("test"));
			final User userd = new User("Mark Davies", "markdavies_@hotmail.com", encodePassword("test"));

			final LoginDetails markdPcwLoginDetails = new LoginDetails(userd,
					PeoplesCollectionService.SERVICE_INFO.getName(), "swnymor", "swnymor", "password");
			userd.add(markdPcwLoginDetails);

			final User alan = new User("Alan Chamberlain", "azc@Cs.Nott.AC.UK", encodePassword("test"));

			final LoginDetails alanPcwLoginDetails = new LoginDetails(alan,
					PeoplesCollectionService.SERVICE_INFO.getName(), "alan_chamberlain", "alan_chamberlain", "g2veji");
			alan.add(alanPcwLoginDetails);

			final User userTest = new User("Everytrail Test User", "everytrail_test@live.co.uk",
					encodePassword("testPass!"));
			final LoginDetails etLoginDetails = new LoginDetails(userTest, EverytrailService.SERVICE_INFO.getName(),
					"275539", "placebooks_everytrail_test", "testPass1!");
			userTest.add(etLoginDetails);
			final LoginDetails pcwLoginDetails = new LoginDetails(userTest,
					PeoplesCollectionService.SERVICE_INFO.getName(), "placebooksTest", "placebooksTest", "testPass1!");
			userTest.add(pcwLoginDetails);

			final User userTest2 = new User("Test User", "placebooks.test@gmail.com",
					encodePassword("testPass!"));
			// final LoginDetails ytLoginDetails = new LoginDetails(userTest2, "YouTube", "",
			// "placebooksTest", "testPass1!");
			final LoginDetails user2etLoginDetails = new LoginDetails(userTest2,
					EverytrailService.SERVICE_INFO.getName(), "", "Placebooks", "testPass1!");
			final LoginDetails user2pcwLoginDetails = new LoginDetails(userTest2,
					PeoplesCollectionService.SERVICE_INFO.getName(), "", "PlacebooksTest", "testPass1!");
			// userTest2.add(ytLoginDetails);
			userTest2.add(user2etLoginDetails);
			userTest2.add(user2pcwLoginDetails);

			manager.persist(userk);
			manager.persist(users);
			manager.persist(userm);
			manager.persist(userd);
			manager.persist(alan);
			manager.persist(userTest);
			manager.persist(userTest2);

			manager.getTransaction().commit();
		}
		catch(Exception e)
		{
			Log.error(e);
		}
		finally
		{
			if (manager.getTransaction().isActive())
			{
				manager.getTransaction().rollback();
			}
			manager.close();
		}
	}
}
