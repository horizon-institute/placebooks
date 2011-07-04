package placebooks.client.ui.openlayers;

public class GoogleLayer extends Layer
{
	public final static native OSMLayer create(final String name)
	/*-{
		return new $wnd.OpenLayers.Layer.Google(name);
	}-*/;

	protected GoogleLayer()
	{
	}
}