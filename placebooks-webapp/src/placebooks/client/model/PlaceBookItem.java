package placebooks.client.model;

import com.google.gwt.core.client.JavaScriptObject;

public abstract class PlaceBookItem extends JavaScriptObject
{
	protected PlaceBookItem() {}
	
	public final native String getKey() /*-{ return this.key; }-*/;

	public final native String getPlaceBook() /*-{ return this.placebook; }-*/;
	
	public final native User getUser() /*-{ return this.owner; }-*/;
	
	//@Persistent
	//private Date timestamp;

	//@Persistent
	//private Geometry geom;

	//@Persistent
	//private URL sourceURL; // The original internet resource string if it exists
	
	// geometry
}
