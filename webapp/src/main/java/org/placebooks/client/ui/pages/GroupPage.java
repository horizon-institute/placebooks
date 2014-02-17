package org.placebooks.client.ui.pages;

import org.placebooks.client.ui.pages.views.GroupEditor;
import org.placebooks.client.ui.pages.views.GroupView;
import org.placebooks.client.ui.pages.views.KioskGroupView;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class GroupPage extends PlaceBookPlace
{
	public enum Type 
	{
		edit, kiosk
	}
	
	@Prefix("group")
	public static class Tokenizer implements PlaceTokenizer<GroupPage>
	{
		@Override
		public GroupPage getPlace(final String token)
		{
			for(Type type: Type.values())
			{
				String tag = type.name() + ":";
				if(token.startsWith(tag))
				{
					return new GroupPage(token.substring(tag.length()), type);
				}
			}
			return new GroupPage(token);
		}

		@Override
		public String getToken(final GroupPage place)
		{
			if (place.type != null) { return place.type.name() + ":" + place.id; }
			return place.id;
		}
	}

	private final String id;

	private final Type type;

	public GroupPage(final String id)
	{
		this.id = id;
		this.type = null;
	}
	
	public GroupPage(final String id, final Type type)
	{
		this.id = id;
		this.type = type;
	}

	@Override
	public Activity createActivity()
	{
		if (type == Type.edit) { return new GroupEditor(id); }
		if (type == Type.kiosk) { return new KioskGroupView(id); }		
		return new GroupView(id);
	}

	public String getId()
	{
		return id;
	}
}
