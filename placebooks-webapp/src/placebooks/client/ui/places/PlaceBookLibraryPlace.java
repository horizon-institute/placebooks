package placebooks.client.ui.places;

import placebooks.client.model.Shelf;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class PlaceBookLibraryPlace extends Place
{
	@Prefix("browse")
	public static class Tokenizer implements PlaceTokenizer<PlaceBookLibraryPlace>
	{
		@Override
		public PlaceBookLibraryPlace getPlace(final String token)
		{
			return new PlaceBookLibraryPlace();
		}

		@Override
		public String getToken(final PlaceBookLibraryPlace place)
		{
			return "library";
		}
	}

	private final Shelf shelf;

	public PlaceBookLibraryPlace()
	{
		super();
		this.shelf = null;
	}

	public PlaceBookLibraryPlace(final Shelf shelf)
	{
		super();
		this.shelf = shelf;
	}
	
	public Shelf getShelf()
	{
		return shelf;
	}
}