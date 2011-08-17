package placebooks.model.json;

import placebooks.controller.PropertiesSingleton;
import placebooks.controller.PlaceBooksAdminHelper;

import org.codehaus.jackson.annotate.JsonProperty;

public class ServerInfo
{
	@JsonProperty
	private String openSpaceKey;

	@JsonProperty
	private String openSpaceHost;

	@JsonProperty
	private String serverName;

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
	}

	public final String getOpenSpaceKey()
	{
		return openSpaceKey;
	}

	public final String getOpenSpaceHost()
	{
		return openSpaceHost;
	}

	public final String getServerName()
	{
		return serverName;
	}
}
