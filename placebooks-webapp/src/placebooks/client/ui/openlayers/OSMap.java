package placebooks.client.ui.openlayers;

import com.google.gwt.dom.client.Element;

public class OSMap extends Map
{
	public final static native OSMap createOSMap(final Element div)
	/*-{
		return new $wnd.OpenSpace.Map(div);//, {
	//		controls : [
	//					new $wnd.OpenLayers.Control.Navigation(),
	//						new $wnd.OpenLayers.Control.PanZoomBar(),
	//						new $wnd.OpenLayers.Control.LayerSwitcher(),
	//						new $wnd.OpenLayers.Control.Attribution()
	//				],
	//		units: "m",
	//		maxResolution: 156543.0339,
	//		maxExtent: new $wnd.OpenLayers.Bounds(-20037508.34, -20037508.34, 20037508.34, 20037508.34),
	//		projection: new $wnd.OpenLayers.Projection("EPSG:900913"),
	//		displayProjection: new $wnd.OpenLayers.Projection("EPSG:4326")					
	//	});
	}-*/;

	protected OSMap()
	{
		
	}
}
