package placebooks.client.ui.openlayers;

import com.google.gwt.core.client.JavaScriptObject;

public class Map extends JavaScriptObject
{
	public final static native Map create(final String id)
	/*-{
		return new $wnd.OpenLayers.Map(id, {
			controls : [
						new $wnd.OpenLayers.Control.Navigation(),
	//						new $wnd.OpenLayers.Control.PanZoomBar(),
	//						new $wnd.OpenLayers.Control.LayerSwitcher(),
	//						new $wnd.OpenLayers.Control.Attribution()
					],
		});
	}-*/;

	protected Map()
	{
	}

	public final native void addControl(final JavaScriptObject control)
	/*-{
		this.addControl(control);
	}-*/;

	public final native void addLayer(Layer layer)
	/*-{
		this.addLayer(layer);
	}-*/;

	public final native Events getEvents()
	/*-{
		return this.events;
	}-*/;

	public final native LonLat getLonLatFromPixel(final JavaScriptObject pixels)
	/*-{
		return this.getLonLatFromPixel(pixels);
	}-*/;

	public final native String getLonLatFromPixelTest(final Event event)
	/*-{
		var latlon = this.getLonLatFromViewPortPx(event.xy);
		return "Lat: " + latlon.lat + " (Pixel.x:" + event.xy.x + ")" + " Lon: " + latlon.lon + " (Pixel.y:" + event.xy.y + ")"
	}-*/;

	public final native LonLat getLonLatFromViewPortPx(final JavaScriptObject pixels)
	/*-{
		return this.getLonLatFromViewPortPx(pixels);
	}-*/;

	public final native Projection getProjection()
	/*-{
		return this.getProjectionObject();
	}-*/;

	public final native void raiseLayer(Layer layer)
	/*-{
		this.raiseLayer(layer);
	}-*/;

	public final native void removeLayer(Layer layer)
	/*-{
		this.removeLayer(layer);
	}-*/;

	public final native void setCenter(final LonLat lonLat, final int zoom)
	/*-{
		this.setCenter(lonLat, zoom);		
	}-*/;

	public final native void zoomToExtent(final Bounds extent)
	/*-{
		this.zoomToExtent(extent);
	}-*/;
}
