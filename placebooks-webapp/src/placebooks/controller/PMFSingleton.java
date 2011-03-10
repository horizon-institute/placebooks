package placebooks.controller;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;


/**
 * Singleton wrapper class with a static instance for the 
 * PersistenceManagerFactory class.
 */
public final class PMFSingleton 
{
    private static final PersistenceManagerFactory pmfInstance =
		JDOHelper.getPersistenceManagerFactory("default");

    private PMFSingleton() {}

    public static PersistenceManagerFactory get() 
	{
        return pmfInstance;
    }
}
