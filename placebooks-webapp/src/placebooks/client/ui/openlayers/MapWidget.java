package placebooks.client.ui.openlayers;

import placebooks.client.ui.PlaceBookCanvas;
import placebooks.client.ui.PlaceBookItemFrame;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.SimplePanel;

public class MapWidget extends SimplePanel
{
	private final static String POINT_PREFIX= "POINT (";
	
	private final String id;

	private Map map;

	private String url;

	private RouteLayer routeLayer;

	private MarkerLayer markerLayer;

	private final PlaceBookCanvas canvas;
	
	public MapWidget(final String id, final PlaceBookCanvas canvas)
	{
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
				map.removeLayer(routeLayer);
			}
			GWT.log("Load gpx at " + url);
			routeLayer = RouteLayer.create(id, url);
			map.addLayer(routeLayer);
			
			refreshMarkers();			
		}
	}

	@Override
	protected void onLoad()
	{
		super.onLoad();

		map = Map.create("mapPanel" + id);
		map.addLayer(OSMLayer.create("Osmarender"));
		map.setCenter(LonLat.create(-1.18f, 52.95f).transform(Projection.create("EPSG:4326"), map.getProjection()), 13);
		markerLayer = MarkerLayer.create("markerLayer");
		map.addLayer(markerLayer);
		
		refreshMarkers();
		
		if (url != null)
		{
			GWT.log("Load gpx at " + url);
			routeLayer = RouteLayer.create(id, url);
			map.addLayer(routeLayer);
		}
	}
	
	private void refreshMarkers()
	{
		markerLayer.clearMarkers();
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
					markerLayer.addMarker(Marker.create(LonLat.create(lon, lat).transform(Projection.create("EPSG:4326"), map.getProjection())));
					GWT.log("Added marker for " + item.getItem().getKey() + " at " + lat + ", " + lon);
				}
			}
		}
		recenter();
	}
	
	private void recenter()
	{
		Extent extent = markerLayer.getExtent();
		if(routeLayer != null)
		{
			extent = extent.extend(routeLayer.getExtent());
		}
		map.zoomToExtent(extent);
	}
	
//	
//	private final native JavaScriptObject createMap(String id)
//	/*-{
//		var map = new $wnd.OpenLayers.Map("mapPanel" + id, {
//			controls : [
//	//					new $wnd.OpenLayers.Control.Navigation(),
//	//					new $wnd.OpenLayers.Control.PanZoomBar(),
//	//					new $wnd.OpenLayers.Control.LayerSwitcher(),
//	//					new $wnd.OpenLayers.Control.Attribution()
//					],
//			numZoomLevels : 19,
//			units : 'm',
//			projection : new $wnd.OpenLayers.Projection("EPSG:900913"),
//			displayProjection : new $wnd.OpenLayers.Projection("EPSG:4326")
//		});
//		
//		var lat=52.95
//		var lon=-1.18
//		var zoom=13
//
//		// Define the map layer
//		layerTilesAtHome = new $wnd.OpenLayers.Layer.OSM.Osmarender("Osmarender");
//		map.addLayer(layerTilesAtHome);
//
//		var lonLat = new $wnd.OpenLayers.LonLat(lon, lat).transform(
//				new $wnd.OpenLayers.Projection("EPSG:4326"), map
//						.getProjectionObject());
//		map.setCenter(lonLat, zoom);
//
//		return map;
//	}-*/;
//	
//	private final native void addMarker(JavaScriptObject map, JavaScriptObject markerLayer, float lat, float lon)
//	/*-{
//		var lonLat = new $wnd.OpenLayers.LonLat(lon, lat).transform(new $wnd.OpenLayers.Projection("EPSG:4326"), map.getProjectionObject());
//		
//		var size = new $wnd.OpenLayers.Size(21, 25);
//		var offset = new $wnd.OpenLayers.Pixel(-(size.w/2), -size.h);
//		var icon = new $wnd.OpenLayers.Icon('http://www.openstreetmap.org/openlayers/img/marker.png',size,offset);
//		layerMarkers.addMarker(new $wnd.OpenLayers.Marker(lonLat,icon));
//	}-*/;
//	
//	private final native JavaScriptObject loadGPX(JavaScriptObject map, String url)
//	/*-{
//		var dataExtent;
//		var setExtent = function()
//		{
//			if(dataExtent)
//				dataExtent.extend(this.getDataExtent());
//			else
//				dataExtent = this.getDataExtent();
//			//map.zoomTo(map.getZoomForExtent(dataExtent));
//			map.zoomToExtent(dataExtent);
//		};
//	
//		// Add the Layer with the GPX Track
//		var lgpx = new $wnd.OpenLayers.Layer.GML("Lakeside cycle ride", url, {
//			format : $wnd.OpenLayers.Format.GPX,
//			style : {
//				strokeColor : "blue",
//				strokeWidth : 5,
//				strokeOpacity : 0.6
//			},
//			projection : new $wnd.OpenLayers.Projection("EPSG:4326")
//		});
//		// This will perform the autozoom as soon as the GPX file is loaded.
//		lgpx.events.register("loadend", lgpx, setExtent);				
//		map.addLayer(lgpx);
//		return lgpx;
//	}-*/;
}