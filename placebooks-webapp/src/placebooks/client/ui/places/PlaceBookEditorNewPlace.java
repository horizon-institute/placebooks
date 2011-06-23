package placebooks.client.ui.places;

import placebooks.client.model.PlaceBook;
import placebooks.client.model.User;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class PlaceBookEditorNewPlace extends Place
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
			return null;
		}
	}

	private final static String newPlaceBook = "{\"items\":[], \"metadata\":{} }";

	private final User user;

	public PlaceBookEditorNewPlace()
	{
		super();
		this.user = null;
	}

	public PlaceBookEditorNewPlace(final User user)
	{
		super();
		this.user = user;
	}

	public PlaceBook getPlaceBook()
	{
		final PlaceBook placebook = PlaceBook.parse(newPlaceBook);
		placebook.setOwner(user);
		return placebook;
	}
}
