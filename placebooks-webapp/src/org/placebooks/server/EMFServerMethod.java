package org.placebooks.server;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.wornchaos.logger.Log;
import org.wornchaos.parser.Parser;
import org.wornchaos.server.AbstractJSONServer;
import org.wornchaos.server.ServerMethod;
import org.wornchaos.server.ServerResponse;

public class EMFServerMethod extends ServerMethod
{
	private final EntityManagerFactory factory;
	
	public EMFServerMethod(final EntityManagerFactory factory, final String prefix, final Method method, final Parser parser)
	{
		super(prefix, method, parser);
		this.factory = factory;
	}

	@Override
	public void invoke(final AbstractJSONServer server, final ServerResponse<?> request, final Object[] parameters) throws IOException, InvocationTargetException, IllegalAccessException
	{
		final EntityManager entityManager = factory.createEntityManager();
		try
		{
			request.put("em", entityManager);
			invoke(server, parameters);
		}
		catch (Exception e)
		{
			entityManager.getTransaction().rollback();			
			Log.error(e);
		}
		finally
		{
			if (entityManager.getTransaction().isActive())
			{
				entityManager.getTransaction().rollback();
				Log.error("Rolling login detail creation");
			}
			entityManager.close();
		}		
	}
}