package placebooks.client.ui.places;

import placebooks.client.model.Shelf;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

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
}
