package placebooks.client.ui.items.maps;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;

public class Icon extends JavaScriptObject
{
	protected Icon()
	{
	}

	public final native Element getImageDiv()
	/*-{
		return this.imageDiv;
	}-*/;
}
