package placebooks.client.ui.pages.places;

import placebooks.client.ui.pages.PlaceBookGroups;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class Groups extends PlaceBookPlace
{
	@Prefix("groups")
	public static class Tokenizer implements PlaceTokenizer<Groups>
	{
		@Override
		public Groups getPlace(final String token)
		{
			return new Groups();
		}

		@Override
		public String getToken(final Groups place)
		{
			return null;
		}
	}

	public Groups()
	{
	}

	@Override
	public Activity createActivity()
	{
		return new PlaceBookGroups();
	}
}
