package org.placebooks.client.controllers;

import org.placebooks.client.PlaceBooks;
import org.placebooks.client.model.ServerInfo;
import org.wornchaos.client.controllers.ControllerBase;

public class ServerInfoController extends ControllerBase<ServerInfo>
{
	private static final ServerInfoController instance = new ServerInfoController();

	public static ServerInfoController getController()
	{
		return instance;
	}

	@Override
	public void load(String id)
	{
		PlaceBooks.getServer().getServerInfo(getCallback());
	}

	@Override
	public void refresh()
	{
		load(null);
	}
}
