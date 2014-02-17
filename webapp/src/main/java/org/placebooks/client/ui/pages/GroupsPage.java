package org.placebooks.client.ui.pages;

import org.placebooks.client.ui.pages.views.GroupsView;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class GroupsPage extends PlaceBookPlace
{
	@Prefix("groups")
	public static class Tokenizer implements PlaceTokenizer<GroupsPage>
	{
		@Override
		public GroupsPage getPlace(final String token)
		{
			return new GroupsPage();
		}

		@Override
		public String getToken(final GroupsPage place)
		{
			return null;
		}
	}

	public GroupsPage()
	{
	}

	@Override
	public Activity createActivity()
	{
		return new GroupsView();
	}
}
