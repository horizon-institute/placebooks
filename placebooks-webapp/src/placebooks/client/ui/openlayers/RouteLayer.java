package placebooks.client.ui.openlayers;

public class RouteLayer extends Layer
{
	public final static native RouteLayer create(String name, String gpxURL, Projection project)
	/*-{	
    return new $wnd.OpenLayers.Layer.Vector("GPX", {
        projection: project,    	
        strategies: [new $wnd.OpenLayers.Strategy.Fixed()],
		style: {strokeColor: "blue", strokeWidth: 3, strokeOpacity: 0.7},        
        protocol: new $wnd.OpenLayers.Protocol.HTTP({
            url: gpxURL,
            format: new $wnd.OpenLayers.Format.GPX(
            	{
					extractWaypoints : false,
					extractRoutes : true,
					extractTracks : true,					
					extractAttributes : true
				}            
            )
        }),
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
