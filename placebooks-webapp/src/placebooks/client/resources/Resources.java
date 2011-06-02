package placebooks.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Resources used by the entire application.
 */
public interface Resources extends ClientBundle
{
	public static final Resources INSTANCE = GWT.create(Resources.class);

	ImageResource dropMenuIcon();

	ImageResource image();

	ImageResource map();

	ImageResource music();

	ImageResource movies();

	ImageResource picture();

	ImageResource progress();
	
	ImageResource progress2();	

	@Source("PlaceBook.css")
	PlaceBookCSS style();

	ImageResource text();

	ImageResource web_page();
}
