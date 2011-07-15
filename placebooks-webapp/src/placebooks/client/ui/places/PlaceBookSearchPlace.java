package placebooks.client.ui.places;

import placebooks.client.model.Shelf;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class PlaceBookSearchPlace extends Place
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
	private final Shelf shelf;

	public PlaceBookSearchPlace(final String search)
	{
		super();
		this.shelf = null;
		this.search = search;
	}

	public PlaceBookSearchPlace(final String search, final Shelf shelf)
	{
		super();
		this.shelf = shelf;
		this.search = search;		
	}
	
	public Shelf getShelf()
	{
		return shelf;
	}
	
	public String getSearch()
	{
		return search;
	}
}