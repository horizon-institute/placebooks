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

	ImageResource headline();

	ImageResource image();

	ImageResource Logo_016();

	ImageResource map();

	ImageResource movies();

	ImageResource music();

	ImageResource phone_Android();

	ImageResource picture();

	@Source("Placebook_128.png")
	ImageResource placebook_published();

	@Source("Placebook_blue.png")
	ImageResource placebook128();

	@Source("Placebook_016.png")
	ImageResource placebook16();

	ImageResource progress();

	ImageResource progress2();

	ImageResource progress3();

	ImageResource save();

	ImageResource search_025();

	ImageResource splash();

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
