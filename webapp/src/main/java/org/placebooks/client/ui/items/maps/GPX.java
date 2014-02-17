package org.placebooks.client.ui.items.maps;

import org.gwtopenmaps.openlayers.client.format.VectorFormat;
import org.gwtopenmaps.openlayers.client.util.JSObject;

public class GPX extends VectorFormat
{
    protected GPX(JSObject gmlFormat)
    {
        super(gmlFormat);
    }

    public GPX()
    {
        this(GPXImpl.create());
    }
}
