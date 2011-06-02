package placebooks.client.ui.openlayers;

import com.google.gwt.core.client.JavaScriptObject;

public class Layer extends JavaScriptObject
{
	protected Layer() {}
	
	public final native void setVisible(final boolean visible)
	/*-{
		this.setVisibility(visible);
	}-*/;	
	
	public final native Events getEvents()
	/*-{
		return this.events;
	}-*/;
}
