package placebooks.client.model;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class PlaceBook extends JavaScriptObject
{
	protected PlaceBook() {}
	
	public static final native PlaceBook parse(final String json) /*-{ return eval('(' + json + ')'); }-*/;
	
	public final native JsArray<PlaceBookItem> getItems() /*-{ return this.items; }-*/;

	public final native String getKey() /*-{ return this.key; }-*/;
	
	public final native User getOwner() /*-{ return this.owner; }-*/;
	
	public final native String getGeometry() /*-{ return this.geom; }-*/;	

	public final native String getMetadata(String name) /*-{ return this.metadata[name]; }-*/;
	
	//@Persistent
	//private Date timestamp;

	//@Persistent(dependent="true")
	//private PlaceBookIndex index;
}
