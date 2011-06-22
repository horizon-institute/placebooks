package placebooks.client.ui.places;

import placebooks.client.model.PlaceBook;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class PlaceBookEditorPlace extends Place
{
	@Prefix("edit")
	public static class Tokenizer implements PlaceTokenizer<PlaceBookEditorPlace>
	{
		@Override
		public PlaceBookEditorPlace getPlace(final String token)
		{
			return new PlaceBookEditorPlace(token);
		}

		@Override
		public String getToken(final PlaceBookEditorPlace place)
		{
			return place.getKey();
		}
	}

	private final String placebookKey;
	private final PlaceBook placebook;
		
	public PlaceBookEditorPlace(final PlaceBook placebook)
	{
		super();
		this.placebook = placebook;
		this.placebookKey = placebook.getKey();
	}
	
	public PlaceBookEditorPlace(final String placebookKey)
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