package placebooks.client.ui.openlayers;

import com.google.gwt.core.client.JavaScriptObject;

public class Events extends JavaScriptObject
{
	public final native static void stop(Event evt, boolean allowDefault)
	/*-{
		$wnd.OpenLayers.Event.stop(evt, allowDefault);
	}-*/;

	protected Events()
	{
	}

	public final native void on(JavaScriptObject object)
	/*-{
		this.on(object);
	}-*/;

	public final native void register(String type, JavaScriptObject obj, EventHanderFunction handler)
	/*-{
		this.register(type, obj, handler);
	}-*/;

	public final native void unregister(String type, JavaScriptObject obj, EventHanderFunction handler)
	/*-{
		this.unregister(type, obj, handler);
	}-*/;
}