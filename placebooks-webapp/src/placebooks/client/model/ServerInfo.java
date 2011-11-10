package placebooks.client.model;

import com.google.gwt.core.client.JavaScriptObject;

public class ServerInfo extends JavaScriptObject
{
	public static final native ServerInfo parse(final String json) /*-{ return eval('(' + json + ')'); }-*/;

	
	protected ServerInfo()
	{
	}

	public final native String getOpenSpaceKey()
	/*-{
		return this.openSpaceKey;
	}-*/;

	public final native String getOpenSpaceHost()
	/*-{
		return this.openSpaceHost;
	}-*/;

	public final native String getOpenSpaceBaseURL()
	/*-{
		return this.openSpaceBaseURL;
	}-*/;
	
	public final native String getServerName()
	/*-{
		return this.serverName;
	}-*/;	
}
