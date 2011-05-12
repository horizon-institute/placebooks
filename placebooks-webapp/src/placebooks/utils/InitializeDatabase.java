package placebooks.utils;

import javax.persistence.EntityManager;

import org.springframework.security.authentication.encoding.Md5PasswordEncoder;

import placebooks.controller.EMFSingleton;
import placebooks.model.LoginDetails;
import placebooks.model.User;

public class InitializeDatabase
{
	/**
	 * @param args
	 */
	public static void main(final String[] args)
	{
		final EntityManager manager = EMFSingleton.getTestEntityManager();

		manager.getTransaction().begin();
		manager.createQuery("DELETE FROM User", User.class).executeUpdate();

		final Md5PasswordEncoder encoder = new Md5PasswordEncoder();

		final User userk = new User("Kevin Glover", "ktg@cs.nott.ac.uk", encoder.encodePassword("test", null));
		final User users = new User("Stuart Reeves", "stuart@tropic.org.uk", encoder.encodePassword("test", null));
		final User userm = new User("Mark Paxton", "mcp@cs.nott.ac.uk", encoder.encodePassword("test", null));

		final User userTest = new User("Everytrail Test User", "everytrail_test@live.co.uk",
				encoder.encodePassword("testPass!", null));
		final LoginDetails loginDetails = new LoginDetails(userTest, "Everytrail", "275539",
				"placebooks_everytrail_test", "testPass1!");
		userTest.add(loginDetails);

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
}
