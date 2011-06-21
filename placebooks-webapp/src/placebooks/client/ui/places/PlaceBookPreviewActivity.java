package placebooks.client.ui.places;

import placebooks.client.AbstractCallback;
import placebooks.client.PlaceBookService;
import placebooks.client.model.PlaceBook;
import placebooks.client.ui.PlaceBookCanvas;
import placebooks.client.ui.PlaceBookItemWidgetFactory;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class PlaceBookPreviewActivity extends AbstractActivity
{
	private final String key;
	private final PlaceBook placebook;
	private final PlaceController placeController;
	
	public PlaceBookPreviewActivity(PlaceController controller, PlaceBook placebook, String key)
	{
		this.key = key;
		this.placebook = placebook;
		this.placeController = controller;
	}
	
	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus)
	{
		final PlaceBookCanvas canvas = new PlaceBookCanvas(placeController, new PlaceBookItemWidgetFactory());
		if(placebook != null)
		{
			canvas.updatePlaceBook(placebook);
		}
		else
		{
			PlaceBookService.getPlaceBook(key, new AbstractCallback()
			{		
				@Override
				public void success(Request request, Response response)
				{
					final PlaceBook placebook = PlaceBook.parse(response.getText());
					canvas.updatePlaceBook(placebook);				
				}
			});
		}
		
		panel.setWidget(canvas);
	}
}
