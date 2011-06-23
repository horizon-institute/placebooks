package placebooks.client.ui.openlayers;

import com.google.gwt.core.client.JavaScriptObject;

public class LonLat extends JavaScriptObject
{
	public final static native LonLat create(final float lon, final float lat)
	/*-{
		return new $wnd.OpenLayers.LonLat(lon, lat);
	}-*/;

	protected LonLat()
	{
	}

	public final native float getLat()
	/*-{
		return this.lat;
	}-*/;

	public final native float getLon()
	/*-{
		return this.lon;
	}-*/;

	public final native LonLat transform(Projection source, Projection dest)
	/*-{
		return this.transform(source, dest);
	}-*/;
}
