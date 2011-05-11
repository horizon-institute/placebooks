package placebooks.client.model;

import com.google.gwt.core.client.JavaScriptObject;

public class PlaceBookItem extends JavaScriptObject
{
	public static final native PlaceBookItem parse(final String json) /*-{ return eval('(' + json + ')'); }-*/;

	protected PlaceBookItem()
	{
	}

	public final native String getClassName() /*-{ return this["@class"]; }-*/;

	public final native String getGeometry() /*-{ return this.geom; }-*/;

	public final native String getKey() /*-{ return this.key; }-*/;

	public final native String getMetadata(String name) /*-{ return this.metadata[name]; }-*/;

	public final native int getParameter(String name) /*-{ return this.parameters[name]; }-*/;
	
	public final native void setParameter(String name, int value) /*-{ this.parameters[name] = value; }-*/;	

	//public final native String getPlaceBookKey() /*-{ return this.placebook; }-*/;

	public final native String getSourceURL() /*-{ return this.sourceURL; }-*/;

	public final native String getText() /*-{ return this.text; }-*/;

	//public final native String getUserKey() /*-{ return this.owner; }-*/;

	public final native boolean hasParameter(String name) /*-{ return name in this.parameters; }-*/;

	// @Persistent
	// private Date timestamp;
}
