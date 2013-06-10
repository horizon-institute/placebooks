package placebooks.client.ui.pages.places;

import placebooks.client.ui.pages.PlaceBookLibrary;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class Library extends PlaceBookPlace
{
	@Prefix("browse")
	public static class Tokenizer implements PlaceTokenizer<Library>
	{
		@Override
		public Library getPlace(final String token)
		{
			return new Library();
		}

		@Override
		public String getToken(final Library place)
		{
			return "library";
		}
	}

	@Override
	public Activity createActivity()
	{
		return new PlaceBookLibrary();
	}
}
