package placebooks.client.model;

import java.util.Iterator;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class PlaceBook extends JavaScriptObject
{
	public static final native PlaceBook parse(final String json)
	/*-{
		return eval('(' + json + ')');
	}-*/;

	protected PlaceBook()
	{
	}

	public final void add(final PlaceBookItem item)
	{
		getItemsInternal().push(item);
	}

	public final native void clearItems()
	/*-{
		this.items = new Array();
	}-*/;

	public final native String getGeometry()
	/*-{
		return this.geom;
	}-*/;

	public final Iterable<PlaceBookItem> getItems()
	{
		return new Iterable<PlaceBookItem>()
		{
			@Override
			public Iterator<PlaceBookItem> iterator()
			{
				return new JSIterator<PlaceBookItem>(getItemsInternal());
			}
		};
	}

	public final native String getKey()
	/*-{
		return this.id;
	}-*/;

	public final native String getMetadata(String name)
	/*-{
		return this.metadata[name];
	}-*/;

	public final native String getMetadata(String name, final String defaultValue)
	/*-{
		if('metadata' in this && name in this.metadata)
		{
			return this.metadata[name];
		}
		return defaultValue;
	}-*/;
	
	public final native String getState() /*-{
		return this.state;
	}-*/;

	public final native User getOwner() /*-{
										return this.owner;
										}-*/;

	public final native boolean hasMetadata(String name) /*-{
															return 'metadata' in this && name in this.metadata;
															}-*/;

	public final native void remove(PlaceBookItem item) /*-{
															var idx = this.items.indexOf(item);
															if (idx != -1) {
															this.items.splice(idx, 1);
															}
															}-*/;

	public final native void setKey(String text) /*-{
													this.id = text;
													}-*/;

	public final native void setMetadata(String name, String value)
	/*-{
		if(!('metadata' in this))
		{
			this.metadata = new Object();
		}
		this.metadata[name] = value;
	}-*/;

	public final native void setOwner(final User user) /*-{ this.owner = user; }-*/;

	private final native JsArray<PlaceBookItem> getItemsInternal()
	/*-{
		if(!('items' in this))
		{
			this.items = new Array();
		}
		return this.items;
	}-*/;

	// @Persistent
	// private Date timestamp;

	// @Persistent(dependent="true")
	// private PlaceBookIndex index;
}
