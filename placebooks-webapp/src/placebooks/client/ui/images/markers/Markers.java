package placebooks.client.ui.images.markers;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface Markers extends ClientBundle
{
	public static final Markers IMAGES = GWT.create(Markers.class); 
	
	ImageResource marker();
}
