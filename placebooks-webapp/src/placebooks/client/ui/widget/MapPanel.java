package placebooks.client.ui.widget;

import placebooks.client.ui.PlaceBookCanvas;
import placebooks.client.ui.PlaceBookItemFrame;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.SimplePanel;

public class MapPanel extends SimplePanel
{
	private final static String POINT_PREFIX= "POINT (";
	
	private final String id;

	private JavaScriptObject map;

	private String url;

	private JavaScriptObject routeLayer;

	private JavaScriptObject markerLayer;

	private final PlaceBookCanvas canvas;
	
	//List<Geometry> markers = new ArrayList<Geometry>();
	
	public MapPanel(final String id, final PlaceBookCanvas canvas)
	{
		// setElement(DOM.createDiv());
		getElement().setId("mapPanel" + id);
		this.id = id;
		this.canvas = canvas;
	}

	public void setURL(final String url)
	{
		this.url = url;
		if (map != null)
		{
			if (routeLayer != null)
			{
				removeLayer(map, routeLayer);
			}
			GWT.log("Load gpx at " + url);
			routeLayer = loadGPX(map, url);
			
			refreshMarkers();			
		}
	}

	@Override
	protected void onLoad()
	{
		super.onLoad();

		map = createMap(id);
		markerLayer = createMarkerLayer(map);
		refreshMarkers();
		
		if (url != null)
		{
			GWT.log("Load gpx at " + url);
			routeLayer = loadGPX(map, url);
		}
	}
	
	private void refreshMarkers()
	{
		clearMarkers(markerLayer);
		for(PlaceBookItemFrame item: canvas.getItems())
		{
			if(item.getItem().hasMetadata("mapItemID") && item.getItem().getMetadata("mapItemID").equals(id) && item.getItem().getGeometry() != null)
			{
				String geometry = item.getItem().getGeometry();
				if(geometry.startsWith(POINT_PREFIX))
				{
					String latLong = geometry.substring(POINT_PREFIX.length(), geometry.length() - 1);
					int comma = latLong.indexOf(" ");
					float lat = Float.parseFloat(latLong.substring(0,comma));
					float lon = Float.parseFloat(latLong.substring(comma + 1));
					addMarker(map, markerLayer, lat, lon);
					GWT.log("Added marker for " + item.getItem().getKey() + " at " + lat + ", " + lon);
				}
			}
		}
	}
	
	private final native JavaScriptObject clearMarkers(JavaScriptObject markerLayer)
	/*-{
		markerLayer.clearMarkers();
	}-*/;
	
	private final native JavaScriptObject createMap(String id)
	/*-{
		var map = new $wnd.OpenLayers.Map("mapPanel" + id, {
			controls : [
	//					new $wnd.OpenLayers.Control.Navigation(),
	//					new $wnd.OpenLayers.Control.PanZoomBar(),
	//					new $wnd.OpenLayers.Control.LayerSwitcher(),
	//					new $wnd.OpenLayers.Control.Attribution()
					],
			numZoomLevels : 19,
			units : 'm',
			projection : new $wnd.OpenLayers.Projection("EPSG:900913"),
			displayProjection : new $wnd.OpenLayers.Projection("EPSG:4326")
		});
		
		var lat=52.95
		var lon=-1.18
		var zoom=13

		// Define the map layer
		layerTilesAtHome = new $wnd.OpenLayers.Layer.OSM.Osmarender("Osmarender");
		map.addLayer(layerTilesAtHome);

		var lonLat = new $wnd.OpenLayers.LonLat(lon, lat).transform(
				new $wnd.OpenLayers.Projection("EPSG:4326"), map
						.getProjectionObject());
		map.setCenter(lonLat, zoom);

		return map;
	}-*/;
	
	private final native void addMarker(JavaScriptObject map, JavaScriptObject markerLayer, float lat, float lon)
	/*-{
		var lonLat = new $wnd.OpenLayers.LonLat(lon, lat).transform(new $wnd.OpenLayers.Projection("EPSG:4326"), map.getProjectionObject());
		
		var size = new $wnd.OpenLayers.Size(21, 25);
		var offset = new $wnd.OpenLayers.Pixel(-(size.w/2), -size.h);
		var icon = new $wnd.OpenLayers.Icon('http://www.openstreetmap.org/openlayers/img/marker.png',size,offset);
		layerMarkers.addMarker(new $wnd.OpenLayers.Marker(lonLat,icon));
	}-*/;
	

	private final native JavaScriptObject createMarkerLayer(JavaScriptObject map)
	/*-{
		layerMarkers = new $wnd.OpenLayers.Layer.Markers("Markers");
		map.addLayer(layerMarkers);

		return layerMarkers;
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
			//map.zoomTo(map.getZoomForExtent(dataExtent));
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

	private final native void removeLayer(final JavaScriptObject map, final JavaScriptObject layer)
	/*-{
		map.removeLayer(layer);
	}-*/;
}
