package org.placebooks.client.ui.pages;

import org.placebooks.client.model.PlaceBook;
import org.placebooks.client.ui.pages.views.KioskPlaceBookView;
import org.placebooks.client.ui.pages.views.PlaceBookEditor;
import org.placebooks.client.ui.pages.views.PlaceBookView;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class PlaceBookPage extends PlaceBookPlace
{
	public enum Type 
	{
		create, edit, kiosk
	}
	
	@Prefix("placebook")
	public static class Tokenizer implements PlaceTokenizer<PlaceBookPage>
	{
		@Override
		public PlaceBookPage getPlace(final String token)
		{
			for(Type type: Type.values())
			{
				String tag = type.name() + ":";
				if(token.equals(type.name()))
				{
					return new PlaceBookPage(type);
				}
				else if(token.startsWith(type.name()))
				{
					return new PlaceBookPage(token.substring(tag.length()), type);
				}
			}
			return new PlaceBookPage(token);
		}

		@Override
		public String getToken(final PlaceBookPage place)
		{
			if(place.type == Type.create) { return "create"; }
			if (place.type != null) { return place.type.name() + ":" + place.id; }
			return place.id;
		}
	}

	private final String id;

	private final Type type;

	public PlaceBookPage(final Type type)
	{
		this.id = null;
		this.type = type;
	}
	
	public PlaceBookPage(final String id)
	{
		this.id = id;
		this.type = null;
	}
	
	public PlaceBookPage(final PlaceBook placebook)
	{
		this.id = placebook.getId();
		this.type = null;
	}
	
	public PlaceBookPage(final String id, final Type type)
	{
		this.id = id;
		this.type = type;
	}

	public PlaceBookPage(final PlaceBook placebook, final Type type)
	{
		this.id = placebook.getId();
		this.type = type;
	}
	
	@Override
	public Activity createActivity()
	{
		if(type == Type.create) { return new PlaceBookEditor(null); }
		if (type == Type.edit) { return new PlaceBookEditor(id); }
		if (type == Type.kiosk) { return new KioskPlaceBookView(id); }		
		return new PlaceBookView(id);
	}

	public String getId()
	{
		return id;
	}
}
