package placebooks.client.ui.places;

import placebooks.client.model.Shelf;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class PlaceBookAppInstallPlace extends PlaceBookPlace
{
	@Prefix("appinstall")
	public static class Tokenizer implements PlaceTokenizer<PlaceBookAppInstallPlace>
	{
		@Override
		public PlaceBookAppInstallPlace getPlace(final String token)
		{
			return new PlaceBookAppInstallPlace();
		}

		@Override
		public String getToken(final PlaceBookAppInstallPlace place)
		{
			return "";
		}
	}

	public PlaceBookAppInstallPlace()
	{
		super(null);
	}

	public PlaceBookAppInstallPlace(final Shelf shelf)
	{
		super(shelf);
	}
}