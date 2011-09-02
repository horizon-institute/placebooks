package placebooks.client.model;

import java.util.Iterator;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class User extends JavaScriptObject
{
	protected User()
	{
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

	private final native JsArray<LoginDetails> getLoginDetailsImpl()
	/*-{
		if(!('loginDetails' in this))
		{
			this.loginDetails = new Array();
		}
		return this.loginDetails;
	}-*/;

	public final native String getName()
	/*-{
		return this.name;
	}-*/;
}
