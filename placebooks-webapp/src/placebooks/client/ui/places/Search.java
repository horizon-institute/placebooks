package placebooks.client.ui.places;

import placebooks.client.ui.PlaceBookSearch;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class Search extends PlaceBookPlace
{

	@Prefix("search")
	public static class Tokenizer implements PlaceTokenizer<Search>
	{
		@Override
		public Search getPlace(final String token)
		{
			return new Search(token);
		}

		@Override
		public String getToken(final Search place)
		{
			return place.search;
		}
	}

	private String search;

	public Search()
	{
		search = "";
	}

	public Search(final String search)
	{
		this.search = search;
	}

	@Override
	public Activity createActivity()
	{
		return new PlaceBookSearch(search);
	}
}
