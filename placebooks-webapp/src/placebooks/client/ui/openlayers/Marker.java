package placebooks.client.ui.openlayers;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.resources.client.ImageResource;

public class Marker extends JavaScriptObject
{
	public final static Marker create(ImageResource image, LonLat lonLat)
	{
		return create(image.getSafeUri().asString(), lonLat, image.getHeight(), image.getWidth());
	}
	
	public final static native Marker create(String markerURL, LonLat lonLat, int iconHeight, int iconWidth)
	/*-{
		var size = new $wnd.OpenLayers.Size(iconWidth, iconHeight);
		var offset = new $wnd.OpenLayers.Pixel(-(iconWidth/2), -iconHeight);
		var icon = new $wnd.OpenLayers.Icon(markerURL,size,offset);
		return new $wnd.OpenLayers.Marker(lonLat,icon);
	}-*/;

	protected Marker()
	{
	}

	public final native Events getEvents()
	/*-{
		return this.events;
	}-*/;

	public final native Icon getIcon()
	/*-{
		return this.icon;
	}-*/;
}
