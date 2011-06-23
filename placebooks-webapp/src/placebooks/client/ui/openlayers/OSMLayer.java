package placebooks.client.ui.openlayers;

public class OSMLayer extends Layer
{
	public final static native OSMLayer create(final String name)
	/*-{
		return new $wnd.OpenLayers.Layer.OSM.Osmarender(name);
	}-*/;

	protected OSMLayer()
	{
	}
}
