package placebooks.client.ui.openlayers;

public class RouteLayer extends Layer
{
	protected RouteLayer() { }
	
	public final static native RouteLayer create(String name, String url)
	/*-{
		// Add the Layer with the GPX Track
		return new $wnd.OpenLayers.Layer.GML(name, url, {
			format : $wnd.OpenLayers.Format.GPX,
			style : {
				strokeColor : "blue",
				strokeWidth : 5,
				strokeOpacity : 0.7
			},
			projection : new $wnd.OpenLayers.Projection("EPSG:4326")
		});
	}-*/;
    
	public final native Bounds getDataExtent()
	/*-{
		return this.getDataExtent();
	}-*/;
}
