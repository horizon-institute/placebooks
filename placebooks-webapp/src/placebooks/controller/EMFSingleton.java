package placebooks.controller;

import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.eclipse.persistence.config.PersistenceUnitProperties;

/**
 * Singleton wrapper class with a static instance for the 
 * PersistenceManagerFactory class.
 */
public final class EMFSingleton
{
	private static EntityManagerFactory emFactory = null;

	public static EntityManager getEntityManager()
	{
		if (emFactory == null)
		{
			final Properties properties = 
				PropertiesSingleton.get(EMFSingleton.class.getClassLoader());
			emFactory = Persistence.createEntityManagerFactory("placebooks", 
															   properties);
		}
		return emFactory.createEntityManager();
	}

	public static EntityManager getTestEntityManager()
	{
		final Properties properties = 
			PropertiesSingleton.get(EMFSingleton.class.getClassLoader());
		properties.put(PersistenceUnitProperties.DDL_GENERATION, 
					   PersistenceUnitProperties.DROP_AND_CREATE);

		final EntityManagerFactory factory = 
			Persistence.createEntityManagerFactory("placebooks", properties);
		return factory.createEntityManager();
	}

	private EMFSingleton()
	{
	}
}
