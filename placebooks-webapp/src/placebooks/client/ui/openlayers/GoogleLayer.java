package placebooks.client.ui.openlayers;

public class GoogleLayer extends Layer
{
	public final static native OSMLayer create(final String name, final Bounds mapBounds)
	/*-{
		return new $wnd.OpenLayers.Layer.Google(name,
		{
			type: $wnd.G_NORMAL_MAP,
			sphericalMercator: true,
			maxExtent: mapBounds
		});
	}-*/;

	protected GoogleLayer()
	{
	}
}