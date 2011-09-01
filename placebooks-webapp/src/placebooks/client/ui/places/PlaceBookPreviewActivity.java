package placebooks.client.ui.places;

import placebooks.client.AbstractCallback;
import placebooks.client.PlaceBookService;
import placebooks.client.model.PlaceBook;
import placebooks.client.ui.PlaceBookPreview;
import placebooks.client.ui.items.frames.PlaceBookItemBlankFrame;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.RootPanel;

public class PlaceBookPreviewActivity extends PlaceBookActivity
{
	private final String key;
	private final PlaceBook placebook;
	private final PlaceController controller;

	public PlaceBookPreviewActivity(PlaceController controller, PlaceBookPreviewPlace place)
	{
		super(place.getShelf());
		this.key = place.getKey();
		this.placebook = place.getPlaceBook();
		this.controller = controller;
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus)
	{
		final PlaceBookPreview preview = new PlaceBookPreview(controller, shelf);
		if (placebook != null)
		{
			preview.setPlaceBook(placebook, PlaceBookItemBlankFrame.FACTORY);
		}
		else
		{
			PlaceBookService.getPlaceBook(key, new AbstractCallback()
			{
				@Override
				public void success(final Request request, final Response response)
				{
					final PlaceBook placebook = PlaceBook.parse(response.getText());
					preview.setPlaceBook(placebook, PlaceBookItemBlankFrame.FACTORY);
				}
			});
		}

		RootPanel.get().getElement().getStyle().clearOverflow();				
		panel.setWidget(preview);
		preview.getCanvas().reflow();
	}
}
