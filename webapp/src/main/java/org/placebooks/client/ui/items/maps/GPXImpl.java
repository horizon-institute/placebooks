package org.placebooks.client.ui.items.maps;

import org.gwtopenmaps.openlayers.client.util.JSObject;

public class GPXImpl
{
    public static native JSObject create()
    /*-{
		return new $wnd.OpenLayers.Format.GPX();
    }-*/;
}
