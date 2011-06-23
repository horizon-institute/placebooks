package placebooks.client.ui.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class PlaceBookHomePlace extends Place
{
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
			return null;
		}
	}
}
