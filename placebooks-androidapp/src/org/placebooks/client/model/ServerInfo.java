package org.placebooks.client.model;

import java.util.ArrayList;
import java.util.List;

public class ServerInfo
{
	private int audioSize;

	private int imageSize;
	private String serverName;
	private List<ServiceInfo> services = new ArrayList<ServiceInfo>();
	private int videoSize;

	public ServerInfo()
	{
	}

	public int getAudioSize()
	{
		return audioSize;
	}

	public int getImageSize()
	{
		return imageSize;
	}

	public String getServerName()
	{
		return serverName;
	}

	public List<ServiceInfo> getServices()
	{
		return services;
	}

	public int getVideoSize()
	{
		return videoSize;
	}

	public void setAudioSize(final int audioSize)
	{
		this.audioSize = audioSize;
	}

	public void setImageSize(final int imageSize)
	{
		this.imageSize = imageSize;
	}

	public void setServerName(final String serverName)
	{
		this.serverName = serverName;
	}

	public void setVideoSize(final int videoSize)
	{
		this.videoSize = videoSize;
	}

}