package placebooks.client.ui.places;

import placebooks.client.model.PlaceBook;
import placebooks.client.model.Shelf;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class PlaceBookEditorNewPlace extends PlaceBookPlace
{
	@Prefix("create")
	public static class Tokenizer implements PlaceTokenizer<PlaceBookEditorNewPlace>
	{
		@Override
		public PlaceBookEditorNewPlace getPlace(final String token)
		{
			return new PlaceBookEditorNewPlace();
		}

		@Override
		public String getToken(final PlaceBookEditorNewPlace place)
		{
			return "placebook";
		}
	}

	private final static String newPlaceBook = "{\"items\":[], \"metadata\":{} }";

	public PlaceBookEditorNewPlace()
	{
		super(null);
	}

	public PlaceBookEditorNewPlace(final Shelf shelf)
	{
		super(shelf);
	}

	public PlaceBook getPlaceBook()
	{
		final PlaceBook placebook = PlaceBook.parse(newPlaceBook);
		if(getShelf() != null)
		{
			placebook.setOwner(getShelf().getUser());
		}
		return placebook;
	}
}