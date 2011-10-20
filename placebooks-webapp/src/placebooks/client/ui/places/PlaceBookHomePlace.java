package placebooks.client.ui.places;

import placebooks.client.model.Shelf;
import placebooks.client.ui.PlaceBookHome;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class PlaceBookHomePlace extends PlaceBookPlace
{
	@Prefix("home")
	public static class Tokenizer implements PlaceTokenizer<PlaceBookHomePlace>
	{
		@Override
		public PlaceBookHomePlace getPlace(final String token)
		{
			return new PlaceBookHomePlace();
		}

		@Override
		public String getToken(final PlaceBookHomePlace place)
		{
			return "";
		}
	}

	public PlaceBookHomePlace()
	{
		super(null);
	}

	public PlaceBookHomePlace(final Shelf shelf)
	{
		super(shelf);
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus)
	{
		final PlaceBookHome home = new PlaceBookHome(getPlaceController(), getShelf());
		panel.setWidget(home);
	}
}
