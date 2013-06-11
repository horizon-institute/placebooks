package placebooks.client.controllers;

import placebooks.client.PlaceBooks;
import placebooks.client.model.ServerInfo;
import placebooks.client.parser.JavaScriptObjectParser;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class ServerInfoController extends CachedController<ServerInfo>
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
