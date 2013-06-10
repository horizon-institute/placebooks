package placebooks.client.ui.items.maps;

import com.google.gwt.core.client.JavaScriptObject;

public class Projection extends JavaScriptObject
{
	public final static native Projection create(String projection)
	/*-{
		return new $wnd.OpenLayers.Projection(projection);
	}-*/;

	protected Projection()
	{
	}

	public final native String getCode()
	/*-{
		return this.getCode();
	}-*/;

	public final native String getUnits()
	/*-{
		return this.getUnits();
	}-*/;
}
