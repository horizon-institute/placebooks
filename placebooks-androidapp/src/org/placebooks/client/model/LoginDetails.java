package org.placebooks.client.model;

public class LoginDetails
{
	private String id;

	private double lastSync;
	private String service;
	private String userID;
	private String username;
	private boolean syncInProgress;

	public LoginDetails()
	{
	}

	public String getId()
	{
		return id;
	}

	public double getLastSync()
	{
		return lastSync;
	}

	public String getService()
	{
		return service;
	}

	public String getUserID()
	{
		return userID;
	}

	public String getUsername()
	{
		return username;
	}

	public boolean isSyncInProgress()
	{
		return syncInProgress;
	}

	public void setId(final String id)
	{
		this.id = id;
	}

	public void setLastSync(final double lastSync)
	{
		this.lastSync = lastSync;
	}

	public void setService(final String service)
	{
		this.service = service;
	}

	public void setSyncInProgress(final boolean syncInProgress)
	{
		this.syncInProgress = syncInProgress;
	}

	public void setUserID(final String userID)
	{
		this.userID = userID;
	}

	public void setUsername(final String username)
	{
		this.username = username;
	}
}
