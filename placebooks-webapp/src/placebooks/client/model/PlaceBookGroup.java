package placebooks.client.model;

import com.google.gwt.core.client.JavaScriptObject;

public class PlaceBookGroup extends JavaScriptObject
{
	protected PlaceBookGroup()
	{
	}

	public final native String getDescription() /*-{ return this.description; }-*/;

	public final native String getId() /*-{ return this.id; }-*/;

	public final native PlaceBookItem getItem() /*-{ return this.image; }-*/;

	public final native String getTitle() /*-{ return this.title; }-*/;

	public final native void setDescription(String text) /*-{ this.description = text; }-*/;

	public final native void setTitle(String text) /*-{ this.title = text; }-*/;
}
