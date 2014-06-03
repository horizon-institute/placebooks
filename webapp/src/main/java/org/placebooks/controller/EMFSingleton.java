package org.placebooks.controller;

import com.google.appengine.api.utils.SystemProperty;
import org.eclipse.persistence.config.PersistenceUnitProperties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Properties;

/**
 * Singleton wrapper class with a static instance for the PersistenceManagerFactory class.
 */
public final class EMFSingleton
{
	private static EntityManagerFactory emFactory = null;

	public static EntityManager getEntityManager()
	{
		if (emFactory == null)
		{
			final Properties properties = PropertiesSingleton.get(EMFSingleton.class.getClassLoader());
			if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production)
			{
				properties.put("javax.persistence.jdbc.driver", "com.mysql.jdbc.GoogleDriver");
				properties.put("javax.persistence.jdbc.url", System.getProperty("cloudsql.url"));
			}
			else
			{
				properties.put("javax.persistence.jdbc.driver", "com.mysql.jdbc.Driver");
				properties.put("javax.persistence.jdbc.url", System.getProperty("cloudsql.url.dev"));
			}
			emFactory = Persistence.createEntityManagerFactory("org", properties);
		}
		return emFactory.createEntityManager();
	}

	public static EntityManager getTestEntityManager()
	{
		final Properties properties = PropertiesSingleton.get(EMFSingleton.class.getClassLoader());
		properties.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.DROP_AND_CREATE);

		final EntityManagerFactory factory = Persistence.createEntityManagerFactory("org", properties);
		return factory.createEntityManager();
	}

	private EMFSingleton()
	{
	}
}
