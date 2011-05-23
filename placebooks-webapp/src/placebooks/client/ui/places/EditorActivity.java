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
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class EditorActivity extends AbstractActivity
{
	private String key;
	
	public EditorActivity(String key)
	{
		this.key = key;
	}
	
	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus)
	{
		final PlaceBookCanvas canvas = new PlaceBookCanvas();
		PlaceBookService.getPlaceBook(key, new AbstractCallback()
		{
			
			@Override
			public void success(Request request, Response response)
			{
				final PlaceBook placebook = PlaceBook.parse(response.getText());
				canvas.setPlaceBook(placebook);				
			}
		});
		
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
