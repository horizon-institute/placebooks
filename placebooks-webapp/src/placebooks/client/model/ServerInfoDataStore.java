package placebooks.client.model;

public class ServerInfoDataStore extends DataStore<ServerInfo>
{
	@Override
	protected String getRequestURL(final String id)
	{
		return getHostURL() + "placebooks/a/admin/serverinfo";
	}

	@Override
	protected String getStorageID(final String id)
	{
		return "server.info";
	}
}
