package placebooks.client.ui.places;

import placebooks.client.model.Shelf;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class PlaceBookBrowsePlace extends Place
{
	@Prefix("browse")
	public static class Tokenizer implements PlaceTokenizer<PlaceBookBrowsePlace>
	{
		@Override
		public PlaceBookBrowsePlace getPlace(final String token)
		{
			return new PlaceBookBrowsePlace();
		}

		@Override
		public String getToken(final PlaceBookBrowsePlace place)
		{
			return "library";
		}
	}

	private final Shelf shelf;

	public PlaceBookBrowsePlace()
	{
		super();
		this.shelf = null;
	}

	public PlaceBookBrowsePlace(final Shelf shelf)
	{
		super();
		this.shelf = shelf;
	}

	public Shelf getShelf()
	{
		return shelf;
	}
}