package placebooks.client.ui.openlayers;

import com.google.gwt.core.client.JavaScriptObject;

public class Layer extends JavaScriptObject
{
	protected Layer() {}
	
	public final native Extent getExtent()
	/*-{
		this.getDataExtent();
	}-*/;
}
