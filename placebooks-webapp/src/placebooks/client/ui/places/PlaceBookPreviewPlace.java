package placebooks.client.ui.places;

import placebooks.client.model.PlaceBook;
import placebooks.client.model.Shelf;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class PlaceBookPreviewPlace extends PlaceBookPlace
{
	@Prefix("preview")
	public static class Tokenizer implements PlaceTokenizer<PlaceBookPreviewPlace>
	{
		@Override
		public PlaceBookPreviewPlace getPlace(final String token)
		{
			return new PlaceBookPreviewPlace(null, token);
		}

		@Override
		public String getToken(final PlaceBookPreviewPlace place)
		{
			return place.getKey();
		}
	}

	private final PlaceBook placebook;
	private final String placebookKey;

	public PlaceBookPreviewPlace(final Shelf shelf, final PlaceBook placebook)
	{
		super(shelf);
		this.placebook = placebook;
		this.placebookKey = placebook.getKey();
	}

	public PlaceBookPreviewPlace(final Shelf shelf, final String placebookKey)
	{
		super(shelf);
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
