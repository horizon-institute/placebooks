package placebooks.client.ui.openlayers;

import com.google.gwt.core.client.JavaScriptObject;

public class Marker extends JavaScriptObject
{
	public final static native Marker create(LonLat lonLat)
	/*-{
		var size = new $wnd.OpenLayers.Size(21, 25);
		var offset = new $wnd.OpenLayers.Pixel(-(size.w/2), -size.h);
		var icon = new $wnd.OpenLayers.Icon('http://www.openstreetmap.org/openlayers/img/marker.png',size,offset);
		return new $wnd.OpenLayers.Marker(lonLat,icon);
	}-*/;

	protected Marker()
	{
	}

	public final native Events getEvents()
	/*-{
		return this.events;
	}-*/;
}
