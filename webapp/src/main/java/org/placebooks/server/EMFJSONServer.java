package org.placebooks.server;

import java.lang.reflect.Method;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.wornchaos.client.server.AsyncCallback;
import org.wornchaos.parser.Parser;
import org.wornchaos.parser.gson.GsonParser;
import org.wornchaos.server.AbstractJSONServer;
import org.wornchaos.server.Exclude;
import org.wornchaos.server.ServerMethod;
import org.wornchaos.server.ServerResponse;

public abstract class EMFJSONServer extends AbstractJSONServer
{
	private EntityManagerFactory entityManagerFactory = null;

	protected EntityManager getEntityManager(AsyncCallback<?> callback)
	{
		final ServerResponse<?> response = getResponse(callback);
		return (EntityManager)response.get("em");
	}
	
	private EntityManagerFactory getEntityManagerFactory()
	{
		if(entityManagerFactory == null)
		{
			entityManagerFactory = createEntityManagerFactory();
		}
		return entityManagerFactory;
	}
	
	protected abstract EntityManagerFactory createEntityManagerFactory();
	
	@Override
	protected ServerMethod createServerMethod(final String prefix, final Method ifaceMethod, final Method implMethod)
	{
		return new EMFServerMethod(getEntityManagerFactory(), prefix, ifaceMethod, createParser(implMethod));
	}

	@Override
	protected Parser createParser(Method method)
	{
		if(method != null)
		{
			final Exclude exclude = method.getAnnotation(Exclude.class);
			if(exclude != null)
			{
				return new GsonParser(exclude.value());
			}
		}
		return new GsonParser();
	}	
}
