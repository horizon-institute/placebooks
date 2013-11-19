package org.placebooks.client.ui.pages;

import org.placebooks.client.ui.pages.views.Welcome;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class WelcomePage extends PlaceBookPlace
{
	@Prefix("home")
	public static class Tokenizer implements PlaceTokenizer<WelcomePage>
	{
		@Override
		public WelcomePage getPlace(final String token)
		{
			return new WelcomePage();
		}

		@Override
		public String getToken(final WelcomePage place)
		{
			return "";
		}
	}

	@Override
	public Activity createActivity()
	{
		return new Welcome();
	}
}
