package placebooks.client.controllers;

import org.wornchaos.client.controller.AbstractReadOnlyController;
import org.wornchaos.client.parser.JavaScriptObjectParser;

import placebooks.client.PlaceBooks;
import placebooks.client.model.ServerInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class ServerInfoController extends AbstractReadOnlyController<ServerInfo>
{
	private static final ServerInfoController instance = new ServerInfoController();

	public static ServerInfoController getController()
	{
		return instance;
	}

	private ServerInfoController()
	{
		super(new JavaScriptObjectParser<ServerInfo>(), "serverinfo");
	}

	@Override
	protected void load(final String id, final AsyncCallback<ServerInfo> callback)
	{
		PlaceBooks.getServer().getServerInfo(callback);
	}
}
