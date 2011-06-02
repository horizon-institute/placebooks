package placebooks.client.ui.places;

import placebooks.client.model.PlaceBook;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class EditorPlace extends Place
{
	@Prefix("placebook")
	public static class Tokenizer implements PlaceTokenizer<EditorPlace>
	{
		@Override
		public EditorPlace getPlace(final String token)
		{
			return new EditorPlace(token);
		}

		@Override
		public String getToken(final EditorPlace place)
		{
			return place.getKey();
		}
	}

	private final String placebookKey;
	private final PlaceBook placebook;
		
	public EditorPlace(final PlaceBook placebook)
	{
		this.placebook = placebook;
		this.placebookKey = placebook.getKey();
	}
	
	public EditorPlace(final String placebookKey)
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
