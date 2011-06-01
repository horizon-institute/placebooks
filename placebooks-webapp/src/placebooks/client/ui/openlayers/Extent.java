package placebooks.client.ui.openlayers;

import com.google.gwt.core.client.JavaScriptObject;

public class Extent extends JavaScriptObject
{
	protected Extent() {}
	
	public final native Extent extend(final Extent extent)
	/*-{
		return this.extend(extent);
	}-*/;
}
