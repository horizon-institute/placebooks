package placebooks.client.ui.openlayers;


public class MarkerLayer extends Layer
{
	protected MarkerLayer() {}
	
	public final static native MarkerLayer create(final String name)
	/*-{
		return new $wnd.OpenLayers.Layer.Markers(name);
	}-*/;

	public final native void addMarker(final Marker marker)
	/*-{
		this.addMarker(marker);
	}-*/;
	
	public final native void clearMarkers()
	/*-{
		this.clearMarkers();
	}-*/;
}
