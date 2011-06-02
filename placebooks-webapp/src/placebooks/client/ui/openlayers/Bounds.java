package placebooks.client.ui.openlayers;

import com.google.gwt.core.client.JavaScriptObject;

public class Bounds extends JavaScriptObject
{
	protected Bounds() {}
	
	public final native void extend(final Bounds bounds)
	/*-{
		this.extend(bounds);
	}-*/;
}
