package placebooks.client.ui.places;

import placebooks.client.model.PlaceBookBinder;
import placebooks.client.ui.KioskPlaceBookView;
import placebooks.client.ui.PlaceBookEditor;
import placebooks.client.ui.PlaceBookView;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class PlaceBook extends PlaceBookPlace
{
	public enum Type 
	{
		edit, kiosk
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
				if(token.startsWith(tag))
				{
					return new PlaceBook(token.substring(tag.length()), type);
				}
			}
			return new PlaceBook(token);
		}

		@Override
		public String getToken(final PlaceBook place)
		{
			if (place.type != null) { return place.type.name() + ":" + place.id; }
			return place.id;
		}
	}

	private final String id;

	private final Type type;

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
		if (type == Type.edit) { return new PlaceBookEditor(id); }
		if (type == Type.kiosk) { return new KioskPlaceBookView(id); }		
		return new PlaceBookView(id);
	}

	public String getId()
	{
		return id;
	}
}
