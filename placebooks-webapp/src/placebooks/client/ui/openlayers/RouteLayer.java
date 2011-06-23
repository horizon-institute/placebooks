package placebooks.client.ui.openlayers;

public class RouteLayer extends Layer
{
	public final static native RouteLayer create(String name, String url)
	/*-{
		return new $wnd.OpenLayers.Layer.Vector(name, {
			protocol : new $wnd.OpenLayers.Protocol.HTTP({
				url : url,
				format : new $wnd.OpenLayers.Format.GPX({
					extractWaypoints : true,
					extractRoutes : true,
					extractAttributes : true
				})
			}),
			strategies : [ new $wnd.OpenLayers.Strategy.Fixed() ],
			style : {
				strokeColor : "blue",
				strokeWidth : 3,
				strokeOpacity : 0.7,
			},
			projection : new $wnd.OpenLayers.Projection("EPSG:4326")
		});
	}-*/;

	protected RouteLayer()
	{
	}

	public final native Bounds getDataExtent()
	/*-{
		return this.getDataExtent();
	}-*/;

	public final native void setUrl(final String url)
	/*-{
		this.setUrl(url);
	}-*/;
}
