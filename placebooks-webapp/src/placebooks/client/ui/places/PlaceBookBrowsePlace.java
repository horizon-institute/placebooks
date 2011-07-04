package placebooks.client.ui.places;

import placebooks.client.model.PlaceBook;
import placebooks.client.model.User;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class PlaceBookBrowsePlace extends Place
{
	@Prefix("browse")
	public static class Tokenizer implements PlaceTokenizer<PlaceBookBrowsePlace>
	{
		@Override
		public PlaceBookBrowsePlace getPlace(final String token)
		{
			return new PlaceBookBrowsePlace();
		}

		@Override
		public String getToken(final PlaceBookBrowsePlace place)
		{
			return "test";
		}
	}

	private final static String newPlaceBook = "{\"items\":[], \"metadata\":{} }";

	private final User user;

	public PlaceBookBrowsePlace()
	{
		super();
		this.user = null;
	}

	public PlaceBookBrowsePlace(final User user)
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