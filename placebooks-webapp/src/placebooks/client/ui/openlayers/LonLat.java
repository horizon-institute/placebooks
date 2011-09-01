package placebooks.client.ui.openlayers;

import com.google.gwt.core.client.JavaScriptObject;

public class LonLat extends JavaScriptObject
{
	public final static native LonLat create(final float lon, final float lat)
	/*-{
		return new $wnd.OpenLayers.LonLat(lon, lat);
	}-*/;

	/**
	 * Parse a string containing a space separated coordinate in the format LON LAT.
	 */
	public final static LonLat create(final String pointString)
	{
		final String[] coords = pointString.split(" ");
		final float lon = Float.parseFloat(coords[0]);
		final float lat = Float.parseFloat(coords[1]);

		final LonLat lonlat = LonLat.create(lon, lat);
		return lonlat;
	}

	/**
	 * Parse a string containing a space separated coordinate in the format LAT LON.
	 */
	public final static LonLat createFromPoint(final String pointString)
	{
		final String[] coords = pointString.split(" ");
		final float lon = Float.parseFloat(coords[1]);
		final float lat = Float.parseFloat(coords[0]);

		final LonLat lonlat = LonLat.create(lon, lat);
		return lonlat;
	}

	protected LonLat()
	{
	}

	public final native LonLat cloneLonLat()
	/*-{
		return this.clone();
	}-*/;

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
