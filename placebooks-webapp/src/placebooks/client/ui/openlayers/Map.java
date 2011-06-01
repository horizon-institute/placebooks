package placebooks.client.ui.openlayers;

import com.google.gwt.core.client.JavaScriptObject;

public class Map extends JavaScriptObject
{
	protected Map() {}
	
	public final static native Map create(final String id)
	/*-{
		return new $wnd.OpenLayers.Map(id, {
			controls : [
//						new $wnd.OpenLayers.Control.Navigation(),
//						new $wnd.OpenLayers.Control.PanZoomBar(),
//						new $wnd.OpenLayers.Control.LayerSwitcher(),
//						new $wnd.OpenLayers.Control.Attribution()
					],
//			numZoomLevels : 19,
//			units : 'm',
			projection : new $wnd.OpenLayers.Projection("EPSG:900913"),
			displayProjection : new $wnd.OpenLayers.Projection("EPSG:4326")
		});
	}-*/;
	
	public final native void addLayer(Layer layer)
	/*-{
		this.addLayer(layer);
	}-*/;
	
	public final native void removeLayer(Layer layer)
	/*-{
		this.removeLayer(layer);
	}-*/;
	
	public final native void setCenter(final LonLat lonLat, final int zoom)
	/*-{
		this.setCenter(lonLat, zoom);		
	}-*/;

	public final native Projection getProjection()
	/*-{
		return this.getProjectionObject();
	}-*/;
	
	public final native void zoomToExtent(final Extent extent)
	/*-{
		this.zoomToExtent(extent);
	}-*/;
}
