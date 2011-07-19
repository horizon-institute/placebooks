package placebooks.client.ui.openlayers;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HTMLPanel;

public class OSMap extends Map
{
	private final static native OSMap createOS(final String id)
	/*-{
		return new $wnd.OpenSpace.Map(id);//, {
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
	
	public final static OSMap createOSMap(final Element div)
	{
		if(div.getId() == null)
		{
			div.setId(HTMLPanel.createUniqueId());
		}
		
		return createOS(div.getId());
	}

	protected OSMap()
	{
		
	}
}
