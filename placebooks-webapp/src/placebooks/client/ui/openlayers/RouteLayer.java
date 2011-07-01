package placebooks.client.ui.openlayers;

public class RouteLayer extends Layer
{
//	public final static native RouteLayer create(String name, String gpxURL, Projection project)
//	/*-{
//		return new $wnd.OpenLayers.Layer.Vector(name, {
//			protocol : new $wnd.OpenLayers.Protocol.HTTP({
//				url : gpxURL,
//				format : new $wnd.OpenLayers.Format.GPX({
//					extractWaypoints : true,
//					extractRoutes : true,
//					extractAttributes : true
//				})
//			}),
//			strategies : [ new $wnd.OpenLayers.Strategy.Fixed() ],
//			style : {
//				strokeColor : "blue",
//				strokeWidth : 3,
//				strokeOpacity : 0.7,
//			},
//			projection : project
//		});
//	}-*/;
	
	public final static native RouteLayer create(String name, String gpxURL, Projection project)
	/*-{	
    return new $wnd.OpenLayers.Layer.Vector("GPX", {
        projection: project,    	
        strategies: [new $wnd.OpenLayers.Strategy.Fixed()],
        protocol: new $wnd.OpenLayers.Protocol.HTTP({
            url: gpxURL,
            format: new $wnd.OpenLayers.Format.GPX()
        }),
    });
	}-*/;
	
//	public final static native RouteLayer create(String name, String gpxURL, Projection project)
//	/*-{	
//	return new $wnd.OpenLayers.Layer.GML(name, gpxURL, {
//		format: $wnd.OpenLayers.Format.GPX,
//		style: {strokeColor: "green", strokeWidth: 5, strokeOpacity: 0.5},
//		projection: project
//	});
//	}-*/;
	
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
