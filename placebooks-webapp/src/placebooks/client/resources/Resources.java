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

	ImageResource add();

	ImageResource arrow_left();

	ImageResource arrow_right();

	ImageResource audio();

	ImageResource book();

	ImageResource book_open();

	ImageResource chrome();

	ImageResource dropMenuIcon();

	ImageResource error();

	ImageResource everytrail();

	ImageResource folder();

	ImageResource image();

	ImageResource map();

	ImageResource movies();

	ImageResource music();

	ImageResource phone_Android();

	ImageResource picture();

	ImageResource placebook();

	ImageResource progress();

	ImageResource progress2();

	ImageResource progress3();

	ImageResource save();

	@Source("PlaceBook.css")
	PlaceBookCSS style();

	ImageResource television();

	ImageResource text();

	ImageResource video();

	ImageResource web_page();

	ImageResource youtube();

	ImageResource zoom_in();

	ImageResource zoom_out();
}
