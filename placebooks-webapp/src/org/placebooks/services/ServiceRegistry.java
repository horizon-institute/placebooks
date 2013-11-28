package org.placebooks.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.placebooks.client.model.ServiceInfo;
import org.placebooks.controller.CommunicationHelper;
import org.placebooks.controller.PropertiesSingleton;
import org.placebooks.model.LoginDetails;
import org.placebooks.model.User;

public class ServiceRegistry
{
	private static final Map<String, Service> services = new HashMap<String, Service>();

	static
	{
		addService(new EverytrailService());
		addService(new PeoplesCollectionService());
		addService(new DropBoxService());
	}

	public static void addService(final Service service)
	{
		services.put(service.getInfo().getName(), service);
	}

	public static Service getService(final String serviceName)
	{
		return services.get(serviceName);
	}

	public static Iterable<ServiceInfo> getServices()
	{
		final List<ServiceInfo> infos = new ArrayList<ServiceInfo>();
		for (final Service service : services.values())
		{
			infos.add(service.getInfo());
		}
		return infos;
	}

	public static void updateService(final EntityManager manager, final User user, final String serviceName)
	{
		if (user == null) { return; }
		final Service service = services.get(serviceName);
		if (service != null)
		{
			service.sync(manager, user, false, Double.parseDouble(PropertiesSingleton.get(	CommunicationHelper.class
																									.getClassLoader())
					.getProperty(PropertiesSingleton.IDEN_SEARCH_LON, "0")), Double.parseDouble(PropertiesSingleton
					.get(CommunicationHelper.class.getClassLoader()).getProperty(PropertiesSingleton.IDEN_SEARCH_LAT,
																					"0")), Double
					.parseDouble(PropertiesSingleton.get(CommunicationHelper.class.getClassLoader())
							.getProperty(PropertiesSingleton.IDEN_SEARCH_RADIUS, "0")));
		}
	}

	public static void updateServices(final EntityManager manager, final User user)
	{
		if (user == null) { return; }
		for (final LoginDetails details : user.getLoginDetails())
		{
			updateService(manager, user, details.getService());
		}
	}
}
