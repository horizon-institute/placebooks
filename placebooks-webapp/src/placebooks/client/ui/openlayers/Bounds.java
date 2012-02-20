package placebooks.client.ui.openlayers;

import com.google.gwt.core.client.JavaScriptObject;

public class Bounds extends JavaScriptObject
{
	public static final native Bounds create()
	/*-{
		return new $wnd.OpenLayers.Bounds();
	}-*/;

	protected Bounds()
	{
	}

	public final native boolean contains(final Bounds bounds)
	/*-{
		return this.containsBounds(bounds);
	}-*/;

	public final native void extend(final Bounds bounds)
	/*-{
		this.extend(bounds);
	}-*/;

	public final native void extend(final LonLat latlon)
	/*-{
		this.extend(latlon);
	}-*/;

	public final native Bounds transform(Projection source, Projection dest)
	/*-{
		return this.transform(source, dest);
	}-*/;
}
