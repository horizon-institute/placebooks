package placebooks.services;

public class ServiceInfo
{
	private String name;
	private String url;
	private boolean oauth;

	public ServiceInfo(final String name, final String url, final boolean oauth)
	{
		this.name = name;
		this.url = url;
		this.oauth = oauth;
	}

	public String getName()
	{
		return name;
	}

	public String getURL()
	{
		return url;
	}

	public boolean isOAuth()
	{
		return oauth;
	}
}
