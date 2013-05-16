package placebooks.client.model;

import java.util.Iterator;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class Shelf extends JavaScriptObject
{
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

	public final native PlaceBookGroup getGroup() /*-{ return this.group; }-*/;

	public final native User getUser() /*-{ return this.user; }-*/;

	public final native void remove(PlaceBookEntry entry)
	/*-{ 
		var idx = this.entries.indexOf(entry);
		if (idx != -1)
		{
			this.entries.splice(idx, 1);
		}
	}-*/;

	private final native JsArray<PlaceBookEntry> getEntriesImpl() /*-{ return this.entries; }-*/;
}
