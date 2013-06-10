package placebooks.client.ui.pages.places;

import placebooks.client.model.PlaceBookBinder;
import placebooks.client.ui.pages.KioskPlaceBookView;
import placebooks.client.ui.pages.PlaceBookEditor;
import placebooks.client.ui.pages.PlaceBookView;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class PlaceBook extends PlaceBookPlace
{
	public enum Type 
	{
		create, edit, kiosk
	}
	
	@Prefix("placebook")
	public static class Tokenizer implements PlaceTokenizer<PlaceBook>
	{
		@Override
		public PlaceBook getPlace(final String token)
		{
			for(Type type: Type.values())
			{
				String tag = type.name() + ":";
				if(token.equals(type.name()))
				{
					return new PlaceBook(type);
				}
				else if(token.startsWith(type.name()))
				{
					return new PlaceBook(token.substring(tag.length()), type);
				}
			}
			return new PlaceBook(token);
		}

		@Override
		public String getToken(final PlaceBook place)
		{
			if(place.type == Type.create) { return "create"; }
			if (place.type != null) { return place.type.name() + ":" + place.id; }
			return place.id;
		}
	}

	private final String id;

	private final Type type;

	public PlaceBook(final Type type)
	{
		this.id = null;
		this.type = type;
	}
	
	public PlaceBook(final String id)
	{
		this.id = id;
		this.type = null;
	}
	
	public PlaceBook(final PlaceBookBinder binder)
	{
		this.id = binder.getId();
		this.type = null;
	}
	
	public PlaceBook(final String id, final Type type)
	{
		this.id = id;
		this.type = type;
	}

	public PlaceBook(final PlaceBookBinder binder, final Type type)
	{
		this.id = binder.getId();
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
