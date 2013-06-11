package placebooks.client.ui.items.maps;

public class OSMLayer extends Layer
{
	public final static native OSMLayer create(final String name)
	/*-{
		return new $wnd.OpenLayers.Layer.OSM(name);
	}-*/;

	protected OSMLayer()
	{
	}
}
