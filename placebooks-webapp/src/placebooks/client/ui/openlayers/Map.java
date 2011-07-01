package placebooks.client.ui.openlayers;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;

public class Map extends JavaScriptObject
{
	public final static native Map create(final Element div)
	/*-{
		//$wnd.Proj4js.defs["EPSG:900913"] = "+proj=merc +a=6378137 +b=6378137 +lat_ts=0.0 +lon_0=0.0 +x_0=0.0 +y_0=0 +k=1.0 +units=m +nadgrids=@null +no_defs";
		
		return new $wnd.OpenLayers.Map(div, {
			controls : [
						new $wnd.OpenLayers.Control.Navigation(),
	//						new $wnd.OpenLayers.Control.PanZoomBar(),
	//						new $wnd.OpenLayers.Control.LayerSwitcher(),
	//						new $wnd.OpenLayers.Control.Attribution()
					],
//			units: "m",
//			maxResolution: 156543.0339,
//			maxExtent: new $wnd.OpenLayers.Bounds(-20037508.34, -20037508.34,
//                                             20037508.34, 20037508.34),
			projection: "EPSG:900913",
//			displayProjection: new $wnd.OpenLayers.Projection("EPSG:4326")					
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

	public final native Projection getDisplayProjection()
	/*-{
		return this.displayProjection;
	}-*/;
}
