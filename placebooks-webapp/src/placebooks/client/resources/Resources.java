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
	
	ImageResource picture();
	
	ImageResource web_page();
	
	ImageResource movies();
	
	ImageResource progress();	
	
	ImageResource text();	

	@Source("PlaceBook.css")
	PlaceBookCSS style();
}
