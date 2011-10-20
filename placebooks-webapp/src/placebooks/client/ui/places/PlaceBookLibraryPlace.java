package placebooks.client.ui.places;

import placebooks.client.model.Shelf;
import placebooks.client.ui.PlaceBookLibrary;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class PlaceBookLibraryPlace extends PlaceBookPlace
{
	@Prefix("browse")
	public static class Tokenizer implements PlaceTokenizer<PlaceBookLibraryPlace>
	{
		@Override
		public PlaceBookLibraryPlace getPlace(final String token)
		{
			return new PlaceBookLibraryPlace();
		}

		@Override
		public String getToken(final PlaceBookLibraryPlace place)
		{
			return "library";
		}
	}

	public PlaceBookLibraryPlace()
	{
		super(null);
	}

	public PlaceBookLibraryPlace(final Shelf shelf)
	{
		super(shelf);
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus)
	{
		final PlaceBookLibrary browse = new PlaceBookLibrary(getPlaceController(), getShelf());
		panel.setWidget(browse);
	}
}