package org.placebooks.client.model;


public class ServiceInfo
{
	private String name;
	private String url;
	private boolean oAuth;

	public ServiceInfo()
	{
	}

	public ServiceInfo(String name, String url, boolean oauth)
	{
		this.name = name;
		this.url = url;
		this.oAuth = oauth;
	}
	
	public String getName()
	{
		return name;
	}

	public String getUrl()
	{
		return url;
	}

	public boolean isOAuth()
	{
		return oAuth;
	}

	public void setName(final String name)
	{
		this.name = name;
	}

	public void setOAuth(final boolean oauth)
	{
		this.oAuth = oauth;
	}

	public void setUrl(final String url)
	{
		this.url = url;
	}
}
