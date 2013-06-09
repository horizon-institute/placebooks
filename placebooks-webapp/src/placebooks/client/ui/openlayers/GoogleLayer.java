package placebooks.client.ui.openlayers;

import placebooks.client.JavaScriptInjector;

public class GoogleLayer extends Layer
{
	public final static GoogleLayer create(final String name, final Bounds mapBounds)
	{
		JavaScriptInjector.add("http://maps.google.com/maps/api/js?v=3.2&sensor=false");

		return createLayer(name, mapBounds);
	}

	private final static native GoogleLayer createLayer(final String name, final Bounds mapBounds)
	/*-{
		return new $wnd.OpenLayers.Layer.Google(name,
		{
			type: $wnd.G_NORMAL_MAP,
			sphericalMercator: true,
			maxExtent: mapBounds
		});
	}-*/;

	// TODO Add alternatives

	protected GoogleLayer()
	{
	}
}