package placebooks.client.ui.items.maps;

public class MarkerLayer extends Layer
{
	public final static native MarkerLayer create(final String name)
	/*-{
		return new $wnd.OpenLayers.Layer.Markers(name);
	}-*/;

	protected MarkerLayer()
	{
	}

	public final native void addMarker(final Marker marker)
	/*-{
		this.addMarker(marker);
	}-*/;

	public final native void clearMarkers()
	/*-{
		this.clearMarkers();
	}-*/;

	public final native Bounds getDataExtent()
	/*-{
		return this.getDataExtent();
	}-*/;
}
