package placebooks.client.model;

import com.google.gwt.core.client.JavaScriptObject;

public class PlaceBookEntry extends JavaScriptObject
{
	public static final native PlaceBookEntry parse(final String json) /*-{ return eval('(' + json + ')'); }-*/;

	protected PlaceBookEntry()
	{
	}

	public final native String getDescription() /*-{ return this.description; }-*/;

	public final native String getKey() /*-{ return this.key; }-*/;

	public final native int getNumItems() /*-{ return this.numItems; }-*/;

	public final native String getOwner() /*-{ return this.owner; }-*/;

	public final native String getOwnerName() /*-{ return this.ownerName; }-*/;

	public final native int getScore() /*-{ return this.score || 0; }-*/;

	public final native String getState() /*-{ return this.state; }-*/;
	
	public final native String getCenter() /*-{ return this.center; }-*/;	
	
	public final native String getPreviewImage() /*-{ return this.previewImage; }-*/;	

	public final String getTitle()
	{
		final String title = getTitleImpl();
		if (title != null && !title.trim().equals(""))
		{
			return title;
		}
		else
		{
			return "No Title (" + getKey() + ")";
		}
	}

	public final native String getTitleImpl() /*-{ return this.title; }-*/;

	public final native void setOwner(User user) /*-{ this.owner = user; }-*/;
}
