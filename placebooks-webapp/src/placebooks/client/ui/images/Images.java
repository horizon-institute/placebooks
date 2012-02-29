package placebooks.client.ui.images;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Resources used by the entire application.
 */
public interface Images extends ClientBundle
{
	ImageResource arrow_left();

	ImageResource arrow_right();

	ImageResource dropMenuIcon();

	ImageResource error();

	ImageResource headline();

	ImageResource listitem_chrome();

	ImageResource listitem_everytrail();

	ImageResource listitem_logo();

	ImageResource listitem_phone();

	ImageResource listitem_video();

	ImageResource listitem_youtube();

	ImageResource page_next();

	ImageResource page_previous();
	
	ImageResource pallette_audio();

	ImageResource pallette_folder();

	ImageResource pallette_image();

	ImageResource pallette_map();

	ImageResource pallette_text();

	ImageResource pallette_video();

	ImageResource pallette_web();

	ImageResource placebook_open();

	@Source("Placebook_128.png")
	ImageResource placebook_published();

	@Source("Placebook_Blue.png")
	ImageResource placebook128();

	@Source("Placebook_016.png")
	ImageResource placebook16();

	ImageResource progress();

	ImageResource progress2();

	ImageResource progress3();

	ImageResource search();

	ImageResource splash();

	ImageResource toolbar_create();

	ImageResource toolbar_library();

	ImageResource zoom_in();

	ImageResource zoom_out();
}