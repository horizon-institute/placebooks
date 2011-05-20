package placebooks.client.ui.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.SimplePanel;

public class MapPanel extends SimplePanel
{
	private String id;

	private JavaScriptObject map;

	private String url;
	
	private JavaScriptObject routeLayer;

	public MapPanel(final String id)
	{
		// setElement(DOM.createDiv());
		getElement().setId(id);
		this.id = id;
	}

	public void setURL(final String url)
	{
		this.url = url;
		if (map != null)
		{
			if(routeLayer != null)
			{
				removeLayer(map, routeLayer);
			}
			GWT.log("Load gpx at " + url);
			routeLayer = loadGPX(map, url);
		}
	}

	@Override
	protected void onLoad()
	{
		super.onLoad();

		map = createMap(id);
		if (url != null)
		{
			GWT.log("Load gpx at " + url);			
			routeLayer = loadGPX(map, url);
		}
	}

	private final native void removeLayer(final JavaScriptObject map, final JavaScriptObject layer)
	/*-{
		map.removeLayer(layer);
	}-*/;
	
	private final native JavaScriptObject createMap(String id)
	/*-{
		var map = new $wnd.OpenLayers.Map(id, {
			controls : [
//					new $wnd.OpenLayers.Control.Navigation(),
//					new $wnd.OpenLayers.Control.PanZoomBar(),
//					new $wnd.OpenLayers.Control.LayerSwitcher(),
//					new $wnd.OpenLayers.Control.Attribution()
					],
//			maxExtent : new $wnd.OpenLayers.Bounds(-20037508.34, -20037508.34,
//					20037508.34, 20037508.34),
//			maxResolution : 156543.0399,
//			numZoomLevels : 19,
			units : 'm',
			projection : new $wnd.OpenLayers.Projection("EPSG:900913"),
			displayProjection : new $wnd.OpenLayers.Projection("EPSG:4326")
		});
		
		var lat=52.95
		var lon=-1.18
		var zoom=13

		// Define the map layer
		// Here we use a predefined layer that will be kept up to date with URL changes
		//layerMapnik = new $wnd.OpenLayers.Layer.OSM.Mapnik("Mapnik");
		//map.addLayer(layerMapnik);
		layerTilesAtHome = new $wnd.OpenLayers.Layer.OSM.Osmarender("Osmarender");
		map.addLayer(layerTilesAtHome);
		//layerCycleMap = new $wnd.OpenLayers.Layer.OSM.CycleMap("CycleMap");
		//map.addLayer(layerCycleMap);
		//layerMarkers = new $wnd.OpenLayers.Layer.Markers("Markers");
		//map.addLayer(layerMarkers);

		var lonLat = new $wnd.OpenLayers.LonLat(lon, lat).transform(
				new $wnd.OpenLayers.Projection("EPSG:4326"), map
						.getProjectionObject());
		map.setCenter(lonLat, zoom);

		//var size = new $wnd.OpenLayers.Size(21, 25);
		//var offset = new $wnd.OpenLayers.Pixel(-(size.w / 2), -size.h);
		//var icon = new $wnd.OpenLayers.Icon('http://www.openstreetmap.org/openlayers/img/marker.png', size,	offset);
		//layerMarkers.addMarker(new $wnd.OpenLayers.Marker(lonLat, icon));

		return map;
	}-*/;

	private final native JavaScriptObject loadGPX(JavaScriptObject map, String url)
	/*-{
		var dataExtent;
		var setExtent = function()
		{
			if(dataExtent)
				dataExtent.extend(this.getDataExtent());
			else
				dataExtent = this.getDataExtent();
			map.zoomToExtent(dataExtent);
		};
 
		// Add the Layer with the GPX Track
		var lgpx = new $wnd.OpenLayers.Layer.GML("Lakeside cycle ride", url, {
			format : $wnd.OpenLayers.Format.GPX,
			style : {
				strokeColor : "blue",
				strokeWidth : 5,
				strokeOpacity : 0.6
			},
			projection : new $wnd.OpenLayers.Projection("EPSG:4326")
		});
		// This will perform the autozoom as soon as the GPX file is loaded.
		lgpx.events.register("loadend", lgpx, setExtent);				
		map.addLayer(lgpx);
		return lgpx;
	}-*/;
}
