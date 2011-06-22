package placebooks.client.model;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class User extends JavaScriptObject
{
	protected User()
	{
	}

	public final native String getName()
	/*-{
		return this.name;
	}-*/;
	
	public final native JsArray<LoginDetails> getLoginDetails()
	/*-{
		if(!('loginDetails' in this))
		{
			this.loginDetails = new Array();
		}
		return this.loginDetails;
	}-*/;
}
