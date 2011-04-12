package placebooks.client.model;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class PlaceBook extends JavaScriptObject
{
	protected PlaceBook() {}
	
	public final native JsArray<PlaceBookItem> getItems() /*-{ return this.items; }-*/;

	public final native String getLastName() /*-{ return this.last_name; }-*/;

	public final native int computeAge() /*-{ return this.computedAge; }-*/;

}
