package placebooks.utils;

import javax.jdo.PersistenceManager;

import org.springframework.security.authentication.encoding.Md5PasswordEncoder;

import placebooks.controller.PMFSingleton;
import placebooks.model.LoginDetails;
import placebooks.model.User;

public class InitializeDatabase
{
	/**
	 * @param args
	 */
	public static void main(final String[] args)
	{
		final PersistenceManager manager = PMFSingleton.getPersistenceManager();

		manager.currentTransaction().begin();
		manager.newQuery(User.class).deletePersistentAll();

		final Md5PasswordEncoder encoder = new Md5PasswordEncoder();

		final User userk = new User("Kevin Glover", "ktg@cs.nott.ac.uk", encoder.encodePassword("test", null));
		final User users = new User("Stuart Reeves", "stuart@tropic.org.uk", encoder.encodePassword("test", null));
		final User userm = new User("Mark Paxton", "mcp@cs.nott.ac.uk", encoder.encodePassword("test", null));

		final User userTest = new User("Test User", "everytrail_test@live.co.uk", encoder.encodePassword("testPass!", null));
		LoginDetails loginDetails = new LoginDetails(userTest, "Everytrail", "275539", "placebooks_everytrail_test", "testPass1!");
		userTest.add(loginDetails);

		manager.makePersistent(userk);
		manager.makePersistent(users);
		manager.makePersistent(userm);
		manager.makePersistent(userTest);
		
		manager.currentTransaction().commit();
	}
}
