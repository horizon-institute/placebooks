package placebooks.client.ui.openlayers;

import com.google.gwt.core.client.JavaScriptObject;

public class Event extends JavaScriptObject
{
	protected Event()
	{
	}

	public final native int getX()
	/*-{
		return this.xy.x;
	}-*/;

	public final native JavaScriptObject getXY()
	/*-{
		return this.xy;
	}-*/;

	public final native int getY()
	/*-{
		return this.xy.y;
	}-*/;
}
