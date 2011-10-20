package placebooks.client.ui.places;

import placebooks.client.AbstractCallback;
import placebooks.client.PlaceBookService;
import placebooks.client.model.PlaceBook;
import placebooks.client.model.Shelf;
import placebooks.client.ui.PlaceBookPreview;
import placebooks.client.ui.items.frames.PlaceBookItemBlankFrame;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.RootPanel;

public class PlaceBookPreviewPlace extends PlaceBookPlace
{
	@Prefix("preview")
	public static class Tokenizer implements PlaceTokenizer<PlaceBookPreviewPlace>
	{
		@Override
		public PlaceBookPreviewPlace getPlace(final String token)
		{
			return new PlaceBookPreviewPlace(null, token);
		}

		@Override
		public String getToken(final PlaceBookPreviewPlace place)
		{
			return place.getKey();
		}
	}

	private final PlaceBook placebook;
	private final String placebookKey;

	public PlaceBookPreviewPlace(final Shelf shelf, final PlaceBook placebook)
	{
		super(shelf);
		this.placebook = placebook;
		this.placebookKey = placebook.getKey();
	}

	public PlaceBookPreviewPlace(final Shelf shelf, final String placebookKey)
	{
		super(shelf);
		this.placebookKey = placebookKey;
		this.placebook = null;
	}

	String getKey()
	{
		return placebookKey;
	}
	
	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus)
	{
		final PlaceBookPreview preview = new PlaceBookPreview(getPlaceController(), getShelf());
		if (placebook != null)
		{
			preview.setPlaceBook(placebook, PlaceBookItemBlankFrame.FACTORY);
		}
		else
		{
			PlaceBookService.getPlaceBook(placebookKey, new AbstractCallback()
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
