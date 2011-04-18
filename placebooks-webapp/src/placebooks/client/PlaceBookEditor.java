package placebooks.client;

import placebooks.client.ui.PlaceBookCanvas;
import placebooks.client.ui.Resources;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;

public class PlaceBookEditor implements EntryPoint
{
	private final PlaceBookCanvas canvas = new PlaceBookCanvas();
	
	public static final Resources RESOURCES = GWT.create(Resources.class);
	
	@Override
	public void onModuleLoad()
	{
		RESOURCES.placebookpanel().ensureInjected();
		RootPanel.get().add(canvas);
		canvas.reflow();
	}
}