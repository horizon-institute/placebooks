package placebooks.client.ui;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Resources used by the entire application.
 */
public interface Resources extends ClientBundle
{
	@Source("dropMenuIcon.png")
	ImageResource dropMenuIcon();

	@Source("placebookpanel.css")
	PlaceBookPanelCSS placebookpanel();
}
