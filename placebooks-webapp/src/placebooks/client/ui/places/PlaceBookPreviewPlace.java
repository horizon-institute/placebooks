package placebooks.client.ui.places;

import placebooks.client.model.PlaceBook;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class PlaceBookPreviewPlace extends Place
{
	@Prefix("preview")
	public static class Tokenizer implements PlaceTokenizer<PlaceBookPreviewPlace>
	{
		@Override
		public PlaceBookPreviewPlace getPlace(final String token)
		{
			return new PlaceBookPreviewPlace(token);
		}

		@Override
		public String getToken(final PlaceBookPreviewPlace place)
		{
			return place.getKey();
		}
	}

	private final PlaceBook placebook;
	private final String placebookKey;

	public PlaceBookPreviewPlace(final PlaceBook placebook)
	{
		this.placebook = placebook;
		this.placebookKey = placebook.getKey();
	}

	public PlaceBookPreviewPlace(final String placebookKey)
	{
		super();
		this.placebookKey = placebookKey;
		this.placebook = null;
	}

	public String getKey()
	{
		return placebookKey;
	}

	public PlaceBook getPlaceBook()
	{
		return placebook;
	}
}
