package org.placebooks.client.ui.pages;

import org.placebooks.client.ui.pages.views.SearchView;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class SearchPage extends PlaceBookPlace
{

	@Prefix("search")
	public static class Tokenizer implements PlaceTokenizer<SearchPage>
	{
		@Override
		public SearchPage getPlace(final String token)
		{
			return new SearchPage(token);
		}

		@Override
		public String getToken(final SearchPage place)
		{
			return place.search;
		}
	}

	private String search;

	public SearchPage()
	{
		search = "";
	}

	public SearchPage(final String search)
	{
		this.search = search;
	}

	@Override
	public Activity createActivity()
	{
		return new SearchView(search);
	}
}
