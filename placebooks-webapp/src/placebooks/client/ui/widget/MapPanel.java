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
}
