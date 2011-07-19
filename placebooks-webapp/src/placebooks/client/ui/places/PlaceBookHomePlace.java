package placebooks.client.ui.places;

import placebooks.client.model.Shelf;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class PlaceBookHomePlace extends Place
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

	private final Shelf shelf;

	public PlaceBookHomePlace()
	{
		this.shelf = null;
	}

	public PlaceBookHomePlace(final Shelf shelf)
	{
		this.shelf = shelf;
	}

	public Shelf getShelf()
	{
		return shelf;
	}
}
