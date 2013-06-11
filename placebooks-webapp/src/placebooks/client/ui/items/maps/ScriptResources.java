package placebooks.client.ui.items.maps;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

public interface ScriptResources extends ClientBundle
{
	public static final ScriptResources INSTANCE = GWT.create(ScriptResources.class);

	@Source("EPSG4326.js")
	TextResource espg4623();

	@Source("EPSG900913.js")
	TextResource espg900913();

	@Source("openspace.js")
	TextResource openspace();
}
