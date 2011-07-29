package placebooks.client.ui.places;

import placebooks.client.AbstractCallback;
import placebooks.client.PlaceBookService;
import placebooks.client.model.PlaceBook;
import placebooks.client.ui.PlaceBookCanvas;
import placebooks.client.ui.items.frames.PlaceBookItemBlankFrame;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.RootPanel;

public class PlaceBookPreviewActivity extends PlaceBookActivity
{
	private final String key;
	private final PlaceBook placebook;

	public PlaceBookPreviewActivity(PlaceBookPreviewPlace place)
	{
		super(place.getShelf());
		this.key = place.getKey();
		this.placebook = place.getPlaceBook();
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus)
	{
		final PlaceBookCanvas canvas = new PlaceBookCanvas();
		if (placebook != null)
		{
			canvas.setPlaceBook(placebook, PlaceBookItemBlankFrame.FACTORY, false);
		}
		else
		{
			PlaceBookService.getPlaceBook(key, new AbstractCallback()
			{
				@Override
				public void success(final Request request, final Response response)
				{
					final PlaceBook placebook = PlaceBook.parse(response.getText());
					canvas.setPlaceBook(placebook, PlaceBookItemBlankFrame.FACTORY, false);
				}
			});
		}

		RootPanel.get().getElement().getStyle().clearOverflow();				
		panel.setWidget(canvas);
		canvas.reflow();
	}
}
