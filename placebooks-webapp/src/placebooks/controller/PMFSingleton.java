package placebooks.controller;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

import java.util.Properties;


/**
 * Singleton wrapper class with a static instance for the 
 * PersistenceManagerFactory class.
 */
public final class PMFSingleton 
{
    private static final PersistenceManagerFactory pmfInstance =
		JDOHelper.getPersistenceManagerFactory("default");

	private static Properties propInstance;

    private PMFSingleton() {}

    public static PersistenceManagerFactory get() 
	{
        return pmfInstance;
    }
}
