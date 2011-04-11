package placebooks.client;

import placebooks.client.ui.PlaceBookCanvas;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

public class PlaceBookEditor implements EntryPoint
{
	final PlaceBookCanvas canvas = new PlaceBookCanvas();
	
	@Override
	public void onModuleLoad()
	{
		RootPanel.get().add(canvas);
	}
}