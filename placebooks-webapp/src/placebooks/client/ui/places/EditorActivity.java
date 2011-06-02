package placebooks.client.ui.places;

import placebooks.client.AbstractCallback;
import placebooks.client.PlaceBookService;
import placebooks.client.model.PlaceBook;
import placebooks.client.model.PlaceBookItem;
import placebooks.client.ui.PlaceBookCanvas;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class EditorActivity extends AbstractActivity
{
	private final static String newPlaceBook = "{\"items\":[], \"metadata\":{} }";
	
	private final String key;
	private final PlaceBook placebook;
	private final PlaceController placeController;
	
	public EditorActivity(PlaceController controller, PlaceBook placebook, String key)
	{
		this.key = key;
		this.placebook = placebook;
		this.placeController = controller;
	}
	
	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus)
	{
		final PlaceBookCanvas canvas = new PlaceBookCanvas(placeController);
		if(placebook != null)
		{
			canvas.updatePlaceBook(placebook);
		}
		else if(key.equals("new"))
		{
			canvas.updatePlaceBook(PlaceBook.parse(newPlaceBook));
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
		
		PlaceBookService.getPaletteItems(new AbstractCallback()
		{
			@Override
			public void success(Request request, Response response)
			{
				final JsArray<PlaceBookItem> items = PlaceBookItem.parseArray(response.getText());
				canvas.setPalette(items);
			}
		});	
		
		panel.setWidget(canvas);
	}
}
