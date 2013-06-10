package placebooks.client.ui.pages.places;

import placebooks.client.ui.pages.PlaceBookHome;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class Home extends PlaceBookPlace
{
	@Prefix("home")
	public static class Tokenizer implements PlaceTokenizer<Home>
	{
		@Override
		public Home getPlace(final String token)
		{
			return new Home();
		}

		@Override
		public String getToken(final Home place)
		{
			return "";
		}
	}

	@Override
	public Activity createActivity()
	{
		return new PlaceBookHome();
	}
}
