package placebooks.client.model;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class Shelf extends JavaScriptObject
{
	public static final native Shelf parse(final String json) /*-{ return eval('(' + json + ')'); }-*/;
	
	protected Shelf() {}	
	
	public final native JsArray<PlaceBookEntry> getEntries() /*-{ return this.entries; }-*/;
	
	public final native User getUser() /*-{ return this.user; }-*/;	
}
