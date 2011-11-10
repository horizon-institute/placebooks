package placebooks.model.json;

import placebooks.controller.PlaceBooksAdminHelper;
import placebooks.controller.PropertiesSingleton;

public class ServerInfo
{
	private final String openSpaceKey;

	private final String openSpaceHost;

	private final String openSpaceBaseURL;

	private final String serverName;

	public ServerInfo()
	{
		this.openSpaceKey = 
			PropertiesSingleton
				.get(PlaceBooksAdminHelper.class.getClassLoader())
				.getProperty(PropertiesSingleton.IDEN_OPENSPACE_APIKEY, "");
		this.openSpaceHost = 
			PropertiesSingleton
				.get(PlaceBooksAdminHelper.class.getClassLoader())
				.getProperty(PropertiesSingleton.IDEN_OPENSPACE_HOST, "");
		this.serverName = 
			PropertiesSingleton
				.get(PlaceBooksAdminHelper.class.getClassLoader())
				.getProperty(PropertiesSingleton.IDEN_SERVER_NAME, "");
		this.openSpaceBaseURL = 
			PropertiesSingleton
				.get(PlaceBooksAdminHelper.class.getClassLoader())
				.getProperty(PropertiesSingleton.IDEN_OPENSPACE_BASEURL, "");

	}

	public final String getOpenSpaceKey()
	{
		return openSpaceKey;
	}

	public final String getOpenSpaceHost()
	{
		return openSpaceHost;
	}

	public final String getOpenSpaceBaseURL()
	{
		return openSpaceBaseURL;
	}

	public final String getServerName()
	{
		return serverName;
	}
}
