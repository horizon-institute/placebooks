package placebooks.client.model;

import java.util.Iterator;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class Shelf extends JavaScriptObject
{
	public static final native Shelf parse(final String json) /*-{ return eval('(' + json + ')'); }-*/;

	protected Shelf()
	{
	}

	public final Iterable<PlaceBookEntry> getEntries()
	{
		return new Iterable<PlaceBookEntry>()
		{
			@Override
			public Iterator<PlaceBookEntry> iterator()
			{
				return new JSIterator<PlaceBookEntry>(getEntriesImpl());
			}
		};
	}

	private final native JsArray<PlaceBookEntry> getEntriesImpl() /*-{ return this.entries; }-*/;

	public final native User getUser() /*-{ return this.user; }-*/;
}
