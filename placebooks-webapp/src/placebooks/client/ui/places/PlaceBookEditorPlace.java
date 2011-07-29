package placebooks.client.ui.places;

import placebooks.client.model.PlaceBook;
import placebooks.client.model.Shelf;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class PlaceBookEditorPlace extends PlaceBookPlace
{
	@Prefix("edit")
	public static class Tokenizer implements PlaceTokenizer<PlaceBookEditorPlace>
	{
		@Override
		public PlaceBookEditorPlace getPlace(final String token)
		{
			return new PlaceBookEditorPlace(token, null);
		}

		@Override
		public String getToken(final PlaceBookEditorPlace place)
		{
			return place.getKey();
		}
	}

	private final PlaceBook placebook;
	private final String placebookKey;

	public PlaceBookEditorPlace(final PlaceBook placebook, final Shelf shelf)
	{
		super(shelf);
		this.placebook = placebook;
		this.placebookKey = placebook.getKey();
	}

	public PlaceBookEditorPlace(final String placebookKey, final Shelf shelf)
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