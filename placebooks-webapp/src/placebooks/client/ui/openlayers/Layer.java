package placebooks.client.ui.openlayers;

import com.google.gwt.core.client.JavaScriptObject;

public class Layer extends JavaScriptObject
{
	protected Layer()
	{
	}

	public final native Events getEvents()
	/*-{
		return this.events;
	}-*/;

	public final native String getZIndex()
	/*-{
		return this.getZIndex();
	}-*/;

	public final native void setVisible(final boolean visible)
	/*-{
		this.setVisibility(visible);
	}-*/;

	public final native void setZIndex(final int zindex)
	/*-{
		this.setZIndex(zindex);
	}-*/;
}
