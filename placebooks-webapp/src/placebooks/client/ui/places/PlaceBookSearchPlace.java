package placebooks.client.ui.places;

import placebooks.client.model.Shelf;
import placebooks.client.ui.PlaceBookSearch;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class PlaceBookSearchPlace extends PlaceBookPlace
{
	@Prefix("search")
	public static class Tokenizer implements PlaceTokenizer<PlaceBookSearchPlace>
	{
		@Override
		public PlaceBookSearchPlace getPlace(final String token)
		{
			return new PlaceBookSearchPlace(token);
		}

		@Override
		public String getToken(final PlaceBookSearchPlace place)
		{
			return place.getSearch();
		}
	}

	private final String search;

	public PlaceBookSearchPlace(final String search)
	{
		super(null);
		this.search = search;
	}

	public PlaceBookSearchPlace(final String search, final Shelf shelf)
	{
		super(shelf);
		this.search = search;
	}

	public String getSearch()
	{
		return search;
	}
	
	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus)
	{
		final PlaceBookSearch browse = new PlaceBookSearch(search, getPlaceController(), getShelf());
		panel.setWidget(browse);
	}	
}