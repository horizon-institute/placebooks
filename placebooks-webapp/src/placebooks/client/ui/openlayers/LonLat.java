package placebooks.client.ui.openlayers;

import com.google.gwt.core.client.JavaScriptObject;

public class LonLat extends JavaScriptObject
{
	/**
	 * Parse a string containing a space separated coordinate.
	 */
	public final static LonLat create(String pointString)
	{
		String[] coords = pointString.split(" ");
		final float lon = Float.parseFloat(coords[0]);
		final float lat = Float.parseFloat(coords[1]);

		LonLat lonlat = LonLat.create(lon, lat);
		return lonlat;
	}
	
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

	public final native LonLat clone()
	/*-{
		return this.clone();
	}-*/;

	
	public final native LonLat transform(Projection source, Projection dest)
	/*-{
		return this.transform(source, dest);
	}-*/;
}
