package placebooks.client.model;

import com.google.gwt.core.client.JavaScriptObject;

public class ServiceInfo extends JavaScriptObject
{
	protected ServiceInfo()
	{
	}

	public final native String getName()
	/*-{
		return this.name;
	}-*/;

	public final native String getURL()
	/*-{
		return this.url;
	}-*/;

	public final native boolean isOAuth()
	/*-{
		return this.oauth; 
	}-*/;
}
