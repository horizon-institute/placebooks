package placebooks.client.model;

import java.util.Iterator;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class ServerInfo extends JavaScriptObject
{
	protected ServerInfo()
	{
	}

	public final native int getAudioSize()
	/*-{
		return this.maxAudioSize;
	}-*/;

	public final native int getImageSize()
	/*-{
		return this.maxImageSize;
	}-*/;

	public final native String getOpenSpaceBaseURL()
	/*-{
		return this.openSpaceBaseURL;
	}-*/;

	public final native String getOpenSpaceHost()
	/*-{
		return this.openSpaceHost;
	}-*/;

	public final native String getOpenSpaceKey()
	/*-{
		return this.openSpaceKey;
	}-*/;

	public final native String getServerName()
	/*-{
		return this.serverName;
	}-*/;

	public final Iterable<ServiceInfo> getServices()
	{
		return new Iterable<ServiceInfo>()
		{
			@Override
			public Iterator<ServiceInfo> iterator()
			{
				return new JSIterator<ServiceInfo>(getServicesInternal());
			}
		};
	}

	public final native int getVideoSize()
	/*-{
		return this.maxVideoSize;
	}-*/;

	private final native JsArray<ServiceInfo> getServicesInternal()
	/*-{
		if(!('services' in this))
		{
			this.services = new Array();
		}
		return this.services;
	}-*/;
}
