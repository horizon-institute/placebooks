package placebooks.client.ui.places;

import placebooks.client.AbstractCallback;
import placebooks.client.PlaceBookService;
import placebooks.client.model.PlaceBook;
import placebooks.client.ui.PlaceBookEditor;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class PlaceBookEditorActivity extends AbstractActivity
{
	private final static String newPlaceBook = "{\"items\":[], \"metadata\":{} }";
	
	private final String key;
	private final PlaceBook placebook;
	private final PlaceController placeController;
	
	public PlaceBookEditorActivity(PlaceController controller, PlaceBook placebook, String key)
	{
		this.key = key;
		this.placebook = placebook;
		this.placeController = controller;
	}
	
	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus)
	{
		final PlaceBookEditor editor = new PlaceBookEditor(placeController);
		if(placebook != null)
		{
			editor.updatePlaceBook(placebook);
		}
		else if(key.equals("new"))
		{
			editor.updatePlaceBook(PlaceBook.parse(newPlaceBook));
		}
		else
		{
			PlaceBookService.getPlaceBook(key, new AbstractCallback()
			{		
				@Override
				public void success(Request request, Response response)
				{
					final PlaceBook placebook = PlaceBook.parse(response.getText());
					editor.updatePlaceBook(placebook);				
				}
			});
		}
		
//		PlaceBookService.getPaletteItems(new AbstractCallback()
//		{
//			@Override
//			public void success(Request request, Response response)
//			{
//				final JsArray<PlaceBookItem> items = PlaceBookItem.parseArray(response.getText());
//				editor.getPallett.setPalette(items);
//			}
//		});	
		
		panel.setWidget(editor);
	}
}
