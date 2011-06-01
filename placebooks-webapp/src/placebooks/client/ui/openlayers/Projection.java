package placebooks.client.ui.openlayers;

import com.google.gwt.core.client.JavaScriptObject;

public class Projection extends JavaScriptObject
{
	public final static native Projection create(String projection)
	/*-{
		return new $wnd.OpenLayers.Projection(projection);
	}-*/;
	
	protected Projection() {}
}
