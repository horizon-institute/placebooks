package placebooks.client.ui.openlayers;

import placebooks.client.JavaScriptInjector;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;

public class Map extends JavaScriptObject
{
	public static Map create(final Element div)
	{
		JavaScriptInjector.inject(ScriptResources.INSTANCE.espg4623().getText());
		JavaScriptInjector.inject(ScriptResources.INSTANCE.espg900913().getText());

		return createMap(div);
	}

	private final static native Map createMap(final Element div)
	/*-{
		return new $wnd.OpenLayers.Map(div, {
			controls : [
						new $wnd.OpenLayers.Control.Navigation(),
	//						new $wnd.OpenLayers.Control.PanZoomBar(),
	//						new $wnd.OpenLayers.Control.LayerSwitcher(),
	//						new $wnd.OpenLayers.Control.Attribution()
					],
			units: "m",
			maxResolution: 156543.0339,
			maxExtent: new $wnd.OpenLayers.Bounds(-20037508.34, -20037508.34, 20037508.34, 20037508.34),
			projection: new $wnd.OpenLayers.Projection("EPSG:900913"),
			displayProjection: new $wnd.OpenLayers.Projection("EPSG:4326")					
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

	public final native Projection getDisplayProjection()
	/*-{
		return this.displayProjection;
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

	public final native Bounds getMaxExtent()
	/*-{
		return this.maxExtent;
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

	public final native void setCenter(final LonLat lonLat)
	/*-{
		this.setCenter(lonLat);		
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
