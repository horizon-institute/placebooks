package placebooks.client.ui.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class EditorPlace extends Place
{
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

	private String placebookKey;

	public EditorPlace(final String placebookKey)
	{
		super();
		this.placebookKey = placebookKey;
	}

	public String getKey()
	{
		return placebookKey;
	}
}
