package placebooks.client.model;

import java.util.Iterator;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class User extends JavaScriptObject
{
	protected User()
	{
	}

	public final native String getEmail()
	/*-{
		return this.email;
	}-*/;

	public final Iterable<PlaceBookGroup> getGroups()
	{
		return new Iterable<PlaceBookGroup>()
		{
			@Override
			public Iterator<PlaceBookGroup> iterator()
			{
				return new JSIterator<PlaceBookGroup>(getGroupsImpl());
			}
		};
	}

	public final Iterable<LoginDetails> getLoginDetails()
	{
		return new Iterable<LoginDetails>()
		{
			@Override
			public Iterator<LoginDetails> iterator()
			{
				return new JSIterator<LoginDetails>(getLoginDetailsImpl());
			}
		};
	}

	public final native String getName()
	/*-{
		return this.name;
	}-*/;

	private final native JsArray<PlaceBookGroup> getGroupsImpl()
	/*-{
		if(!('groups' in this))
		{
			this.groups = new Array();
		}
		return this.groups;
	}-*/;

	private final native JsArray<LoginDetails> getLoginDetailsImpl()
	/*-{
		if(!('loginDetails' in this))
		{
			this.loginDetails = new Array();
		}
		return this.loginDetails;
	}-*/;
}
