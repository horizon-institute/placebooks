package placebooks.client.model;

import com.google.gwt.core.client.JavaScriptObject;

public class User extends JavaScriptObject
{
	protected User()
	{
	}

	public final native String getName()
	/*-{
		return this.name;
	}-*/;

}
