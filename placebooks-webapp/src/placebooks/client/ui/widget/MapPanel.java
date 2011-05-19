package placebooks.client.ui.widget;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.SimplePanel;

public class MapPanel extends SimplePanel
{
	private String id;

	private JavaScriptObject map;

	public MapPanel(String id)
	{
		// setElement(DOM.createDiv());
		getElement().setId(id);
		this.id = id;
	}

	@Override
	protected void onLoad()
	{
		super.onLoad();

		map = createMap(id);
	}

	public void setURL(final String url)
	{
		loadGPX(map, url);
	}

	private final native JavaScriptObject createMap(String id)
	/*-{
		var map = new $wnd.OpenLayers.Map(id, {
			controls : [],
			maxExtent : new $wnd.OpenLayers.Bounds(-20037508.34, -20037508.34,
					20037508.34, 20037508.34),
			maxResolution : 156543.0399,
			numZoomLevels : 19,
			units : 'm',
			projection : new $wnd.OpenLayers.Projection("EPSG:900913"),
			displayProjection : new $wnd.OpenLayers.Projection("EPSG:4326")
		});
		
		var lat=47.496792
		var lon=7.571726
		var zoom=13

		// Define the map layer
		// Here we use a predefined layer that will be kept up to date with URL changes
		layerMapnik = new $wnd.OpenLayers.Layer.OSM.Mapnik("Mapnik");
		map.addLayer(layerMapnik);
		layerTilesAtHome = new $wnd.OpenLayers.Layer.OSM.Osmarender("Osmarender");
		map.addLayer(layerTilesAtHome);
		layerCycleMap = new $wnd.OpenLayers.Layer.OSM.CycleMap("CycleMap");
		map.addLayer(layerCycleMap);
		layerMarkers = new $wnd.OpenLayers.Layer.Markers("Markers");
		map.addLayer(layerMarkers);

		var lonLat = new $wnd.OpenLayers.LonLat(lon, lat).transform(
				new $wnd.OpenLayers.Projection("EPSG:4326"), map
						.getProjectionObject());
		map.setCenter(lonLat, zoom);

		var size = new $wnd.OpenLayers.Size(21, 25);
		var offset = new $wnd.OpenLayers.Pixel(-(size.w / 2), -size.h);
		var icon = new $wnd.OpenLayers.Icon('http://www.openstreetmap.org/openlayers/img/marker.png', size,	offset);
		layerMarkers.addMarker(new $wnd.OpenLayers.Marker(lonLat, icon));

		return map;
	}-*/;

	private final native void loadGPX(JavaScriptObject map, String url)
	/*-{
		// Add the Layer with the GPX Track
		var lgpx = new $wnd.OpenLayers.Layer.GML("Lakeside cycle ride", url, {
			format : $wnd.OpenLayers.Format.GPX,
			style : {
				strokeColor : "green",
				strokeWidth : 5,
				strokeOpacity : 0.5
			},
			projection : new $wnd.OpenLayers.Projection("EPSG:4326")
		});
		map.addLayer(lgpx);
	}-*/;
}
