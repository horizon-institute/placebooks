package placebooks.client.model;

import com.google.gwt.core.client.JavaScriptObject;

public class LoginDetails extends JavaScriptObject
{
	protected LoginDetails()
	{
	}

	public final native String getID() /*-{ return this.id; }-*/;

	public final native String getService() /*-{ return this.service; }-*/;

	public final native String getUserID() /*-{ return this.userid; }-*/;

	public final native String getUsername() /*-{ return this.username; }-*/;
	
	public final native double getLastSync() /*-{ return this.lastSync || 0; }-*/;
	
	public final native boolean isSyncInProgress() /*-{ return this.syncInProgress; }-*/;

	public final native void setSyncInProgress(boolean b) /*-{ this.syncInProgress = b; }-*/;
}
