package org.placebooks.client.ui.pages;

import org.placebooks.client.ui.pages.views.LibraryView;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class LibraryPage extends PlaceBookPlace
{
	@Prefix("library")
	public static class Tokenizer implements PlaceTokenizer<LibraryPage>
	{
		@Override
		public LibraryPage getPlace(final String token)
		{
			return new LibraryPage();
		}

		@Override
		public String getToken(final LibraryPage place)
		{
			return "";
		}
	}

	@Override
	public Activity createActivity()
	{
		return new LibraryView();
	}
}
